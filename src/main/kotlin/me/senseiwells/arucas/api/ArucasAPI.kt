package me.senseiwells.arucas.api

import com.google.gson.JsonElement
import me.senseiwells.arucas.api.ArucasAPI.Builder
import me.senseiwells.arucas.api.docs.visitor.ArucasDocParser
import me.senseiwells.arucas.api.docs.visitor.impl.CodeDocVisitor
import me.senseiwells.arucas.api.impl.DefaultArucasIO
import me.senseiwells.arucas.api.impl.GitHubArucasLibrary
import me.senseiwells.arucas.api.impl.MultiArucasLibrary
import me.senseiwells.arucas.api.impl.MultiArucasPoller
import me.senseiwells.arucas.builtin.*
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.RuntimeError
import me.senseiwells.arucas.extensions.JavaClassDef
import me.senseiwells.arucas.extensions.JavaDef
import me.senseiwells.arucas.extensions.JsonDef
import me.senseiwells.arucas.extensions.NetworkDef
import me.senseiwells.arucas.utils.*
import me.senseiwells.arucas.utils.Util.File.ensureParentExists
import me.senseiwells.arucas.utils.impl.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Future
import java.util.stream.Stream
import kotlin.reflect.KClass

/**
 * API for Arucas, used to set up the interpreter.
 *
 * This class intends to provide the interpreter with the wanted extensions,
 * built-in classes, importable classes, Java reflection support, and library manager.
 *
 * You may implement this, however it is advised that you instead use the [Builder] instead.
 *
 * To see how to use the API, see the full documentation [here](https://github.com/senseiwells/Arucas/blob/main/docs/ArucasAPI.md).
 *
 * An example API can be setup with this:
 * ```kotlin
 * val builder = ArucasAPI.Builder()
 *     .addDefault() // Adds the default built-in extensions and classes, etc.
 * val api = builder.build();
 * ```
 *
 * To then create an interpreter we can do this:
 * ```kotlin
 * val name: String = /* Interpreter Name */
 * val code: String = /* Arucas Code */
 * val interpreter = Interpreter.of(code, name, api)
 * ```
 *
 * @see Interpreter
 */
interface ArucasAPI {
    /**
     * This method should return any built-in functions.
     *
     * These functions will be available in the global scope.
     *
     * @return the list of extensions.
     */
    fun getBuiltInExtensions(): List<ArucasExtension> = listOf()

    /**
     * This method should return any built-in definitions.
     *
     * These definitions will not require importing to be used.
     *
     * @return the list of primitive generators.
     */
    fun getBuiltInDefinitions(): List<(Interpreter) -> PrimitiveDefinition<*>> = listOf()

    /**
     * This method should return any class definitions.
     *
     * These definitions will require importing to be used.
     *
     * @return a map of list of primitive generators. The key of the map will be the
     * import path and the values are the generators for that given path.
     */
    fun getClassDefinitions(): Map<String, List<(Interpreter) -> PrimitiveDefinition<*>>> = mapOf()

    /**
     * This method should return the interpreter's input handler.
     *
     * @return the input handler.
     * @see ArucasInput
     */
    fun getInput(): ArucasInput

    /**
     * This method should return the interpreter's output handler.
     *
     * @return the output handler.
     * @see ArucasOutput
     */
    fun getOutput(): ArucasOutput

    /**
     * This method should return the interpreter's error handler.
     *
     * @return the error handler.
     * @see ArucasErrorHandler
     */
    fun getErrorHandler(): ArucasErrorHandler

    /**
     * This method should return an API that handles
     * obfuscation.
     *
     * @return the obfuscation handler.
     * @see ArucasObfuscator
     */
    fun getObfuscator(): ArucasObfuscator

    /**
     * This method should return a converter that
     * converts Java values into Arucas ones.
     *
     * @return the value converter.
     * @see ValueConverter
     */
    fun getConverter(): ValueConverter

    /**
     * This method should return the Arucas library
     * handler which can download libraries.
     *
     * @return the library manager.
     * @see ArucasLibrary
     */
    fun getLibraryManager(): ArucasLibrary

    /**
     * This method should return the main
     * Arucas Executor which will be used to
     * start the execution of scripts.
     *
     * If no executor is provided then the default
     * async executor is used instead.
     *
     * @return the arucas executor.
     * @see ArucasExecutor
     */
    fun getMainExecutor(): ArucasExecutor?

    /**
     * This method should return a poller used during the interpreter's execution.
     *
     * @return the poller.
     * @see ArucasPoller
     */
    fun getPoller(): ArucasPoller

    /**
     * This method should return the properties
     * you want for the interpreter.
     *
     * @return the property generator.
     */
    fun getProperties(): () -> Properties

    /**
     * This method generates the BuiltIn libraries as
     * Arucas code and puts them in the users imports.
     *
     * @param rootPath the path to generate the native files to.
     */
    fun generateNativeFiles(rootPath: Path) {
        val visitor = CodeDocVisitor()
        ArucasDocParser(this).addVisitor(visitor).parse()

        val builtInPath = rootPath.resolve("BuiltIn.arucas").ensureParentExists()
        Files.writeString(builtInPath, visitor.getBuiltIns())

        for ((importPath, content) in visitor.getModules()) {
            val path = importPath.replace('.', File.separatorChar) + ".arucas"
            val generationPath = rootPath.resolve(path).ensureParentExists()
            Files.writeString(generationPath, content)
        }

        val extensionPath = rootPath.resolve("Extensions.arucas").ensureParentExists()
        Files.writeString(extensionPath, visitor.getExtensions())
    }


    /**
     * API builder for Arucas.
     *
     * This can be used to build custom [ArucasAPI] with ease.
     *
     * @see ArucasAPI
     */
    @Suppress("UNUSED")
    class Builder {
        private val builtInExtensions = ArrayList<ArucasExtension>()
        private val builtInDefinitions = ArrayList<(Interpreter) -> PrimitiveDefinition<*>>()
        private val classDefinitions = HashMap<String, ArrayList<(Interpreter) -> PrimitiveDefinition<*>>>()
        private val converter = ValueConverter()

        /**
         * The input handler.
         *
         * @see ArucasInput
         */
        var input: ArucasInput
            private set

        /**
         * The output handler.
         *
         * @see ArucasOutput
         */
        var output: ArucasOutput
            private set

        /**
         * The error handler.
         *
         * @see ArucasErrorHandler
         */
        var errorHandler = ArucasErrorHandler.default
            private set

        /**
         * The obfuscation handler.
         *
         * @see ArucasObfuscator
         */
        var obfuscator = ArucasObfuscator.default
            private set

        /**
         * The library manager.
         *
         * @see ArucasLibrary
         * @see MultiArucasLibrary
         */
        var library: ArucasLibrary = MultiArucasLibrary()
            private set

        /**
         * The main executor.
         *
         * @see ArucasExecutor
         */
        var executor: ArucasExecutor? = null
            private set

        /**
         * The poller for the interpreter.
         * @see ArucasPoller
         */
        var poller = MultiArucasPoller()
            private set

        /**
         * The property Generator.
         */
        var properties = { Properties() }
            private set

        init {
            val io = DefaultArucasIO()
            this.input = io
            this.output = io
        }

        /**
         * This adds an extension to the Arucas API.
         *
         * @param extension the extension to add.
         * @return the builder.
         */
        fun addBuiltInExtension(extension: ArucasExtension): Builder {
            this.builtInExtensions.add(extension)
            return this
        }

        /**
         * This adds built in definition generators.
         *
         * @param definitions the vararg definition generators.
         * @return the builder.
         */
        fun addBuiltInDefinitions(vararg definitions: (Interpreter) -> PrimitiveDefinition<*>): Builder {
            this.builtInDefinitions.addAll(definitions)
            return this
        }

        /**
         * This adds class definition to be imported.
         *
         * @param name the import path that will be used to import classes.
         * @param definitions the vararg definition generators.
         * @return the builder.
         */
        fun addClassDefinitions(name: String, vararg definitions: (Interpreter) -> PrimitiveDefinition<*>): Builder {
            this.classDefinitions.getOrPut(name) { ArrayList() }.addAll(definitions)
            return this
        }

        /**
         * This adds an [ArucasLibrary] given that the current [library] is
         * a [MultiArucasLibrary] allowing for multiple libraries to be added.
         *
         * If not then nothing will be mutated.
         *
         * @param libraryName the name of the library, this can be used to remove it later.
         * @param generator the library generator with the given library path.
         * @return the builder.
         */
        fun addArucasLibrary(libraryName: String, generator: ArucasLibrary): Builder {
            val library = this.library
            if (library is MultiArucasLibrary) {
                library.addLibrary(libraryName, generator)
            }
            return this
        }

        /**
         * This sets the input handler of the API.
         *
         * @param input the new input handler.
         * @return the builder.
         */
        fun setInput(input: ArucasInput): Builder {
            this.input = input
            return this
        }

        /**
         * This sets the output handler of the API.
         *
         * @param output the new output handler.
         * @return the builder.
         */
        fun setOutput(output: ArucasOutput): Builder {
            this.output = output
            return this
        }

        /**
         * This sets the error handler of the API.
         *
         * @param errorHandler the new error handler.
         * @return the builder.
         */
        fun setErrorHandler(errorHandler: ArucasErrorHandler): Builder {
            this.errorHandler = errorHandler
            return this
        }

        /**
         * This sets the obfuscator for the API.
         *
         * @param obfuscator the new obfuscator.
         * @return the builder.
         */
        fun setObfuscator(obfuscator: ArucasObfuscator): Builder {
            this.obfuscator = obfuscator
            return this
        }

        /**
         * This sets the library manager for the API.
         *
         * It is suggested that you use [addArucasLibrary] instead to allow
         * for multiple libraries to be added, however you may use your own custom setup.
         *
         * @param library the new library manager.
         * @return the builder.
         */
        fun setLibraryManager(library: ArucasLibrary): Builder {
            this.library = library
            return this
        }

        /**
         * This sets the main executor used for running scripts.
         *
         * @param executor the new executor.
         * @return the builder.
         */
        fun setMainExecutor(executor: ArucasExecutor): Builder {
            this.executor = executor
            return this
        }

        /**
         * This adds a poller for the interpreter.
         *
         * @param poller the poller to add.
         * @return the builder.
         */
        fun addPoller(poller: ArucasPoller): Builder {
            this.poller.addPoller(poller)
            return this
        }

        /**
         * This sets the property generator for the API.
         *
         * @param properties the property generator.
         * @return the builder.
         */
        fun setInterpreterProperties(properties: () -> Properties): Builder {
            this.properties = properties
            return this
        }

        /**
         * This adds a conversion from an instance of a [KClass] to a [ClassInstance].
         *
         * @param T the type to convert.
         * @param klass the class type to convert from
         * @param converter the converter.
         * @return the builder.
         * @see ValueConverter
         */
        fun <T: Any> addConversion(klass: KClass<T>, converter: Converter<T>): Builder {
            return this.addConversion(klass.java, converter)
        }

        /**
         * This adds a conversion from an instance of a [Class] to a [ClassInstance].
         *
         * @param T the type to convert.
         * @param clazz the class type to convert from
         * @param converter the converter.
         * @return the builder.
         * @see ValueConverter
         */
        fun <T: Any> addConversion(clazz: Class<T>, converter: Converter<T>): Builder {
            this.converter.addClass(clazz, converter)
            return this
        }

        /**
         * Adds all the default libraries, conversions, extensions,
         * built-in extensions, built-in classes, and importable
         * classes.
         *
         * It is advised that you always call this on your builder
         * as it adds the basic classes for [StringDef], [BooleanDef], etc.
         * Without these the interpreter may fail.
         *
         * @return the builder.
         */
        fun addDefault(): Builder {
            this.addDefaultLibrary()
            this.addDefaultConversions()
            this.addDefaultExtensions()
            this.addDefaultBuiltInDefinitions()
            this.addDefaultClassDefinitions()
            return this
        }

        /**
         * Adds the `ArucasLibraries` GitHub library. You
         * can find the repository [here](https://github.com/senseiwells/ArucasLibraries).
         *
         * @return the builder.
         */
        fun addDefaultLibrary(): Builder {
            return this.addArucasLibrary("ArucasLibraries", GitHubArucasLibrary())
        }

        /**
         * Adds [BuiltInExtension] which is the default built-in
         * extension providing the very basic functions such as `print`, and `sleep`.
         *
         * @return the builder.
         */
        fun addDefaultExtensions(): Builder {
            this.addBuiltInExtension(
                BuiltInExtension()
            )
            return this
        }

        /**
         * Adds all the built-in definitions. This includes
         * all the bare-bones requirements as well as some
         * standard classes, such as [MathDef], and [FileDef].
         *
         * @return the builder.
         */
        fun addDefaultBuiltInDefinitions(): Builder {
            this.addBuiltInDefinitions(
                ::ObjectDef,
                ::StringDef,
                ::NumberDef,
                ::BooleanDef,
                ::FunctionDef,
                ::NullDef,
                ::ListDef,
                ::MapDef,
                ::SetDef,
                ::IterableDef,
                ::IteratorDef,
                ::ErrorDef,
                ::EnumDef,
                ::TypeDef,
                ::CollectionDef,
                ::FileDef,
                ::ThreadDef,
                ::TaskDef,
                ::FutureDef,
                ::MathDef,
                ::NetworkDef
            )
            return this
        }

        /**
         * Adds the default importable classes. These include
         * [JavaDef], [JavaClassDef] from `util.Internal` and
         * [JsonDef] from `util.Json`.
         *
         * @return the builder.
         */
        fun addDefaultClassDefinitions(): Builder {
            this.addClassDefinitions("util.Internal", ::JavaDef, ::JavaClassDef)
            this.addClassDefinitions("util.Json", ::JsonDef)
            return this
        }

        /**
         * Adds all the default conversions from Java to [ClassInstance].
         *
         * These include the primitive types and their arrays, collections
         * and any other utility objects.
         *
         * @return the builder.
         */
        fun addDefaultConversions(): Builder {
            // We need to specify Java primitives because... Java
            this.addConversion(java.lang.Boolean::class) { b, i -> i.createBool(b.booleanValue()) }
            this.addConversion(java.lang.Byte::class) { b, i -> i.create(NumberDef::class, b.toDouble()) }
            this.addConversion(java.lang.Character::class) { c, i -> i.create(StringDef::class, c.toString()) }
            this.addConversion(java.lang.Double::class) { d, i -> i.create(NumberDef::class, d.toDouble()) }
            this.addConversion(java.lang.Float::class) { f, i -> i.create(NumberDef::class, f.toDouble()) }
            this.addConversion(java.lang.Integer::class) { int, i -> i.create(NumberDef::class, int.toDouble()) }
            this.addConversion(java.lang.Long::class) { l, i -> i.create(NumberDef::class, l.toDouble()) }
            this.addConversion(java.lang.Short::class) { s, i -> i.create(NumberDef::class, s.toDouble()) }
            // And all the arrays
            this.addConversion(BooleanArray::class) { b, i -> i.create(ListDef::class, ArucasList(b.map { i.createBool(it) })) }
            this.addConversion(ByteArray::class) { b, i -> i.create(ListDef::class, ArucasList(b.map { i.create(NumberDef::class, it.toDouble()) })) }
            this.addConversion(CharArray::class) { c, i -> i.create(ListDef::class, ArucasList(c.map { i.create(StringDef::class, it.toString()) })) }
            this.addConversion(DoubleArray::class) { d, i -> i.create(ListDef::class, ArucasList(d.map { i.create(NumberDef::class, it) })) }
            this.addConversion(FloatArray::class) { f, i -> i.create(ListDef::class, ArucasList(f.map { i.create(NumberDef::class, it.toDouble()) })) }
            this.addConversion(IntArray::class) { ints, i -> i.create(ListDef::class, ArucasList(ints.map { i.create(NumberDef::class, it.toDouble()) })) }
            this.addConversion(LongArray::class) { l, i -> i.create(ListDef::class, ArucasList(l.map { i.create(NumberDef::class, it.toDouble()) })) }
            this.addConversion(ShortArray::class) { s, i -> i.create(ListDef::class, ArucasList(s.map { i.create(NumberDef::class, it.toDouble()) })) }
            this.addConversion(Array::class) { a, i -> i.create(ListDef::class, ArucasList(a.map { i.convertValue(it) })) }

            this.addConversion(String::class) { s, i -> i.create(StringDef::class, s) }
            this.addConversion(Unit::class) { _, i -> i.getNull() }
            this.addConversion(ArucasFunction::class) { f, i -> i.create(FunctionDef::class, f) }
            this.addConversion(RuntimeError::class) { e, i -> e.getInstance(i) }
            this.addConversion(ClassDefinition::class) { c, _ -> c.getTypeInstance() }
            this.addConversion(ArucasIterable::class) { a, i -> i.create(IterableDef::class, a) }
            this.addConversion(Iterable::class) { it, i -> i.create(IterableDef::class, ArucasIterable.wrap(i, it)) }
            this.addConversion(ArucasIterator::class) { it, i -> i.create(IteratorDef::class, it) }
            this.addConversion(Iterator::class) { it, i -> i.create(IteratorDef::class, ArucasIterator.wrap(i, it)) }
            this.addConversion(ArucasList::class) { a, i -> i.create(ListDef::class, a) }
            this.addConversion(List::class) { l, i -> i.create(ListDef::class, ArucasList(l.map { i.convertValue(it) })) }
            this.addConversion(ArucasMap::class) { m, i -> i.create(MapDef::class, m) }
            this.addConversion(Map::class) { m, i -> i.create(MapDef::class, ArucasOrderedMap(i, m)) }
            this.addConversion(ArucasSet::class) { s, i -> i.create(SetDef::class, s) }
            this.addConversion(Set::class) { s, i -> i.create(SetDef::class, ArucasSet(i, s)) }
            this.addConversion(Stream::class) { s, i -> i.convertValue(s.toList()) }
            this.addConversion(JsonElement::class) { j, i -> i.create(JsonDef::class, j) }
            this.addConversion(File::class) { f, i -> i.create(FileDef::class, f) }
            this.addConversion(Path::class) { p, i -> i.create(FileDef::class, p.toFile()) }
            this.addConversion(ArucasThread::class) { a, i -> i.create(ThreadDef::class, a) }
            this.addConversion(Task::class) { t, i -> i.create(TaskDef::class, t) }
            this.addConversion(Future::class) { f, i -> i.create(FutureDef::class, f) }
            this.addConversion(Class::class) { c, i -> i.create(JavaClassDef::class, c) }
            this.addConversion(KClass::class) { k, i -> i.create(JavaClassDef::class, k.java) }
            return this
        }

        /**
         * This builds the [ArucasAPI.Builder] into an actual [ArucasAPI].
         *
         * These are simply just references to the builder itself so if
         * the builder is mutated so will the [ArucasAPI].
         *
         * @return the built [ArucasAPI].
         */
        fun build(): ArucasAPI {
            return object: ArucasAPI {
                override fun getBuiltInExtensions() = this@Builder.builtInExtensions

                override fun getBuiltInDefinitions() = this@Builder.builtInDefinitions

                override fun getClassDefinitions() = this@Builder.classDefinitions

                override fun getInput() = this@Builder.input

                override fun getOutput() = this@Builder.output

                override fun getErrorHandler() = this@Builder.errorHandler

                override fun getObfuscator() = this@Builder.obfuscator

                override fun getConverter() = this@Builder.converter

                override fun getLibraryManager() = this@Builder.library

                override fun getMainExecutor() = this@Builder.executor

                override fun getPoller() = this@Builder.poller

                override fun getProperties() = this@Builder.properties
            }
        }
    }
}