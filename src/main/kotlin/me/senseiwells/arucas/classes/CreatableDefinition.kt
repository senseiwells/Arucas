package me.senseiwells.arucas.classes

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.interpreter.Interpreter

/**
 * This class defines a [PrimitiveDefinition] that is able to be
 * created. This should generally be the case for a wrapper
 * class definition.
 *
 * Barring this difference all the other implementation remains the same.
 *
 * @param T the primitive value type that you are wrapping.
 * @param name the name of the class.
 * @param interpreter the interpreter that the definition was defined on.
 * @see PrimitiveDefinition
 */
abstract class CreatableDefinition<T: Any>(
    name: String,
    interpreter: Interpreter
): PrimitiveDefinition<T>(name, interpreter) {
    /**
     * This creates an instance of the definition setting the primitive value of it.
     * This method is public allowing anyone to create instances of this class.
     *
     * @param value the primitive value to create the [ClassInstance] from.
     * @return the [ClassInstance].
     */
    public override fun create(value: T): ClassInstance {
        return super.create(value)
    }
}