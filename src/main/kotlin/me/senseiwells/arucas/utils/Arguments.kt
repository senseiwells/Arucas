package me.senseiwells.arucas.utils

import me.senseiwells.arucas.api.ArucasAPI
import me.senseiwells.arucas.builtin.*
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.extensions.JavaDef
import me.senseiwells.arucas.functions.builtin.BuiltInFunction
import kotlin.reflect.KClass

/**
 * This class provides all the context for [BuiltInFunction]
 * calls. it provides the arguments passed into the function,
 * the interpreter that it was called from, and the [BuiltInFunction]
 * that was actually called.
 *
 * This class also holds methods that are helpful in collecting the
 * arguments that you need for your function, you can use the
 * indexes of the arguments however it is advised that you
 * 'pop' each argument one by one.
 *
 * This can be done through the `next` methods:
 *
 * ```kotlin
 * val arguments = /* get arguments */
 *
 * // Gets the next argument as a regular ClassInstance
 * arguments.next() // -> ClassInstance
 *
 * // Gets the next argument as a ClassInstance only if
 * // it is of the given type, otherwise an error is thrown
 * arguments.next(StringDef::class) // -> ClassInstance (That is of StringDef)
 *
 * // Gets the next argument as its primitive value only
 * // if it is of the given type, otherwise an error is thrown
 * arguments.nextPrimitive(StringDef::class) // -> String
 *
 * // Checks whether the next argument to be popped
 * // is of a given type, true if yes, false otherwise
 * arguments.isNext(StringDef::class) // -> Boolean
 *
 * // Checks whether the next argument is either an enum
 * // or a string, then returns the string constant, otherwise throws
 * arguments.nextConstant() // -> String
 * ```
 */
@Suppress("UNUSED")
open class Arguments(
    val arguments: List<ClassInstance>,
    val interpreter: Interpreter,
    val function: BuiltInFunction,
) {
    private var index = 0

    /**
     * @return The current api.
     */
    fun api(): ArucasAPI {
        return this.interpreter.api
    }

    /**
     * @return The size of the arguments.
     */
    fun size(): Int {
        return this.arguments.size
    }

    /**
     * This sets the index of the argument iterator.
     *
     * @param index The new index.
     * @return This.
     */
    fun setIndex(index: Int): Arguments {
        this.checkBounds(index)
        this.index = index
        return this
    }

    /**
     * Resets the index to 0.
     *
     * @return This.
     */
    fun resetIndex() = this.setIndex(0)

    /**
     * Checks whether the current argument iterator index is valid.
     *
     * @return Whether the index is valid.
     */
    fun hasNext(): Boolean {
        return this.index < this.size()
    }

    /**
     * This skips the current index of the argument iterator.
     *
     * @return This.
     */
    fun skip(): Arguments {
        this.checkBounds(this.index++)
        return this
    }

    /**
     * This gets the remaining ClassInstances in the argument iterator as a list.
     *
     * @return The remaining ClassInstances.
     */
    fun getRemaining(): List<ClassInstance> {
        return this.arguments.subList(this.index, this.size())
    }

    /**
     * This gets the ClassInstance at a given index in the arguments.
     *
     * @param index The index of the ClassInstance to get.
     * @return The ClassInstance at the given index.
     */
    fun get(index: Int): ClassInstance {
        if (index < 0 || index >= this.size()) {
            throw IllegalArgumentException("Index ${displayedIndex(index)} out of bounds, incorrect amount of arguments")
        }
        return this.arguments[index]
    }

    /**
     * This gets the ClassInstance at a given index in the arguments,
     * ensuring it is of a specific type or inherits that specific
     * type otherwise it throws an error.
     *
     * @param index The index of the ClassInstance to get.
     * @param definition The definition required.
     * @return The ClassInstance.
     */
    fun get(index: Int, definition: ClassDefinition): ClassInstance {
        val value = this.get(index)
        if (value.isOf(definition)) {
            return value
        }
        runtimeError("Must pass ${definition.name} into parameter ${this.displayedIndex(index)} for '${this.getFunctionName()}'")
    }

    /**
     * This gets the ClassInstance at a given index in the arguments,
     * ensuring it is of a specific primitive type or inherits that
     * specific primitive type otherwise it throws an error.
     *
     * @param index The index of the ClassInstance to get.
     * @param clazz The primitive class required.
     * @return The ClassInstance.
     */
    fun <T: PrimitiveDefinition<*>> get(index: Int, clazz: Class<T>): ClassInstance {
        return this.get(index, this.interpreter.getPrimitive(clazz))
    }

    /**
     * This gets the ClassInstance at a given index in the arguments,
     * ensuring it is of a specific primitive type or inherits that
     * specific primitive type otherwise it throws an error.
     *
     * @param index The index of the ClassInstance to get.
     * @param clazz The primitive class required.
     * @return The ClassInstance.
     */
    fun <T: PrimitiveDefinition<*>> get(index: Int, clazz: KClass<T>): ClassInstance {
        return this.get(index, this.interpreter.getPrimitive(clazz))
    }

    /**
     * This gets the primitive value at a given index in the arguments.
     *
     * @param index The index of the primitive value to get.
     * @param definition The definition required.
     */
    fun <T: Any> getPrimitive(index: Int, definition: PrimitiveDefinition<T>): T {
        return this.get(index, definition).asPrimitive(definition)
    }

    /**
     * This gets the primitive value at a given index in the arguments.
     *
     * @param index The index of the primitive value to get.
     * @param clazz The primitive class required.
     */
    fun <T: PrimitiveDefinition<V>, V: Any> getPrimitive(index: Int, clazz: Class<T>): V {
        return this.getPrimitive(index, this.interpreter.getPrimitive(clazz))
    }

    /**
     * This gets the primitive value at a given index in the arguments.
     *
     * @param index The index of the primitive value to get.
     * @param clazz The primitive class required.
     */
    fun <T: PrimitiveDefinition<V>, V: Any> getPrimitive(index: Int, clazz: KClass<T>): V {
        return this.getPrimitive(index, this.interpreter.getPrimitive(clazz))
    }

    /**
     * This gets the next ClassInstance in the argument iterator,
     * and increments the index by one.
     *
     * @return The next ClassInstance.
     */
    fun next(): ClassInstance {
        return this.get(this.index++)
    }

    /**
     * This gets the next ClassInstance in the argument iterator,
     * and increments the index by one and ensuring that it is of a specific
     * type or inherits that specific type otherwise it throws an error.
     *
     * @param definition The definition required.
     * @return The next ClassInstance.
     */
    fun next(definition: ClassDefinition): ClassInstance {
        return this.get(this.index++, definition)
    }

    /**
     * This gets the next ClassInstance in the argument iterator,
     * and increments the index by one and ensuring that it is of a specific
     * primitive type or inherits that specific primitive type otherwise it throws an error.
     *
     * @param clazz The primitive class required.
     * @return The next ClassInstance.
     */
    fun <T: PrimitiveDefinition<*>> next(clazz: Class<T>): ClassInstance {
        return this.get(this.index++, clazz)
    }

    /**
     * This gets the next ClassInstance in the argument iterator,
     * and increments the index by one and ensuring that it is of a specific
     * primitive type or inherits that specific primitive type otherwise it throws an error.
     *
     * @param clazz The primitive class required.
     * @return The next ClassInstance.
     */
    fun <T: PrimitiveDefinition<*>> next(clazz: KClass<T>): ClassInstance {
        return this.get(this.index++, clazz)
    }

    /**
     * This gets the next primitive value in the argument iterator.
     *
     * @param definition The definition required.
     * @return The next primitive value.
     */
    fun <T: Any> nextPrimitive(definition: PrimitiveDefinition<T>): T {
        return this.getPrimitive(this.index++, definition)
    }

    /**
     * This gets the next primitive value in the argument iterator.
     *
     * @param clazz The primitive class required.
     * @return The next primitive value.
     */
    fun <T: PrimitiveDefinition<V>, V: Any> nextPrimitive(clazz: Class<T>): V {
        return this.getPrimitive(this.index++, clazz)
    }

    /**
     * This gets the next primitive value in the argument iterator.
     *
     * @param clazz The primitive class required.
     * @return The next primitive value.
     */
    fun <T: PrimitiveDefinition<V>, V: Any> nextPrimitive(clazz: KClass<T>): V {
        return this.getPrimitive(this.index++, clazz)
    }

    /**
     * This checks whether the next ClassInstance (if there is one) in the
     * argument iterator is of a specific type or inherits that specific type.
     *
     * @return Whether the next ClassInstance is of the correct type.
     */
    fun isNext(definition: ClassDefinition): Boolean {
        return this.index < this.size() && this.get(this.index).isOf(definition)
    }

    /**
     * This checks whether the next ClassInstance (if there is one) in the
     * argument iterator is of a specific primitive type or inherits that specific type.
     *
     * @return Whether the next ClassInstance is of the correct type.
     */
    fun <T: PrimitiveDefinition<*>> isNext(clazz: Class<T>): Boolean {
        return this.index < this.size() && this.get(this.index).isOf(this.interpreter.getPrimitive(clazz))
    }

    /**
     * This checks whether the next ClassInstance (if there is one) in the
     * argument iterator is of a specific primitive type or inherits that specific type.
     *
     * @return Whether the next ClassInstance is of the correct type.
     */
    fun <T: PrimitiveDefinition<*>> isNext(clazz: KClass<T>): Boolean {
        return this.index < this.size() && this.get(this.index).isOf(this.interpreter.getPrimitive(clazz))
    }

    /**
     * This gets the next constant, either an Arucas String or an Enum
     * and returns it as a [String], otherwise throws an error.
     *
     * @return the next constant.
     */
    fun nextConstant(): String {
        val index = this.index
        val instance = this.next()
        instance.getPrimitive(StringDef::class)?.let { return it }
        instance.getPrimitive(EnumDef::class)?.let { return it.name }
        runtimeError("Must pass a String or Enum into parameter ${this.displayedIndex(index)} for '${this.getFunctionName()}'")
    }

    // Some functions to retrieve the basic built in types.

    fun nextString(): ClassInstance {
        return this.next(StringDef::class)
    }

    fun nextNumber(): ClassInstance {
        return this.next(NumberDef::class)
    }

    fun nextBoolean(): ClassInstance {
        return this.next(BooleanDef::class)
    }

    fun nextFunction(): ClassInstance {
        return this.next(FunctionDef::class)
    }

    fun nextList(): ClassInstance {
        return this.next(ListDef::class)
    }

    fun nextMap(): ClassInstance {
        return this.next(MapDef::class)
    }

    fun nextSet(): ClassInstance {
        return this.next(SetDef::class)
    }

    fun nextIterator(): ClassInstance {
        return this.next(IterableDef::class)
    }

    fun nextCollection(): ClassInstance {
        return this.next(CollectionDef::class)
    }

    fun nextError(): ClassInstance {
        return this.next(ErrorDef::class)
    }

    fun nextJava(): ClassInstance {
        return this.next(JavaDef::class)
    }

    fun nextType(): ClassInstance {
        return this.next(TypeDef::class)
    }

    protected open fun displayedIndex(index: Int) = index + 1

    protected open fun getFunctionName(): String = "${this.function.name}::${this.displayedIndex(this.function.count - 1)}"

    private fun checkBounds(index: Int) {
        if (index < 0 || index >= this.size()) {
            throw IllegalArgumentException("Index ${displayedIndex(index)} out of bounds")
        }
    }

    class Member(
        arguments: List<ClassInstance>,
        interpreter: Interpreter,
        function: BuiltInFunction,
    ): Arguments(arguments, interpreter, function) {
        override fun displayedIndex(index: Int): Int {
            return index
        }

        override fun getFunctionName(): String {
            return "<${this.arguments[0].definition.name}>.${super.getFunctionName()}"
        }
    }
}