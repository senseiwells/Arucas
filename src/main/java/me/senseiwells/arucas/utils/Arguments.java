package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.WrapperClassValue;
import me.senseiwells.arucas.values.functions.mod.FunctionValue;

import java.util.List;

@SuppressWarnings("unused")
public class Arguments {
	private final Context context;
	private final FunctionValue function;
	private final List<Value> arguments;

	public Arguments(Context context, FunctionValue function, List<Value> arguments) {
		this.context = context;
		this.function = function;
		this.arguments = arguments;
	}

	public Context getContext() {
		return this.context;
	}

	public FunctionValue getFunction() {
		return this.function;
	}

	public List<Value> getAll() {
		return this.arguments;
	}

	public Value get(int index) throws RuntimeError {
		if (index < 0 || index >= this.size()) {
			throw this.function.getError(this.context, "Index %d out of bounds", index);
		}
		return this.arguments.get(index);
	}

	public <T extends Value> T get(int index, Class<T> type) throws RuntimeError {
		Value value = this.get(index);
		if (type.isInstance(value)) {
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

	public <T extends IArucasWrappedClass> T getWrapper(int index, Class<T> type) throws RuntimeError {
		WrapperClassValue wrapperValue = this.get(index, WrapperClassValue.class);
		return wrapperValue.getWrapper(type);
	}

	public Value getFirst() throws RuntimeError {
		return this.get(0);
	}

	public <T extends Value> T getFirst(Class<T> type) throws RuntimeError {
		return this.get(0, type);
	}

	public Value getSecond() throws RuntimeError {
		return this.get(1);
	}

	public <T extends Value> T getSecond(Class<T> type) throws RuntimeError {
		return this.get(1, type);
	}

	public Value getThird() throws RuntimeError {
		return this.get(2);
	}

	public <T extends Value> T getThird(Class<T> type) throws RuntimeError {
		return this.get(2, type);
	}

	public Value getFourth() throws RuntimeError {
		return this.get(3);
	}

	public <T extends Value> T getFourth(Class<T> type) throws RuntimeError {
		return this.get(3, type);
	}

	public Value getFifth() throws RuntimeError {
		return this.get(4);
	}

	public <T extends Value> T getFifth(Class<T> type) throws RuntimeError {
		return this.get(4, type);
	}

	public int size() {
		return this.arguments.size();
	}
}
