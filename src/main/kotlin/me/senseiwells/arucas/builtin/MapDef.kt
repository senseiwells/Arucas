package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.*
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.functions.builtin.Arguments
import me.senseiwells.arucas.functions.builtin.BuiltInFunction
import me.senseiwells.arucas.functions.builtin.ConstructorFunction
import me.senseiwells.arucas.functions.builtin.MemberFunction
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.utils.*
import me.senseiwells.arucas.utils.impl.ArucasList
import me.senseiwells.arucas.utils.impl.ArucasMap
import me.senseiwells.arucas.utils.impl.ArucasOrderedMap
import me.senseiwells.arucas.utils.misc.Types.MAP

@ClassDoc(
    name = MAP,
    desc = [
        "This class is used to create a map of objects, using keys and values.",
        "This class cannot be directly instantiated, but can be extended to create a map of your own type."
    ],
    superclass = CollectionDef::class
)
class MapDef(interpreter: Interpreter): CreatableDefinition<ArucasMap>(MAP, interpreter) {
    override fun canConstructDirectly() = false

    override fun superclass(): PrimitiveDefinition<in ArucasMap> {
        return this.getPrimitiveDef(CollectionDef::class)
    }

    override fun bracketAccess(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, trace: LocatableTrace): ClassInstance {
        return instance.asPrimitive(this).get(interpreter, index) ?: interpreter.getNull()
    }

    override fun bracketAssign(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, assignee: ClassInstance, trace: LocatableTrace): ClassInstance {
        return instance.asPrimitive(this).put(interpreter, index, assignee) ?: interpreter.getNull()
    }

