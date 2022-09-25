package me.senseiwells.arucas.api.docs.parser

import me.senseiwells.arucas.api.ArucasAPI
import me.senseiwells.arucas.api.ArucasExtension
import me.senseiwells.arucas.api.docs.ConstructorDoc
import me.senseiwells.arucas.api.docs.FieldDoc
import me.senseiwells.arucas.api.docs.FunctionDoc
import me.senseiwells.arucas.classes.ClassDefinition

open class MarkdownParser protected constructor(): DocParser() {
    companion object {
        @JvmStatic
        fun of(api: ArucasAPI): MarkdownParser {
            return MarkdownParser().also { it.fromApi(api) }
        }
    }

    override fun parse(): String {
        return "${this.parseExtensions()}\n\n${this.parseClasses()}"
    }

    fun parseExtensions(): String {
        return this.extensions.joinToString("\n\n") { this.parseExtension(it) }
    }

    fun parseClasses(): String {
        return this.definitions.joinToString("\n\n") { this.parseClass(it) }
    }

    private fun parseExtension(extension: ArucasExtension): String {
        val builder = StringBuilder("## ")
        builder.append(extension.getName()).append("\n\n")

        val docs = ParsedExtensionDocs(extension)
        builder.append(docs.functionDocs.joinToString("\n") { this.parseFunction(null, it) })
        return builder.toString()
    }

    private fun parseClass(definition: ClassDefinition): String {
        val docs = ParsedClassDocs(definition)

        val classDoc = docs.classDocOrThrow()
        val builder = StringBuilder("# ")
        builder.append(classDoc.name)
        builder.append(" class\n")
        builder.append(classDoc.name)
        builder.append(" class for Arucas\n\n")

        builder.append(this.arrayToString(classDoc.desc)).append("\n")

        if (classDoc.importPath.isNotEmpty()) {
            builder.append("Import with `import ")
            builder.append(classDoc.name)
            builder.append(" from ")
            builder.append(classDoc.importPath)
            builder.append(";`\n\n")
        } else {
            builder.append("Class does not need to be imported\n\n")
        }

        if (docs.staticFields.isNotEmpty()) {
            builder.append("## Static Fields\n\n")
            this.parseField(builder, classDoc.name, docs.staticFields)
            builder.append("\n")
        }

        if (docs.constructors.isNotEmpty()) {
            builder.append("## Constructors\n\n")
            builder.append(docs.constructors.joinToString("\n") { this.parseConstructor(classDoc.name, it) })
            builder.append("\n")
        }

        if (docs.methods.isNotEmpty()) {
            builder.append("## Methods\n\n")
            val memberClass = "<${classDoc.name}>"
            builder.append(docs.methods.joinToString("\n") { this.parseFunction(memberClass, it) })
            builder.append("\n")
        }

        if (docs.staticMethods.isNotEmpty()) {
            builder.append("## Static Methods\n\n")
            builder.append(docs.staticMethods.joinToString("\n") { this.parseFunction(classDoc.name, it) })
            builder.append("\n")
        }

        return builder.toString()
    }

    private fun parseFunction(clazz: String?, function: FunctionDoc): String {
        val builder = StringBuilder("### `")
        clazz?.let {
            builder.append(clazz).append(".")
        }

        builder.append(function.name).append("(")
        val params = Param.of(function.params)
        builder.append(params.joinToString(", ") { it.name })
        builder.append(")`\n")

        if (function.deprecated.isNotEmpty()) {
            builder.append("- Deprecated: ")
            builder.append(this.arrayToString(function.deprecated))
            builder.append("\n")
        }

        this.parseDescription(builder, function.desc)

        this.parseParameters(builder, params)

        if (function.returns.isNotEmpty()) {
            if (function.returns.size != 2) {
                throw IllegalStateException("Incorrect returns: ${function.returns.contentToString()}")
            }
            builder.append("- Returns - ")
            builder.append(function.returns[0])
            builder.append(": ")
            builder.append(function.returns[1])
            builder.append("\n")
        }

        this.parseExamples(builder, function.examples)

        return builder.toString()
    }

    private fun parseConstructor(clazz: String, constructor: ConstructorDoc): String {
        val builder = StringBuilder()
        builder.append("### `new ")
        builder.append(clazz)
        builder.append("(")

        val params = Param.of(constructor.params)
        builder.append(params.joinToString(", ") { it.name })
        builder.append(")`\n")

        this.parseDescription(builder, constructor.desc)
        this.parseParameters(builder, params)
        this.parseExamples(builder, constructor.examples)
        return builder.toString()
    }

    private fun parseField(builder: StringBuilder, clazz: String, members: ArrayList<FieldDoc>) {
        for (field in members) {
            builder.append("### `")
            builder.append(clazz)
            builder.append(".")
            builder.append(field.name)
            builder.append("`\n")

            this.parseDescription(builder, field.desc)

            builder.append("- Type: ")
            builder.append(field.type)
            builder.append("\n")

            builder.append("- Assignable: ")
            builder.append(field.assignable)
            builder.append("\n")

            this.parseExamples(builder, field.examples)
        }
    }

    private fun parseParameters(builder: StringBuilder, params: List<Param>) {
        if (params.isEmpty()) {
            return
        }

        if (params.size == 1) {
            val param = params[0]
            builder.append("- Parameter - ")
            builder.append(param.type)
            builder.append(" (`")
            builder.append(param.name)
            builder.append("`): ")
            builder.append(param.desc)
            builder.append("\n")
            return
        }

        builder.append("- Parameters:\n")
        for (param in params) {
            builder.append("  - ")
            builder.append(param.type)
            builder.append(" (`")
            builder.append(param.name)
            builder.append("`): ")
            builder.append(param.desc)
            builder.append("\n")
        }
    }

    private fun parseDescription(builder: StringBuilder, desc: Array<String>) {
        builder.append("- Description: ")
        builder.append(this.arrayToString(desc))
        builder.append("\n")
    }

    private fun parseExamples(builder: StringBuilder, examples: Array<String>) {
        builder.append(if (examples.size == 1) "- Example:\n" else "- Examples:\n")
        for (example in examples) {
            builder.append("```kotlin\n")
            builder.append(example.trimIndent().replace("\t", "    "))

            while (builder.endsWith("\n")) {
                builder.deleteAt(builder.length - 1)
            }

            builder.append("\n```\n")
        }
    }

    private fun arrayToString(array: Array<String>): String {
        return array.joinToString("\n") { it }
    }

    private data class Param(val type: String, val name: String, val desc: String) {
        companion object {
            fun of(params: Array<String>): List<Param> {
                if (params.size % 3 != 0) {
                    throw IllegalStateException("Incorrect parameters: ${params.contentToString()}")
                }
                val list = ArrayList<Param>()
                for (i in params.indices step 3) {
                    list.add(Param(params[i], params[i + 1], params[i + 2]))
                }
                return list
            }
        }
    }
}