package me.senseiwells.arucas.core

import me.senseiwells.arucas.builtin.BooleanDef
import me.senseiwells.arucas.builtin.NullDef
import me.senseiwells.arucas.builtin.NumberDef
import me.senseiwells.arucas.builtin.StringDef
import me.senseiwells.arucas.core.Type.*
import me.senseiwells.arucas.exceptions.compileError
import me.senseiwells.arucas.nodes.*
import me.senseiwells.arucas.utils.ConstructorInit
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.Parameter
import me.senseiwells.arucas.utils.Trace
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap
import kotlin.reflect.KMutableProperty0

class Parser(tokens: List<Token>): TokenReader(tokens) {
    private val cachedTrue = LiteralExpression(BooleanDef::class) { b -> b.TRUE }
    private val cacheFalse = LiteralExpression(BooleanDef::class) { b -> b.FALSE }
    private val cachedNull = LiteralExpression(NullDef::class) { n -> n.NULL }

    private var canUnpack = true
    private var lambdaCount = 1

    fun parse(): List<Statement> {
        val statements = ArrayList<Statement>()

        while (!this.isAtEnd()) {
            statements.add(this.declaration())
        }

        return statements
    }

    private fun declaration(): Statement {
        return when (this.peekType()) {
            LOCAL -> this.localDeclaration()
            FUN -> this.functionDeclaration(false)
            CLASS -> this.classDeclaration()
            ENUM -> this.enumDeclaration()
            INTERFACE -> this.interfaceDeclaration()
            else -> this.statement()
        }
    }

    private fun localDeclaration(): Statement {
        this.check(LOCAL)
        val name = this.check(IDENTIFIER, "Expected a variable name")
        this.check(ASSIGN_OPERATOR, "Expected '=' after variable name")
        val expression = this.expression()
        this.check(SEMICOLON, "Expected ';' after local variable declaration")
        return LocalVarStatement(name.content, expression, name.trace)
    }

    private fun functionDeclaration(isClass: Boolean): FunctionStatement {
        this.check(FUN)
        val name = this.check(IDENTIFIER, "Expected function name")
        this.check(LEFT_BRACKET, "Expected '(' after function name")

        val (parameters, isArbitrary) = this.getFunctionParameters(isClass)

        val returnTypes = this.getTypeHint()
        val body = this.statement()

        return FunctionStatement(name.content, isClass, parameters, isArbitrary, returnTypes, body, name.trace)
    }

    private fun classDeclaration(): ClassStatement {
        this.check(CLASS)
        val name = this.check(IDENTIFIER, "Expected class name")
        val superclasses = ArrayList<String>()
        this.match(COLON)?.let {
            do {
                superclasses.add(this.checkIdentifier("Expected super class name"))
            } while (this.isMatch(COMMA))
        }
        val type = if (superclasses.isEmpty()) "class" else "super class"
        this.check(LEFT_CURLY_BRACKET, "Expected '{' after $type name")
        val body = this.classBodyStatements(name.content)
        return ClassStatement(name.content, superclasses, body, name.trace)
    }

    private fun enumDeclaration(): Statement {
        this.check(ENUM)
        val enumName = this.check(IDENTIFIER, "Expected enum name")
        val superclasses = ArrayList<String>()
        this.match(COLON)?.let {
            do {
                superclasses.add(this.checkIdentifier("Expected super class name"))
            } while (this.isMatch(COMMA))
        }
        this.check(LEFT_CURLY_BRACKET, "Expected '{' after enum name")
        val enums = LinkedHashMap<String, Pair<List<Expression>, LocatableTrace>>()
        while (true) {
            val name = this.match(IDENTIFIER) ?: break
            if (enums.containsKey(name.content)) {
                this.error("Enum cannot have a duplicate constant: '${name.content}'", name.trace)
            }
            if (name.content == "type") {
                this.error("Enum cannot defined constant 'type'", name.trace)
            }
            val arguments = if (this.isMatch(LEFT_BRACKET)) {
                val expressions = if (this.peekType() != RIGHT_BRACKET) this.expressions() else listOf()
                this.check(RIGHT_BRACKET, "Expected ')' after enum arguments")
                expressions
            } else {
                listOf()
            }
            enums[name.content] = arguments to name.trace
            this.isMatch(COMMA)
        }
        val body = if (!this.isMatch(SEMICOLON)) {
            this.check(RIGHT_CURLY_BRACKET, "Expected '}' or ';' after enums")
            VoidStatement.INSTANCE
        } else {
            this.classBodyStatements(enumName.content)
        }
        return EnumStatement(enumName.content, superclasses, enums, body, enumName.trace)
    }

