package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.ClassDoc
import me.senseiwells.arucas.api.docs.annotations.ConstructorDoc
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.ConstructorFunction
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.MemberFunction
import me.senseiwells.arucas.utils.Util.Types.COLLECTION
import me.senseiwells.arucas.utils.impl.ArucasCollection

@ClassDoc(
    name = COLLECTION,
    desc = [
        "This class is used to represent a collection of objects,",
        "this class is used internally as the parent of maps, lists, and sets.",
        "This cannot be instantiated directly.",
        "All collections inherit Iterable, and thus can be iterated over"
    ],
    superclass = IterableDef::class
)
class CollectionDef(interpreter: Interpreter): PrimitiveDefinition<ArucasCollection>(COLLECTION, interpreter) {
    override fun canConstructDirectly() = false

    override fun superclass(): PrimitiveDefinition<in ArucasCollection> = this.getPrimitiveDef(IterableDef::class)

    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        return instance.asPrimitive(this).toString(interpreter, trace)
    }

    override fun defineConstructors(): List<ConstructorFunction> {
        return listOf(
            ConstructorFunction.of(0, this::construct)
        )
    }

    @ConstructorDoc(
        desc = ["This creates a collection, this cannot be called directly, only from child classes"],
        examples = [
            """
            class ChildCollection: Collection {
                ChildCollection(): super();
                
                fun size() {
                    return 0;
                }
            }
            """
        ]
    )
    private fun construct(arguments: Arguments) {
        val instance = arguments.next()
        instance.setPrimitive(this, ArucasCollection.EMPTY)
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
        returns = ReturnDoc(NumberDef::class, ["The size of the list."]),
        examples = ["['object', 81, 96, 'case'].size();"]
    )
    private fun size(arguments: Arguments): Int {
        val collection = arguments.nextPrimitive(this)
        if (collection === ArucasCollection.EMPTY) {
            runtimeError("Collection has not properly overridden 'size' method")
        }
        return collection.length()
    }

    @FunctionDoc(
        name = "isEmpty",
        desc = ["This allows you to check if the collection is empty"],
        returns = ReturnDoc(BooleanDef::class, ["True if the collection is empty."]),
        examples = ["['object', 81, 96, 'case'].isEmpty(); // false"]
    )
    private fun isEmpty(arguments: Arguments): Boolean {
        return arguments.next().callMemberPrimitive(arguments.interpreter, "size", listOf(), NumberDef::class).toInt() == 0
    }
}