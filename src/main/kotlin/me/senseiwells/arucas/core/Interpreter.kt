package me.senseiwells.arucas.core

import me.senseiwells.arucas.api.ArucasAPI
import me.senseiwells.arucas.api.ThreadHandler
import me.senseiwells.arucas.builtin.*
import me.senseiwells.arucas.classes.*
import me.senseiwells.arucas.exceptions.FatalError
import me.senseiwells.arucas.exceptions.Propagator
import me.senseiwells.arucas.exceptions.RuntimeError
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.nodes.*
import me.senseiwells.arucas.utils.*
import me.senseiwells.arucas.utils.Properties
import me.senseiwells.arucas.utils.impl.ArucasList
import me.senseiwells.arucas.utils.impl.ArucasOrderedMap
import me.senseiwells.arucas.utils.impl.ArucasThread
import java.util.*
import kotlin.reflect.KClass

sealed class Interpreter: StatementVisitor<Unit>, ExpressionVisitor<ClassInstance> {
    companion object {
        @JvmStatic
        fun of(
            content: String,
            name: String,
            api: ArucasAPI = ArucasAPI.Builder().addDefault().build(),
            factory: (Interpreter) -> ThreadHandler = ::ThreadHandler
        ): Interpreter {
            return Mother(content, name, api, factory)
        }

        @JvmStatic
        fun dummy(api: ArucasAPI = ArucasAPI.Builder().addDefault().build()): Interpreter {
            return of("", "dummy", api).also { it.loadApi() }
        }
    }

    abstract val content: String
    abstract val name: String
    abstract val threadHandler: ThreadHandler
    abstract val api: ArucasAPI
    abstract val properties: Properties
    abstract val globalTable: StackTable
    abstract val primitives: PrimitiveDefinitionMap
    abstract val modules: ModuleMap

    protected abstract val functions: FunctionMap
    protected abstract val localCache: LocalCache
    protected abstract var currentTable: StackTable

    protected val stackTrace = Stack<Trace>()

    private val returnThrowable by lazy { Propagator.Return(this.getNull()) }

    abstract fun loadApi()

    open fun isMain() = false

    open fun compile(): List<Statement> {
        this.loadApi()
        val compileStart = System.nanoTime()
        return Arucas.compile(this.content, this.name).also {
            val resolver = Resolver(it, this.primitives, this.functions.map { f -> f.getPrimitive(FunctionDef::class)!! })
            this.localCache.mergeWith(resolver.run())
            val compileTime = Util.nanosToString(System.nanoTime() - compileStart)
            this.logDebug("Compile time for '${this.name}' was $compileTime")
        }
    }

    fun interpret() {
        val statements = this.compile()
        val executionStart = System.nanoTime()
        try {
            statements.forEach(this::execute)
        } finally {
            val executionTime = Util.nanosToString(System.nanoTime() - executionStart)
            this.logDebug("Execution time for '${this.name}' was $executionTime")
        }
    }

    fun safe(block: () -> Unit) {
        this.safe(Unit, block)
    }

