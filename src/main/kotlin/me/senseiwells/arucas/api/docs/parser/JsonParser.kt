package me.senseiwells.arucas.api.docs.parser

import com.google.gson.*
import me.senseiwells.arucas.Arucas
import me.senseiwells.arucas.api.ArucasAPI
import me.senseiwells.arucas.api.docs.FunctionDoc

@Deprecated("This has been replaced by JsonDocVisitor")
open class JsonParser protected constructor(): DocParser() {
    companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create()

        @JvmStatic
        fun of(api: ArucasAPI): JsonParser {
            val parser = JsonParser()
            parser.fromApi(api)
            return parser
        }
    }

    override fun parse(): String {
        return GSON.toJson(this.toJson())
    }

    open fun toJson(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("version", Arucas.VERSION)
        jsonObject.add("extensions", this.getExtensionsAsJson())
        jsonObject.add("classes", this.getClassesAsJson())
        return jsonObject
    }

    private fun getExtensionsAsJson(): JsonElement {
        val allExtensions = JsonObject()
        for (extension in this.extensions) {
            val docs = ParsedExtensionDocs(extension)
            val array = JsonArray()
            for (doc in docs.functionDocs) {
                array.add(getFunctionAsJson(doc))
            }
            allExtensions.add(extension.getName(), array)
        }
        return allExtensions
    }

    private fun getClassesAsJson(): JsonElement {
        val allClasses = JsonObject()
        for (definition in this.definitions) {
            val classObject = JsonObject()
            val docs = ParsedClassDocs(definition)
            val classDoc = docs.classDocOrThrow()
            classObject.addProperty("name", classDoc.name)
            val description = this.arrayToJson(*classDoc.desc)
            var importPath: JsonElement? = JsonNull.INSTANCE
            if (classDoc.importPath.isNotBlank()) {
                importPath = JsonPrimitive(classDoc.importPath)
            }
            classObject.add("desc", description)
            classObject.add("import_path", importPath)
            classObject.addProperty("superclass", definition.superclass().name)
            val staticMembers = JsonArray()
            for (doc in docs.staticFields) {
                val memberObject = JsonObject()
                memberObject.addProperty("name", doc.name)
                memberObject.addProperty("assignable", doc.assignable)
                memberObject.add("desc", this.arrayToJson(*doc.desc))
                memberObject.addProperty("type", doc.type)
                memberObject.add("examples", this.examplesToJson(*doc.examples))
                staticMembers.add(memberObject)
            }
            classObject.add("static_members", staticMembers)
            val constructors = JsonArray()
            for (doc in docs.constructors) {
                val constructor = JsonObject()
                constructor.add("desc", this.arrayToJson(*doc.desc))
                if (doc.params.size % 3 == 0) {
                    if (doc.params.isNotEmpty()) {
                        constructor.add("params", paramsToJson(doc.params))
                    }
                } else {
                    throw IllegalStateException("Invalid parameter documentation: '${doc.params.contentToString()}'")
                }
                constructor.add("examples", this.examplesToJson(*doc.examples))
                constructors.add(constructor)
            }
            classObject.add("constructors", constructors)
            val methods = JsonArray()
            for (doc in docs.methods) {
                methods.add(getFunctionAsJson(doc))
            }
            classObject.add("methods", methods)
            val staticMethods = JsonArray()
            for (doc in docs.staticMethods) {
                staticMethods.add(getFunctionAsJson(doc))
            }
            classObject.add("static_methods", staticMethods)
            allClasses.add(classDoc.name, classObject)
        }
        return allClasses
    }

    private fun getFunctionAsJson(doc: FunctionDoc): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("name", doc.name)
        jsonObject.addProperty("is_arbitrary", doc.isVarArgs)
        // object.addProperty("is_static", doc.isStatic());
        if (doc.deprecated.isNotEmpty()) {
            jsonObject.add("deprecated", this.arrayToJson(*doc.deprecated))
        }
        jsonObject.add("desc", this.arrayToJson(*doc.desc))
        if (doc.params.size % 3 == 0) {
            if (doc.params.isNotEmpty()) {
                jsonObject.add("params", paramsToJson(doc.params))
            }
        } else {
            throw IllegalStateException("Parameters documented incorrectly: '${doc.params}'")
        }
        if (doc.returns.size == 2) {
            val returnObject = JsonObject()
            returnObject.addProperty("type", doc.returns[0])
            returnObject.addProperty("desc", doc.returns[1])
            jsonObject.add("returns", returnObject)
        }
        jsonObject.add("examples", this.examplesToJson(*doc.examples))
        return jsonObject
    }

    private fun paramsToJson(params: Array<String>): JsonArray {
        val allParameters = JsonArray()
        var i = 0
        while (i < params.size) {
            val param = JsonObject()
            val type = params[i++]
            val name = params[i++]
            val desc = params[i++]
            param.addProperty("name", name)
            param.addProperty("type", type)
            param.addProperty("desc", desc)
            allParameters.add(param)
        }
        return allParameters
    }

    private fun arrayToJson(vararg array: String): JsonArray {
        val jsonArray = JsonArray()
        for (string in array) {
            jsonArray.add(string)
        }
        return jsonArray
    }

    private fun examplesToJson(vararg array: String): JsonArray {
        val jsonArray = JsonArray()
        for (string in array) {
            jsonArray.add(string.trimIndent().replace("\t", "    "))
        }
        return jsonArray
    }
}