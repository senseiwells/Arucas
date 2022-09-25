package me.senseiwells.arucas.api.docs.parser

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import me.senseiwells.arucas.api.ArucasAPI

open class SnippetParser protected constructor(): DocParser() {
    companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

        @JvmStatic
        fun of(api: ArucasAPI): SnippetParser {
            return SnippetParser().also { it.fromApi(api) }
        }
    }

    override fun parse(): String {
        return GSON.toJson(this.toJson())
    }

    open fun toJson(): JsonObject {
        return this.addClasses(JsonObject())
    }

    private fun addClasses(json: JsonObject): JsonObject {
        for (definition in this.definitions) {
            this.addClass(ParsedClassDocs(definition), json)
        }
        return json
    }

    private fun addClass(docs: ParsedClassDocs, json: JsonObject) {
        val classDoc = docs.classDocOrThrow()
        val className = classDoc.name

        // Class
        val classJson = JsonObject()
        classJson.addProperty("prefix", className)
        classJson.addProperty("body", className)
        val from = if (classDoc.importPath.isEmpty()) "." else " from ${classDoc.importPath}."
        val description = "The $className class$from\n${classDoc.desc.joinToString("\n")}\n"
        classJson.addProperty("description", description)
        json.add("$className Class", classJson)

        // Static fields
        for (doc in docs.staticFields) {
            val fieldJson = JsonObject()
            val id = "$className.${doc.name}"
            fieldJson.addProperty("prefix", id)
            fieldJson.addProperty("body", id)
            fieldJson.addProperty("description", id + "\n" + doc.desc.joinToString("\n"))
            json.add("Static Field $id", fieldJson)
        }

        // Static methods
        for (doc in docs.staticMethods) {
            val methodJson = JsonObject()
            val (params, paramDesc) = this.joinParameters(doc.params)
            val id = "$className.${doc.name}(${params.joinToString(", ")})"
            val body = "$className.${doc.name}(${params.joinToString(", ") { "\$$it" }})"
            methodJson.addProperty("prefix", id)
            methodJson.addProperty("body", body)
            methodJson.addProperty("description", id + "\n" + doc.desc.joinToString("\n") + paramDesc)
            json.add("Static Method $id", methodJson)
        }

        // Methods
        for (doc in docs.methods) {
            val methodJson = JsonObject()
            val (params, paramDesc) = this.joinParameters(doc.params)
            val trailing = ".${doc.name}(${params.joinToString(", ")})"
            val id = "<$className>$trailing"
            val body = ".${doc.name}(${params.joinToString(", ") { "\$$it" }})"
            methodJson.addProperty("prefix", body)
            methodJson.addProperty("body", body)
            methodJson.addProperty("description", id + "\n" + doc.desc.joinToString("\n") + paramDesc)
            json.add("Method $id", methodJson)
        }
    }

    private fun joinParameters(params: Array<String>): Pair<List<String>, String> {
        if (params.size % 3 != 0) {
            throw IllegalArgumentException("Illegal params: ${params.contentToString()}")
        }

        val paramNames = ArrayList<String>()
        val description = StringBuilder("\n")
        var i = 0
        while (i < params.size) {
            val type = params[i++]
            val name = params[i++]
            val desc = params[i++]

            paramNames.add(name)
            description.append("- Parameter - $type (`$name`): $desc\n")
        }
        return paramNames to description.toString()
    }
}