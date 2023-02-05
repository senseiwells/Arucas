package me.senseiwells.arucas.api.docs.visitor.impl

import com.google.gson.*
import me.senseiwells.arucas.api.docs.visitor.*
import me.senseiwells.arucas.core.Arucas

class JsonDocVisitor: ArucasDocVisitor() {
    companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create()
    }

    private val root = JsonObject()
    private val extensions = JsonObject()
    private val classes = JsonObject()

    init {
        this.root.addProperty("version", Arucas.VERSION)
        this.root.add("extensions", this.extensions)
        this.root.add("classes", this.classes)
    }

    fun getJson(): String {
        return GSON.toJson(this.root)
    }

    override fun visitExtension(extensionDoc: ExtensionDoc, functions: List<FunctionDoc>) {
        val array = JsonArray()
        for (function in functions) {
            array.add(this.getFunctionAsJson(function))
        }
        this.extensions.add(extensionDoc.getName(), array)
    }

    override fun visitClass(classDoc: ClassDoc, fields: List<FieldDoc>, constructors: List<ConstructorDoc>, methods: List<FunctionDoc>, staticMethods: List<FunctionDoc>) {
        this.classes.add(classDoc.getName(), this.getClassAsJson(classDoc, fields, constructors, methods, staticMethods))
    }

    private fun getClassAsJson(doc: ClassDoc, fields: List<FieldDoc>, constructors: List<ConstructorDoc>, methods: List<FunctionDoc>, staticMethods: List<FunctionDoc>): JsonElement {
        val classObject = JsonObject()
        classObject.addProperty("name", doc.getName())
        val description = this.arrayToJson(doc.getDescription())
        var importPath: JsonElement? = JsonNull.INSTANCE
        if (doc.isImportable()) {
            importPath = JsonPrimitive(doc.getImportPath())
        }
        classObject.add("desc", description)
        classObject.add("import_path", importPath)
        classObject.addProperty("superclass", doc.getSuperclass().getName())
        val staticMembers = JsonArray()
        for (fieldDoc in fields) {
            val memberObject = JsonObject()
            memberObject.addProperty("name", fieldDoc.getName())
            memberObject.addProperty("assignable", fieldDoc.isAssignable())
            memberObject.add("desc", this.arrayToJson(fieldDoc.getDescription()))
            memberObject.addProperty("type", fieldDoc.getType().getName())
            memberObject.add("examples", this.examplesToJson(fieldDoc.getExamples()))
            staticMembers.add(memberObject)
        }
        classObject.add("static_members", staticMembers)
        val constructorArray = JsonArray()
        for (constructorDoc in constructors) {
            val constructor = JsonObject()
            constructor.add("desc", this.arrayToJson(constructorDoc.getDescription()))
            if (constructorDoc.hasParameters()) {
                constructor.add("params", this.paramsToJson(constructorDoc.getParameters()))
            }
            constructor.add("examples", this.examplesToJson(constructorDoc.getExamples()))
            constructorArray.add(constructor)
        }
        classObject.add("constructors", constructorArray)
        val methodArray = JsonArray()
        for (methodDoc in methods) {
            methodArray.add(this.getFunctionAsJson(methodDoc))
        }
        classObject.add("methods", methodArray)
        val staticMethodArray = JsonArray()
        for (staticMethodDoc in staticMethods) {
            staticMethodArray.add(this.getFunctionAsJson(staticMethodDoc))
        }
        classObject.add("static_methods", staticMethodArray)
        return classObject
    }

    private fun getFunctionAsJson(doc: FunctionDoc): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("name", doc.getName())
        jsonObject.addProperty("is_arbitrary", doc.isVarArgs())
        // object.addProperty("is_static", doc.isStatic());
        if (doc.isDeprecated()) {
            jsonObject.add("deprecated", this.arrayToJson(doc.getDeprecated()))
        }
        jsonObject.add("desc", this.arrayToJson(doc.getDescription()))
        if (doc.hasParameters()) {
            jsonObject.add("params", this.paramsToJson(doc.getParameters()))
        }
        if (doc.hasReturns()) {
            val returns = doc.getReturns()
            val returnObject = JsonObject()
            returnObject.addProperty("type", returns.getType().getName())
            returnObject.addProperty("desc", returns.getFormattedDescription())
            jsonObject.add("returns", returnObject)
        }
        jsonObject.add("examples", this.examplesToJson(doc.getExamples()))
        return jsonObject
    }

    private fun paramsToJson(params: List<ParameterDoc>): JsonArray {
        val allParameters = JsonArray()
        for (param in params) {
            val parameterObject = JsonObject()
            parameterObject.addProperty("name", param.getName())
            parameterObject.addProperty("type", param.getType().getName())
            parameterObject.addProperty("desc", param.getFormattedDescription())
            allParameters.add(parameterObject)
        }
        return allParameters
    }

    private fun examplesToJson(array: Array<String>): JsonArray {
        val jsonArray = JsonArray()
        for (string in array) {
            jsonArray.add(string.trimIndent().replace("\t", "    "))
        }
        return jsonArray
    }

    private fun arrayToJson(array: Array<String>): JsonArray {
        val jsonArray = JsonArray()
        for (string in array) {
            jsonArray.add(string)
        }
        return jsonArray
    }
}