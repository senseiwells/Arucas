package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.api.docs.ConstructorDoc
import me.senseiwells.arucas.api.docs.FunctionDoc
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.*
import me.senseiwells.arucas.utils.Util.Types.BOOLEAN
import me.senseiwells.arucas.utils.Util.Types.COLLECTION
import me.senseiwells.arucas.utils.Util.Types.FUNCTION
import me.senseiwells.arucas.utils.Util.Types.LIST
import me.senseiwells.arucas.utils.Util.Types.OBJECT
import me.senseiwells.arucas.utils.Util.Types.SET
import me.senseiwells.arucas.utils.impl.ArucasList
import me.senseiwells.arucas.utils.impl.ArucasMap
import me.senseiwells.arucas.utils.impl.ArucasSet

@ClassDoc(
    name = SET,
    desc = [
        "Sets are collections of unique values. Similar to maps, without the values.",
        "An instance of the class can be created by using `Set.of(values...)`"
    ],
    superclass = CollectionDef::class
)
class SetDef(interpreter: Interpreter): CreatableDefinition<ArucasSet>(SET, interpreter) {
    override fun superclass() = this.getPrimitiveDef(CollectionDef::class)

    override fun equals(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Boolean {
        val otherSet = other.getPrimitive(this) ?: return false
        return instance.asPrimitive(this).equals(interpreter, otherSet)
    }

    override fun hashCode(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Int {
        return instance.asPrimitive(this).hashCode(interpreter)
    }

    override fun defineConstructors(): List<ConstructorFunction> {
        return listOf(
            ConstructorFunction.of(this::construct)
        )
    }

    @ConstructorDoc(
        desc = ["This creates an empty set"],
        examples = ["new Set();"]
    )
    private fun construct(arguments: Arguments) {
        val instance = arguments.next()
        instance.setPrimitive(this, ArucasSet())
    }

    override fun defineStaticMethods(): List<BuiltInFunction> {
        return listOf(
            BuiltInFunction.arb("of", this::of),
            BuiltInFunction.of("unordered", this::unordered)
        )
    }

    @FunctionDoc(
        isVarArgs = true,
        isStatic = true,
        name = "of",
        desc = ["This allows you to create a set with an arbitrary number of values"],
        params = [OBJECT, "values...", "the values you want to add to the set"],
        returns = [SET, "the set you created"],
        examples = ["Set.of('object', 81, 96, 'case');"]
    )
    private fun of(arguments: Arguments): ClassInstance {
        val set = ArucasSet()
        set.addAll(arguments.interpreter, arguments.arguments)
        return this.create(set)
    }

    @FunctionDoc(
        isStatic = true,
        name = "unordered",
        desc = ["This creates an unordered set"],
        returns = [SET, "the unordered set"],
        examples = ["Set.unordered();"]
    )
    @Suppress("UNUSED_PARAMETER")
    private fun unordered(arguments: Arguments): ClassInstance {
        return this.create(ArucasSet(ArucasMap()))
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("add", 1, this::add),
            MemberFunction.of("get", 1, this::get),
            MemberFunction.of("remove", 1, this::remove),
            MemberFunction.of("addAll", 1, this::addAll),
            MemberFunction.of("removeAll", 1, this::removeAll),
            MemberFunction.of("contains", 1, this::contains),
            MemberFunction.of("containsAll", 1, this::containsAll),
            MemberFunction.of("clear", this::clear),
            MemberFunction.of("toList", this::toList),
            MemberFunction.of("filter", 1, this::filter),
            MemberFunction.of("map", 1, this::map),
            MemberFunction.of("reduce", 1, this::reduce)
        )
    }

    @FunctionDoc(
        name = "add",
        desc = ["This allows you to add a value to the set"],
        params = [OBJECT, "value", "the value you want to add to the set"],
        returns = [BOOLEAN, "whether the value was successfully added to the set"],
        examples = ["Set.of().add('object');"]
    )
    private fun add(arguments: Arguments): Boolean {
        val instance = arguments.nextPrimitive(this)
        return instance.add(arguments.interpreter, arguments.next())
    }

    @FunctionDoc(
        name = "get",
        desc = [
            "This allows you to get a value from in the set.",
            "The reason this might be useful is if you want to retrieve something",
            "from the set that will have the same hashcode but be in a different state",
            "as the value you are passing in"
        ],
        params = [OBJECT, "value", "the value you want to get from the set"],
        returns = [OBJECT, "the value you wanted to get, null if it wasn't in the set"],
        examples = ["Set.of('object').get('object');"]
    )
    private operator fun get(arguments: Arguments): ClassInstance {
        val instance = arguments.nextPrimitive(this)
        return instance.get(arguments.interpreter, arguments.next()) ?: arguments.interpreter.getNull()
    }

    @FunctionDoc(
        name = "remove",
        desc = ["This allows you to remove a value from the set"],
        params = [OBJECT, "value", "the value you want to remove from the set"],
        returns = [BOOLEAN, "whether the value was removed from the set"],
        examples = ["Set.of('object').remove('object');"]
    )
    private fun remove(arguments: Arguments): Boolean {
        val instance = arguments.nextPrimitive(this)
        return instance.remove(arguments.interpreter, arguments.next())
    }

    @FunctionDoc(
        name = "removeAll",
        desc = ["This allows you to remove all values in a collection from the set"],
        params = [COLLECTION, "value", "the values you want to remove from the set"],
        returns = [SET, "the set with the values removed"],
        examples = ["Set.of('object', 'object').removeAll(Set.of('object'));"]
    )
    private fun removeAll(arguments: Arguments): ClassInstance {
        val instance = arguments.nextSet()
        val collection = arguments.nextPrimitive(CollectionDef::class).asCollection()
        instance.asPrimitive(this).removeAll(arguments.interpreter, collection)
        return instance
    }

    @FunctionDoc(
        name = "addAll",
        desc = ["This allows you to add all the values in a collection into the set"],
        params = [COLLECTION, "collection", "the collection of values you want to add"],
        returns = [SET, "the modified set"],
        examples = ["Set.of().addAll(Set.of('object', 81, 96, 'case'));"]
    )
    private fun addAll(arguments: Arguments): ClassInstance {
        val instance = arguments.nextSet()
        val collection = arguments.nextPrimitive(CollectionDef::class).asCollection()
        instance.asPrimitive(this).addAll(arguments.interpreter, collection)
        return instance
    }

    @FunctionDoc(
        name = "contains",
        desc = ["This allows you to check whether a value is in the set"],
        params = [OBJECT, "value", "the value that you want to check in the set"],
        returns = [BOOLEAN, "whether the value is in the set"],
        examples = ["Set.of('object').contains('object');"]
    )
    private fun contains(arguments: Arguments): Boolean {
        val instance = arguments.nextPrimitive(this)
        return instance.contains(arguments.interpreter, arguments.next())
    }

    @FunctionDoc(
        name = "containsAll",
        desc = ["This allows you to check whether a collection of values are all in the set"],
        params = [COLLECTION, "collection", "the collection of values you want to check in the set"],
        returns = [BOOLEAN, "whether all the values are in the set"],
        examples = ["Set.of('object').containsAll(Set.of('object', 81, 96, 'case'));"]
    )
    private fun containsAll(arguments: Arguments): Boolean {
        val instance = arguments.nextPrimitive(this)
        val collection = arguments.nextPrimitive(CollectionDef::class).asCollection()
        return instance.containsAll(arguments.interpreter, collection)
    }

    @FunctionDoc(
        name = "clear",
        desc = ["This removes all values from inside the set"],
        examples = ["Set.of('object').clear();"]
    )
    private fun clear(arguments: Arguments) {
        arguments.nextPrimitive(this).clear()
    }

    @FunctionDoc(
        name = "toList",
        desc = ["This returns a list of all the values in the set"],
        returns = [LIST, "the list of values in the set"],
        examples = ["Set.of('object', 81, 96, 'case').toList();"]
    )
    private fun toList(arguments: Arguments): ArucasList {
        val instance = arguments.nextPrimitive(this)
        return instance.asCollection()
    }

    @FunctionDoc(
        name = "filter",
        desc = ["This allows you to filter the set"],
        params = [FUNCTION, "function", "the function you want to filter the set by"],
        returns = [SET, "the filtered set"],
        examples = ["Set.of(-9, 81, 96, 15).filter(fun(value) { return value > 80; });"]
    )
    private fun filter(arguments: Arguments): ClassInstance {
        val instance = arguments.nextPrimitive(this)
        val predicate = arguments.nextFunction()
        val newSet = ArucasSet()
        for (value in instance) {
            val returnValue = arguments.interpreter.call(predicate, listOf(value))
            val boolean = returnValue.getPrimitive(BooleanDef::class) ?: runtimeError("Predicate function must return a boolean")
            if (boolean) {
                newSet.add(arguments.interpreter, value)
            }
        }
        return this.create(newSet)
    }

    @FunctionDoc(
        name = "map",
        desc = ["This allows you to map the set"],
        params = [FUNCTION, "function", "the function you want to map the set by"],
        returns = [SET, "the mapped set"],
        examples = ["Set.of(-9, 81, 96, 15).map(fun(value) { return value * 2; });"]
    )
    private fun map(arguments: Arguments): ClassInstance {
        val instance = arguments.nextPrimitive(this)
        val transformer = arguments.nextFunction()
        val newSet = ArucasSet()
        for (value in instance) {
            val returnValue = arguments.interpreter.call(transformer, listOf(value))
            newSet.add(arguments.interpreter, returnValue)
        }
        return this.create(newSet)
    }

    @FunctionDoc(
        name = "reduce",
        desc = ["This allows you to reduce the set"],
        params = [FUNCTION, "function", "the function you want to reduce the set by"],
        returns = [OBJECT, "the reduced set"],
        examples = ["Set.of(-9, 81, 96, 15).reduce(fun(value, next) { return value + next; });"]
    )
    private fun reduce(arguments: Arguments): ClassInstance {
        val collection = arguments.nextPrimitive(this).asCollection()
        val reducer = arguments.nextFunction()
        if (collection.isEmpty()) {
            runtimeError("Empty set cannot be reduced")
        }
        return collection.reduce { a, b ->
            arguments.interpreter.call(reducer, listOf(a, b))
        }
    }

    @FunctionDoc(
        name = "reduce",
        desc = ["This reduces the list using the reducer starting with an identity"],
        params = [
            OBJECT, "identity", "the identity",
            FUNCTION, "reducer", "a function that takes a value and returns a new value"
        ],
        returns = [OBJECT, "the reduced value"],
        examples = ["Set.of(-9, 81, 96, 15).reduce(\"\", fun(value, next) { return value + next; });" ]
    )
    private fun reduce2(arguments: Arguments): Any {
        val collection = arguments.nextPrimitive(this).asCollection()
        val identity = arguments.next()
        val reducer = arguments.nextFunction()
        return collection.stream().reduce(identity) { a, b ->
            arguments.interpreter.call(reducer, listOf(a, b))
        }
    }
}