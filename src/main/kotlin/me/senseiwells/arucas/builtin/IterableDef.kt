package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.api.docs.ConstructorDoc
import me.senseiwells.arucas.api.docs.FunctionDoc
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.ConstructorFunction
import me.senseiwells.arucas.utils.MemberFunction
import me.senseiwells.arucas.utils.Util.Types.ITERABLE
import me.senseiwells.arucas.utils.Util.Types.ITERATOR
import me.senseiwells.arucas.utils.impl.ArucasIterable
import me.senseiwells.arucas.utils.impl.ArucasIterator

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
            MemberFunction.of("iterator", this::iterator)
        )
    }

    @FunctionDoc(
        name = "iterator",
        desc = ["This gets the generated iterator"],
        returns = [ITERATOR, "the generated iterator"],
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
            runtimeError("Iterable has not properly overriden 'iterator' method")
        }
        return instance.iterator()
    }
}