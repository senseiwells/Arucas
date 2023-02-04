package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.ClassDoc
import me.senseiwells.arucas.api.docs.annotations.ConstructorDoc
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.ConstructorFunction
import me.senseiwells.arucas.utils.MemberFunction
import me.senseiwells.arucas.utils.Util.Types.ITERATOR
import me.senseiwells.arucas.utils.impl.ArucasIterator

@ClassDoc(
    name = ITERATOR,
    desc = [
        "This class represents an object that iterates.",
        "This is what is used internally to iterate in a",
        "foreach loop and you can create your own iterators",
        "to use be able to use them inside a foreach"
    ]
)
class IteratorDef(interpreter: Interpreter): CreatableDefinition<ArucasIterator>(ITERATOR, interpreter) {
    override fun canConstructDirectly() = false

    override fun defineConstructors(): List<ConstructorFunction> {
        return listOf(
            ConstructorFunction.of(0, this::construct)
        )
    }

    @ConstructorDoc(
        desc = ["This creates an iterator, this cannot be called directly, only from child classes"],
        examples = [
            """
            class IteratorImpl: Iterator {
                IteratorImpl(): super();
                
                fun hasNext() {
                    return false;
                }
                
                fun next() {
                    throw new Error("Nothing next");
                }
            }
            """
        ]
    )
    private fun construct(arguments: Arguments) {
        val instance = arguments.next()
        instance.setPrimitive(this, ArucasIterator.EMPTY)
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("hasNext", this::hasNext),
            MemberFunction.of("next", this::next)
        )
    }

    @FunctionDoc(
        name = "hasNext",
        desc = ["Checks whether the iterator has a next item to iterate"],
        returns = ReturnDoc(BooleanDef::class, ["Whether there are items left to iterate."]),
        examples = [
            """
            iterator = [].iterator();
            iterator.hasNext();
            """
        ]
    )
    private fun hasNext(arguments: Arguments): Boolean {
        val instance = arguments.nextPrimitive(this)
        if (instance === ArucasIterator.EMPTY) {
            runtimeError("Iterator has not properly overridden 'hasNext' method")
        }
        return instance.hasNext()
    }

    @FunctionDoc(
        name = "next",
        desc = ["Gets the next item in the iterator, may throw if there is no next item"],
        returns = ReturnDoc(ObjectDef::class, ["The next item."]),
        examples = [
            """
            iterator = [10, 20].iterator();
            iterator.next(); // 10
            iterator.next(); // 20
            """
        ]
    )
    private fun next(arguments: Arguments): ClassInstance {
        val instance = arguments.nextPrimitive(this)
        if (instance === ArucasIterator.EMPTY) {
            runtimeError("Iterator has not properly overridden 'next' method")
        }
        return instance.next()
    }
}