    private fun interfaceDeclaration(): Statement {
        this.check(INTERFACE)
        val name = this.check(IDENTIFIER, "Expected interface name")
        this.check(LEFT_CURLY_BRACKET, "Expected '{' after interface name")
        val functions = ArrayList<Pair<String, Int>>()
        while (!this.isMatch(RIGHT_CURLY_BRACKET)) {
            this.check(FUN, "Expected function blueprint in interface")
            val functionName = this.checkIdentifier("Expected function name after 'fun' in interface")
            this.check(LEFT_BRACKET, "Expected '(' after function name")
            var count = 0
            if (!this.isMatch(RIGHT_BRACKET)) {
                val parameters = this.getFunctionParameters(false)
                count = if (parameters.second) -1 else parameters.first.size
            }
            functions.add(functionName to count)
            this.getTypeHint()
            this.check(SEMICOLON, "Expected ';' after function declaration")
        }
        return InterfaceStatement(name.content, functions, name.trace)
    }

    private fun classBodyStatements(className: String): ClassBodyStatement {
        val trace = this.peek().trace
        val fields = HashMap<Parameter, Expression>()
        val staticFields = HashMap<Parameter, Expression>()
        val staticInitializer = ArrayList<Statement>()
        val constructors = ArrayList<ConstructorStatement>()
        val methods = ArrayList<FunctionStatement>()
        val staticMethods = ArrayList<FunctionStatement>()
        val operators = ArrayList<Pair<FunctionStatement, Type>>()
        while (!this.isMatch(RIGHT_CURLY_BRACKET)) {
            val currentTrace = this.peek().trace
            val static = this.isMatch(STATIC)
            when {
                this.isMatch(VAR) -> {
                    val name = this.check(IDENTIFIER, "Expected field name after 'var'")
                    val field = Parameter(name.content, this.getTypeHint())
                    val correctFields = if (static) staticFields else fields
                    if (correctFields.containsKey(field)) {
                        this.error("Class cannot contain duplicate field name", name.trace)
                    }
                    if (static && name.content == "type") {
                        this.error("Class cannot defined static field 'type'", name.trace)
                    }
                    val expression = when {
                        this.isMatch(ASSIGN_OPERATOR) -> {
                            val expression = this.expression()
                            this.check(SEMICOLON, "Expected ';' after field assignment")
                            expression
                        }
                        this.isMatch(SEMICOLON) -> this.cachedNull
                        else -> this.error("Expected ';' or assignment after field declaration")
                    }
                    correctFields[field] = expression
                }
                this.isMatch(IDENTIFIER) -> {
                    if (static) {
                        this.error("Class constructor cannot be static")
                    }
                    if (this.peek(-1).content != className) {
                        this.error("Constructor must have the same name as the class")
                    }
                    this.check(LEFT_BRACKET, "Expected '(' after constructor")
                    val (parameters, isArbitrary) = this.getFunctionParameters(true)
                    val constructorInit = if (this.isMatch(COLON)) {
                        val (initProvider, initType) = when {
                            this.isMatch(SUPER) -> ConstructorInit.Companion::initSuper to "super"
                            this.isMatch(THIS) -> ConstructorInit.Companion::initThis to "this"
                            else -> this.error("Expected 'this' or 'super' constructor call")
                        }
                        this.check(LEFT_BRACKET, "Expected '(' after $initType")
                        val expressions = if (this.peekType() != RIGHT_BRACKET) this.expressions() else listOf()
                        this.check(RIGHT_BRACKET, "Expected ')' after $initType call")
                        initProvider(expressions)
                    } else {
                        ConstructorInit.initNone()
                    }
                    val body = this.statement()
                    constructors.add(ConstructorStatement(parameters, isArbitrary, constructorInit, body, currentTrace))
                }
                this.peekType() == FUN -> {
                    val function = this.functionDeclaration(!static)
                    (if (static) staticMethods else methods).add(function)
                }
                this.isMatch(OPERATOR) -> {
                    if (static) {
                        this.error("Operator method cannot be static")
                    }
                    val token = this.peek().also { this.advance() }
                    val typeAsString = if (token.type == LEFT_SQUARE_BRACKET) {
                        this.check(RIGHT_SQUARE_BRACKET, "Expected closing ']'")
                        "[]"
                    } else {
                        token.type.toString()
                    }

                    this.check(LEFT_BRACKET, "Expected '(' after operator")
                    val (parameters, isArbitrary) = this.getFunctionParameters(true)
                    val count = if (isArbitrary) -1 else parameters.size

                    if (!Type.isOperatorOverridable(count, token.type)) {
                        this.error("No such operator '$typeAsString' with $count parameters")
                    }
                    val returnTypes = this.getTypeHint()
                    val body = this.statement()
                    val function = FunctionStatement("\$$typeAsString::$count", true, parameters, isArbitrary, returnTypes, body, token.trace)
                    operators.add(function to token.type)
                }
                this.peekType() == LEFT_CURLY_BRACKET -> {
                    if (!static) {
                        this.error("Class initializer must be preceded by 'static'")
                    }
                    staticInitializer.add(this.scopedStatement())
                }
                else -> this.error("Unexpected token in class statement: '${this.peek()}'")
            }
        }
        return ClassBodyStatement(fields, staticFields, staticInitializer, constructors, methods, staticMethods, operators, trace)
    }

