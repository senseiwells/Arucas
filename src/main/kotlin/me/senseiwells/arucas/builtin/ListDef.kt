package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.*
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.compiler.Trace
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.functions.builtin.Arguments
import me.senseiwells.arucas.functions.builtin.ConstructorFunction
import me.senseiwells.arucas.functions.builtin.MemberFunction
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.utils.ExceptionUtils
import me.senseiwells.arucas.utils.impl.ArucasList
import me.senseiwells.arucas.utils.misc.Types.LIST

@ClassDoc(
    name = LIST,
    desc = ["This class is used for collections of ordered elements"],
    superclass = CollectionDef::class
)
class ListDef(interpreter: Interpreter): CreatableDefinition<ArucasList>(LIST, interpreter) {
    override fun canConstructDirectly() = false

    override fun superclass(): PrimitiveDefinition<in ArucasList> {
        return this.getPrimitiveDef(CollectionDef::class)
    }

    override fun bracketAccess(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, trace: LocatableTrace): ClassInstance {
        val list = instance.asPrimitive(this)
        val listIndex = index.getPrimitive(NumberDef::class)?.toInt()
        listIndex ?: runtimeError("Indexer for lists must result a number", trace)
        return ExceptionUtils.traceable(trace) { return@traceable list[listIndex] }
    }

    override fun bracketAssign(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, assignee: ClassInstance, trace: LocatableTrace): ClassInstance {
        val list = instance.asPrimitive(this)
        val listIndex = index.getPrimitive(NumberDef::class)?.toInt()
        listIndex ?: runtimeError("Indexer for lists must result a number", trace)
        ExceptionUtils.traceable(trace) { list[listIndex] = assignee }
        return assignee
    }

    override fun copy(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): ClassInstance {
        return this.create(ArucasList(instance.asPrimitive(this)))
    }

