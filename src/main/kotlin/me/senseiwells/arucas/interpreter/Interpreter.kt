package me.senseiwells.arucas.interpreter

import me.senseiwells.arucas.api.ArucasAPI
import me.senseiwells.arucas.api.ArucasErrorHandler
import me.senseiwells.arucas.api.docs.visitor.ArucasDocParser
import me.senseiwells.arucas.builtin.*
import me.senseiwells.arucas.classes.*
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.*
import me.senseiwells.arucas.compiler.lexer.Lexer
import me.senseiwells.arucas.exceptions.*
import me.senseiwells.arucas.functions.builtin.BuiltInFunction
import me.senseiwells.arucas.functions.user.DelegatedConstructor
import me.senseiwells.arucas.functions.user.UserConstructorFunction
import me.senseiwells.arucas.functions.user.UserDefinedClassFunction
import me.senseiwells.arucas.functions.user.UserDefinedFunction
import me.senseiwells.arucas.interpreter.Interpreter.Companion.dummy
import me.senseiwells.arucas.interpreter.Interpreter.Companion.of
import me.senseiwells.arucas.nodes.*
import me.senseiwells.arucas.nodes.expressions.*
import me.senseiwells.arucas.nodes.statements.*
import me.senseiwells.arucas.typed.LazyDefinitions
import me.senseiwells.arucas.utils.*
import me.senseiwells.arucas.utils.collections.FunctionMap
import me.senseiwells.arucas.utils.collections.ModuleMap
import me.senseiwells.arucas.utils.collections.PrimitiveDefinitionMap
import me.senseiwells.arucas.utils.impl.ArucasList
import me.senseiwells.arucas.utils.impl.ArucasOrderedMap
import me.senseiwells.arucas.utils.impl.ArucasThread
import me.senseiwells.arucas.utils.misc.ErrorSafe
import java.util.*
import java.util.concurrent.Future
import kotlin.reflect.KClass

/**
 * The interpreter class. This is responsible for running
 * the Arucas code.
 *
 * This class visits all statement and expressions that have been parsed
 * interpreting it. It holds much of the state while executing and is
 * **NOT** thread safe. You must [branch] the interpreter for use
 * either at a later time or on a different thread.
 *
 * The interpreter can be created with [of], this will be the root
 * interpreter. You are able to configure the [ArucasAPI] that the
 * interpreter uses.
 *
 * Here is an example:
 * ```kotlin
 * val code = "print('Hello from Arucas!');"
 * val name = "MyInterpreter"
 * val builder = ArucasAPI.Builder()
 *     .addDefault()
 * val api = builder.build()
 * val interpreter = Interpreter.of(code, name, api)
 * // Blocks the current thread
 * interpreter.executeBlocking()
 * // Alternatively you can do
 * interpreter.executeAsync()
 * ```
 *
 * You are also able to create [dummy] interpreters. These are intended
 * for data generation, e.g. generating documentation based on the
 * documentation annotations.
 *
 * @see ArucasAPI
 * @see ArucasDocParser
 */
@Suppress("UNUSED")
sealed class Interpreter: StatementVisitor<Unit>, ExpressionVisitor<ClassInstance>, ErrorSafe {
    companion object {
        /**
         * Creates a new root interpreter.
         *
         * @param content the content to interpret.
         * @param name the name of the interpreter.
         * @param api the api used by the interpreter.
         * @return the new interpreter.
         */
        @JvmStatic
        fun of(content: String, name: String, api: ArucasAPI = ArucasAPI.Builder().addDefault().build()): Interpreter {
            return Mother(content, name, api)
        }

        /**
         * Creates a dummy interpreter for data generation.
         *
         * @param api the api used by the interpreter.
         * @return the dummy interpreter.
         *
         * @see ArucasDocParser
         */
        @JvmStatic
        fun dummy(api: ArucasAPI = ArucasAPI.Builder().addDefault().build()): Interpreter {
            return of("", "dummy", api).also { it.loadApi() }
        }
    }

    /**
     * Whether the current interpreter is the root.
     */
    open val isMain = false

    /**
     * The content that the interpreter is executing.
     */
    abstract val content: String

    /**
     * The name of the interpreter.
     */
    abstract val name: String

