package me.senseiwells.arucas.api.docs.parser

import me.senseiwells.arucas.api.ArucasExtension
import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.api.docs.FunctionDoc
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.utils.Util

class CodeDocParser : DocParser() {
    companion object {
        private const val INDENT = "    "
        private const val NO_INDENT = ""
    }

    override fun parse(): String {
        return "${this.parseExtensions()}\n\n\n${this.parseClasses()}"
    }

    fun parseExtensions(extensions: Iterable<ArucasExtension> = this.extensions): String {
        val builder = StringBuilder()
        builder.append("/* Natively implemented on the JVM\n")
        builder.append(" * These functions are treated as if\n")
        builder.append(" * they were defined in the global scope.\n")
        builder.append(" * These do not need to be imported\n")
        builder.append(" */\n\n")
        for (extension in extensions) {
            builder.append("/* ").append(extension.getName()).append(" */\n")
            val docs = ParsedExtensionDocs(extension)
            for (doc in docs.functionDocs) {
                val (params, returnType) = addFunctionDoc(builder, NO_INDENT, doc)
                builder.append("fun ").append(doc.name).append("(")
                addArgumentsToBuilder(builder, params)
                builder.append("): ").append(returnType).append(";\n\n")
            }
        }
        return builder.toString()
    }

    fun parseClasses(abstractClassDefinitions: Iterable<ClassDefinition> = this.definitions): String {
        val builder = StringBuilder()
        for (definition in abstractClassDefinitions) {
            builder.append(parseClass(definition)).append("\n\n")
        }
        return builder.toString()
    }

    private fun parseClass(definition: ClassDefinition): String {
        val builder = StringBuilder()
        val docs = ParsedClassDocs(definition)
        val classDoc = docs.classDocOrThrow()
        builder.append("/* Native, implemented in ${classDoc.language}\n")
        for (desc in classDoc.desc) {
            builder.append(" * ").append(desc).append("\n")
        }
        if (classDoc.importPath.isEmpty()) {
            builder.append(" * This class does not need to be imported.")
        } else {
            builder.append(" * Import with 'import ").append(classDoc.name)
            builder.append(" from ").append(classDoc.importPath).append("'")
        }
        builder.append("\n */\n")
        builder.append("class ")
        builder.append(classDoc.name)

        val superclassDoc = classDoc.superclass.java.getAnnotation(ClassDoc::class.java)
        superclassDoc ?: throw IllegalStateException("Class '${classDoc.superclass}' was not documented")
        if (superclassDoc.name != Util.Types.OBJECT) {
            builder.append(": ").append(superclassDoc.name)
        }

        builder.append(" {\n")
        var hadVars = false
        for (doc in docs.staticFields) {
            builder.append(INDENT).append("/*\n")
            for (desc in doc.desc) {
                builder.append(INDENT).append(" * ").append(desc).append("\n")
            }
            builder.append(INDENT).append(" * This field is ").append(if (doc.assignable) "" else "not ").append("assignable\n")
            builder.append(INDENT).append(" */\n")
            builder.append(INDENT).append("static var ").append(doc.name).append(": ").append(doc.type).append(";\n")
            hadVars = true
        }
        if (hadVars) {
            builder.append("\n")
        }
        for (doc in docs.constructors) {
            builder.append(INDENT).append("/*\n")
            for (desc in doc.desc) {
                builder.append(INDENT).append(" * ").append(desc).append("\n")
            }
            val parameterNames = addParameters(builder, INDENT, doc.params)
            builder.append(INDENT).append(" */\n")
            builder.append(INDENT).append(classDoc.name).append("(")
            addArgumentsToBuilder(builder, parameterNames)
            builder.append(");\n\n")
        }
        for (doc in docs.methods) {
            val (params, returnType) = addFunctionDoc(builder, INDENT, doc)
            builder.append(INDENT).append("fun ").append(doc.name).append("(")
            addArgumentsToBuilder(builder, params)
            builder.append("): ").append(returnType).append(";\n\n")
        }
        for (doc in docs.staticMethods) {
            val (params, returnType) = addFunctionDoc(builder, INDENT, doc)
            builder.append(INDENT).append("static fun ").append(doc.name).append("(")
            addArgumentsToBuilder(builder, params)
            builder.append("): ").append(returnType).append(";\n\n")
        }
        var classAsString = builder.toString()
        if (classAsString.endsWith("\n\n")) {
            classAsString = classAsString.substring(0, classAsString.length - 1)
        }
        return "$classAsString}"
    }

    private fun addArgumentsToBuilder(builder: StringBuilder, args: List<String>) {
        val stringIterator = args.iterator()
        while (stringIterator.hasNext()) {
            val argName = stringIterator.next()
            builder.append(argName)
            if (stringIterator.hasNext()) {
                builder.append(", ")
            }
        }
    }

    private fun addFunctionDoc(builder: StringBuilder, indent: String, doc: FunctionDoc): Pair<List<String>, String> {
        builder.append(indent).append("/*\n")
        var deprecated = false
        for (deprecate in doc.deprecated) {
            builder.append(indent).append(" * ")
            if (!deprecated) {
                builder.append("Deprecated: ")
                deprecated = true
            }
            builder.append(deprecate).append("\n")
        }
        for (desc in doc.desc) {
            builder.append(indent).append(" * ").append(desc).append("\n")
        }
        val parameterNames = addParameters(builder, indent, doc.params)
        val returns = doc.returns
        val returnType = if (returns.size == 2) {
            val returnType = returns[0]
            builder.append(indent).append(" * ").append("Returns - ").append(returnType)
            builder.append(": ").append(returns[1]).append("\n")
            returnType
        } else {
            Util.Types.NULL
        }
        builder.append(indent).append(" */\n")
        return parameterNames to returnType
    }

    private fun addParameters(builder: StringBuilder, indent: String, params: Array<String>): List<String> {
        if (params.size % 3 == 0) {
            val parameterNames: MutableList<String> = ArrayList()
            var i = 0
            while (i < params.size) {
                val type = params[i]
                val name = params[i + 1]
                val desc = params[i + 2]
                builder.append(indent).append(" * ").append("Parameter - ")
                builder.append(type).append(" (").append(name).append("): ")
                builder.append(desc).append("\n")
                parameterNames.add("$name: $type")
                i += 3
            }
            return parameterNames
        }
        throw IllegalStateException("Parameters had invalid size: '${params.contentToString()}'")
    }
}
