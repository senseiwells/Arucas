package me.senseiwells.arucas.api.docs.visitor.impl

import me.senseiwells.arucas.api.docs.visitor.*
import me.senseiwells.arucas.utils.Util

class CodeDocVisitor: ArucasDocVisitor() {
    companion object {
        private const val INDENT = "    "
        private const val NO_INDENT = ""
    }

    private val extensions = StringBuilder()
    private val builtIns = StringBuilder()
    private val classes = HashMap<String, StringBuilder>()

    fun getExtensions(): String {
        return this.extensions.toString()
    }

    fun getBuiltIns(): String {
        return this.builtIns.toString()
    }

    fun getModules(): Map<String, String> {
        return this.classes.map { (a, b) -> a to b.toString() }.toMap()
    }

    override fun startExtensions() {
        this.extensions.append("/*\n")
        this.extensions.append(" * Natively implemented on the JVM")
        this.extensions.append(" * These functions are treated as if\n")
        this.extensions.append(" * they were defined in the global scope.\n")
        this.extensions.append(" * These do not need to be imported.\n")
        this.extensions.append(" */\n\n")
    }

    override fun visitExtension(extensionDoc: ExtensionDoc) {
        this.extensions.append("/* ").append(extensionDoc.getName()).append(" */\n")
    }

    override fun visitExtensionFunction(extensionDoc: ExtensionDoc, functionDoc: FunctionDoc) {
        this.addFunctionDoc(this.extensions, NO_INDENT, functionDoc)
        this.extensions.append("fun ").append(functionDoc.getName()).append("(")
        this.addArgumentsToBuilder(this.extensions, functionDoc.getParameters())
        this.extensions.append("): ").append(functionDoc.getReturns().getType().getName()).append(";\n\n")
    }

    override fun visitClass(classDoc: ClassDoc, fields: List<FieldDoc>, constructors: List<ConstructorDoc>, methods: List<FunctionDoc>, staticMethods: List<FunctionDoc>) {
        val builder = if (!classDoc.isImportable()) this.builtIns else this.classes.getOrPut(classDoc.getImportPath()) { StringBuilder() }
        builder.append("/* Native, implemented in ${classDoc.getLanguage()}\n")
        for (desc in classDoc.getDescription()) {
            builder.append(" * ").append(desc).append("\n")
        }
        if (!classDoc.isImportable()) {
            builder.append(" * This class does not need to be imported.")
        } else {
            builder.append(" * Import with 'import ").append(classDoc.getName())
            builder.append(" from ").append(classDoc.getImportPath()).append("'")
        }
        builder.append("\n */\n")
        builder.append("class ")
        builder.append(classDoc.getName())

        val superclassName = classDoc.getSuperclass().getName()
        if (superclassName != Util.Types.OBJECT) {
            builder.append(": ").append(superclassName)
        }

        builder.append(" {\n")
        var hadVars = false
        for (doc in fields) {
            builder.append(INDENT).append("/*\n")
            for (desc in doc.getDescription()) {
                builder.append(INDENT).append(" * ").append(desc).append("\n")
            }
            builder.append(INDENT).append(" * This field is ").append(if (doc.isAssignable()) "" else "not ").append("assignable\n")
            builder.append(INDENT).append(" */\n")
            builder.append(INDENT).append("static var ").append(doc.getName()).append(": ").append(doc.getType().getName()).append(";\n")
            hadVars = true
        }
        if (hadVars) {
            builder.append("\n")
        }
        for (doc in constructors) {
            builder.append(INDENT).append("/*\n")
            for (desc in doc.getDescription()) {
                builder.append(INDENT).append(" * ").append(desc).append("\n")
            }
            this.addParameters(builder, INDENT, doc.getParameters())
            builder.append(INDENT).append(" */\n")
            builder.append(INDENT).append(classDoc.getName()).append("(")
            this.addArgumentsToBuilder(builder, doc.getParameters())
            builder.append(");\n\n")
        }
        for (doc in methods) {
            this.addFunctionDoc(builder, INDENT, doc)
            builder.append(INDENT).append("fun ").append(doc.getName()).append("(")
            this.addArgumentsToBuilder(builder, doc.getParameters())
            builder.append("): ").append(doc.getReturns().getType().getName()).append(";\n\n")
        }
        for (doc in staticMethods) {
            this.addFunctionDoc(builder, INDENT, doc)
            builder.append(INDENT).append("static fun ").append(doc.getName()).append("(")
            addArgumentsToBuilder(builder, doc.getParameters())
            builder.append("): ").append(doc.getReturns().getType().getName()).append(";\n\n")
        }
        if (builder.endsWith("\n\n")) {
            builder.setLength(builder.length - 1)
        }
        builder.append("}\n\n")
    }

    private fun addArgumentsToBuilder(builder: StringBuilder, args: List<ParameterDoc>) {
        builder.append(args.joinToString(", ") {
            "${it.getName()}: ${it.getAllTypes().joinToString(" | ") { t -> t.getName() }}"
        })
    }

    private fun addFunctionDoc(builder: StringBuilder, indent: String, doc: FunctionDoc) {
        builder.append(indent).append("/*\n")
        var deprecated = false
        for (deprecate in doc.getDeprecated()) {
            builder.append(indent).append(" * ")
            if (!deprecated) {
                builder.append("Deprecated: ")
                deprecated = true
            }
            builder.append(deprecate).append("\n")
        }
        for (desc in doc.getDescription()) {
            builder.append(indent).append(" * ").append(desc).append("\n")
        }
        this.addParameters(builder, indent, doc.getParameters())
        if (doc.hasReturns()) {
            val returns = doc.getReturns()
            builder.append(indent).append(" * ").append("Returns - ").append(returns.getType().getName())
            builder.append(": ").append(returns.getFormattedDescription()).append("\n")
        }
        builder.append(indent).append(" */\n")
    }

    private fun addParameters(builder: StringBuilder, indent: String, params: List<ParameterDoc>) {
        for (param in params) {
            val type = param.getType().getName()
            builder.append(indent).append(" * ").append("Parameter - ")
            builder.append(type).append(" (").append(param.getName()).append("): ")
            builder.append(param.getFormattedDescription()).append("\n")
        }
    }
}