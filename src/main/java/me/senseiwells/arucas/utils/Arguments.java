package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.classes.WrapperClassValue;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.List;

@SuppressWarnings("unused")
public class Arguments {
	private final Context context;
	private final FunctionValue function;
	private final List<Value> arguments;
	private int index;

	public Arguments(Context context, FunctionValue function, List<Value> arguments) {
		this.context = context;
		this.function = function;
		this.arguments = arguments;
		this.index = 0;
	}

	/**
	 * @return The context of the function
	 */
	public Context getContext() {
		return this.context;
	}

	/**
	 * @return The position of the function
	 */
	public ISyntax getPosition() {
		return this.function.getPosition();
	}

	/**
	 * @return All the arguments passed in as arguments
	 */
	public List<Value> getAll() {
		return this.arguments;
	}

	/**
	 * Creates a RuntimeError with the given details,
	 * using the function's position and context
	 *
	 * @param details the details of the error
	 * @return the error
	 */
	public RuntimeError getError(String details) {
		return this.function.getError(this.context, details);
	}

	/**
	 * Creates a RuntimeError with the given details,
	 * formatted with the given values, using the
	 * function's position and context
	 *
	 * @param details the details of the error
	 * @param objects the values to format the error with
	 * @return the error
	 */
	public RuntimeError getError(String details, Object... objects) {
		return this.function.getError(this.context, details, objects);
	}