    /**
     * The API that is being used by the interpreter.
     *
     * @see ArucasAPI
     */
    abstract val api: ArucasAPI

    /**
     * The interpreter's properties.
     *
     * @see Properties
     */
    abstract val properties: Properties

    /**
     * The table containing the variables, functions, and classes in the global scope.
     *
     * @see StackTable
     */
    abstract val globalTable: StackTable

    /**
     * A collection of all the primitive definitions.
     *
     * @see PrimitiveDefinitionMap
     */
    abstract val primitives: PrimitiveDefinitionMap

    /**
     * A collection of all the available modules.
     *
     * @see ModuleMap
     */
    abstract val modules: ModuleMap

    /**
     * The interpreter's thread handler.
     *
     * @see ThreadHandler
     */
    protected abstract val threadHandler: ThreadHandler

    /**
     * All the built-in functions.
     *
     * @see FunctionMap
     */
    protected abstract val functions: FunctionMap

    /**
     * The local cache containing locations for different [Visitable]s.
     *
     * @see LocalCache
     */
    protected abstract val localCache: LocalCache

    /**
     * The current stack table (the current scope).
     *
     * @see StackTable
     */
    protected abstract var currentTable: StackTable

    /**
     * The stacktrace of the interpreter.
     *
     * @see call
     */
    protected val stackTrace = Stack<Trace>()

    /**
     * The [Propagator.Return] instance, we only have one instance since
     * we are single-threaded we can deal with mutations and save on objects.
     *
     * @see Propagator.Return
     */
    private val returnThrowable by lazy { Propagator.Return(this.getNull()) }

    /**
     * This executes the interpreter asynchronously. This
     * provides a [Future] for any [ClassInstance] that it returns.
     *
     * Any exceptions thrown by will be handled by this method.
     *
     * @return the class instance that the script returns wrapped in a future.
     * @see Interpreter
     */
    fun executeAsync(): Future<ClassInstance?> {
        return this.threadHandler.async(this, this.api.getMainExecutor(), this::execute)
    }

    /**
     * This executes the interpreter on the current thread
     * blocking its execution till the interpreter has finished.
     *
     * Any exceptions will also not be handled by this method.
     *
     * @return the class instance that the script returns.
     * @see Interpreter
     */
    fun executeBlocking(): ClassInstance {
        return this.threadHandler.blocking(this, this.api.getMainExecutor(), this::execute)
    }

    /**
     * This compiles the [content] into an abstract syntax tree of [Statement] and [Expression].
     * These can then be visited by [StatementVisitor] and [ExpressionVisitor], usually an [Interpreter].
     *
     * @return the list of [Statement]s that form the AST.
     */
    internal fun compile(): List<Statement> {
        this.loadApi()
        val compileStart = System.nanoTime()
        return Parser(Lexer(this.content, this.name).createTokens()).parse().also {
            val resolver = Resolver(it, this.primitives, this.functions.map { f -> f.getPrimitive(FunctionDef::class)!! })
            this.localCache.mergeWith(resolver.run())
            val compileTime = TimeUtils.nanosToString(System.nanoTime() - compileStart)
            this.logDebug("Compile time for '${this.name}' was $compileTime")
        }
    }

    /**
     * This compiles the [content] then visits it with this [Interpreter].
     *
     * @see compile
     */
    internal fun interpret() {
        val statements = this.compile()
        val executionStart = System.nanoTime()
        try {
            statements.forEach(this::execute)
        } finally {
            val executionTime = TimeUtils.nanosToString(System.nanoTime() - executionStart)
            this.logDebug("Execution time for '${this.name}' was $executionTime")
        }
    }

    /**
     * This checks whether the interpreter should be running.
     *
     * @return whether the interpreter should be running.
     */
    fun isRunning(): Boolean {
        return this.threadHandler.running
    }

    /**
     * This adds a callback for when the root interpreter stops executing.
     *
     * This can happen as a result of a propagated error or closing naturally
     * or [forcibly stopped](stop).
     *
     * @param runnable the callback.
     */
    fun addStopEvent(runnable: Runnable) {
        this.threadHandler.addShutdownEvent(runnable)
    }

