package me.senseiwells.arucas.api.docs.visitor.impl

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import me.senseiwells.arucas.api.docs.visitor.*

class VSCSnippetDocVisitor: ArucasDocVisitor() {
    companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create()
    }

    private val root = JsonObject()

    fun getJson(): String {
        return GSON.toJson(this.root)
    }

    override fun visitExtensionFunction(extensionDoc: ExtensionDoc, functionDoc: FunctionDoc) {
        val methodJson = JsonObject()
        val (params, paramDesc) = this.joinParameters(functionDoc.getParameters())
        val id = "${functionDoc.getName()}(${params.joinToString(", ")})"
        val body = "${functionDoc.getName()}(${params.joinToString(", ") { "\$$it" }})"
        methodJson.addProperty("prefix", body)
        methodJson.addProperty("body", body)
        methodJson.addProperty("description", id + "\n" + functionDoc.getFormattedDescription("\n") + paramDesc)
        this.root.add("Function $id", methodJson)
    }

    override fun visitClass(classDoc: ClassDoc) {
        val classJson = JsonObject()
        classJson.addProperty("prefix", classDoc.getName())
        classJson.addProperty("body", classDoc.getName())
        val from = if (!classDoc.isImportable()) "." else " from ${classDoc.getImportPath()}."
        val description = "The ${classDoc.getName()} class$from\n${classDoc.getFormattedDescription("\n")}\n"
        classJson.addProperty("description", description)
        this.root.add("${classDoc.getName()} Class", classJson)
    }

    override fun visitStaticField(classDoc: ClassDoc, fieldDoc: FieldDoc) {
        val fieldJson = JsonObject()
        val id = "${classDoc.getName()}.${fieldDoc.getName()}"
        fieldJson.addProperty("prefix", id)
        fieldJson.addProperty("body", id)
        fieldJson.addProperty("description", id + "\n" + fieldDoc.getFormattedDescription("\n"))
        this.root.add("Static Field $id", fieldJson)
    }

    override fun visitConstructor(classDoc: ClassDoc, constructorDoc: ConstructorDoc) {
        val methodJson = JsonObject()
        val (params, paramDesc) = this.joinParameters(constructorDoc.getParameters())
        val id = "new ${classDoc.getName()}(${params.joinToString(", ")})"
        val body = "new ${classDoc.getName()}(${params.joinToString(", ") { "\$$it" }})"
        methodJson.addProperty("prefix", body)
        methodJson.addProperty("body", body)
        methodJson.addProperty("description", id + "\n" + constructorDoc.getFormattedDescription("\n") + paramDesc)
        this.root.add("Constructor $id", methodJson)
    }

    override fun visitMethod(classDoc: ClassDoc, functionDoc: FunctionDoc) {
        val methodJson = JsonObject()
        val (params, paramDesc) = this.joinParameters(functionDoc.getParameters())
        val trailing = ".${functionDoc.getName()}(${params.joinToString(", ")})"
        val id = "<${classDoc.getName()}>$trailing"
        val body = ".${functionDoc.getName()}(${params.joinToString(", ") { "\$$it" }})"
        methodJson.addProperty("prefix", body)
        methodJson.addProperty("body", body)
        methodJson.addProperty("description", id + "\n" + functionDoc.getFormattedDescription("\n") + paramDesc)
        this.root.add("Method $id", methodJson)
    }

    override fun visitStaticMethod(classDoc: ClassDoc, functionDoc: FunctionDoc) {
        val methodJson = JsonObject()
        val (params, paramDesc) = this.joinParameters(functionDoc.getParameters())
        val id = "${classDoc.getName()}.${functionDoc.getName()}(${params.joinToString(", ")})"
        val body = "${classDoc.getName()}.${functionDoc.getName()}(${params.joinToString(", ") { "\$$it" }})"
        methodJson.addProperty("prefix", id)
        methodJson.addProperty("body", body)
        methodJson.addProperty("description", id + "\n" + functionDoc.getFormattedDescription("\n") + paramDesc)
        this.root.add("Static Method $id", methodJson)
    }

    private fun joinParameters(params: List<ParameterDoc>): Pair<List<String>, String> {
        val paramNames = ArrayList<String>()
        val description = StringBuilder("\n")
        for (param in params) {
            paramNames.add(param.getName())
            val parameter = "- Parameter - ${param.getAllTypes().joinToString(" | ") { it.getName() }} (`${param.getName()}"
            description.append("$parameter${if (param.isVarargs()) "..." else ""}`): ${param.getFormattedDescription()}\n")
        }
        return paramNames to description.toString()
    }
}