	/**
	 * Creates a CodeError with the given details,
	 * using the function's position and context
	 *
	 * @param details the details of the error
	 * @param values  the values to format the error with
	 * @return the error
	 * @throws CodeError if the values error while converting to string
	 */
	public RuntimeError getError(String details, Value... values) throws CodeError {
		Object[] strings = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			strings[i] = values[i].getAsString(this.context);
		}
		return this.getError(details, strings);
	}

	/**
	 * Casts a value to a class in the
	 * context of the arguments context
	 * and syntax position
	 *
	 * @param value the value to cast
	 * @param clazz the class to cast to
	 * @param <T>   the type of class
	 * @return the casted value
	 */
	public <T extends Value> T castAs(Value value, Class<T> clazz) {
		return value.castAs(this.context, clazz, this.getPosition());
	}

	/**
	 * This sets the index of the argument iterator
	 *
	 * @param index the new index
	 * @return this
	 */
	public Arguments set(int index) {
		this.index = index;
		return this;
	}

	/**
	 * This skips the current index of the argument iterator
	 *
	 * @return this
	 */
	public Arguments skip() {
		this.index++;
		return this;
	}

	/**
	 * This gets the Value at a given index in the arguments
	 *
	 * @param index the index of the Value to get
	 * @return the Value at the given index
	 * @throws RuntimeError if the index is out of bounds
	 */
	public Value get(int index) throws RuntimeError {
		if (index < 0 || index >= this.size()) {
			throw this.function.getError(this.context, "Index %d out of bounds, incorrect amount of parameters", this.modifyIndex(index));
		}
		return this.arguments.get(index);
	}

	/**
	 * This gets the Value at the current index in the arguments
	 *
	 * @param index the index of the Value to get
	 * @return the Value at the current index
	 * @throws RuntimeError if the index is out of bounds
	 */
	public Object getGenericValue(int index) throws RuntimeError {
		return this.get(index).getValue();
	}

	/**
	 * This gets the Value at a given index in the arguments,
	 * and converts it to a specific type, otherwise throwing
	 * a RuntimeError for wrong type
	 *
	 * @param index the index of the Value to get
	 * @param type  the type to convert the Value to
	 * @param <T>   the type to convert the Value to
	 * @return the converted Value
	 * @throws RuntimeError if the index is out of bounds, or the Value is not the correct type
	 */
	public <T extends Value> T get(int index, Class<T> type) throws RuntimeError {
		Value value = this.get(index);
		if (!type.isInstance(value)) {
			value = this.castAs(value, type);
			if (value == null) {
				throw this.function.getError(
					this.context, "Must pass %s into parameter %d for %s()",
					type.getSimpleName(), this.modifyIndex(index), this.function.getName()
				);
			}
		}
		return type.cast(value);
	}

	/**
	 * This gets any type of Value at a given index in the
	 * arguments, this is intended for getting values that
	 * implement interfaces, if not an instance then it will
	 * throw a RuntimeError for wrong type
	 *
	 * @param index the index of the Value to get
	 * @param type  the type of to convert the Value to
	 * @param <T>   the type to convert the Value to
	 * @return the converted Value
	 * @throws RuntimeError if the index is out of bounds, or the Value is not the correct type
	 */
	public <T> T getAny(int index, Class<T> type) throws RuntimeError {
		Value value = this.get(index);
		if (!type.isInstance(value)) {
			throw this.function.getError(
				this.context, "Must pass %s into parameter %d for %s()",
				type.getSimpleName(), this.modifyIndex(index), this.function.getName()
			);
		}
		return type.cast(value);
	}

	/**
	 * Gets the Value at the current index in the arguments,
	 * and converts it to a specific type, otherwise throwing
	 * a RuntimeError for wrong type
	 *
	 * @param index the index of the Value to get
	 * @param type  the type to convert the Value to
	 * @param <S>   the wrapped value type
	 * @param <T>   the Value type
	 * @return the converted Value
	 * @throws RuntimeError if the index is out of bounds, or the Value is not the correct type
	 */
	public <S, T extends GenericValue<S>> S getGenericValue(int index, Class<T> type) throws RuntimeError {
		return this.get(index, type).getValue();
	}

	/**
	 * This gets the Value at a given index in the arguments,
	 * and gets its Java value, otherwise throwing
	 * a RuntimeError for wrong type
	 *
	 * @param index the index of the Value to get
	 * @param type  the type to convert the Value to
	 * @param <T>   the type to convert the Value to
	 * @return the converted Value
	 * @throws RuntimeError if the index is out of bounds, or the Value is not the correct type
	 */
	public <T> T getAsValue(int index, Class<T> type) throws RuntimeError {
		Object object = this.get(index).asJavaValue();
		if (!type.isInstance(object)) {
			throw this.function.getError(
				this.context, "Must pass '%s' into parameter %d for %s()",
				type.getSimpleName(), index + 1, this.function.getName()
			);
		}
		return type.cast(object);
	}

	/**
	 * Gets a wrapped Value at the current index in the arguments,
	 * and converts it to a specific type, otherwise throwing
	 * a RuntimeError for wrong type
	 *
	 * @param index the index of the Value to get
	 * @param type  the type to convert the Value to
	 * @param <T>   the Wrapper type
	 * @return the wrapper class value
	 * @throws RuntimeError if the index is out of bounds, or the Value is not the correct type
	 */
	public <T extends IArucasWrappedClass> T getWrapper(int index, Class<T> type) throws RuntimeError {
		WrapperClassValue wrapperValue = this.get(index, WrapperClassValue.class);
		return wrapperValue.getWrapper(type);
	}

	/**
	 * This gets the next Value in the argument iterator, and increments the index
	 *
	 * @return the next Value in the argument iterator
	 * @throws RuntimeError if there are no more Values in the argument iterator
	 */
	public Value getNext() throws RuntimeError {
		return this.get(this.index++);
	}

	/**
	 * This gets the next Value in the argument iterator, and increments the index,
	 * and converts it to a specific type, otherwise throwing
	 * a RuntimeError for wrong type
	 *
	 * @param type the type to convert the Value to
	 * @param <T>  the type to convert the Value to
	 * @return the converted Value
	 * @throws RuntimeError if there are no more Values in the argument iterator, or the Value is not the correct type
	 */
	public <T extends Value> T getNext(Class<T> type) throws RuntimeError {
		return this.get(this.index++, type);
	}

	/**
	 * This gets the next Value in the argument iterator, and increments the index,
	 * and converts it to a specific type, otherwise throwing
	 * a RuntimeError for wrong type
	 *
	 * @param type the type to convert the Value to
	 * @param <T>  the type to convert the Value to
	 * @return the converted Value
	 * @throws RuntimeError if there are no more Values in the argument iterator, or the Value is not the correct type
	 */
	public <T> T getAnyNext(Class<T> type) throws RuntimeError {
		return this.getAny(this.index++, type);
	}

	public boolean isNext(Class<? extends Value> type) throws RuntimeError {
		return this.castAs(this.get(this.index), type) != null;
	}

	public BooleanValue getNextBoolean() throws RuntimeError {
		return this.getNext(BooleanValue.class);
	}

	public StringValue getNextString() throws RuntimeError {
		return this.getNext(StringValue.class);
	}

	public NumberValue getNextNumber() throws RuntimeError {
		return this.getNext(NumberValue.class);
	}

	public ListValue getNextList() throws RuntimeError {
		return this.getNext(ListValue.class);
	}

	public MapValue getNextMap() throws RuntimeError {
		return this.getNext(MapValue.class);
	}

	public SetValue getNextSet() throws RuntimeError {
		return this.getNext(SetValue.class);
	}

	public FunctionValue getNextFunction() throws RuntimeError {
		return this.getNext(FunctionValue.class);
	}

	public <T extends IArucasWrappedClass> T getNextWrapper(Class<T> type) throws RuntimeError {
		WrapperClassValue wrapper = this.getNext(WrapperClassValue.class);
		return wrapper.getWrapper(type);
	}

	public <S, T extends GenericValue<S>> S getNextGeneric(Class<T> type) throws RuntimeError {
		return this.getGenericValue(this.index++, type);
	}

	public <T> T getNextAsValue(Class<T> type) throws RuntimeError {
		return this.getAsValue(this.index++, type);
	}

	public <T extends Value> T find(Class<T> type) {
		for (Value value : this.arguments) {
			if (type.isInstance(value)) {
				return type.cast(value);
			}
		}
		return null;
	}

	/**
	 * Gets the remaining Values in the argument iterator as a list
	 *
	 * @return the remaining Values in the argument iterator as a list
	 */
	public List<Value> getRemaining() {
		return this.arguments.subList(this.index, this.size());
	}

	/**
	 * Resets the index to 0
	 *
	 * @return this
	 */
	public Arguments reset() {
		this.index = 0;
		return this;
	}

	/**
	 * @return the size of the arguments
	 */
	public int size() {
		return this.arguments.size();
	}

	protected int modifyIndex(int index) {
		return index + 1;
	}

	/**
	 * This class is for Member functions, since 'this'
	 * is passed in as the first argument we don't want
	 * to include it in the index
	 */
	public static class Member extends Arguments {
		public Member(Context context, FunctionValue function, List<Value> arguments) {
			super(context, function, arguments);
		}

		@Override
		protected int modifyIndex(int index) {
			return index;
		}
	}
}
