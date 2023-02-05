package me.senseiwells.arucas.api.docs.parser

import me.senseiwells.arucas.api.ArucasAPI
import me.senseiwells.arucas.api.ArucasExtension
import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.api.docs.ConstructorDoc
import me.senseiwells.arucas.api.docs.FieldDoc
import me.senseiwells.arucas.api.docs.FunctionDoc
import me.senseiwells.arucas.api.docs.visitor.impl.ArucasDocVisitors
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.core.Interpreter
import java.nio.file.Path

@Deprecated("Replaced with ArucasDocParser")
abstract class DocParser protected constructor() {
    companion object {
        @JvmStatic
        fun generateAll(path: Path, api: ArucasAPI) {
            ArucasDocVisitors.generateDefault(path, api)
        }
    }

    val definitions = ArrayList<ClassDefinition>()
    val extensions = ArrayList<ArucasExtension>()

    abstract fun parse(): String

    protected fun fromApi(api: ArucasAPI) {
        val interpreter = Interpreter.dummy(api)
        api.getBuiltInExtensions()?.forEach {
            this.extensions.add(it)
        }
        this.definitions.addAll(interpreter.modules.builtIns())
        interpreter.modules.forEach { _, d ->
            d.forEach(this.definitions::add)
        }
        this.extensions.sortWith { a, b -> a.getName().compareTo(b.getName()) }
        this.definitions.sortWith { a, b -> a.name.compareTo(b.name) }
    }

    protected inner class ParsedExtensionDocs constructor(extension: ArucasExtension) {
        val functionDocs = ArrayList<FunctionDoc>()

        init {
            for (method in extension::class.java.declaredMethods) {
                method.getAnnotation(FunctionDoc::class.java)?.let {
                    this.functionDocs.add(it)
                }
            }
            this.functionDocs.sortWith { a, b ->
                val result = a.name.compareTo(b.name)
                if (result == 0) a.params.size.compareTo(b.params.size) else result
            }
        }
    }

    protected inner class ParsedClassDocs constructor(val definition: ClassDefinition) {
        val classDoc: ClassDoc? = this.definition::class.java.getAnnotation(ClassDoc::class.java)
        val constructors = ArrayList<ConstructorDoc>()
        val staticFields = ArrayList<FieldDoc>()
        val staticMethods = ArrayList<FunctionDoc>()
        val fields = ArrayList<FieldDoc>()
        val methods = ArrayList<FunctionDoc>()

        init {
            this.parse(this.definition::class.java)
        }

        fun classDocOrThrow(): ClassDoc {
            return this.classDoc ?: throw IllegalStateException("Class '${this.definition.name}' was not documented!")
        }

        private fun parse(definitionClass: Class<*>) {
            for (method in definitionClass.declaredMethods) {
                val constructorDoc = method.getAnnotation(ConstructorDoc::class.java)
                if (constructorDoc != null) {
                    this.constructors.add(constructorDoc)
                    continue
                }
                method.getAnnotation(FunctionDoc::class.java)?.let {
                    if (it.isStatic) {
                        this.staticMethods.add(it)
                    } else {
                        this.methods.add(it)
                    }
                }
            }
            for (field in definitionClass.declaredFields) {
                val fieldDoc = field.getAnnotation(FieldDoc::class.java)
                if (fieldDoc != null) {
                    if (fieldDoc.isStatic) this.staticFields.add(fieldDoc) else this.fields.add(fieldDoc)
                }
            }

            this.constructors.sortWith { a, b -> a.params.size.compareTo(b.params.size) }
            this.staticFields.sortWith { a, b -> a.name.compareTo(b.name) }
            this.staticMethods.sortWith { a, b ->
                val result = a.name.compareTo(b.name)
                if (result == 0) a.params.size.compareTo(b.params.size) else result
            }
            this.fields.sortWith { a, b -> a.name.compareTo(b.name) }
            this.methods.sortWith { a, b ->
                val result = a.name.compareTo(b.name)
                if (result == 0) a.params.size.compareTo(b.params.size) else result
            }
        }
    }
}