    private fun scopedStatement(): Statement {
        return when (this.peekType()) {
            LEFT_CURLY_BRACKET -> this.scope()
            else -> ScopeStatement(this.statement())
        }
    }

    private fun scope(): Statement {
        val statements = this.statements()
        if (statements == VoidStatement.INSTANCE) {
            return statements
        }
        return ScopeStatement(statements)
    }

    private fun statements(): Statement {
        this.check(LEFT_CURLY_BRACKET)

        if (this.isMatch(RIGHT_CURLY_BRACKET)) {
            return VoidStatement.INSTANCE
        }

        val statements = ArrayList<Statement>()
        this.clearSemicolons()
        do {
            statements.add(this.declaration())
            this.clearSemicolons()
        } while (!this.isMatch(RIGHT_CURLY_BRACKET))

        return Statements(statements)
    }

    private fun statement(): Statement {
        return when (this.peekType()) {
            IF -> this.ifStatement()
            SWITCH -> this.switchStatement()
            WHILE -> this.whileStatement()
            FOR -> this.forStatement()
            FOREACH -> this.foreachStatement()
            TRY -> this.tryStatement()
            THROW -> this.throwStatement()
            RETURN -> this.returnStatement()
            CONTINUE -> this.continueStatement()
            BREAK -> this.breakStatement()
            IMPORT -> this.importStatement()
            SEMICOLON -> VoidStatement.INSTANCE.also { this.advance() }
            LEFT_CURLY_BRACKET -> this.scopedStatement()
            else -> this.expressionStatement()
        }
    }

    private fun ifStatement(): IfStatement {
        this.check(IF)
        val trace = this.check(LEFT_BRACKET, "Expected '(' after 'if'").trace
        val condition = this.expression()
        this.check(RIGHT_BRACKET, "Expected ')' after if condition")
        val body = this.scopedStatement()
        val otherwise = if (this.isMatch(ELSE)) scopedStatement() else VoidStatement.INSTANCE
        return IfStatement(condition, body, otherwise, trace)
    }