    override fun equals(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Boolean {
        val otherMap = other.getPrimitive(this) ?: return false
        return instance.asPrimitive(this).equals(interpreter, otherMap)
    }

    override fun hashCode(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Int {
        return instance.asPrimitive(this).hashCode(interpreter)
    }

    override fun copy(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): ClassInstance {
        return this.create(ArucasOrderedMap(interpreter, instance.asPrimitive(this)))
    }

    override fun defineConstructors(): List<ConstructorFunction> {
        return listOf(
            ConstructorFunction.of(this::construct)
        )
    }

    @ConstructorDoc(
        desc = ["This creates an empty map, this cannot be called directly, only from child classes."],
        examples = [
            """
            class ChildMap: Map {
                ChildMap(): super();
            }
            """
        ]
    )
    private fun construct(arguments: Arguments) {
        val instance = arguments.next()
        instance.setPrimitive(this, ArucasOrderedMap())
    }

    override fun defineStaticMethods(): List<BuiltInFunction> {
        return listOf(
            BuiltInFunction.of("unordered", this::unordered)
        )
    }

    @FunctionDoc(
        isStatic = true,
        name = "unordered",
        desc = ["This function allows you to create an unordered map."],
        returns = ReturnDoc(MapDef::class, ["An unordered map."]),
        examples = ["Map.unordered();"]
    )
    @Suppress("UNUSED_PARAMETER")
    private fun unordered(arguments: Arguments): ClassInstance {
        return this.create(ArucasMap())
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("get", 1, this::get),
            MemberFunction.of("put", 2, this::put),
            MemberFunction.of("putAll", 1, this::putAll),
            MemberFunction.of("putIfAbsent", 2, this::putIfAbsent),
            MemberFunction.of("remove", 1, this::remove),
            MemberFunction.of("clear", 0, this::clear),
            MemberFunction.of("containsKey", 1, this::containsKey),
            MemberFunction.of("containsValue", 1, this::containsValue),
            MemberFunction.of("getKeys", this::getKeys),
            MemberFunction.of("getValues", this::getValues),
            MemberFunction.of("map", this::map)
        )
    }

    @FunctionDoc(
        name = "get",
        desc = ["This allows you to get the value of a key in the map."],
        params = [ParameterDoc(ObjectDef::class, "key", ["The key you want to get the value of."])],
        returns = ReturnDoc(ObjectDef::class, ["The value of the key, will return null if non-existent."]),
        examples = ["{'key': 'value'}.get('key'); // 'value'"]
    )
    private fun get(arguments: Arguments): ClassInstance {
        val instance = arguments.nextMap()
        val key = arguments.next()
        return instance.bracketAccess(key, arguments.interpreter)
    }

    @FunctionDoc(
        name = "put",
        desc = ["This allows you to put a key and value in the map."],
        params = [
            ParameterDoc(ObjectDef::class, "key", ["The key you want to put."]),
            ParameterDoc(ObjectDef::class, "value", ["The value you want to put."])
        ],
        returns = ReturnDoc(ObjectDef::class, ["The previous value associated with the key, null if none."]),
        examples = ["{'key': 'value'}.put('key2', 'value2'); // null"]
    )
    private fun put(arguments: Arguments): ClassInstance {
        val instance = arguments.nextMap()
        val key = arguments.next()
        val value = arguments.next()
        return instance.bracketAssign(key, arguments.interpreter, value)
    }

    @FunctionDoc(
        name = "putAll",
        desc = ["This allows you to put all the keys and values of another map into this map."],
        params = [ParameterDoc(MapDef::class, "anotherMap", ["The map you want to merge into this map."])],
        examples = ["(map = {'key': 'value'}).putAll({'key2': 'value2'}); // map = {'key': 'value', 'key2': 'value2'}"]
    )
    private fun putAll(arguments: Arguments) {
        val instance = arguments.nextPrimitive(this)
        instance.putAll(arguments.interpreter, arguments.nextPrimitive(this))
    }

    @FunctionDoc(
        name = "putIfAbsent",
        desc = ["This allows you to put a key and value in the map if it doesn't exist."],
        params = [
            ParameterDoc(ObjectDef::class, "key", ["The key you want to put."]),
            ParameterDoc(ObjectDef::class, "value", ["The value you want to put."])
        ],
        examples = ["(map = {'key': 'value'}).putIfAbsent('key2', 'value2'); // map = {'key': 'value', 'key2': 'value2'}"]
    )
    private fun putIfAbsent(arguments: Arguments) {
        val instance = arguments.nextPrimitive(this)
        val key = arguments.next()
        val value = arguments.next()
        instance.putIfAbsent(arguments.interpreter, key, value)
    }

    @FunctionDoc(
        name = "remove",
        desc = ["This allows you to remove a key and its value from the map."],
        params = [ParameterDoc(ObjectDef::class, "key", ["The key you want to remove."])],
        returns = ReturnDoc(ObjectDef::class, ["The value associated with the key, null if none."]),
        examples = ["{'key': 'value'}.remove('key'); // 'value'"]
    )
    private fun remove(arguments: Arguments): ClassInstance? {
        val instance = arguments.nextPrimitive(this)
        return instance.remove(arguments.interpreter, arguments.next())
    }

    @FunctionDoc(
        name = "clear",
        desc = ["This allows you to clear the map of all the keys and values."],
        examples = ["(map = {'key': 'value'}).clear(); // map = {}"]
    )
    private fun clear(arguments: Arguments) {
        arguments.nextPrimitive(this).clear()
    }

    @FunctionDoc(
        name = "containsKey",
        desc = ["This allows you to check if the map contains a specific key."],
        params = [ParameterDoc(ObjectDef::class, "key", ["The key you want to check."])],
        returns = ReturnDoc(BooleanDef::class, ["True if the map contains the key, false otherwise."]),
        examples = ["{'key': 'value'}.containsKey('key'); // true"]
    )
    private fun containsKey(arguments: Arguments): Boolean {
        val instance = arguments.nextPrimitive(this)
        val key = arguments.next()
        return instance.containsKey(arguments.interpreter, key)
    }

    @FunctionDoc(
        name = "containsValue",
        desc = ["This allows you to check if the map contains a specific value."],
        params = [ParameterDoc(ObjectDef::class, "value", ["The value you want to check."])],
        returns = ReturnDoc(BooleanDef::class, ["True if the map contains the value, false otherwise."]),
        examples = ["{'key': 'value'}.containsValue('foo'); // false"]
    )
    private fun containsValue(arguments: Arguments): Boolean {
        val instance = arguments.nextPrimitive(this)
        val value = arguments.next()
        return instance.containsValue(arguments.interpreter, value)
    }

    @FunctionDoc(
        name = "getKeys",
        desc = ["This allows you to get the keys in the map."],
        returns = ReturnDoc(ListDef::class, ["A complete list of all the keys."]),
        examples = ["{'key': 'value', 'key2': 'value2'}.getKeys(); // ['key', 'key2']"]
    )
    private fun getKeys(arguments: Arguments): ArucasList {
        return arguments.nextPrimitive(this).keys()
    }

    @FunctionDoc(
        name = "getValues",
        desc = ["This allows you to get the values in the map."],
        returns = ReturnDoc(ListDef::class, ["A complete list of all the values."]),
        examples = ["{'key': 'value', 'key2': 'value2'}.getValues(); // ['value', 'value2']"]
    )
    private fun getValues(arguments: Arguments): ArucasList {
        return arguments.nextPrimitive(this).values()
    }

    @FunctionDoc(
        name = "map",
        desc = ["This allows you to map the values in the map and returns a new map."],
        params = [ParameterDoc(FunctionDef::class, "remapper", ["The function you want to map the values with."])],
        returns = ReturnDoc(MapDef::class, ["A new map with the mapped values."]),
        examples = [
            """
            map = {'key': 'value', 'key2': 'value2'}
            map.map(fun(k, v) {
                return [v, k];
            });
            // map = {'value': 'key', 'value2': 'key2'}
            """
        ]
    )
    private fun map(arguments: Arguments): ArucasMap {
        val instance = arguments.nextPrimitive(this)
        val function = arguments.nextFunction()
        val remappedMap = ArucasOrderedMap()
        val interpreter = arguments.interpreter
        for ((key, value) in instance.pairSet()) {
            val returnValue = interpreter.call(function, listOf(key, value))
            returnValue.getPrimitive(ListDef::class)?.let {
                val size = returnValue.callMemberPrimitive(interpreter, "size", listOf(), NumberDef::class).toInt()
                if (size == 2) it else null
            } ?: runtimeError("'<Map>'.map(remapper) must return a list with 2 elements")
            val newKey = returnValue.bracketAccess(interpreter.create(NumberDef::class, 0.0), interpreter)
            val newValue = returnValue.bracketAccess(interpreter.create(NumberDef::class, 1.0), interpreter)
            remappedMap.put(interpreter, newKey, newValue)
        }
        return remappedMap
    }
}