    fun <T> safe(default: T, block: () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            this.threadHandler.handleError(e)
            default
        }
    }

    fun <T> interuptable(block: () -> T): T {
        return try {
            block()
        } catch (e: InterruptedException) {
            throw Propagator.Stop.INSTANCE
        }
    }

    fun convertValue(any: Any?) = this.api.getConverter().convertFrom(any, this)

    fun <T: PrimitiveDefinition<*>> getPrimitive(clazz: Class<out T>): T {
        return this.primitives.get(clazz) ?: throw IllegalArgumentException("No such definition for '${clazz.simpleName}'")
    }

    fun <T: PrimitiveDefinition<*>> getPrimitive(klass: KClass<out T>) = this.getPrimitive(klass.java)

    fun <T: CreatableDefinition<V>, V> create(clazz: Class<out T>, value: V) = this.getPrimitive(clazz).create(value)

    fun <T: CreatableDefinition<V>, V> create(klass: KClass<out T>, value: V) = this.create(klass.java, value)

    fun createBool(boolean: Boolean): ClassInstance = this.getPrimitive(BooleanDef::class).from(boolean)

    fun getNull(): ClassInstance = this.getPrimitive(NullDef::class).NULL

    fun branch(): Interpreter = Branch(this)

    fun child(content: String, name: String): Interpreter = Child(content, name, false, this)

    fun logDebug(message: String) {
        if (this.properties.isDebug) {
            this.api.getOutput().logln(message)
        }
    }

    fun execute(table: StackTable, vararg statements: Statement) {
        this.jumpTable(table) { statements.forEach { this.execute(it) } }
    }

    fun evaluate(table: StackTable, expression: Expression): ClassInstance {
        return this.jumpTable(table) { this.evaluate(expression) }
    }

    fun call(instance: ClassInstance, args: List<ClassInstance>, trace: CallTrace = Trace.INTERNAL): ClassInstance {
        this.canRun()
        try {
            this.stackTrace.push(trace)
            @Suppress("DEPRECATION")
            return instance.call(this, args)
        } catch (runtime: RuntimeError) {
            runtime.fillStackTrace(this.stackTrace)
            throw runtime
        } catch (fatal: FatalError) {
            throw fatal
        } catch (propagator: Propagator) {
            throw propagator
        } catch (stackOverflow: StackOverflowError) {
            RuntimeError("Ran out of space on the stack", stackOverflow, trace).also {
                it.fillStackTrace(this.stackTrace)
                throw it
            }
        } catch (throwable: Throwable) {
            throw FatalError("An unexpected error was thrown", throwable, this.stackTrace)
        } finally {
            this.stackTrace.pop()
        }
    }

    private fun execute(statement: Statement) = statement.visit(this)

    private fun executeNext(vararg statements: Statement) {
        this.jumpNextTable { statements.forEach { this.execute(it) } }
    }

    private fun evaluate(expression: Expression) = expression.visit(this)

    @Suppress("SameParameterValue")
    private fun getVariable(name: String, trace: Trace, visitable: Visitable? = null): ClassInstance {
        return this.getVariableNullable(name, visitable) ?: runtimeError("No such variable '$name' exists", trace)
    }

    private fun getVariableNullable(name: String, visitable: Visitable? = null): ClassInstance? {
        visitable ?: return this.currentTable.getVar(name)
        val distance = this.localCache.getVar(visitable)
        distance?.let {
            this.currentTable.getVar(name, distance)?.let { return it }
            throw IllegalArgumentException("Failed to fetch variable at cached location")
        }
        return this.globalTable.getVar(name)
    }

    private fun getFunction(name: String, parameters: Int, trace: Trace, visitable: Visitable? = null): ClassInstance {
        if (visitable != null) {
            this.localCache.getFunction(visitable)?.let { distance ->
                this.currentTable.getFunction(name, parameters, distance)?.let { return it }
                throw IllegalArgumentException("Failed to fetch function at cached location")
            }
            this.localCache.getVar(visitable)?.let { distance ->
                this.currentTable.getVar(name, distance)?.let { return it }
                throw IllegalArgumentException("Failed to fetch function variable at cached location")
            }
            this.globalTable.getFunction(name, parameters)?.let { return it }
            this.globalTable.getVar(name)?.let { return it }
        } else {
            this.currentTable.getFunction(name, parameters)?.let { return it }
            this.currentTable.getVar(name)?.let { return it }
        }
        return this.functions.get(name, parameters) ?: this.noFunctionError(name, parameters, trace)
    }

    private fun getClass(name: String, trace: Trace, visitable: Visitable? = null): ClassDefinition {
        if (visitable != null) {
            this.localCache.getClass(visitable)?.let { distance ->
                this.currentTable.getClass(name, distance)?.let { return it }
                throw IllegalArgumentException("Failed to fetch class at cached location")
            }
            this.globalTable.getClass(name)?.let { return it }
        }
        this.currentTable.getClass(name)?.let { return it }
        runtimeError("No such class '$name' exists", trace)
    }

    private fun noFunctionError(name: String, parameters: Int, trace: Trace): Nothing {
        val error = if (parameters == 0) "" else " with $parameters parameter${if (parameters == 1) "" else "s"}"
        runtimeError("No such function '$name'$error exists", trace)
    }

    private fun <T> jumpNextTable(supplier: () -> T) = this.jumpTable(StackTable(this.currentTable), supplier)

    private fun <T> jumpTable(table: StackTable, supplier: () -> T): T {
        val previous = this.currentTable
        try {
            this.currentTable = table
            return supplier()
        } finally {
            this.currentTable = previous
        }
    }

    private fun canRun(): Boolean {
        val thread = Thread.currentThread()
        if (thread.isInterrupted) {
            throw Propagator.Stop.INSTANCE
        } else if (thread is ArucasThread && thread.isFrozen) {
            thread.freeze()
        }
        return true
    }

    private fun isInstanceType(instance: ClassInstance, typeNames: Array<String>?, trace: Trace): Boolean {
        typeNames ?: return true
        for (name in typeNames) {
            val definition = this.currentTable.getClass(name)
            definition ?: runtimeError("No such class with name '$name'", trace)
            if (instance.isOf(definition)) {
                return true
            }
        }
        return false
    }

    protected open fun defineClass(table: StackTable, definition: ClassDefinition) {
        table.defineClass(definition)
    }

    private fun tryImport(importPath: String, local: Boolean): Boolean {
        if (!this.modules.tried(importPath)) {
            val content = this.api.getLibraryManager().getImport(importPath.split("."), local, this)
            content ?: return false
            val child: Interpreter = Child(content, importPath, true, this)
            child.interpret()
            this.localCache.mergeWith(child.localCache)
            val definitions = child.globalTable.getClasses()
            if (definitions.isNotEmpty()) {
                for (definition in definitions) {
                    this.modules.add(importPath, definition)
                }
                return true
            }
        }
        return false
    }

    private fun visitClassBody(definition: ArucasClassDefinition, body: ClassBodyStatement, needsSuper: Boolean, type: String) {
        definition.fields.value.putAll(body.fields)

        if (needsSuper && body.constructors.isEmpty()) {
            runtimeError("Derived class constructor must initialise super constructor", body.trace)
        }
        body.constructors.forEach { c ->
            if (needsSuper && c.init.type == ConstructorInit.InitType.NONE) {
                runtimeError("Derived class constructor must initialise super constructor", c.trace)
            }
            val parameters = c.parameters.map { p -> p.toTyped(this.currentTable, c.trace) }
            UserConstructorFunction.of(c.arbitrary, definition, c.init, parameters, c.body, this.currentTable, c.trace).let {
                definition.constructors.value.add(this.create(FunctionDef::class, it))
            }
        }

        body.methods.forEach { m ->
            val parameters = m.parameters.map { p -> p.toTyped(this.currentTable, m.trace) }
            val returnTypes = Parameter.namesToDefinitions(this.currentTable, m.returnTypes, m.trace)
            UserDefinedClassFunction.of(m.arbitrary, m.name, parameters, m.body, this.currentTable, m.trace, returnTypes).let {
                definition.methods.value.add(this.create(FunctionDef::class, it))
            }
        }

        body.operators.forEach { (m, t) ->
            val parameters = m.parameters.map { p -> p.toTyped(this.currentTable, m.trace) }
            val returnTypes = Parameter.namesToDefinitions(this.currentTable, m.returnTypes, m.trace)
            UserDefinedClassFunction.of(m.arbitrary, m.name, parameters, m.body, this.currentTable, m.trace, returnTypes).let {
                definition.operators.value.add(t, this.create(FunctionDef::class, it))
            }
        }

        body.staticMethods.forEach { m ->
            val parameters = m.parameters.map { p -> p.toTyped(this.currentTable, m.trace) }
            val returnTypes = Parameter.namesToDefinitions(this.currentTable, m.returnTypes, m.trace)
            UserDefinedFunction.of(m.arbitrary, m.name, parameters, m.body, this.currentTable, m.trace, returnTypes).let {
                definition.staticMethods.value.add(this.create(FunctionDef::class, it))
            }
        }

        body.staticFields.forEach { (p, e) ->
            val typedParameter = p.toTyped(this.currentTable, body.trace)
            val fieldName = "${definition.name}.${p.name}"
            HintedField(fieldName, typedParameter.types, true, this.evaluate(e)).let {
                definition.staticFields.value[p.name] = it
            }
        }

        body.staticInitializers.forEach { this.execute(it) }

        for (interfaceDefinition in definition.interfaces()) {
            if (!interfaceDefinition.hasRequiredMethods(definition)) {
                runtimeError("$type '${definition.name}' has not properly implemented '${interfaceDefinition.name}'", body.trace)
            }
        }
    }

    private fun visitSuper(superExpression: SuperExpression, trace: Trace): Pair<ClassInstance, ClassDefinition> {
        val instance = this.getVariable("this", trace, superExpression)
        val name = this.localCache.getSuper(superExpression) ?: throw IllegalStateException("Could not get super context name")
        return instance to instance.definition.superclassOf(name)
    }

    override fun visitVoid(void: VoidStatement) = Unit

    override fun visitStatements(statements: Statements) {
        statements.statements.forEach { this.execute(it) }
    }

    override fun visitScope(scope: ScopeStatement) {
        this.executeNext(scope.statements)
    }

    override fun visitLocalVar(local: LocalVarStatement) {
        this.currentTable.defineVar(local.name, this.evaluate(local.assignee))
    }

    override fun visitExpression(expression: ExpressionStatement) {
        this.evaluate(expression.expression)
    }

    override fun visitIf(ifStatement: IfStatement) {
        val condition = this.evaluate(ifStatement.condition).getPrimitive(BooleanDef::class)
        condition ?: runtimeError("Condition must result in a Boolean", ifStatement.trace)
        if (condition) {
            this.execute(ifStatement.body)
        } else {
            this.execute(ifStatement.otherwise)
        }
    }

    override fun visitSwitch(switch: SwitchStatement) {
        val condition = this.evaluate(switch.condition)
        try {
            switch.casesList.forEachIndexed { i, cases ->
                for (case in cases) {
                    if (condition.equals(this, this.evaluate(case), switch.trace)) {
                        this.execute(switch.caseStatements[i])
                        return
                    }
                }
            }
            switch.defaultStatement?.let { this.execute(it) }
        } catch (_: Propagator.Break) { }
    }

    override fun visitFunction(function: FunctionStatement) {
        val parameters = function.parameters.map { it.toTyped(this.currentTable, function.trace) }
        val returnTypes = Parameter.namesToDefinitions(this.currentTable, function.returnTypes, function.trace)
        UserDefinedFunction.of(function.arbitrary, function.name, parameters, function.body, this.currentTable, function.trace, returnTypes).let {
            if (!function.isClass) {
                this.currentTable.defineFunction(this.create(FunctionDef::class, it))
            }
        }
    }

    override fun visitReturn(returnStatement: ReturnStatement) {
        val returnValue = this.evaluate(returnStatement.expression)
        throw this.returnThrowable.also { it.returnValue = returnValue }
    }

    override fun visitWhile(whileStatement: WhileStatement) {
        while (this.canRun()) {
            val condition = this.evaluate(whileStatement.condition).getPrimitive(BooleanDef::class)
            condition ?: runtimeError("Condition must result in Boolean", whileStatement.trace)
            if (!condition) {
                break
            }

            try {
                this.execute(whileStatement.body)
            } catch (ignored: Propagator.Continue) {
                continue
            } catch (breakPropagator: Propagator.Break) {
                break
            }
        }
    }

    override fun visitFor(forStatement: ForStatement) {
        this.jumpNextTable {
            this.execute(forStatement.initial)
            while (this.canRun()) {
                val condition = this.evaluate(forStatement.condition).getPrimitive(BooleanDef::class)
                condition ?: runtimeError("Condition must result in Boolean", forStatement.trace)
                if (!condition) {
                    break
                }

                try {
                    this.execute(forStatement.body)
                } catch (breakPropagator: Propagator.Break) {
                    break
                } catch (_: Propagator.Continue) { }

                this.evaluate(forStatement.end)
            }
        }
    }

    override fun visitForeach(foreach: ForeachStatement) {
        val iterable = this.evaluate(foreach.iterable)
        if (!iterable.isOf(IterableDef::class)) {
            runtimeError("'foreach' loop must iterate over an Iterable value", foreach.trace)
        }

        val iterator = iterable.callMember(this, "iterator", listOf(), IteratorDef::class, foreach.trace)

        val hasNext = { iterator.callMemberPrimitive(this, "hasNext", listOf(), BooleanDef::class, foreach.trace) }
        val getNext = { iterator.callMember(this, "next", listOf(), ObjectDef::class, foreach.trace) }
        while (hasNext()) {
            this.canRun()
            val shouldBreak = this.jumpNextTable {
                this.currentTable.defineVar(foreach.name, getNext())
                try {
                    this.execute(foreach.body)
                } catch (breakPropagator: Propagator.Break) {
                    return@jumpNextTable true
                } catch (_: Propagator.Continue) { }
                false
            }

            if (shouldBreak) {
                break
            }
        }
    }

    override fun visitContinue(continueStatement: ContinueStatement) {
        throw Propagator.Continue.INSTANCE
    }

    override fun visitBreak(breakStatement: BreakStatement) {
        throw Propagator.Break.INSTANCE
    }

    override fun visitTry(tryStatement: TryStatement) {
        try {
            this.execute(tryStatement.body)
        } catch (error: RuntimeError) {
            tryStatement.catchParameter ?: throw error
            this.jumpNextTable {
                val instance = error.getInstance(this)
                if (!this.isInstanceType(instance, tryStatement.catchParameter.typeNames, tryStatement.trace)) {
                    throw instance.getPrimitive(ErrorDef::class)!!
                }
                this.logDebug("Error '$error' was caught")
                this.currentTable.defineVar(tryStatement.catchParameter.name, instance)
                this.execute(tryStatement.catchBody)
            }
        } finally {
            this.execute(tryStatement.finally)
        }
    }

    override fun visitThrow(throwStatement: ThrowStatement) {
        val error = this.evaluate(throwStatement.throwable).getPrimitive(ErrorDef::class)
        error ?: runtimeError("Tried to throw a non Error value", throwStatement.trace)
        error.topTrace = throwStatement.trace
        throw error
    }

    override fun visitConstructor(constructor: ConstructorStatement) {
        throw IllegalStateException("Interpreter visited constructor")
    }

    override fun visitClassBody(classBody: ClassBodyStatement) {
        throw IllegalStateException("Interpreter visited class body")
    }

    override fun visitClass(classStatement: ClassStatement) {
        val interfaces = HashSet<InterfaceDefinition>()
        var superclass: ClassDefinition? = null
        var needsSuper = false
        for (parent in classStatement.parents) {
            val parentClass = this.getClass(parent, classStatement.trace, classStatement)
            if (parentClass is InterfaceDefinition) {
                interfaces.add(parentClass)
                continue
            }
            if (superclass == null) {
                if (!parentClass.canExtend() || !parentClass.constructors.isInitialized()) {
                    runtimeError("Cannot extend class '${parentClass.name}'", classStatement.trace)
                }
                val constructors = parentClass.constructors.value
                needsSuper = !constructors.isEmpty() && !constructors.has("", 0)
                superclass = parentClass
                continue
            }
            runtimeError("Classes can only extend one non-interface super class", classStatement.trace)
        }

        this.jumpNextTable {
            val definition = ArucasClassDefinition(classStatement.name, this, this.currentTable, superclass, interfaces)
            this.defineClass(this.currentTable.parent!!, definition)

            this.visitClassBody(definition, classStatement.body, needsSuper, "class")
        }
    }

    override fun visitEnum(enumStatement: EnumStatement) {
        val interfaces = HashSet<InterfaceDefinition>()
        for (parent in enumStatement.parents) {
            val parentClass = this.getClass(parent, enumStatement.trace, enumStatement)
            if (parentClass !is InterfaceDefinition) {
                runtimeError("Enums can only implement interfaces, cannot extend class '${parentClass.name}'", enumStatement.trace)
            }
            interfaces.add(parentClass)
        }

        this.jumpNextTable {
            val definition = EnumDefinition(enumStatement.name, this, this.currentTable, interfaces)
            this.defineClass(this.currentTable.parent!!, definition)

            if (enumStatement.body is ClassBodyStatement) {
                this.visitClassBody(definition, enumStatement.body, false, "enum")
            }

            enumStatement.enums.forEach { name, (args, trace) ->
                val elements = ArrayList<ClassInstance>()
                for (expression in args) {
                    elements.add(this.evaluate(expression))
                }
                definition.addEnum(this, name, elements, trace)
            }
        }
    }

    override fun visitInterface(interfaceStatement: InterfaceStatement) {
        val definition = InterfaceDefinition(interfaceStatement.name, this, interfaceStatement.requiredMethods)
        this.defineClass(this.currentTable, definition)
    }

    override fun visitImport(importStatement: ImportStatement) {
        val importPath = importStatement.path
        val local = importStatement.local
        if (!this.modules.has(importPath)) {
            if (!this.tryImport(importPath, local)) {
                val details = "Module '$importPath' couldn't be found" + if (local) " locally" else ""
                runtimeError(details, importStatement.trace)
            }
        }

        if (importStatement.names.isNotEmpty()) {
            for (name in importStatement.names) {
                var definition = this.modules.get(importPath, name)

                if (definition == null) {
                    this.tryImport(importPath, local)
                    definition = this.modules.get(importPath, name)
                        ?: runtimeError("Class '$name' couldn't be found in module '$importPath'", importStatement.trace)
                }
                this.currentTable.addModule(definition)
            }
        } else {
            // We need to try to import everything
            this.tryImport(importPath, local)
            this.modules.forEach(importPath) {
                this.currentTable.addModule(it)
            }
        }
    }

    override fun visitExact(exact: ExactExpression): ClassInstance {
        return exact.instance
    }

    override fun <L: PrimitiveDefinition<*>> visitLiteral(literal: LiteralExpression<L>): ClassInstance {
        return literal.cache ?: literal.supplier(this.getPrimitive(literal.klass)).also { literal.cache = it }
    }

    override fun visitBracket(bracket: BracketExpression): ClassInstance {
        return this.evaluate(bracket.expression)
    }

    override fun visitList(list: ListExpression): ClassInstance {
        val elements = ArucasList()
        for (expression in list.expressions) {
            elements.add(this.evaluate(expression))
        }
        return this.create(ListDef::class, elements)
    }

    override fun visitMap(map: MapExpression): ClassInstance {
        val elements = ArucasOrderedMap()
        map.expressions.forEach { (k, v) ->
            elements.put(this, this.evaluate(k), this.evaluate(v))
        }
        return this.create(MapDef::class, elements)
    }

    override fun visitFunction(function: FunctionExpression): ClassInstance {
        val arguments = function.parameters.map { it.toTyped(this.currentTable, function.trace) }
        val returnTypes = Parameter.namesToDefinitions(this.currentTable, function.returnTypes, function.trace)
        UserDefinedFunction.of(function.arbitrary, function.name, arguments, function.body, this.currentTable, function.trace, returnTypes).let {
            return this.create(FunctionDef::class, it)
        }
    }

    override fun visitUnary(unary: UnaryExpression): ClassInstance {
        if (unary.expression is SuperExpression) {
            this.visitSuper(unary.expression, unary.expression.trace).let { (i, d) ->
                return d.unary(i, this, unary.type, unary.trace)
            }
        }
        return this.evaluate(unary.expression).unary(this, unary.type, unary.trace)
    }

    override fun visitBinary(binary: BinaryExpression): ClassInstance {
        if (binary.left is SuperExpression) {
            this.visitSuper(binary.left, binary.left.trace).let { (i, d) ->
                return d.binary(i, this, binary.type, { this.evaluate(binary.right) }, binary.trace)
            }
        }
        return this.evaluate(binary.left).binary(this, binary.type, { this.evaluate(binary.right) }, binary.trace)
    }

    override fun visitUnpackAssign(assign: UnpackAssignExpression): ClassInstance {
        val list = this.evaluate(assign.assignee)
        if (!list.isOf(ListDef::class)) {
            runtimeError("Expression in unpacking assignment must result in an List value", assign.trace)
        }

        val size = list.callMemberPrimitive(this, "size", ArrayList(), NumberDef::class, assign.trace).toInt()
        if (size != assign.assignables.size) {
            runtimeError("Unpacking assign size does not match list size", assign.trace)
        }

        val callTrace = CallTrace(assign.trace, "Unpacking")
        assign.assignables.forEachIndexed { i, assignable ->
            val value = list.bracketAccess(this.create(NumberDef::class, i.toDouble()), this, callTrace)
            this.evaluate(assignable.copyWith(value))
        }
        return list
    }

    override fun visitAccess(access: AccessExpression): ClassInstance {
        val variable = this.getVariableNullable(access.name, access)
        variable?.let { return it }
        if (this.functions.has(access.name) || this.currentTable.hasFunction(access.name)) {
            val child = this.branch()
            val delegate = BuiltInFunction.arb("\$delegate.${access.name}", {
                val callTrace = CallTrace(access.trace, "${access.name}::${it.size()}")
                // We need to create a branch of the branch, this is because
                // this delegate may be used again in the future, or maybe
                // even by another thread, we cannot guarantee thread safety
                val inner = child.branch()
                inner.call(inner.getFunction(access.name, it.size(), access.trace), it.arguments, callTrace)
            })
            return this.create(FunctionDef::class, delegate)
        }
        runtimeError("No such variable '${access.name}' exists", access.trace)
    }

    override fun visitFunctionAccess(access: FunctionAccessExpression): ClassInstance {
        return this.getFunction(access.name, access.parameters, access.trace, access)
    }

    override fun visitAssign(assign: AssignExpression): ClassInstance {
        val instance = this.evaluate(assign.assignee)
        this.localCache.getVar(assign)?.let {
            this.currentTable.defineVar(assign.name, instance, it)
            return instance
        }
        // Normally you could assume that we always
        // define the variable here, but there's an edge
        // case, that is if the variable is in a function
        // the variable may not have been defined yet
        if (!this.currentTable.assignVar(assign.name, instance)) {
            this.currentTable.defineVar(assign.name, instance)
        }
        return instance
    }

    override fun visitCall(call: CallExpression): ClassInstance {
        val arguments = ArrayList<ClassInstance>()
        for (expression in call.arguments) {
            arguments.add(this.evaluate(expression))
        }
        val function = this.evaluate(call.expression)
        return this.call(function, arguments, CallTrace(call.trace, function.toString(this, call.trace)))
    }

    override fun visitThis(thisExpression: ThisExpression): ClassInstance {
        return this.getVariable("this", thisExpression.trace, thisExpression)
    }

    override fun visitSuper(superExpression: SuperExpression): ClassInstance {
        runtimeError("Cannot directly access 'super'", superExpression.trace)
    }

    override fun visitMemberAccess(access: MemberAccessExpression): ClassInstance {
        val instance = when (val expression = access.expression) {
            is AccessExpression -> {
                val instance = this.getVariableNullable(expression.name, expression)
                instance ?: this.getClass(expression.name, expression.trace, expression).let {
                    return it.staticMemberAccess(this, access.name, access.trace)
                }
            }
            is SuperExpression -> {
                this.visitSuper(expression, expression.trace).let { (i, d) ->
                    return d.memberAccess(i, this, access.name, access.trace)
                }
            }
            else -> this.evaluate(access.expression)
        }
        return instance.memberAccess(this, access.name, access.trace)
    }

    override fun visitMemberAssign(assign: MemberAssignExpression): ClassInstance {
        val assignee = this.evaluate(assign.assignee)
        val instance = if (assign.expression is AccessExpression) {
            val instance = this.getVariableNullable(assign.expression.name, assign.expression)
            instance ?: this.getClass(assign.expression.name, assign.expression.trace, assign).let {
                it.staticMemberAssign(this, assign.name, assignee, assign.trace)
                return assignee
            }
        } else {
            this.evaluate(assign.expression)
        }
        return instance.memberAssign(assign.name, assignee, assign.trace)
    }

    override fun visitMemberCall(call: MemberCallExpression): ClassInstance {
        val arguments = ArrayList<ClassInstance>()
        for (expression in call.arguments) {
            arguments.add(this.evaluate(expression))
        }
        val instance = when (val expression = call.expression) {
            is AccessExpression -> {
                val instance = this.getVariableNullable(expression.name, expression)
                instance ?: this.getClass(expression.name, expression.trace, expression).let {
                    return it.staticFunctionCall(this, call.name, arguments, call.trace)
                }
            }
            is SuperExpression -> {
                this.visitSuper(expression, expression.trace).let { (i, d) ->
                    val function = d.memberFunctionAccess(i, call.name , arguments, call.trace, d)
                    val callTrace = CallTrace(call.trace, "<${d.name}>.super.${call.name}::${arguments.size - 1}")
                    return this.call(function, arguments, callTrace)
                }
            }
            else -> this.evaluate(call.expression)
        }
        return instance.callMember(this, call.name, arguments, call.trace)
    }

    override fun visitNewAccess(access: NewAccessExpression): ClassInstance {
        val classDefinition = this.getClass(access.name, access.trace, access)
        return classDefinition.accessConstructor(access.trace)
    }

    override fun visitNewCall(call: NewCallExpression): ClassInstance {
        this.canRun()
        val classDefinition = this.getClass(call.name, call.trace, call)
        val arguments = ArrayList<ClassInstance>()
        for (expression in call.arguments) {
            arguments.add(this.evaluate(expression))
        }
        return classDefinition.callConstructor(this, arguments, call.trace)
    }

    override fun visitBracketAccess(access: BracketAccessExpression): ClassInstance {
        val index = this.evaluate(access.index)
        if (access.expression is SuperExpression) {
            this.visitSuper(access.expression, access.trace).let { (i, d) ->
                return d.bracketAccess(i, this, index, access.trace)
            }
        }
        return this.evaluate(access.expression).bracketAccess(index, this, access.trace)
    }

    override fun visitBracketAssign(assign: BracketAssignExpression): ClassInstance {
        val index = this.evaluate(assign.index)
        val assignee = this.evaluate(assign.assignee)
        if (assign.expression is SuperExpression) {
            this.visitSuper(assign.expression, assign.trace).let { (i, d) ->
                return d.bracketAssign(i, this, index, assignee, assign.trace)
            }
        }
        return this.evaluate(assign.expression).bracketAssign(index, this, assignee, assign.trace)
    }

    private class Mother(
        override val content: String,
        override val name: String,
        override val api: ArucasAPI,
        threadHandlerFactory: (Interpreter) -> ThreadHandler
    ): Interpreter() {
        override val threadHandler = threadHandlerFactory(this)
        override val properties = this.api.getProperties()()
        override val modules = ModuleMap()
        override val functions = FunctionMap()
        override val primitives = PrimitiveDefinitionMap()
        override val globalTable = StackTable()
        override val localCache = LocalCache()
        override var currentTable = this.globalTable

        override fun loadApi() {
            this.api.getBuiltInDefinitions()?.forEach { p ->
                val primitive = p(this)
                this.primitives.add(primitive)
                this.modules.addBuiltIn(primitive)
                this.globalTable.addModule(primitive)
            }
            this.primitives.forEach { it.merge() }
            this.api.getBuiltInExtensions()?.forEach { e ->
                e.getBuiltInFunctions().forEach {
                    this.functions.add(this.create(FunctionDef::class, it))
                }
            }
            this.api.getClassDefinitions()?.forEach { (importPath, definitions) ->
                for (factory in definitions) {
                    val primitive = factory(this)
                    this.primitives.add(primitive)
                    this.modules.add(importPath, primitive)
                    primitive.merge()
                }
            }
        }

        override fun isMain() = true
    }

    private class Child(
        override val content: String,
        override val name: String,
        val isImporting: Boolean,
        parent: Interpreter
    ): Interpreter() {
        override val threadHandler = parent.threadHandler
        override val api = parent.api
        override val properties = parent.properties
        override val globalTable = StackTable()
        override var currentTable = this.globalTable
        override val modules = parent.modules
        override val functions = parent.functions
        override val primitives = parent.primitives
        override val localCache = parent.localCache

        override fun loadApi() {
            // Most of API already loaded by parent
            this.modules.forEachBuiltIn {
                this.globalTable.addModule(it)
            }
        }

        override fun defineClass(table: StackTable, definition: ClassDefinition) {
            super.defineClass(table, definition)
            if (this.isImporting) {
                this.modules.add(this.name, definition)
            }
        }
    }

    private class Branch(interpreter: Interpreter): Interpreter() {
        override val content = interpreter.content
        override val name = interpreter.name
        override val threadHandler = interpreter.threadHandler
        override val api = interpreter.api
        override val properties = interpreter.properties
        override val modules = interpreter.modules
        override val functions = interpreter.functions
        override val primitives = interpreter.primitives
        override val globalTable = interpreter.globalTable
        override var currentTable = interpreter.currentTable
        override var localCache = interpreter.localCache

        init {
            for (trace in interpreter.stackTrace) {
                this.stackTrace.push(trace)
            }
        }

        override fun loadApi() {
            throw IllegalStateException("Branch cannot reload API")
        }
    }
}