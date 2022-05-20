package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.docs.ClassDoc;
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

	public Context getContext() {
		return this.context;
	}

	public ISyntax getPosition() {
		return this.function.getPosition();
	}

	public List<Value> getAll() {
		return this.arguments;
	}

	public RuntimeError getError(String details) {
		return this.function.getError(this.context, details);
	}

	public RuntimeError getError(String details, Object... objects) {
		return this.function.getError(this.context, details, objects);
	}

	public RuntimeError getError(String details, Value... values) throws CodeError {
		Object[] strings = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			strings[i] = values[i].getAsString(this.context);
		}
		return this.getError(details, strings);
	}

	public Arguments set(int index) {
		this.index = index;
		return this;
	}

	public Arguments skip() {
		this.index++;
		return this;
	}

	public Value get(int index) throws RuntimeError {
		if (index < 0 || index >= this.size()) {
			throw this.function.getError(this.context, "Index %d out of bounds, incorrect amount of parameters", index);
		}
		return this.arguments.get(index);
	}

	public Object getVal(int index) throws RuntimeError {
		return this.get(index).getValue();
	}

	public <T extends Value> T get(int index, Class<T> type) throws RuntimeError {
		Value value = this.get(index);
		if (!type.isInstance(value)) {
			ClassDoc doc = type.getAnnotation(ClassDoc.class);
			String className = doc == null ? type.getSimpleName() : doc.name();
			throw this.function.getError(
				this.context, "Must pass %s into parameter %d for %s()",
				className, index + 1, this.function.getName()
			);
		}
		@SuppressWarnings("unchecked")
		T typedValue = (T) value;
		return typedValue;
	}

	public <S, T extends GenericValue<S>> S getVal(int index, Class<T> type) throws RuntimeError {
		return this.get(index, type).getValue();
	}

	public <T extends IArucasWrappedClass> T getWrapper(int index, Class<T> type) throws RuntimeError {
		WrapperClassValue wrapperValue = this.get(index, WrapperClassValue.class);
		return wrapperValue.getWrapper(type);
	}

	public Value getNext() throws RuntimeError {
		return this.get(this.index++);
	}

	public <T extends Value> T getNext(Class<T> type) throws RuntimeError {
		return this.get(this.index++, type);
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

	public <S, T extends GenericValue<S>> S getNextVal(Class<T> type) throws RuntimeError {
		return this.getNext(type).getValue();
	}

	public List<Value> getRemaining() {
		return this.arguments.subList(this.index, this.size());
	}

	public Arguments reset() {
		this.index = 0;
		return this;
	}

	public int size() {
		return this.arguments.size();
	}
}
