package me.senseiwells.arucas.core

import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.exceptions.compileError
import me.senseiwells.arucas.nodes.*
import me.senseiwells.arucas.utils.ArucasFunction
import me.senseiwells.arucas.utils.LocalCache
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.Parameter
import java.util.*
import kotlin.reflect.KMutableProperty0

class Resolver(
    private val statements: List<Statement>,
    classes: Iterable<ClassDefinition>,
    functions: Iterable<ArucasFunction>
): StatementVisitor<Unit>, ExpressionVisitor<Unit> {
    private val localCache = LocalCache()

    private val classes = HashSet<String>()
    private val functions = HashSet<Id>()

    private val varScope = Stack<HashSet<String>>()
    private val functionScope = Stack<HashSet<Id>>()
    private val classScope = Stack<HashSet<String>>()

    private var currentClass: String? = null
    private var inConstructor = false
    private var inFinally = false
    private var inLoop = false

    init {
        classes.forEach {
            this.classes.add(it.name)
        }
        functions.forEach {
            this.functions.add(Id(it.name, it.count))
        }
    }

    fun run(): LocalCache {
        this.pushScope()
        this.resolveStatements(this.statements)
        this.popScope()
        return this.localCache
    }

    private fun pushScope() {
        this.varScope.push(HashSet())
        this.functionScope.push(HashSet())
        this.classScope.push(HashSet())
    }

    private fun popScope() {
        this.varScope.pop()
        this.functionScope.pop()
        this.classScope.pop()
    }

    private fun isVarDefined(name: String): Unit? {
        this.varScope.forEach { if (it.contains(name)) return null }
        return Unit
    }

    private fun isFunctionDefined(id: Id): Unit? {
        if (this.functions.contains(id)) {
            return null
        }
        this.functionScope.forEach { if (it.contains(id)) return null }
        return Unit
    }

    private fun isClassDefined(name: String): Unit? {
        if (this.classes.contains(name)) {
            return null
        }
        this.classScope.forEach { if (it.contains(name)) return null }
        return Unit
    }

    private fun defineVar(name: String, trace: LocatableTrace) {
        this.isClassDefined(name) ?: this.alreadyDefined(name, "variable", "class", trace)
        this.isFunctionDefined(Id(name, -2)) ?: this.alreadyDefined(name, "variable", "function", trace)
        if (this.varScope.isNotEmpty()) {
            val scope = this.varScope.peek()
            if (scope.contains(name)) {
                compileError("variable '$name' is already declared locally in this scope", trace)
            }
            scope.add(name)
        }
    }

    private fun defineFunction(name: String, parameters: Int, trace: LocatableTrace) {
        val id = Id(name, parameters)
        this.isFunctionDefined(id) ?: this.alreadyDefined(name, "function", "function", trace, " with $parameters parameters")
        this.isVarDefined(name) ?: this.alreadyDefined(name, "function", "variable", trace)
        this.isClassDefined(name) ?: this.alreadyDefined(name, "function", "class", trace)
        if (this.functionScope.isNotEmpty()) {
            this.functionScope.peek().add(id)
        }
    }

    private fun defineClass(name: String, trace: LocatableTrace) {
        this.isClassDefined(name) ?: this.alreadyDefined(name, "class", "class", trace)
        this.isVarDefined(name) ?: this.alreadyDefined(name, "class", "variable", trace)
        this.isFunctionDefined(Id(name, -2)) ?: this.alreadyDefined(name, "class", "function", trace)
        if (this.classScope.isNotEmpty()) {
            this.classScope.peek().add(name)
        }
    }

    private fun alreadyDefined(name: String, new: String, old: String, trace: LocatableTrace, extra: String = ""): Nothing {
        compileError("$new '$name' cannot be defined because a $old$extra is already defined with that name", trace)
    }

    /**
     * This is used to cache the scope of a variable.
     * This is needed when accessing and assigning.
     *
     * Returns whether the variable existed.
     */
    private fun cacheVar(visitable: Visitable, name: String): Boolean {
        val scopeSize = this.varScope.size - 1
        for (i in scopeSize downTo 0) {
            val scope = this.varScope[i]
            if (scope.contains(name)) {
                this.localCache.setVar(visitable, scopeSize - i)
                return true
            }
        }
        return false
    }

    private fun cacheFunction(visitable: Visitable, name: String, parameters: Int): Boolean {
        val id = Id(name, parameters)
        if (this.functions.contains(id)) {
            return true
        }

        val scopeSize = this.functionScope.size - 1
        for (i in scopeSize downTo 0) {
            val scope = this.functionScope[i]
            if (scope.contains(id)) {
                this.localCache.setFunction(visitable, scopeSize - i)
                return true
            }
        }

        return parameters != -1 && this.cacheFunction(visitable, name, -1)
    }

    private fun cacheClass(visitable: Visitable, name: String) {
        if (this.classes.contains(name)) {
            return
        }

        val scopeSize = this.classScope.size - 1
        for (i in scopeSize downTo 0) {
            val scope = this.classScope[i]
            if (scope.contains(name)) {
                this.localCache.setClass(visitable, scopeSize - i)
            }
        }
    }

    private fun resolveStatements(statements: Collection<Statement>) {
        for (statement in statements) {
            this.resolve(statement)
        }
    }

    private fun resolve(statement: Statement) {
        statement.visit(this)
    }

    private fun resolveExpressions(expressions: Collection<Expression>) {
        for (expression in expressions) {
            this.resolve(expression)
        }
    }

    private fun resolve(expression: Expression) {
        expression.visit(this)
    }

    private inline fun <T> pushState(property: KMutableProperty0<T>, value: T, function: () -> Unit) {
        val old = property.get()
        try {
            property.set(value)
            function()
        } finally {
            property.set(old)
        }
    }

    private fun visitParameter(parameter: Parameter, trace: LocatableTrace) {
        this.defineVar(parameter.name, trace)
        // Should cache classes here?
    }

    private fun visitParameters(parameters: List<Parameter>, trace: LocatableTrace) {
        parameters.forEach { this.visitParameter(it, trace) }
    }

    private fun visitFunction(params: List<Parameter>, body: Statement, trace: LocatableTrace, constructor: (() -> Unit)? = null) {
        this.pushState(this::inFinally, false) {
            this.pushState(this::inLoop, false) {
                this.pushState(this::inConstructor, constructor != null) {
                    this.pushScope()
                    this.visitParameters(params, trace)
                    constructor?.let { it() }
                    this.resolve(body)
                    this.popScope()
                }
            }
        }
    }

    override fun visitVoid(void: VoidStatement) {

    }

    override fun visitStatements(statements: Statements) {
        statements.statements.forEach { this.resolve(it) }
    }

    override fun visitScope(scope: ScopeStatement) {
        this.pushScope()
        this.resolve(scope.statements)
        this.popScope()
    }

    override fun visitLocalVar(local: LocalVarStatement) {
        //if (this.varScope.isEmpty()) {
        //    compileError("Cannot declare local variable in the global scope", local.trace)
        //}
        this.resolve(local.assignee)
        this.defineVar(local.name, local.trace)
    }

    override fun visitExpression(expression: ExpressionStatement) {
        this.resolve(expression.expression)
    }

    override fun visitIf(ifStatement: IfStatement) {
        this.resolve(ifStatement.condition)
        this.resolve(ifStatement.body)
        this.resolve(ifStatement.otherwise)
    }

    override fun visitSwitch(switch: SwitchStatement) {
        this.resolve(switch.condition)
        for (i in switch.caseStatements.indices) {
            this.resolveExpressions(switch.casesList[i])
            this.resolve(switch.caseStatements[i])
        }
        switch.defaultStatement?.let { this.resolve(it) }
    }

    override fun visitFunction(function: FunctionStatement) {
        if (!function.isClass) {
            val parameters = if (function.arbitrary) -1 else function.parameters.size
            this.defineFunction(function.name, parameters, function.start)
        }
        this.visitFunction(function.parameters, function.body, function.start)
    }

    override fun visitReturn(returnStatement: ReturnStatement) {
        when {
            this.inFinally -> compileError("Cannot return inside a finally statement", returnStatement.trace)
            this.inConstructor -> compileError("Cannot return inside a constructor", returnStatement.trace)
        }
        this.resolve(returnStatement.expression)
    }

    override fun visitWhile(whileStatement: WhileStatement) {
        this.pushState(this::inLoop, true) {
            this.resolve(whileStatement.condition)
            this.resolve(whileStatement.body)
        }
    }

    override fun visitFor(forStatement: ForStatement) {
        this.pushState(this::inLoop, true) {
            this.pushScope()
            this.resolve(forStatement.initial)
            this.resolve(forStatement.condition)
            this.resolve(forStatement.body)
            this.resolve(forStatement.expression)
            this.popScope()
        }
    }

    override fun visitForeach(foreach: ForeachStatement) {
        this.pushState(this::inLoop, true) {
            this.resolve(foreach.iterable)
            this.pushScope()
            this.defineVar(foreach.name, foreach.start)
            this.resolve(foreach.body)
            this.popScope()
        }
    }

    override fun visitContinue(continueStatement: ContinueStatement) {
        when {
            this.inFinally -> compileError("Cannot continue inside a finally statement", continueStatement.trace)
            !this.inLoop -> compileError("Cannot continue outside a loop", continueStatement.trace)
        }
    }

    override fun visitBreak(breakStatement: BreakStatement) {
        when {
            this.inFinally -> compileError("Cannot break inside a finally statement", breakStatement.trace)
            !this.inLoop -> compileError("Cannot break outside a loop", breakStatement.trace)
        }
    }

    override fun visitTry(tryStatement: TryStatement) {
        this.resolve(tryStatement.body)
        this.pushScope()
        tryStatement.catchParameter?.let { this.visitParameter(it, tryStatement.start) }
        this.resolve(tryStatement.catchBody)
        this.popScope()
        this.pushState(this::inFinally, true) {
            this.resolve(tryStatement.finally)
        }
    }

    override fun visitThrow(throwStatement: ThrowStatement) {
        if (this.inFinally) {
            compileError("Cannot throw inside a finally statement", throwStatement.trace)
        }
        this.resolve(throwStatement.throwable)
    }

    override fun visitConstructor(constructor: ConstructorStatement) {
        this.visitFunction(constructor.parameters, constructor.body, constructor.start) {
            this.resolveExpressions(constructor.init.arguments)
        }
    }

    override fun visitClassBody(classBody: ClassBodyStatement) {
        this.resolveExpressions(classBody.fields.values)
        this.resolveStatements(classBody.constructors)
        this.resolveStatements(classBody.methods)
        this.resolveStatements(classBody.operators.map { it.first })
        this.resolveStatements(classBody.staticMethods)
        this.resolveExpressions(classBody.staticFields.values)
        this.resolveStatements(classBody.staticInitializers)
    }

    override fun visitClass(classStatement: ClassStatement) {
        this.defineClass(classStatement.name, classStatement.start)
        this.pushState(this::currentClass, classStatement.name) {
            this.pushScope()
            this.defineVar("this", classStatement.start)
            this.resolve(classStatement.body)
            this.popScope()
        }
    }

    override fun visitEnum(enumStatement: EnumStatement) {
        this.defineClass(enumStatement.name, enumStatement.start)
        this.pushState(this::currentClass, enumStatement.name) {
            this.pushScope()
            this.defineVar("this", enumStatement.start)
            this.resolve(enumStatement.body)
            this.popScope()
        }
    }

    override fun visitInterface(interfaceStatement: InterfaceStatement) {
        this.defineClass(interfaceStatement.name, interfaceStatement.start)
    }

    override fun visitImport(importStatement: ImportStatement) {

    }

    override fun visitExact(exact: ExactExpression) {

    }

    override fun <L : PrimitiveDefinition<*>> visitLiteral(literal: LiteralExpression<L>) {

    }

    override fun visitBracket(bracket: BracketExpression) {
        this.resolve(bracket.expression)
    }

    override fun visitList(list: ListExpression) {
        this.resolveExpressions(list.expressions)
    }

    override fun visitMap(map: MapExpression) {
        map.expressions.forEach { (a, b) ->
            this.resolve(a)
            this.resolve(b)
        }
    }

    override fun visitFunction(function: FunctionExpression) {
        this.visitFunction(function.parameters, function.body, function.start)
    }

    override fun visitUnary(unary: UnaryExpression) {
        this.resolve(unary.expression)
    }

    override fun visitBinary(binary: BinaryExpression) {
        this.resolve(binary.left)
        this.resolve(binary.right)
    }

    override fun visitUnpackAssign(assign: UnpackAssignExpression) {
        this.resolve(assign.assignee)
        for (assignable in assign.assignables) {
            this.resolve(assignable)
        }
    }

    override fun visitAccess(access: AccessExpression) {
        this.cacheVar(access, access.name)
    }

    override fun visitFunctionAccess(access: FunctionAccessExpression) {
        if (!this.cacheFunction(access, access.name, access.parameters)) {
            this.cacheVar(access, access.name)
        }
    }

    override fun visitAssign(assign: AssignExpression) {
        this.resolve(assign.assignee)
        if (!this.cacheVar(assign, assign.name)) {
            this.defineVar(assign.name, assign.trace)
        }
    }

    override fun visitCall(call: CallExpression) {
        this.resolve(call.expression)
        this.resolveExpressions(call.arguments)
    }

    override fun visitThis(thisExpression: ThisExpression) {
        this.currentClass ?: compileError("Cannot use 'this' outside of classes", thisExpression.trace)
        this.cacheVar(thisExpression, "this")
    }

    override fun visitSuper(superExpression: SuperExpression) {
        val clazz = this.currentClass ?: compileError("Cannot use 'super' outside of classes", superExpression.trace)
        this.cacheVar(superExpression, "this")
        this.localCache.setSuper(superExpression, clazz)
    }

    override fun visitMemberAccess(access: MemberAccessExpression) {
        this.resolve(access.expression)
    }

    override fun visitMemberAssign(assign: MemberAssignExpression) {
        this.resolve(assign.assignee)
        this.resolve(assign.expression)
    }

    override fun visitMemberCall(call: MemberCallExpression) {
        this.resolve(call.expression)
        this.resolveExpressions(call.arguments)
    }

    override fun visitNewAccess(access: NewAccessExpression) {
        this.cacheClass(access, access.name)
    }

    override fun visitNewCall(call: NewCallExpression) {
        this.cacheClass(call, call.name)
        this.resolveExpressions(call.arguments)
    }

    override fun visitBracketAccess(access: BracketAccessExpression) {
        this.resolve(access.expression)
        this.resolve(access.index)
    }

    override fun visitBracketAssign(assign: BracketAssignExpression) {
        this.resolve(assign.expression)
        this.resolve(assign.index)
        this.resolve(assign.assignee)
    }

    private class Id(val name: String, val parameters: Int) {
        override fun equals(other: Any?): Boolean {
            return other is Id && other.name == name && (other.parameters == this.parameters || this.parameters == -2)
        }

        override fun hashCode(): Int {
            return this.name.hashCode()
        }
    }
}