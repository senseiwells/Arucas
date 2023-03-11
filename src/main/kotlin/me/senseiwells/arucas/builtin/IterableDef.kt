package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.*
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.Trace
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.functions.builtin.Arguments
import me.senseiwells.arucas.functions.builtin.ConstructorFunction
import me.senseiwells.arucas.functions.builtin.MemberFunction
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.utils.CollectionUtils
import me.senseiwells.arucas.utils.FunctionUtils
import me.senseiwells.arucas.utils.impl.ArucasIterable
import me.senseiwells.arucas.utils.impl.ArucasIterator
import me.senseiwells.arucas.utils.misc.Types.ITERABLE

@ClassDoc(
    name = ITERABLE,
    desc = [
        "This class represents an object that can be iterated over.",
        "This class is used internally to denote whether an object can be",
        "iterated over inside a foreach loop"
    ]
)
class IterableDef(interpreter: Interpreter): CreatableDefinition<ArucasIterable>(ITERABLE, interpreter) {
    override fun canConstructDirectly() = false

    override fun defineConstructors(): List<ConstructorFunction> {
        return listOf(
            ConstructorFunction.of(0, this::construct)
        )
    }

    @ConstructorDoc(
        desc = ["This creates an iterable, this cannot be called directly, only from child classes"],
        examples = [
            """
            class IterableImpl: Iterable {
                IterableImpl(): super();
                
                fun iterator() {
                    // Example
                    return [].iterator();
                }
            }
            """
        ]
    )
    private fun construct(arguments: Arguments) {
        val instance = arguments.next()
        instance.setPrimitive(this, ArucasIterable.EMPTY)
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("iterator", this::iterator),
            MemberFunction.of("first", 1, this::first),
            MemberFunction.of("has", 1, this::has),
            MemberFunction.of("all", 1, this::all),
            MemberFunction.of("forEach", 1, this::forEach)
        )
    }

    @FunctionDoc(
        name = "iterator",
        desc = ["This gets the generated iterator"],
        returns = ReturnDoc(IteratorDef::class, ["The generated iterator."]),
        examples = [
            """
            iterable = [];
            i = iterable.iterator();
            while (i.hasNext()) {
                next = i.next();
            }
            
            // Or just, compiles to above
            foreach (next : iterable); 
            """
        ]
    )
    private fun iterator(arguments: Arguments): ArucasIterator {
        val instance = arguments.nextPrimitive(this)
        if (instance === ArucasIterable.EMPTY) {
            runtimeError("Iterable has not properly overridden 'iterator' method")
        }
        return instance.iterator()
    }

    @FunctionDoc(
        name = "first",
        desc = [
            "This finds the first element in the iterable that meets a condition.",
            "If no element is found then an error will be thrown."
        ],
        params = [ParameterDoc(FunctionDef::class, "predicate", ["The predicate to check if the element meets the conditions, must return a boolean."])],
        returns = ReturnDoc(ObjectDef::class, ["The first element that meets the condition."]),
        examples = ["[0, 1, 2, 3].first(fun(x) x > 2); // 3"]
    )
    private fun first(arguments: Arguments): ClassInstance {
        val iterator = arguments.nextIterator()
        val predicate = arguments.nextFunction()
        for (value in iterator) {
            if (FunctionUtils.callAsPredicate(arguments.interpreter, predicate, value)) {
                return value
            }
        }
        runtimeError("No such element could be found")
    }

    @FunctionDoc(
        name = "has",
        desc = ["Checks whether the iterable has a given elements that meets a given condition."],
        params = [ParameterDoc(FunctionDef::class, "predicate", ["The predicate to check if the element meets the conditions, must return a boolean."])],
        returns = ReturnDoc(BooleanDef::class, ["Whether the iterable has an element that meets the condition."]),
        examples = ["[0, 1, 2, 3].has(fun(x) x < -2); // false"]
    )
    private fun has(arguments: Arguments): Boolean {
        val iterator = arguments.nextIterator()
        val predicate = arguments.nextFunction()
        for (value in iterator) {
            if (FunctionUtils.callAsPredicate(arguments.interpreter, predicate, value)) {
                return true
            }
        }
        return false
    }

    @FunctionDoc(
        name = "all",
        desc = ["This checks whether all the elements in the iterable meet a condition."],
        params = [ParameterDoc(FunctionDef::class, "predicate", ["The predicate to check if the elements meet the conditions, must return a boolean."])],
        returns = ReturnDoc(BooleanDef::class, ["Whether all the elements meet the condition"]),
        examples = ["[0, 1, 2, 3].all(fun(x) x >= 0); // true"]
    )
    private fun all(arguments: Arguments): Boolean {
        val iterator = arguments.nextIterator()
        val predicate = arguments.nextFunction()
        for (value in iterator) {
            if (!FunctionUtils.callAsPredicate(arguments.interpreter, predicate, value)) {
                return false
            }
        }
        return true
    }

    @FunctionDoc(
        name = "forEach",
        desc = ["This allows you to iterate over all the elements in the iterable with a lambda."],
        params = [ParameterDoc(FunctionDef::class, "consumer", ["The consumer that accepts one element at a time."])],
        examples = ["[0, 1, 2, 3].forEach(fun(x) print(x));"]
    )
    private fun forEach(arguments: Arguments) {
        val iterator = arguments.nextIterator()
        val consumer = arguments.nextFunction()
        for (value in iterator) {
            arguments.interpreter.call(consumer, listOf(value))
        }
    }
}