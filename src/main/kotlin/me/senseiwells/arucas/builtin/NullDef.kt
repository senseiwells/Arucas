package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.Util.Types.NULL

@ClassDoc(
    name = NULL,
    desc = [
        "This class is used for the null object,",
        "this cannot be instantiated or extended"
    ]
)
class NullDef(interpreter: Interpreter): PrimitiveDefinition<Unit>(NULL, interpreter) {
    val NULL = this.create(Unit)

    override fun canExtend() = false

    override fun asJavaValue(instance: ClassInstance): Any? {
        return null
    }

    override fun hashCode(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Int {
        return 0
    }

    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        return "null"
    }
}