package me.senseiwells.arucas.utils

import com.google.gson.*
import me.senseiwells.arucas.builtin.*
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.RuntimeError
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.impl.ArucasIterable
import me.senseiwells.arucas.utils.impl.ArucasList
import me.senseiwells.arucas.utils.impl.ArucasMap
import me.senseiwells.arucas.utils.impl.ArucasOrderedMap
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

object Util {
    fun nanosToString(nanos: Long): String {
        var unit = "μs"
        var time = nanos / 1_000
        if (time > 5_000) {
            unit = "ms"
            time /= 1_000
        }
        if (time > 10_000) {
            unit = "s"
            time /= 1_000
        }
        return "$time$unit"
    }

    object Collection {
        infix fun <A, B, C> Pair<A, B>.to(that: C): Triple<A, B, C> = Triple(this.first, this.second, that)

        fun kotlin.collections.Collection<ClassDefinition>.sort() = this.sortedWith { a, b -> a.name.compareTo(b.name) }

        fun <T> emptyIterator(): Iterator<T> {
            return object: Iterator<T> {
                override fun hasNext() = false

                override fun next() = throw NoSuchElementException("Empty iterator has no values")
            }
        }

        fun rangeIterable(interpreter: Interpreter, end: Double, start: Double = 0.0, step: Double = 1.0): ArucasIterable {
            return object: ArucasIterable {
                override fun iterator(): Iterator<ClassInstance> {
                    return RangeIterator(start, end, step) { interpreter.create(NumberDef::class, it) }
                }
            }
        }

        private class RangeIterator(
            var current: Double,
            val end: Double,
            val step: Double,
            val converter: (Double) -> ClassInstance
        ): Iterator<ClassInstance> {
            override fun hasNext(): Boolean {
                return if (this.step > 0) this.current < this.end else this.end < this.current
            }

            override fun next(): ClassInstance {
                val next = this.converter(this.current)
                this.current += this.step
                return next
            }
        }
    }

    object Exception {
        fun <T> traceable(trace: Trace, function: () -> T): T {
            return try {
                function()
            } catch (e: RuntimeError) {
                if (e.topTrace == null || e.topTrace is InternalTrace) {
                    e.topTrace = trace
                }
                throw e
            }
        }

        fun <T> catchAsNull(function: () -> T): T? {
            return try {
                function()
            } catch (e: kotlin.Exception) {
                null
            }
        }
    }

    object File {
        fun Path.ensureParentExists(): Path {
            this.parent.ensureExists()
            return this
        }

        fun Path.ensureExists(): Path {
            if (!Files.exists(this)) {
                Files.createDirectories(this)
            }
            return this
        }
    }

    object Json {
        @JvmStatic
        val GSON: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create()

        fun serialize(element: JsonElement): String {
            return GSON.toJson(element)
        }

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

    object Network {
        fun getStringFromUrl(url: String): String? {
            return try {
                val input = URL(url).openStream()
                input.bufferedReader().use { it.readText() }
            } catch (exception: IOException) {
                null
            }
        }

        fun downloadFile(url: String, file: java.io.File): Boolean {
            return try {
                FileOutputStream(file).use { output ->
                    URL(url).openStream().use { input ->
                        input.copyTo(output)
                    }
                }
                true
            } catch (e: IOException) {
                false
            }
        }
    }

    @Suppress("UNUSED")
    enum class Language {
        Java, Kotlin;

        override fun toString(): String {
            return this.name
        }
    }

    object Types {
        const val BOOLEAN = "Boolean"
        const val COLLECTION = "Collection"
        const val ENUM = "Enum"
        const val ERROR = "Error"
        const val FILE = "File"
        const val FUNCTION = "Function"
        const val ITERABLE = "Iterator"
        const val JAVA = "Java"
        const val JAVA_CLASS = "JavaClass"
        const val JSON = "Json"
        const val LIST = "List"
        const val MAP = "Map"
        const val MATH = "Math"
        const val NETWORK = "Network"
        const val NULL = "Null"
        const val NUMBER = "Number"
        const val OBJECT = "Object"
        const val SET = "Set"
        const val STRING = "String"
        const val THREAD = "Thread"
        const val TYPE = "Type"
    }
}