    override fun equals(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Boolean {
        val otherList = other.getPrimitive(this) ?: return false
        return instance.asPrimitive(this).equals(interpreter, otherList)
    }

    override fun hashCode(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Int {
        return instance.asPrimitive(this).hashCode(interpreter)
    }

    override fun defineConstructors(): List<ConstructorFunction> {
        return listOf(
            ConstructorFunction.of(0, this::construct)
        )
    }

    @ConstructorDoc(
        desc = ["This creates a list, this cannot be called directly, only from child classes"],
        examples = [
            """
            class ChildList: List {
                ChildList(): super();
            }
            """
        ]
    )
    private fun construct(arguments: Arguments) {
        val instance = arguments.next()
        instance.setPrimitive(this, ArucasList())
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("get", 1, this::get),
            MemberFunction.of("set", 2, this::set),
            MemberFunction.of("remove", 1, this::remove),
            MemberFunction.of("append", 1, this::append),
            MemberFunction.of("prepend", 1, this::prepend),
            MemberFunction.of("insert", 2, this::insert),
            MemberFunction.of("indexOf", 1, this::indexOf),
            MemberFunction.of("lastIndexOf", 1, this::lastIndexOf),
            MemberFunction.of("contains", 1, this::contains),
            MemberFunction.of("containsAll", 1, this::containsAll),
            MemberFunction.of("addAll", 1, this::addAll),
            MemberFunction.of("removeAll", 1, this::removeAll),
            MemberFunction.of("retainAll", 1, this::retainAll),
            MemberFunction.of("clear", this::clear),
            MemberFunction.of("sort", this::sort),
            MemberFunction.of("sort", 1, this::sort1),
            MemberFunction.of("filter", 1, this::filter),
            MemberFunction.of("map", 1, this::map),
            MemberFunction.of("reduce", 1, this::reduce),
            MemberFunction.of("reduce", 2, this::reduce2),
            MemberFunction.of("flatten", this::flatten),
            MemberFunction.of("reverse", this::reverse),
            MemberFunction.of("shuffle", this::shuffle),
            MemberFunction.of("slice", 1, this::slice)
        )
    }

    @FunctionDoc(
        name = "get",
        desc = [
            "This allows you to get the value at a specific index, alternative to bracket accessor,",
            "this will throw an error if the index given is out of bounds"
        ],
        params = [ParameterDoc(NumberDef::class, "index", ["The index of the value you want to get."])],
        returns = ReturnDoc(ObjectDef::class, ["The value at the index."]),
        examples = ["['object', 81, 96, 'case'].get(1); // 81"]
    )
    private fun get(arguments: Arguments): ClassInstance {
        return arguments.nextList().bracketAccess(arguments.nextNumber(), arguments.interpreter)
    }

    @FunctionDoc(
        name = "set",
        desc = [
            "This allows you to set the value at a specific index, alternative to bracket assignment,",
            "this will throw an erroor if the index given is out of bounds"
        ],
        params = [
            ParameterDoc(ObjectDef::class, "value", ["The value you want to set."]),
            ParameterDoc(NumberDef::class, "index", ["The index you want to set the value at."])
        ],
        returns = ReturnDoc(ListDef::class, ["The list."]),
        examples = ["['object', 81, 96, 'case'].set('foo', 1); // ['object', 'foo', 96, 'case']"]
    )
    private fun set(arguments: Arguments): ClassInstance {
        val list = arguments.nextList()
        list.bracketAssign(arguments.nextNumber(), arguments.interpreter, arguments.next())
        return list
    }

    @FunctionDoc(
        name = "remove",
        desc = [
            "This allows you to remove the value at a specific index, alternative to bracket assignment.",
            "This will throw an error if the index is out of bounds"
        ],
        params = [ParameterDoc(NumberDef::class, "index", ["The index of the value you want to remove."])],
        returns = ReturnDoc(ObjectDef::class, ["The value that was removed."]),
        examples = ["['object', 81, 96, 'case'].remove(1); // 81"]
    )
    private fun remove(arguments: Arguments): ClassInstance {
        val list = arguments.nextPrimitive(this)
        val index = arguments.nextPrimitive(NumberDef::class).toInt()
        return list.removeAt(index)
    }

    @FunctionDoc(
        name = "append",
        desc = ["This allows you to append a value to the end of the list"],
        params = [ParameterDoc(ObjectDef::class, "value", ["The value you want to append."])],
        returns = ReturnDoc(ListDef::class, ["The list."]),
        examples = ["['object', 81, 96, 'case'].append('foo'); // ['object', 81, 96, 'case', 'foo']"]
    )
    private fun append(arguments: Arguments): ClassInstance {
        val list = arguments.nextList()
        list.asPrimitive(this).add(arguments.next())
        return list
    }

    @FunctionDoc(
        name = "prepend",
        desc = ["This allows you to prepend a value to the beginning of the list"],
        params = [ParameterDoc(ObjectDef::class, "value", ["The value you want to prepend."])],
        returns = ReturnDoc(ListDef::class, ["The list."]),
        examples = ["['object', 81, 96].prepend('foo'); // ['foo', 'object', 81, 96]"]
    )
    private fun prepend(arguments: Arguments): ClassInstance {
        val list = arguments.nextList()
        list.asPrimitive(this).add(0, arguments.next())
        return list
    }

    @FunctionDoc(
        name = "insert",
        desc = ["This allows you to insert a value at a specific index, this will throw an error if the index is out of bounds"],
        params = [ParameterDoc(ObjectDef::class, "value", ["The value you want to insert."]), ParameterDoc(NumberDef::class, "index", ["The index you want to insert the value at."])],
        returns = ReturnDoc(ListDef::class, ["The list."]),
        examples = ["['object', 81, 96, 'case'].insert('foo', 1); // ['object', 'foo', 81, 96, 'case']"]
    )
    private fun insert(arguments: Arguments): ClassInstance {
        val list = arguments.nextList()
        val index = arguments.nextPrimitive(NumberDef::class).toInt()
        list.asPrimitive(this).add(index, arguments.next())
        return list
    }

    @FunctionDoc(
        name = "indexOf",
        desc = ["This allows you to get the index of a specific value"],
        params = [ParameterDoc(ObjectDef::class, "value", ["The value you want to get the index of."])],
        returns = ReturnDoc(NumberDef::class, ["The index of the value."]),
        examples = ["['object', 81, 96, 'case', 81].indexOf(81); // 1"]
    )
    private fun indexOf(arguments: Arguments): Int {
        val list = arguments.nextList()
        return list.asPrimitive(this).indexOf(arguments.interpreter, arguments.next())
    }

    @FunctionDoc(
        name = "lastIndexOf",
        desc = ["This allows you to get the last index of a specific value"],
        params = [ParameterDoc(ObjectDef::class, "value", ["The value you want to get the last index of."])],
        returns = ReturnDoc(NumberDef::class, ["The last index of the value."]),
        examples = ["['object', 81, 96, 'case', 96].lastIndexOf(96); // 4"]
    )
    private fun lastIndexOf(arguments: Arguments): Int {
        val list = arguments.nextList()
        return list.asPrimitive(this).lastIndexOf(arguments.interpreter, arguments.next())
    }

    @FunctionDoc(
        name = "contains",
        desc = ["This allows you to check if the list contains a specific value"],
        params = [ParameterDoc(ObjectDef::class, "value", ["The value you want to check."])],
        returns = ReturnDoc(BooleanDef::class, ["True if the list contains the value."]),
        examples = ["['object', 81, 96, 'case'].contains('case'); // true"]
    )
    private fun contains(arguments: Arguments): Boolean {
        val list = arguments.nextList()
        return list.asPrimitive(this).contains(arguments.interpreter, arguments.next())
    }

    @FunctionDoc(
        name = "containsAll",
        desc = ["This allows you to check if the list contains all the values in another collection"],
        params = [ParameterDoc(CollectionDef::class, "collection", ["The collection you want to check agains."])],
        returns = ReturnDoc(BooleanDef::class, ["True if the list contains all the values in the collection."]),
        examples = ["['object', 81, 96, 'case'].containsAll(['foo', 'object']); // false"]
    )
    private fun containsAll(arguments: Arguments): Boolean {
        val list = arguments.nextList().asPrimitive(this)
        val collection = arguments.nextPrimitive(CollectionDef::class)
        return list.containsAll(arguments.interpreter, collection)
    }

    @FunctionDoc(
        name = "addAll",
        desc = ["This allows you to add all the values in another collection to the list"],
        params = [ParameterDoc(CollectionDef::class, "collection", ["The collection you want to add to the list."])],
        returns = ReturnDoc(ListDef::class, ["The list."]),
        examples = ["['object', 81, 96, 'case'].addAll(['foo', 'object']); // ['object', 81, 96, 'case', 'foo', 'object']"]
    )
    private fun addAll(arguments: Arguments): ClassInstance {
        val list = arguments.nextList()
        val collection = arguments.nextPrimitive(CollectionDef::class)
        list.asPrimitive(this).addAll(collection.asCollection())
        return list
    }

    @FunctionDoc(
        name = "removeAll",
        desc = ["This allows you to remove all the values in another collection from the list"],
        params = [ParameterDoc(CollectionDef::class, "collection", ["The collection you want to remove from the list."])],
        returns = ReturnDoc(ListDef::class, ["The list."]),
        examples = ["['object', 81, 96, 'case'].removeAll(['foo', 'object']); // [81, 96, 'case']"]
    )
    private fun removeAll(arguments: Arguments): ClassInstance {
        val list = arguments.nextList()
        val collection = arguments.nextPrimitive(CollectionDef::class)
        list.asPrimitive(this).removeAll(collection.asCollection())
        return list
    }

    @FunctionDoc(
        name = "retainAll",
        desc = ["This allows you to retain only the values that are in both lists"],
        params = [ParameterDoc(ListDef::class, "list", ["The list you want to retain values from."])],
        returns = ReturnDoc(ListDef::class, ["The list."]),
        examples = ["['object', 81, 96, 'case'].retainAll(['case', 'object', 54]); // ['object', 'case']"]
    )
    private fun retainAll(arguments: Arguments): ClassInstance {
        val list = arguments.nextList()
        val otherList = arguments.nextList()
        list.asPrimitive(this).retainAll(otherList.asPrimitive(this))
        return list
    }

    @FunctionDoc(
        name = "clear",
        desc = ["This allows you to clear the list"],
        examples = ["['object', 81, 96, 'case'].clear(); // []"]
    )
    private fun clear(arguments: Arguments) {
        val list = arguments.nextList()
        list.asPrimitive(this).clear()
    }

    @FunctionDoc(
        name = "sort",
        desc = ["This allows you to sort the list using the elements compare method"],
        returns = ReturnDoc(ListDef::class, ["The sorted list."]),
        examples = ["['d', 'a', 'c', 'b'].sort(); // ['a', 'b', 'c', 'd']"]
    )
    private fun sort(arguments: Arguments): ClassInstance {
        val list = arguments.nextList()
        list.asPrimitive(this).sortWith { a, b ->
            a.compare(arguments.interpreter, b)
        }
        return list
    }

    @FunctionDoc(
        name = "sort",
        desc = ["This allows you to sort the list using a comparator function"],
        params = [ParameterDoc(FunctionDef::class, "comparator", ["The comparator function."])],
        returns = ReturnDoc(ListDef::class, ["The sorted list."]),
        examples = ["[6, 5, 9, -10].sort(fun(a, b) { return a - b; }); // [-10, 5, 6, 9]"]
    )
    private fun sort1(arguments: Arguments): ClassInstance {
        val list = arguments.nextList()
        val comparator = arguments.next()
        list.asPrimitive(this).sortWith { a, b ->
            val returnValue = arguments.interpreter.call(comparator, listOf(a, b))
            val num = returnValue.getPrimitiveOrThrow(NumberDef::class, "Comparator function must return a number")
            if (num > 0) 1 else if (num < 0) -1 else 0
        }
        return list
    }

    @FunctionDoc(
        name = "filter",
        desc = [
            "This filters the list using the predicate, a function that either returns",
            "true or false, based on the element on whether it should be kept or not,",
            "and returns a new list with the filtered elements"
        ],
        params = [ParameterDoc(FunctionDef::class, "predicate", ["A function that takes a value and returns Boolean."])],
        returns = ReturnDoc(ListDef::class, ["The filtered collection."]),
        examples = [
            """
            (list = [1, 2, 3]).filter(fun(v) {
                return v > 1;
            });
            // list = [2, 3]
            """
        ]
    )
    private fun filter(arguments: Arguments): ClassInstance {
        val list = arguments.nextPrimitive(this)
        val predicate = arguments.nextFunction()
        val newList = list.filterTo(ArucasList()) {
            val returnValue = arguments.interpreter.call(predicate, listOf(it))
            returnValue.getPrimitiveOrThrow(BooleanDef::class, "Predicate function must return a boolean")
        }
        return this.create(newList)
    }

    @FunctionDoc(
        name = "map",
        desc = [
            "This maps the list using the mapper, a function that takes a value and",
            "returns a new value, and returns a new list with the mapped elements"
        ],
        params = [ParameterDoc(FunctionDef::class, "mapper", ["A function that takes a value and returns a new value."])],
        returns = ReturnDoc(ListDef::class, ["The mapped collection."]),
        examples = [
            """
            (list = [1, 2, 3]).map(fun(v) {
                return v * 2;
            });
            // list = [2, 4, 6]
            """
        ]
    )
    private fun map(arguments: Arguments): ClassInstance {
        val list = arguments.nextPrimitive(this)
        val mapper = arguments.nextFunction()
        val newList = list.mapTo(ArucasList()) {
            arguments.interpreter.call(mapper, listOf(it))
        }
        return this.create(newList)
    }

    @FunctionDoc(
        name = "reduce",
        desc = [
            "This reduces the list using the reducer, a function that takes an",
            "accumulated value and a new value and returns the next accumulated value",
        ],
        params = [ParameterDoc(FunctionDef::class, "reducer", ["A function that takes a value and returns a new value."])],
        returns = ReturnDoc(ObjectDef::class, ["The reduced value."]),
        examples = [
            """
            // a will start at 1 and b at 2
            // next accumulator will be 3
            // a will be 3 and b will be 3 = 6
            (list = [1, 2, 3]).reduce(fun(a, b) {
                return a + b;
            });
            // 6
            """
        ]
    )
    private fun reduce(arguments: Arguments): Any {
        val list = arguments.nextPrimitive(this)
        val reducer = arguments.nextFunction()
        if (list.isEmpty()) {
            runtimeError("Empty list cannot be reduced")
        }
        return list.reduce { a, b ->
            arguments.interpreter.call(reducer, listOf(a, b))
        }
    }

    @FunctionDoc(
        name = "reduce",
        desc = [
            "This reduces the list using the reducer starting with an identity"
        ],
        params = [
            ParameterDoc(ObjectDef::class, "identity", ["The identity."]),
            ParameterDoc(FunctionDef::class, "reducer", ["A function that takes a value and returns a new value."])
        ],
        returns = ReturnDoc(ObjectDef::class, ["The reduced value."]),
        examples = [
            """
            (list = [1, 2, 3]).reduce("", fun(a, b) {
                return a + b;
            });
            // "123"
            """
        ]
    )
    private fun reduce2(arguments: Arguments): Any {
        val list = arguments.nextPrimitive(this)
        val identity = arguments.next()
        val reducer = arguments.nextFunction()
        return list.stream().reduce(identity) { a, b ->
            arguments.interpreter.call(reducer, listOf(a, b))
        }
    }

    @FunctionDoc(
        name = "flatten",
        desc = [
            "If there are any objects in the list that are collections they will",
            "be expanded and added to the list. However collections inside those",
            "collections will not be flattened, this is returned as a new list"
        ],
        returns = ReturnDoc(ListDef::class, ["The flattened list."]),
        examples = [
            """
            list = [1, 2, 3, [4, 5], [6, [7]]].flatten();
            // list = [1, 2, 3, 4, 5, 6, [7]]
            """
        ]
    )
    private fun flatten(arguments: Arguments): ArucasList {
        val instance = arguments.nextPrimitive(this)
        val newList = ArucasList()
        for (value in instance.toArray()) {
            value.getPrimitive(CollectionDef::class)?.let {
                 newList.addAll(it.asCollection())
            } ?: newList.add(value)
        }
        return newList
    }

    @FunctionDoc(
        name = "reverse",
        desc = ["This allows you to reverse the list - this mutates the existing list."],
        returns = ReturnDoc(ListDef::class, ["The reversed list."]),
        examples = ["['a', 'b', 'c', 'd'].reverse(); // ['d', 'c', 'b', 'a']"]
    )
    private fun reverse(arguments: Arguments): ClassInstance {
        val list = arguments.nextList()
        list.asPrimitive(this).reverse()
        return list
    }

    @FunctionDoc(
        name = "shuffle",
        desc = ["This allows you to shuffle the list - this mutates the existing list."],
        returns = ReturnDoc(ListDef::class, ["The shuffled list."]),
        examples = ["['a', 'b', 'c', 'd'].shuffle(); // some random order ¯\\_(ツ)_/¯"]
    )
    private fun shuffle(arguments: Arguments): ClassInstance {
        val list = arguments.nextList()
        list.asPrimitive(this).shuffle()
        return list
    }

    @FunctionDoc(
        name = "slice",
        desc = ["This creates a new list with the desired contents from the list."],
        params = [ParameterDoc(IterableDef::class, "indexes", ["An iterable object containing the indexes you want to slice"])],
        returns = ReturnDoc(ListDef::class, ["The sliced list."]),
        examples = [
            "['a', 'b', 'c', 'd'].slice([2, 0]); // ['c', 'a']",
            "['a', 'b', 'c', 'd', 'e'].slice(range(1, 4)); // ['b', 'c', 'd']"
        ]
    )
    private fun slice(arguments: Arguments): ClassInstance {
        val list = arguments.nextList()
        val iter = arguments.nextIterator()
        val interpreter = arguments.interpreter
        val newList = ArucasList()
        for (index in iter) {
            index.expect(NumberDef::class, "Expected numbers when slicing")
            newList.add(list.callMember(interpreter, "get", listOf(index), Trace.INTERNAL))
        }
        return this.create(newList)
    }
}