    /**
     * This stops the interpreter from executing further.
     */
    fun stop() {
        this.threadHandler.stop()
    }

    /**
     * This runs a function asynchronously to the main interpreter thread.
     *
     * @param function the code to run async.
     * @return a [Future] of a possible [ClassInstance], null if an error is thrown within the [function].
     */
    fun runAsync(function: () -> ClassInstance): Future<ClassInstance?> {
        return this.threadHandler.async(this, function)
    }

    /**
     * This runs a callable [ClassInstance] on a new [ArucasThread].
     *
     * @param callable the callable [ClassInstance].
     * @param name the name of the thread.
     * @return the [ArucasThread] that the [callable] is running on.
     */
    fun runFunctionOnThread(callable: ClassInstance, name: String = "Arucas Async Thread"): ArucasThread {
        return this.threadHandler.runFunctionOnThread(callable, this, name)
    }

    /**
     * Converts [Any] into a [ClassInstance].
     *
     * @param any the value to convert.
     * @return the [ClassInstance].
     * @see ValueConverter
     */
    fun convertValue(any: Any?): ClassInstance {
        return this.api.getConverter().convertFrom(any, this)
    }

    /**
     * Gets the primitive definition from its [Class].
     *
     * This will throw an exception if the definition is not found.
     *
     * @param T the exact type of [PrimitiveDefinition].
     * @param clazz the class of the [PrimitiveDefinition].
     * @return the [PrimitiveDefinition].
     */
    fun <T: PrimitiveDefinition<*>> getPrimitive(clazz: Class<out T>): T {
        return this.primitives.get(clazz) ?: throw IllegalArgumentException("No such definition for '${clazz.simpleName}'")
    }

    /**
     * Gets the primitive definition from its [KClass].
     *
     * This will throw an exception if the definition is not found.
     *
     * @param T the exact type of [PrimitiveDefinition].
     * @param klass the class of the [PrimitiveDefinition].
     * @return the [PrimitiveDefinition].
     * @see getPrimitive
     */
    fun <T: PrimitiveDefinition<*>> getPrimitive(klass: KClass<out T>): T {
        return this.getPrimitive(klass.java)
    }

    /**
     * This creates a [ClassInstance] of a [CreatableDefinition] with a given primitive [value].
     *
     * @param T the type of [CreatableDefinition].
     * @param V the primitive type of [T].
     * @param clazz the class of the [CreatableDefinition].
     * @param value the primitive value.
     * @return the created [ClassInstance]
     */
    fun <T: CreatableDefinition<V>, V> create(clazz: Class<out T>, value: V): ClassInstance {
        return this.getPrimitive(clazz).create(value)
    }

    /**
     * This creates a [ClassInstance] of a [CreatableDefinition] with a given primitive [value].
     *
     * @param T the type of [CreatableDefinition].
     * @param V the primitive type of [T].
     * @param klass the class of the [CreatableDefinition].
     * @param value the primitive value.
     * @return the created [ClassInstance]
     * @see create
     */
    fun <T: CreatableDefinition<V>, V> create(klass: KClass<out T>, value: V): ClassInstance {
        return this.create(klass.java, value)
    }

    /**
     * This 'creates' a boolean [ClassInstance].
     *
     * In reality this just gets one of the existing constant
     * [ClassInstance] so they will have the same identity.
     *
     * @param boolean the boolean value.
     * @return the boolean [ClassInstance].
     */
    fun createBool(boolean: Boolean): ClassInstance {
        return this.getPrimitive(BooleanDef::class).from(boolean)
    }

    /**
     * Gets the null [ClassInstance].
     *
     * @return the null [ClassInstance].
     */
    fun getNull(): ClassInstance {
        return this.getPrimitive(NullDef::class).NULL
    }

    /**
     * Checks whether the interpreter
     */
    fun isWithinStack(stack: StackTable): Boolean {
        return this.currentTable.hasAncestor(stack)
    }