    private fun switchStatement(): Statement {
        val trace = this.check(SWITCH).trace
        this.check(LEFT_BRACKET, "Expected '(' after 'switch'")
        val condition = this.expression()
        this.check(RIGHT_BRACKET, "Expected ')' after switch condition")
        this.check(LEFT_CURLY_BRACKET, "Expected '{' after switch condition")

        val casesList = ArrayList<List<Expression>>()
        val caseStatements = ArrayList<Statement>()
        var default: Statement? = null

        while (!this.isMatch(RIGHT_CURLY_BRACKET)) {
            if (this.isMatch(DEFAULT)) {
                if (default != null) {
                    this.error("Switch statement can only have one default case")
                }
                this.check(POINTER, "Expected '->' after 'default'")
                default = this.scopedStatement()
                continue
            }
            this.check(CASE, "Expected 'case' or 'default' in switch body")
            val cases = this.expressions()
            this.check(POINTER, "Expected '->' after 'case' expressions")
            val statement = this.scopedStatement()
            casesList.add(cases)
            caseStatements.add(statement)
        }
        return SwitchStatement(condition, casesList, caseStatements, default, trace)
    }

    private fun whileStatement(): WhileStatement {
        this.check(WHILE)
        val trace = this.check(LEFT_BRACKET, "Expected '(' after 'while'").trace
        val condition = this.expression()
        this.check(RIGHT_BRACKET, "Expected ')' after while condition")
        val body = this.scopedStatement()
        return WhileStatement(condition, body, trace)
    }

    private fun forStatement(): ForStatement {
        this.check(FOR)
        this.check(LEFT_BRACKET, "Expected '(' after 'for'")
        val initial = when (this.peekType()) {
            LOCAL -> this.localDeclaration()
            SEMICOLON -> VoidStatement.INSTANCE.also { this.advance() }
            else -> this.expressionStatement()
        }
        val trace = this.peek().trace
        val condition = if (this.peekType() == SEMICOLON) this.cachedTrue else this.expression()
        this.check(SEMICOLON, "Expected ';' after for condition")
        val end = if (this.peekType() == RIGHT_BRACKET) this.cachedNull else this.expression()
        this.check(RIGHT_BRACKET, "Expected ')' after for expressions")
        val body = this.statement()
        return ForStatement(initial, condition, end, body, trace)
    }

    private fun foreachStatement(): ForeachStatement {
        this.check(FOREACH)
        this.check(LEFT_BRACKET, "Expected '(' after 'foreach'")
        val name = this.checkIdentifier("Expected foreach variable name after '('")
        this.check(COLON, "Expected ':' between variable name and iterator expression")
        val trace = this.peek().trace
        val iteratorExpression = this.expression()
        this.check(RIGHT_BRACKET, "Expected ')' after iterator expression")
        val body = this.statement()
        return ForeachStatement(name, iteratorExpression, body, trace)
    }

    private fun tryStatement(): TryStatement {
        this.check(TRY)
        val body = this.scopedStatement()
        val parameter: Parameter?
        val trace: Trace
        val catchBody: Statement
        if (this.isMatch(CATCH)) {
            this.check(LEFT_BRACKET, "Expected '(' after 'catch'")
            parameter = Parameter(this.checkIdentifier("Expected catch variable name after '('"))
            trace = this.peek().trace
            parameter.typeNames = this.getTypeHint()
            this.check(RIGHT_BRACKET, "Expected ')' after catch parameter")
            catchBody = this.statement()
        } else {
            parameter = null
            trace = this.peek().trace
            catchBody = VoidStatement.INSTANCE
        }
        val finally = if (this.isMatch(FINALLY)) this.scopedStatement() else VoidStatement.INSTANCE
        return TryStatement(body, catchBody, parameter, finally, trace)
    }

    private fun throwStatement(): ThrowStatement {
        this.check(THROW)
        val trace = this.peek().trace
        val expression = this.expression()
        this.check(SEMICOLON, "Expected ';' after throw expression")
        return ThrowStatement(expression, trace)
    }

    private fun returnStatement(): ReturnStatement {
        this.check(RETURN)
        val trace = this.peek().trace
        if (this.isMatch(SEMICOLON)) {
            return ReturnStatement(this.cachedNull, trace)
        }
        val expression = this.expression()
        this.check(SEMICOLON, "Expected ';' after return expression")
        return ReturnStatement(expression, trace)
    }

    private fun continueStatement(): ContinueStatement {
        val trace = this.peek().trace
        this.check(CONTINUE)
        this.check(SEMICOLON, "Expected ';' after 'continue'")
        return ContinueStatement(trace)
    }

