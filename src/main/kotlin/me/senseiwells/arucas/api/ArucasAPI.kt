package me.senseiwells.arucas.api

import com.google.gson.JsonElement
import me.senseiwells.arucas.api.docs.parser.CodeDocParser
import me.senseiwells.arucas.builtin.*
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.RuntimeError
import me.senseiwells.arucas.extensions.JavaClassDef
import me.senseiwells.arucas.extensions.JavaDef
import me.senseiwells.arucas.extensions.JsonDef
import me.senseiwells.arucas.extensions.NetworkDef
import me.senseiwells.arucas.utils.ArucasFunction
import me.senseiwells.arucas.utils.Converter
import me.senseiwells.arucas.utils.Properties
import me.senseiwells.arucas.utils.Util.Collection.sort
import me.senseiwells.arucas.utils.Util.File.ensureParentExists
import me.senseiwells.arucas.utils.ValueConverter
import me.senseiwells.arucas.utils.impl.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Future
import java.util.stream.Stream
import kotlin.reflect.KClass

/**
 * Api for Arucas, used to set up the interpreter.
 */
interface ArucasAPI {
    /**
     * This method should return any built-in functions.
     *
     * These functions will be available in the global scope.
     */
    fun getBuiltInExtensions(): List<ArucasExtension>? = null

    /**
     * This method should return any built-in definitions.
     *
     * These definitions will not require importing to be used.
     */
    fun getBuiltInDefinitions(): List<(Interpreter) -> PrimitiveDefinition<*>>? = null

    /**
     * This method should return any class definitions.
     *
     * These definitions will require importing to be used.
     */
    fun getClassDefinitions(): Map<String, List<(Interpreter) -> PrimitiveDefinition<*>>>? = null

    /**
     * This method should return the interpreters input handler.
     *
     * @see ArucasInput
     */
    fun getInput(): ArucasInput

    /**
     * This method should return the interpreters output handler.
     *
     * @see ArucasOutput
     */
    fun getOutput(): ArucasOutput

    /**
     * This method should return an API that handles
     * obfuscation.
     *
     * @see ArucasObfuscator
     */
    fun getObfuscator(): ArucasObfuscator

    /**
     * This method should return a converter that
     * converts Java values into Arucas ones.
     *
     * @see ValueConverter
     */
    fun getConverter(): ValueConverter

    /**
     * This method should return the Arucas library
     * handler which can download libraries.
     *
     * @see ArucasLibrary
     */
    fun getLibraryManager(): ArucasLibrary

    /**
     * This method should return the properties
     * you want for the interpreter.
     */
    fun getProperties(): () -> Properties

    /**
     * This method generates the BuiltIn libraries as
     * Arucas code and puts them in the users imports.
     */
    fun generateNativeFiles(rootPath: Path = this.getLibraryManager().importPath) {
        val docParser = CodeDocParser()
        val dummy = Interpreter.dummy(this)

        dummy.globalTable.getModules().sort().let {
            val generationPath = rootPath.resolve("BuiltIn.arucas").ensureParentExists()
            Files.writeString(generationPath, docParser.parseClasses(it))
        }

        dummy.modules.forEach { p, c ->
            val path = p.replace('.', '/') + ".arucas"
            val generationPath = rootPath.resolve(path).ensureParentExists()
            Files.writeString(generationPath, docParser.parseClasses(c.sort()))
        }

        this.getBuiltInExtensions()?.let {
            val generationPath = rootPath.resolve("Extensions.arucas").ensureParentExists()
            Files.writeString(generationPath, docParser.parseExtensions(it))
        }
    }


    /**
     * API builder for Arucas.
     */
    @Suppress("UNUSED")
    class Builder {
        private val builtInExtensions = ArrayList<ArucasExtension>()
        private val builtInDefinitions = ArrayList<(Interpreter) -> PrimitiveDefinition<*>>()
        private val classDefinitions = HashMap<String, ArrayList<(Interpreter) -> PrimitiveDefinition<*>>>()
        private val converter = ValueConverter()

        var input: ArucasInput
            private set
        var output: ArucasOutput
            private set
        var obfuscator = ArucasObfuscator.default
            private set
        var library: ArucasLibrary = ImplArucasLibrary()
            private set
        var properties = { Properties() }
            private set

        init {
            ImplArucasIO().let {
                this.input = it
                this.output = it
            }
        }

        fun addBuiltInExtension(extension: ArucasExtension): Builder {
            this.builtInExtensions.add(extension)
            return this
        }

        fun addBuiltInDefinitions(vararg definitions: (Interpreter) -> PrimitiveDefinition<*>): Builder {
            this.builtInDefinitions.addAll(definitions)
            return this
        }

        fun addClassDefinitions(name: String, vararg definitions: (Interpreter) -> PrimitiveDefinition<*>): Builder {
            this.classDefinitions.getOrPut(name) { ArrayList() }.addAll(definitions)
            return this
        }

        fun setInput(input: ArucasInput): Builder {
            this.input = input
            return this
        }

        fun setOutput(output: ArucasOutput): Builder {
            this.output = output
            return this
        }

        fun setObfuscator(obfuscator: ArucasObfuscator): Builder {
            this.obfuscator = obfuscator
            return this
        }

        fun setLibraryManager(library: ArucasLibrary): Builder {
            this.library = library
            return this
        }

        fun setInterpreterProperties(properties: () -> Properties): Builder {
            this.properties = properties
            return this
        }

        fun <T: Any> addConversion(klass: KClass<T>, converter: Converter<T>): Builder {
            return this.addConversion(klass.java, converter)
        }

        fun <T: Any> addConversion(clazz: Class<T>, converter: Converter<T>): Builder {
            this.converter.addClass(clazz, converter)
            return this
        }

        fun addDefault(): Builder {
            ImplArucasIO().let { this.setInput(it).setOutput(it) }
            this.addDefaultConversions()
            this.addDefaultExtensions()
            this.addDefaultBuiltInDefinitions()
            this.addDefaultClassDefinitions()
            this.addDefaultConversions()
            return this
        }

        fun addDefaultExtensions(): Builder {
            this.addBuiltInExtension(
                BuiltInExtension()
            )
            return this
        }

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
                ::ErrorDef,
                ::EnumDef,
                ::TypeDef,
                ::CollectionDef,
                ::FileDef,
                ::ThreadDef,
                ::TaskDef,
                ::FutureDef,
                ::JsonDef,
                ::MathDef,
                ::NetworkDef
            )
            return this
        }

        fun addDefaultClassDefinitions(): Builder {
            this.addClassDefinitions("util.Internal", ::JavaDef, ::JavaClassDef)
            return this
        }

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
            return this
        }

        fun build(): ArucasAPI {
            return object: ArucasAPI {
                override fun getBuiltInExtensions() = this@Builder.builtInExtensions

                override fun getBuiltInDefinitions() = this@Builder.builtInDefinitions

                override fun getClassDefinitions() = this@Builder.classDefinitions

                override fun getInput() = this@Builder.input

                override fun getOutput() = this@Builder.output

                override fun getObfuscator() = this@Builder.obfuscator

                override fun getConverter() = this@Builder.converter

                override fun getLibraryManager() = this@Builder.library

                override fun getProperties() = this@Builder.properties
            }
        }
    }
}