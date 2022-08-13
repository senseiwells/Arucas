package me.senseiwells.arucas.api.docs.parser

import me.senseiwells.arucas.api.ArucasAPI
import me.senseiwells.arucas.api.ArucasExtension
import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.api.docs.ConstructorDoc
import me.senseiwells.arucas.api.docs.FieldDoc
import me.senseiwells.arucas.api.docs.FunctionDoc
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.core.Interpreter

abstract class DocParser protected constructor() {
    val definitions = ArrayList<ClassDefinition>()
    val extensions = ArrayList<ArucasExtension>()

    abstract fun parse(): String

    protected fun fromApi(api: ArucasAPI) {
        val interpreter = Interpreter.dummy()
        api.getBuiltInExtensions()?.forEach {
            this.extensions.add(it)
        }
        api.getBuiltInDefinitions()?.forEach {
            this.definitions.add(it(interpreter))
        }
        api.getClassDefinitions()?.values?.flatten()?.forEach {
            this.definitions.add(it(interpreter))
        }
    }

    protected inner class ParsedExtensionDocs internal constructor(extension: ArucasExtension) {
        val functionDocs = ArrayList<FunctionDoc>()

        init {
            for (method in extension::class.java.declaredMethods) {
                method.getAnnotation(FunctionDoc::class.java)?.let {
                    this.functionDocs.add(it)
                }
            }
        }
    }

    protected inner class ParsedClassDocs internal constructor(definition: ClassDefinition) {
        val classDoc: ClassDoc? = definition::class.java.getAnnotation(ClassDoc::class.java)
        val constructors = ArrayList<ConstructorDoc>()
        val staticFields = ArrayList<FieldDoc>()
        val staticMethods = ArrayList<FunctionDoc>()
        val fields = ArrayList<FieldDoc>()
        val methods = ArrayList<FunctionDoc>()

        init {
            this.parse(definition::class.java)
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
        }
    }
}