    private fun breakStatement(): BreakStatement {
        val trace = this.peek().trace
        this.check(BREAK)
        this.check(SEMICOLON, "Expected ';' after 'break'")
        return BreakStatement(trace)
    }

    private fun importStatement(): Statement {
        val trace = this.check(IMPORT).trace
        val isLocal = this.isMatch(LOCAL)
        val names = ArrayList<String>()
        if (!this.isMatch(MULTIPLY)) {
            do {
                names.add(this.checkIdentifier("Expected import class name"))
            } while (this.isMatch(COMMA))
        }
        this.check(FROM, "Expected 'from' after import names")
        val builder = StringBuilder(this.checkIdentifier("Expected module name"))
        while (this.isMatch(DOT)) {
            builder.append(".").append(this.checkIdentifier("expected submodule name after '.'"))
        }
        this.check(SEMICOLON, "Expected ';' after module name")
        return ImportStatement(names, builder.toString(), isLocal, trace)
    }

    private fun expressionStatement(): Statement {
        val expression = this.pushState(this::canUnpack, true) {
            this.expression()
        }
        this.check(SEMICOLON, "Expected ';' after expression")
        return ExpressionStatement(expression)
    }

    private fun expression() = this.assignment()

    private fun assignment(): Expression {
        val left = this.logicalOr()
        return when {
            this.canUnpack && this.peekType() == COMMA -> this.unpackAssignment(left)
            this.isMatch(ASSIGN_OPERATOR) -> {
                if (left !is Assignable) {
                    this.error("Cannot assign '$left'")
                }
                val right = this.assignment()
                left.toAssignable(right)
            }
            else -> left
        }
    }

    private fun unpackAssignment(first: Expression): UnpackAssignExpression {
        val assignables = ArrayList<AssignableExpression>()
        var next = first
        do {
            if (next !is Assignable) {
                this.error("Cannot assign '$next'")
            }
            assignables.add(next.toAssignable(this.cachedNull))
        } while (this.isMatch(COMMA).also { if (it) next = this.logicalOr() })
        this.check(ASSIGN_OPERATOR, "Expected '=' after unpack variables")
        val trace = this.peek().trace
        val assignee = this.expression()
        return UnpackAssignExpression(assignables, assignee, trace)
    }

    private fun logicalOr(): Expression {
        var left = this.logicalAnd()
        while (true) {
            val current = this.match(OR)
            current?.let {
                val right = this.logicalAnd()
                left = BinaryExpression(left, it.type, right, it.trace)
            } ?: break
        }
        return left
    }

    private fun logicalAnd(): Expression {
        var left = this.bitOr()
        while (true) {
            val current = this.match(AND)
            current?.let {
                val right = this.bitOr()
                left = BinaryExpression(left, it.type, right, it.trace)
            } ?: break
        }
        return left
    }

    private fun bitOr(): Expression {
        var left = this.xor()
        while (true) {
            val current = this.match(BIT_OR)
            current?.let {
                val right = this.xor()
                left = BinaryExpression(left, it.type, right, it.trace)
            } ?: break
        }
        return left
    }

    private fun xor(): Expression {
        var left = this.bitAnd()
        while (true) {
            val current = this.match(XOR)
            current?.let {
                val right = this.bitAnd()
                left = BinaryExpression(left, it.type, right, it.trace)
            } ?: break
        }
        return left
    }

    private fun bitAnd(): Expression {
        var left = this.equality()
        while (true) {
            val current = this.match(BIT_AND)
            current?.let {
                val right = this.equality()
                left = BinaryExpression(left, it.type, right, it.trace)
            } ?: break
        }
        return left
    }

    private fun equality(): Expression {
        var left = this.relational()
        while (true) {
            val current = this.match(EQUALS, NOT_EQUALS)
            current?.let {
                val right = this.relational()
                left = BinaryExpression(left, it.type, right, it.trace)
            } ?: break
        }
        return left
    }

    private fun relational(): Expression {
        var left = this.shift()
        while (true) {
            val current = this.match(LESS_THAN, LESS_THAN_EQUAL, MORE_THAN, MORE_THAN_EQUAL)
            current?.let {
                val right = this.shift()
                left = BinaryExpression(left, it.type, right, it.trace)
            } ?: break
        }
        return left
    }

