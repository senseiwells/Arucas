package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.api.docs.FunctionDoc
import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.MemberFunction
import me.senseiwells.arucas.utils.Util.Types.BOOLEAN
import me.senseiwells.arucas.utils.Util.Types.COLLECTION
import me.senseiwells.arucas.utils.Util.Types.NUMBER
import me.senseiwells.arucas.utils.impl.ArucasCollection

@ClassDoc(
    name = COLLECTION,
    desc = [
        "This class is used to represent a collection of objects,",
        "this class is used internally as the parent of maps, lists, and sets.",
        "This cannot be extended or instantiated directly.",
        "All collections inherit Iterable, and thus can be iterated over"
    ],
    superclass = IterableDef::class
)
class CollectionDef(interpreter: Interpreter): PrimitiveDefinition<ArucasCollection>(COLLECTION, interpreter) {
    override fun canExtend() = false

    override fun superclass(): PrimitiveDefinition<in ArucasCollection> = this.getPrimitiveDef(IterableDef::class)

    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        return instance.asPrimitive(this).toString(interpreter, trace)
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("size", this::size),
            MemberFunction.of("isEmpty", this::isEmpty),
        )
    }

    @FunctionDoc(
        name = "size",
        desc = ["This allows you to get the size of the collection"],
        returns = [NUMBER, "the size of the list"],
        examples = ["['object', 81, 96, 'case'].size();"]
    )
    private fun size(arguments: Arguments): Int {
        val collection = arguments.nextPrimitive(this)
        return collection.length()
    }

    @FunctionDoc(
        name = "isEmpty",
        desc = ["This allows you to check if the collection is empty"],
        returns = [BOOLEAN, "true if the collection is empty"],
        examples = ["['object', 81, 96, 'case'].isEmpty(); // false"]
    )
    private fun isEmpty(arguments: Arguments): Boolean {
        return arguments.nextPrimitive(this).length() == 0
    }
}