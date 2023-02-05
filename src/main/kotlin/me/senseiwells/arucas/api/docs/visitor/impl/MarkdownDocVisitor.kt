package me.senseiwells.arucas.api.docs.visitor.impl

import me.senseiwells.arucas.api.docs.visitor.*
import java.util.*

class MarkdownDocVisitor: ArucasDocVisitor() {
    private val extensions = StringBuilder()
    private val classes = TreeMap<String, String>()

    fun getExtensions(): String {
        return this.extensions.toString()
    }

    fun getClasses(): String {
        return this.classes.values.joinToString("\n\n")
    }

    override fun visitExtension(extensionDoc: ExtensionDoc, functions: List<FunctionDoc>) {
        this.extensions.append("## ")
        this.extensions.append(extensionDoc.getName()).append("\n\n")

        this.extensions.append(functions.joinToString("\n") { this.parseFunction(null, it) })
    }

    override fun visitClass(classDoc: ClassDoc, fields: List<FieldDoc>, constructors: List<ConstructorDoc>, methods: List<FunctionDoc>, staticMethods: List<FunctionDoc>) {
        val builder = StringBuilder("# ")
        builder.append(classDoc.getName())
        builder.append(" class\n")
        builder.append(classDoc.getName())
        builder.append(" class for Arucas\n\n")

        builder.append(this.arrayToString(classDoc.getDescription())).append("\n")

        if (classDoc.isImportable()) {
            builder.append("Import with `import ")
            builder.append(classDoc.getName())
            builder.append(" from ")
            builder.append(classDoc.getImportPath())
            builder.append(";`\n\n")
        } else {
            builder.append("Class does not need to be imported\n\n")
        }

        if (fields.isNotEmpty()) {
            builder.append("## Static Fields\n\n")
            for (field in fields) {
                this.parseField(builder, classDoc.getName(), field)
            }
            builder.append("\n")
        }

        if (constructors.isNotEmpty()) {
            builder.append("## Constructors\n\n")
            builder.append(constructors.joinToString("\n") { this.parseConstructor(classDoc.getName(), it) })
            builder.append("\n")
        }

        if (methods.isNotEmpty()) {
            builder.append("## Methods\n\n")
            val memberClass = "<${classDoc.getName()}>"
            builder.append(methods.joinToString("\n") { this.parseFunction(memberClass, it) })
            builder.append("\n")
        }

        if (staticMethods.isNotEmpty()) {
            builder.append("## Static Methods\n\n")
            builder.append(staticMethods.joinToString("\n") { this.parseFunction(classDoc.getName(), it) })
            builder.append("\n")
        }

        this.classes[classDoc.getName()] = builder.toString()
    }

    private fun parseFunction(clazz: String?, function: FunctionDoc): String {
        val builder = StringBuilder("### `")
        if (clazz != null) {
            builder.append(clazz).append(".")
        }

        builder.append(function.getName()).append("(")
        builder.append(function.getParameters().joinToString(", ") { it.getName() + if (it.isVarargs()) "..." else "" })
        builder.append(")`\n")

        if (function.isDeprecated()) {
            builder.append("- Deprecated: ")
            builder.append(this.arrayToString(function.getDeprecated()))
            builder.append("\n")
        }

        this.parseDescription(builder, function.getDescription())

        this.parseParameters(builder, function.getParameters())

        if (function.hasReturns()) {
            val returns = function.getReturns()
            builder.append("- Returns - ")
            builder.append(returns.getType().getName())
            builder.append(": ")
            builder.append(returns.getFormattedDescription())
            builder.append("\n")
        }

        this.parseExamples(builder, function.getExamples())

        return builder.toString()
    }

    private fun parseConstructor(clazz: String, constructor: ConstructorDoc): String {
        val builder = StringBuilder()
        builder.append("### `new ")
        builder.append(clazz)
        builder.append("(")

        builder.append(constructor.getParameters().joinToString(", ") { it.getName() + if (it.isVarargs()) "..." else "" })
        builder.append(")`\n")

        this.parseDescription(builder, constructor.getDescription())
        this.parseParameters(builder, constructor.getParameters())
        this.parseExamples(builder, constructor.getExamples())
        return builder.toString()
    }

    private fun parseField(builder: StringBuilder, clazz: String, fieldDoc: FieldDoc) {
        builder.append("### `")
        builder.append(clazz)
        builder.append(".")
        builder.append(fieldDoc.getName())
        builder.append("`\n")

        this.parseDescription(builder, fieldDoc.getDescription())

        builder.append("- Type: ")
        builder.append(fieldDoc.getType().getName())
        builder.append("\n")

        builder.append("- Assignable: ")
        builder.append(fieldDoc.isAssignable())
        builder.append("\n")

        this.parseExamples(builder, fieldDoc.getExamples())
    }

    private fun parseParameters(builder: StringBuilder, params: List<ParameterDoc>) {
        if (params.isEmpty()) {
            return
        }

        if (params.size == 1) {
            val param = params[0]
            builder.append("- Parameter - ")
            builder.append(param.getType().getName())
            builder.append(" (`")
            builder.append(param.getName())
            builder.append("`): ")
            builder.append(param.getFormattedDescription())
            builder.append("\n")
            return
        }

        builder.append("- Parameters:\n")
        for (param in params) {
            builder.append("  - ")
            builder.append(param.getType().getName())
            builder.append(" (`")
            builder.append(param.getName())
            builder.append("`): ")
            builder.append(param.getFormattedDescription())
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
}