    private fun shift(): Expression {
        var left = this.additive()
        while (true) {
            val current = this.match(SHIFT_LEFT, SHIFT_RIGHT)
            current?.let {
                val right = this.additive()
                left = BinaryExpression(left, it.type, right, it.trace)
            } ?: break
        }
        return left
    }

    private fun additive(): Expression {
        var left = this.multiplicative()
        while (true) {
            val current = this.match(PLUS, MINUS)
            current?.let {
                val right = this.multiplicative()
                left = BinaryExpression(left, it.type, right, it.trace)
            } ?: break
        }
        return left
    }

    private fun multiplicative(): Expression {
        var left = unary()
        while (true) {
            val current = this.match(MULTIPLY, DIVIDE)
            current?.let {
                val right = unary()
                left = BinaryExpression(left, it.type, right, it.trace)
            } ?: break
        }
        return left
    }

    private fun unary(): Expression {
        val current = this.match(NOT, PLUS, MINUS)
        current?.let {
            val unary = this.unary()
            return UnaryExpression(it.type, unary, it.trace)
        }
        return power()
    }

    private fun power(): Expression {
        var left = this.post()
        while (true) {
            val current = this.match(POWER)
            current?.let {
                val right = this.unary()
                left = BinaryExpression(left, it.type, right, it.trace)
            } ?: break
        }
        return left
    }

    private fun post(): Expression {
        var expression = this.atom()
        while (true) {
            when {
                this.isMatch(LEFT_BRACKET) -> {
                    val trace = this.peek(-1).trace
                    val arguments: List<Expression>
                    if (!this.isMatch(RIGHT_BRACKET)) {
                        arguments = this.expressions()
                        this.check(RIGHT_BRACKET, "Expected ')' after call arguments")
                    } else {
                        arguments = listOf()
                    }
                    expression = if (expression is Callable) expression.toCallable(arguments) else CallExpression(expression, arguments, trace)
                }
                this.isMatch(LEFT_SQUARE_BRACKET) -> {
                    val trace = this.peek().trace
                    val index = this.expression()
                    this.check(RIGHT_SQUARE_BRACKET, "Expected ']' after index")
                    expression = BracketAccessExpression(expression, index, trace)
                }
                this.isMatch(DOT) -> {
                    val name = this.check(IDENTIFIER, "Expected field name after '.'")
                    expression = MemberAccessExpression(expression, name.content, name.trace)
                }
                this.isMatch(INCREMENT) -> {
                    expression = this.modifyExpression(expression, PLUS)
                }
                this.isMatch(DECREMENT) -> {
                    expression = this.modifyExpression(expression, MINUS)
                }
                else -> break
            }
        }
        return expression
    }

    private fun atom(): Expression {
        val current = this.peek()
        return when {
            this.isMatch(TRUE) -> this.cachedTrue
            this.isMatch(FALSE) -> this.cacheFalse
            this.isMatch(NULL) -> this.cachedNull
            this.isMatch(IDENTIFIER) -> AccessExpression(current.content, current.trace)
            this.isMatch(NUMBER) -> LiteralExpression(NumberDef::class) { n -> n.literal(current.content) }
            this.isMatch(STRING) -> LiteralExpression(StringDef::class) { s -> s.literal(current.content) }
            this.isMatch(THIS) -> ThisExpression(current.trace)
            this.isMatch(SUPER) -> SuperExpression(current.trace)
            this.isMatch(LEFT_SQUARE_BRACKET) -> this.listLiteral()
            this.isMatch(LEFT_CURLY_BRACKET) -> this.mapLiteral()
            this.isMatch(FUN) -> this.functionExpression()
            this.isMatch(NEW) -> {
                val name = this.check(IDENTIFIER, "Expected class name after 'new'")
                NewAccessExpression(name.content, name.trace)
            }
            this.isMatch(LEFT_BRACKET) -> {
                val expression = this.expression()
                this.check(RIGHT_BRACKET, "Expected closing ')' after expression")
                BracketExpression(expression)
            }
            else -> this.error()
        }
    }