    /**
     * Creates a [Branch]. This is needed for when going off thread
     * or when saving the interpreter's state for later use.
     *
     * Creating a branch saves the current stack table, other than
     * that branches inherit all other properties from their parent.
     *
     * Here is an example of how to use [branch]:
     * ```kotlin
     * val interpreter = /* Get interpreter */
     * // We must branch here to save the current stack state.
     * val branch = interpreter.branch()
     * Thread {
     *     for (i in 0..100) {
     *        // This runAsync call can be on any interpreter
     *        interpreter.runAsync {
     *            // We must create a branch of the branch to ensure thread safety
     *            val inner = branch.branch()
     *            // Do stuff with new inner branch
     *        }
     *    }
     * }.start()
     * ```
     */
    fun branch(): Interpreter {
        return Branch(this)
    }

    /**
     * Creates a [Child]. This is needed for running sub-scripts within
     * a script. This is needed because creating another root interpreter will
     * result in different instances of class definitions.
     *
     * A child inherits all the parent's definitions, functions, etc. however
     * gains its own global table to run its own script. To execute a child
     * interpreter it is the same a root interpreter: [executeBlocking] or [executeAsync].
     *
     * Children are used for importing, they are able to define classes and
     * add them to the [modules] field.
     *
     * @param content the content of the child script.
     * @param name the name of the child.
     * @return the child interpreter.
     */
    fun child(content: String, name: String): Interpreter {
        return Child(content, name, false, this)
    }

    /**
     * Tries to log a message to the debug output. It will
     * only log the message if the [Properties.isDebug] field
     * is set to true.
     *
     * @param message the message to log.
     */
    fun logDebug(message: String) {
        if (this.properties.isDebug) {
            this.api.getOutput().logln(message)
        }
    }

    /**
     * Checks if the interpreter is in experimental mode. If
     * it is not then an error will be thrown.
     *
     * @param message a lazy message.
     */
    fun throwIfNotExperimental(message: () -> String = { "Interpreter is not in experimental mode" }) {
        if (!this.properties.isExperimental) {
            runtimeError(message())
        }
    }

    /**
     * Runs a function, and catches any [Exception]s.
     *
     * This mirrors the functionality of [ErrorSafe] however
     * provides `inline` functionality for performance.
     *
     * @see ErrorSafe.runSafe
     */
    inline fun <T> runSafe(block: () -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            this.handleError(e)
            null
        }
    }

    /**
     * Runs a function, and catches any [Exception]s.
     *
     * This mirrors the functionality of [ErrorSafe] however
     * provides `inline` functionality for performance.
     *
     * @see ErrorSafe.runSafe
     */
    inline fun <T> runSafe(default: T, block: () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            this.handleError(e)
            default
        }
    }

    /**
     * Runs a function, and catches any [InterruptedException]s.
     *
     * This mirrors the functionality of [ErrorSafe] however
     * provides `inline` functionality for performance.
     *
     * @see ErrorSafe.canInterrupt
     */
    inline fun <T> canInterrupt(block: () -> T): T {
        return try {
            block()
        } catch (e: InterruptedException) {
            throw Propagator.Stop.INSTANCE
        }
    }

    /**
     * This handles any uncaught exceptions by passing them to the
     * [ThreadHandler]. This will deal with the error appropriately
     * calling the [ArucasErrorHandler] methods and stopping the script.
     *
     * @param throwable the uncaught [Throwable].
     */
    override fun handleError(throwable: Throwable) {
        this.threadHandler.handleError(throwable, this)
    }

    /**
     * This executes the interpreter. This is used by [executeBlocking] and [executeAsync]
     *
     * @return the class instance that the script returns.
     */
    protected abstract fun execute(): ClassInstance

    /**
     * This loads any API elements, such as the built-in classes and extensions.
     *
     * @see [ArucasAPI]
     */
    protected abstract fun loadApi()

    /**
     * Called when a class is defined.
     *
     * @param table the stack that the class is being defined on/
     */
    protected open fun defineClass(table: StackTable, definition: ClassDefinition) {
        table.defineClass(definition)
    }

    /**
     * Executes a statement on a given [StackTable].
     *
     * @param table the table to jump to.
     * @param statement the statement to execute.
     */
    internal fun execute(table: StackTable, statement: Statement) {
        this.jumpTable(table) { this.execute(statement) }
    }

    /**
     * Evaluates an expression on a given [StackTable].
     *
     * @param table the table to jump to.
     * @param expression the expression to evaluate.
     * @return the result of the [expression].
     */
    /* internal */ fun evaluate(table: StackTable, expression: Expression): ClassInstance {
        return this.jumpTable(table) { this.evaluate(expression) }
    }

    /**
     * This method calls a [ClassInstance] and pushes it to the [stackTrace] for errors.
     *
     * @param instance the [ClassInstance] to call.
     * @param args the arguments to provide for the call.
     * @param trace the [Trace] to push to the [stackTrace].
     * @return the [ClassInstance] returned by the instance call.
     */
    /* internal */ fun call(instance: ClassInstance, args: List<ClassInstance>, trace: CallTrace = Trace.INTERNAL): ClassInstance {
        this.poll()
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
            throw RuntimeError("Ran out of space on the stack", stackOverflow, trace).also {
                it.fillStackTrace(this.stackTrace)
            }
        } catch (throwable: Throwable) {
            throw FatalError("An unexpected error was thrown", throwable, this.stackTrace)
        } finally {
            this.stackTrace.pop()
        }
    }

    /**
     * Jumps to the next stack, the child of the current stack table.
     * It then executes [block], then pops the stack back to the original.
     *
     * @param T the return type of [block].
     * @param block the block to execute within the next stack.
     * @return the return value of [block].
     * @see StackTable
     * @see jumpTable
     */
    private inline fun <T> jumpNextTable(block: () -> T): T {
        return this.jumpTable(StackTable(this.modules, this.currentTable), block)
    }

    /**
     * Jumps to an arbitrary given stack then executes the [block],
     * then pops the stack back to the original.
     *
     * @param T the return type of [block].
     * @param table the [StackTable] to jump to.
     * @param block the block to execute within the stack.
     * @return the return value of [block].
     * @see StackTable
     * @see jumpNextTable
     */
    private inline fun <T> jumpTable(table: StackTable, block: () -> T): T {
        val previous = this.currentTable
        try {
            this.currentTable = table
            return block()
        } finally {
            this.currentTable = previous
        }
    }

    /**
     * Checks whether the interpreter is still able to run.
     *
     * @return whether the interpreter can still run.
     */
    private fun poll(): Boolean {
        this.api.getPoller().poll(this)

        if (!this.isRunning()) {
            throw Propagator.Stop.INSTANCE
        }

        val thread = Thread.currentThread()
        if (thread is ArucasThread && thread.isFrozen) {
            thread.freeze()
        }
        return true
    }

    private fun execute(statement: Statement) {
        statement.visit(this)
    }

    private fun executeNext(vararg statements: Statement) {
        this.jumpNextTable { statements.forEach { this.execute(it) } }
    }

    private fun evaluate(expression: Expression): ClassInstance {
        return expression.visit(this)
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

    private fun tryImport(importPath: String, local: Boolean) {
        if (!this.modules.tried(importPath)) {
            this.modules.addLazy(importPath) {
                val content = this.api.getLibraryManager().getImport(importPath.split("."), local, this)
                content ?: return@addLazy
                val child: Interpreter = Child(content, importPath, true, this)
                child.execute()
                this.localCache.mergeWith(child.localCache)
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun getVariable(name: String, trace: Trace, visitable: Visitable? = null): ClassInstance {
        return this.getVariableNullable(name, trace, visitable) ?: runtimeError("No such variable '$name' exists", trace)
    }

    private fun getVariableNullable(name: String, trace: Trace, visitable: Visitable? = null): ClassInstance? {
        visitable ?: return this.currentTable.getVar(name)
        val distance = this.localCache.getVar(visitable)
        distance?.let {
            this.currentTable.getVar(name, distance)?.let { variable -> return variable }

            // So theoretically it's possible that a variable defined in a function
            // was cached in that location but was since defined in the previous scopes
            // where it currently resides. This means that we need to check scopes above.
            // But this should be avoided as it can lead to unwanted behaviours.
            this.currentTable.getVar(name)?.let { variable ->
                this.logDebug("Local variable '$name' was defined previously in a scope but was accessed in scopes above")
                return variable
            }
            if (name == "this") {
                runtimeError("Tried to access 'this' or 'super' in a static context", trace)
            }
            throw IllegalArgumentException("Failed to fetch variable '${name}' at cached location (${it})")
        }
        return this.globalTable.getVar(name)
    }

    private fun getFunction(name: String, parameters: Int, trace: Trace, visitable: Visitable? = null): ClassInstance {
        if (visitable != null) {
            this.localCache.getFunction(visitable)?.let { distance ->
                this.currentTable.getFunction(name, parameters, distance)?.let { return it }
                throw IllegalArgumentException("Failed to fetch function '${name}::${parameters}' at cached location (${distance})")
            }
            this.localCache.getVar(visitable)?.let { distance ->
                this.currentTable.getVar(name, distance)?.let { return it }
                throw IllegalArgumentException("Failed to fetch function '${name}::${parameters}' at cached location (${distance})")
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
                throw IllegalArgumentException("Failed to fetch class '${name}' at cached location (${distance})")
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

    private fun visitClassBody(definition: ArucasClassDefinition, body: ClassBodyStatement, needsSuper: Boolean, type: String) {
        definition.fields.value.addAll(body.fields)

        if (needsSuper && body.constructors.isEmpty()) {
            runtimeError("Derived class constructor must initialise super constructor", body.start)
        }
        body.constructors.forEach { c ->
            if (needsSuper && c.delegate.type == DelegatedConstructor.Type.NONE) {
                runtimeError("Derived class constructor must initialise super constructor", c.start)
            }
            val parameters = c.parameters.map { it.create(this.currentTable, c.start) }
            UserConstructorFunction.of(c.arbitrary, definition, c.delegate, parameters, c.body, this.currentTable, c.start, c.private).let {
                definition.constructors.value.add(this.create(FunctionDef::class, it))
            }
        }

        body.methods.forEach { m ->
            if (m.private) {
                if (definition.canOverride(m.name, m.parameters.size)) {
                    runtimeError("Cannot override the public method '${m.name}' with private implementation in class '${definition.name}'", m.start)
                }
            }
            val parameters = m.parameters.map { it.create(this.currentTable, m.start) }
            val returns = LazyDefinitions.of(m.returnTypes, this.currentTable, m.start)
            UserDefinedClassFunction.of(m.arbitrary, m.name, parameters, m.body, this.currentTable, m.start, returns, m.private).let {
                definition.methods.value.add(this.create(FunctionDef::class, it))
            }
        }

        body.operators.forEach { (m, t) ->
            val parameters = m.parameters.map { it.create(this.currentTable, m.start) }
            val returns = LazyDefinitions.of(m.returnTypes, this.currentTable, m.start)
            UserDefinedClassFunction.of(m.arbitrary, m.name, parameters, m.body, this.currentTable, m.start, returns, m.private).let {
                definition.operators.value.add(t, this.create(FunctionDef::class, it))
            }
        }

        body.staticMethods.forEach { m ->
            val parameters = m.parameters.map { it.create(this.currentTable, m.start) }
            val returns = LazyDefinitions.of(m.returnTypes, this.currentTable, m.start)
            UserDefinedFunction.of(m.arbitrary, m.name, parameters, m.body, this.currentTable, m.start, returns, m.private).let {
                definition.staticMethods.value.add(this.create(FunctionDef::class, it))
            }
        }

        body.staticFields.forEach { v ->
            definition.staticFields.value[v.name] = v.create(this, this.currentTable, body.start)
        }

        body.staticInitializers.forEach { this.execute(it) }

        if (definition.staticFields.isInitialized()) {
            definition.staticFields.value.values.forEach { it.finalise(body.start) }
        }

        for (interfaceDefinition in definition.interfaces()) {
            if (!interfaceDefinition.hasRequiredMethods(definition)) {
                runtimeError("$type '${definition.name}' has not properly implemented '${interfaceDefinition.name}'", body.start)
            }
        }
    }

    private fun visitSuper(superExpression: SuperExpression, trace: Trace): Pair<ClassInstance, ClassDefinition> {
        val instance = this.getVariable("this", trace, superExpression)
        val name = this.localCache.getSuper(superExpression) ?: throw IllegalStateException("Could not get super context name")
        return instance to instance.definition.superclassOf(name)
    }

    // Visiting Implementations:

    override fun visitVoid(void: VoidStatement) { }

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
        condition ?: runtimeError("Condition must result in a Boolean", ifStatement.start)
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
                    if (condition.equals(this, this.evaluate(case), switch.start)) {
                        this.execute(switch.caseStatements[i])
                        return
                    }
                }
            }
            switch.defaultStatement?.let { this.execute(it) }
        } catch (_: Propagator.Break) { }
    }

    override fun visitFunction(function: FunctionStatement) {
        val parameters = function.parameters.map { it.create(this.currentTable, function.start) }
        val returns = LazyDefinitions.of(function.returnTypes, this.currentTable, function.start)
        UserDefinedFunction.of(function.arbitrary, function.name, parameters, function.body, this.currentTable, function.start, returns).let {
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
        while (this.poll()) {
            val condition = this.evaluate(whileStatement.condition).getPrimitive(BooleanDef::class)
            condition ?: runtimeError("Condition must result in Boolean", whileStatement.Start)
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
            while (this.poll()) {
                val condition = this.evaluate(forStatement.condition).getPrimitive(BooleanDef::class)
                condition ?: runtimeError("Condition must result in Boolean", forStatement.start)
                if (!condition) {
                    break
                }

                try {
                    this.execute(forStatement.body)
                } catch (breakPropagator: Propagator.Break) {
                    break
                } catch (_: Propagator.Continue) { }

                this.evaluate(forStatement.expression)
            }
        }
    }

    override fun visitForeach(foreach: ForeachStatement) {
        val result = this.evaluate(foreach.iterable)
        val iterator = if (result.isOf(IterableDef::class)) {
            result.callMember(this, "iterator", listOf(), IteratorDef::class, foreach.start)
        } else {
            result.expect(IteratorDef::class, "'foreach' loop must iterate over an Iterable or Iterator", foreach.start)
            result
        }

        val wrapped = CollectionUtils.wrapIterator(this, iterator, foreach.start)
        for (value in wrapped) {
            this.poll()
            val shouldBreak = this.jumpNextTable {
                this.currentTable.defineVar(foreach.name, value)
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
                if (!this.isInstanceType(instance, tryStatement.catchParameter.hints, tryStatement.start)) {
                    throw instance.getPrimitive(ErrorDef::class)!!
                }
                this.logDebug("Error '$error' was caught")
                this.currentTable.defineVar(tryStatement.catchParameter.name, instance)
                this.execute(tryStatement.catchBody)
            }
        } finally {
            // Executed even if the error is not a RuntimeError
            this.execute(tryStatement.finally)
        }
    }

    override fun visitThrow(throwStatement: ThrowStatement) {
        val error = this.evaluate(throwStatement.throwable).getPrimitive(ErrorDef::class)
        error ?: runtimeError("Tried to throw a non Error value", throwStatement.trace)
        error.pushToTop(throwStatement.trace)
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
            val parentClass = this.getClass(parent, classStatement.start, classStatement)
            if (parentClass is InterfaceDefinition) {
                interfaces.add(parentClass)
                continue
            }
            if (superclass == null) {
                if (!parentClass.canExtend() || !parentClass.constructors.isInitialized()) {
                    runtimeError("Cannot extend class '${parentClass.name}'", classStatement.start)
                }
                needsSuper = true
                superclass = parentClass
                continue
            }
            runtimeError("Classes can only extend one non-interface super class", classStatement.start)
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
            val parentClass = this.getClass(parent, enumStatement.start, enumStatement)
            if (parentClass !is InterfaceDefinition) {
                runtimeError("Enums can only implement interfaces, cannot extend class '${parentClass.name}'", enumStatement.start)
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
            this.tryImport(importPath, local)
        }

        if (importStatement.names.isNotEmpty()) {
            for (name in importStatement.names) {
                this.currentTable.addModule(name, importPath)
            }
        } else {
            this.currentTable.addAllModules(importPath)
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
        val parameters = function.parameters.map { it.create(this.currentTable, function.start) }
        val returns = LazyDefinitions.of(function.returnTypes, this.currentTable, function.start)
        UserDefinedFunction.of(function.arbitrary, function.name, parameters, function.body, this.currentTable, function.start, returns).let {
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
        val variable = this.getVariableNullable(access.name, access.trace, access)
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
                val instance = this.getVariableNullable(expression.name, access.trace, expression)
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
            val instance = this.getVariableNullable(assign.expression.name, assign.trace, assign.expression)
            instance ?: this.getClass(assign.expression.name, assign.expression.trace, assign).let {
                it.staticMemberAssign(this, assign.name, assignee, assign.trace)
                return assignee
            }
        } else {
            this.evaluate(assign.expression)
        }
        return instance.memberAssign(this, assign.name, assignee, assign.trace)
    }

    override fun visitMemberCall(call: MemberCallExpression): ClassInstance {
        val arguments = ArrayList<ClassInstance>()
        for (expression in call.arguments) {
            arguments.add(this.evaluate(expression))
        }
        val instance = when (val expression = call.expression) {
            is AccessExpression -> {
                val instance = this.getVariableNullable(expression.name, call.trace, expression)
                instance ?: this.getClass(expression.name, expression.trace, expression).let {
                    return it.staticFunctionCall(this, call.name, arguments, call.trace)
                }
            }
            is SuperExpression -> {
                this.visitSuper(expression, expression.trace).let { (i, d) ->
                    val function = d.memberFunctionAccess(i, this, call.name , arguments, call.trace, d)
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
        return classDefinition.accessConstructor(this, access.trace)
    }

    override fun visitNewCall(call: NewCallExpression): ClassInstance {
        this.poll()
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
    ): Interpreter() {
        override val isMain = true
        override val threadHandler = ThreadHandler(this)
        override val properties = this.api.getProperties().invoke()
        override val modules = ModuleMap()
        override val functions = FunctionMap()
        override val primitives = PrimitiveDefinitionMap()
        override val globalTable = StackTable(this.modules)
        override val localCache = LocalCache()
        override var currentTable = this.globalTable

        override fun execute(): ClassInstance {
            return this.threadHandler.execute()
        }

        override fun loadApi() {
            this.api.getBuiltInDefinitions().forEach { p ->
                val primitive = p(this)
                this.primitives.add(primitive)
                this.modules.addBuiltIn(primitive)
            }
            this.primitives.forEach { it.merge() }
            this.api.getBuiltInExtensions().forEach { e ->
                e.getBuiltInFunctions().forEach {
                    this.functions.add(this.create(FunctionDef::class, it))
                }
            }
            this.api.getClassDefinitions().forEach { (importPath, definitions) ->
                for (factory in definitions) {
                    val primitive = factory(this)
                    this.primitives.add(primitive)
                    this.modules.add(importPath, primitive)
                    primitive.merge()
                }
            }
        }
    }

    private class Child(
        override val content: String,
        override val name: String,
        val isImporting: Boolean,
        parent: Interpreter
    ): Interpreter() {
        override val api = parent.api
        override val properties = parent.properties
        override val globalTable = StackTable(parent.modules)
        override var currentTable = this.globalTable
        override val threadHandler = parent.threadHandler
        override val modules = parent.modules
        override val functions = parent.functions
        override val primitives = parent.primitives
        override val localCache = parent.localCache

        override fun execute(): ClassInstance {
            return try {
                this.interpret()
                this.getNull()
            } catch (returnPropagator: Propagator.Return) {
                returnPropagator.returnValue
            } catch (compileError: CompileError) {
                // Compile errors are propagated as runtime errors
                // so parent script can handle accordingly
                runtimeError("Compiling failed", compileError)
            }
        }

        override fun loadApi() {
            // API is already loaded
        }

        override fun defineClass(table: StackTable, definition: ClassDefinition) {
            super.defineClass(table, definition)
            if (table == this.globalTable && this.isImporting) {
                this.modules.add(this.name, definition)
            }
        }
    }

    private class Branch(interpreter: Interpreter): Interpreter() {
        override val content = interpreter.content
        override val name = interpreter.name
        override val api = interpreter.api
        override val properties = interpreter.properties
        override val modules = interpreter.modules
        override val threadHandler = interpreter.threadHandler
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

        override fun execute(): ClassInstance {
            throw IllegalStateException("Branch cannot be executed")
        }

        override fun loadApi() {
            throw IllegalStateException("Branch cannot reload API")
        }
    }
}