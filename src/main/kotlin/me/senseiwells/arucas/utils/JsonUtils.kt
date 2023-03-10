package me.senseiwells.arucas.utils

import com.google.gson.*
import me.senseiwells.arucas.builtin.*
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.utils.impl.ArucasList
import me.senseiwells.arucas.utils.impl.ArucasMap
import me.senseiwells.arucas.utils.impl.ArucasOrderedMap

/**
 * Utility object for converting JSON
 * to and from Arucas objects.
 */
object JsonUtils {
    @Suppress("PropertyName")
    @JvmField
    val GSON: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create()

    /**
     * Converts a [JsonElement] to a String.
     *
     * @param element the json element to convert.
     * @return the string of the json element.
     */
    fun serialize(element: JsonElement): String {
        return GSON.toJson(element)
    }

    /**
     * Converts a [JsonElement] into a [ClassInstance].
     *
     * @param interpreter the interpreter to create the class instance.
     * @param element the element to convert.
     * @return the class instance.
     */
    fun toInstance(interpreter: Interpreter, element: JsonElement): ClassInstance {
        return when {
            element.isJsonPrimitive -> {
                val primitive = element.asJsonPrimitive
                when {
                    primitive.isBoolean -> interpreter.createBool(primitive.asBoolean)
                    primitive.isNumber -> interpreter.create(NumberDef::class, primitive.asDouble)
                    else -> interpreter.create(StringDef::class, primitive.asString)
                }
            }
            element.isJsonArray -> this.toList(interpreter, element.asJsonArray)
            element.isJsonObject -> this.toMap(interpreter, element.asJsonObject)
            else -> interpreter.getNull()
        }
    }

    private fun toList(interpreter: Interpreter, array: JsonArray): ClassInstance {
        val list = ArucasList()
        for (element in array) {
            list.add(this.toInstance(interpreter, element))
        }
        return interpreter.create(ListDef::class, list)
    }

    private fun toMap(interpreter: Interpreter, element: JsonObject): ClassInstance {
        val map = ArucasOrderedMap()
        element.entrySet().forEach { (s, e) ->
            map.put(interpreter, interpreter.create(StringDef::class, s), this.toInstance(interpreter, e))
        }
        return interpreter.create(MapDef::class, map)
    }

    /**
     * Converts a [ClassInstance] into a [JsonElement].
     *
     * @param interpreter the interpreter to create the element.
     * @param instance the instance to convert.
     * @param depth the depth at which to recursively create elements.
     * @return the json element.
     */
    fun fromInstance(interpreter: Interpreter, instance: ClassInstance, depth: Int): JsonElement {
        if (depth < 0) {
            runtimeError("JSON serialisation went too deep")
        }
        if (instance === interpreter.getNull()) {
            return JsonNull.INSTANCE
        }
        instance.getPrimitive(BooleanDef::class)?.let { return JsonPrimitive(it) }
        instance.getPrimitive(NumberDef::class)?.let { return JsonPrimitive(it) }
        instance.getPrimitive(ListDef::class)?.let { return fromList(interpreter, it, depth - 1) }
        instance.getPrimitive(MapDef::class)?.let { return fromMap(interpreter, it, depth - 1) }
        return JsonPrimitive(instance.toString(interpreter))
    }

    private fun fromList(interpreter: Interpreter, list: ArucasList, depth: Int): JsonArray {
        return JsonArray().also {
            list.toArray().forEach { e -> it.add(this.fromInstance(interpreter, e, depth)) }
        }
    }

    private fun fromMap(interpreter: Interpreter, map: ArucasMap, depth: Int): JsonObject {
        return JsonObject().also {
            map.pairSet().forEach { (k, v) -> it.add(k.toString(interpreter), this.fromInstance(interpreter, v, depth)) }
        }
    }
}