    private fun modifyExpression(expression: Expression, type: Type): AssignableExpression {
        val one = LiteralExpression(NumberDef::class) { n -> n.create(1.0) }
        if (expression !is Assignable) {
            this.error("Cannot increment a non assignable expression")
        }
        return expression.toAssignable(BinaryExpression(expression, type, one, this.peek().trace))
    }

    private fun listLiteral(): ListExpression {
        if (!this.isMatch(RIGHT_SQUARE_BRACKET)) {
            val expressions = this.expressions()
            this.check(RIGHT_SQUARE_BRACKET, "Expected closing ']' after list expressions")
            return ListExpression(expressions)
        }
        return ListExpression(listOf())
    }

    private fun mapLiteral(): MapExpression {
        return this.pushState(this::canUnpack, false) {
            if (!this.isMatch(RIGHT_CURLY_BRACKET)) {
                val map = LinkedHashMap<Expression, Expression>()
                do {
                    val key = this.expression()
                    this.check(COLON, "Expected ':' between key and value in a map")
                    map[key] = this.expression()
                } while (this.isMatch(COMMA))
                this.check(RIGHT_CURLY_BRACKET, "Expected closing '}' after map expressions")
                return@pushState MapExpression(map)
            }
            return@pushState MapExpression(mapOf())
        }
    }

    private fun functionExpression(): FunctionExpression {
        val trace = this.check(LEFT_BRACKET, "Expected '(' after 'fun'").trace

        val (parameters, isArbitrary) = this.getFunctionParameters(false)

        val returnTypes = this.getTypeHint()
        val body = if (this.peekType() == LEFT_CURLY_BRACKET) {
            this.statement()
        } else {
            ReturnStatement(this.expression(), trace)
        }

        return FunctionExpression("${this.lambdaCount++}\$lambda", parameters, isArbitrary, returnTypes, body, trace)
    }

    private fun expressions(): List<Expression> {
        return this.pushState(this::canUnpack, false) {
            val expressions = ArrayList<Expression>()
            do {
                expressions.add(this.expression())
            } while (this.isMatch(COMMA))
            expressions
        }
    }

    private fun getFunctionParameters(isClass: Boolean): Pair<List<Parameter>, Boolean> {
        return this.pushState(this::canUnpack, false) {
            val arguments = ArrayList<Parameter>()
            if (isClass) {
                arguments.add(Parameter("this"))
            }

            var isFirst = true
            while (!this.isMatch(RIGHT_BRACKET) && (isFirst || this.isMatch(COMMA))) {
                val parameter = Parameter(this.checkIdentifier("Expected argument name"))
                arguments.add(parameter)

                if (this.isMatch(ARBITRARY)) {
                    if (!isFirst) {
                        this.error("Cannot have multiple arguments with arbitrary function")
                    }
                    this.check(RIGHT_BRACKET, "Expected ')' after arbitrary argument")
                    return@pushState arguments to true
                }

                parameter.typeNames = this.getTypeHint()
                isFirst = false
            }
            // this.check(RIGHT_BRACKET, "Expected ')' after arguments")
            arguments to false
        }
    }

    private fun getTypeHint(): Array<String>? {
        return this.match(COLON)?.let {
            val typeNames = ArrayList<String>()
            do {
                typeNames.add(this.checkIdentifier("Expected class name"))
            } while (this.match(BIT_OR) != null)
            typeNames.toTypedArray()
        }
    }

    @Suppress("ControlFlowWithEmptyBody")
    private fun clearSemicolons() {
        while (this.isMatch(SEMICOLON));
    }

    private fun check(type: Type, message: String = "Unexpected token '${this.peek()}', expected type '$type'"): Token {
        val current = this.peek()
        if (current.type != type) {
            this.error(message, current.trace)
        }
        this.advance()
        return current
    }

    private fun checkIdentifier(message: String = "Unexpected token '${this.peek()}', expected type '$IDENTIFIER'"): String {
        return this.check(IDENTIFIER, message).content
    }

    private fun error(message: String = "Unexpected token '${this.peek()}'", trace: Trace = this.peek().trace): Nothing {
        compileError(message, trace)
    }

    private fun <T, R> pushState(property: KMutableProperty0<T>, value: T, function: () -> R): R {
        val old = property.get()
        return try {
            property.set(value)
            function()
        } finally {
            property.set(old)
        }
    }
}