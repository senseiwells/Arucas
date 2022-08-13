package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.Util.Types.ITERABLE
import me.senseiwells.arucas.utils.impl.ArucasIterable

@ClassDoc(
    name = ITERABLE,
    desc = [
        "This class represents an object that can be iterated over.",
        "This class is used internally to denote whether an object can be",
        "iterated over inside a foreach loop, this class cannot be extended",
        "or instantiated"
    ]
)
class IterableDef(interpreter: Interpreter): CreatableDefinition<ArucasIterable>(ITERABLE, interpreter) {
    override fun canExtend() = false
}