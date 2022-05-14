package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.lang.invoke.MethodHandle;

public class ArucasMemberHandle {
	final String name;
	final MethodHandle setter;
	final MethodHandle getter;
	final boolean isStatic;
	final boolean isAssignable;

	public ArucasMemberHandle(String name, MethodHandle getter, MethodHandle setter, boolean isStatic, boolean isAssignable) {
		this.name = name;
		this.getter = getter;
		this.setter = setter;
		this.isStatic = isStatic;
		this.isAssignable = isAssignable;
	}

	public String getName() {
		return this.name;
	}

	public boolean isStatic() {
		return this.isStatic;
	}

	public boolean isAssignable() {
		return this.isAssignable;
	}

	public Value get(IArucasWrappedClass parent) {
		try {
			Value value;
			if (this.isStatic) {
				value = (Value) this.getter.invoke();
			}
			else {
				value = (Value) this.getter.invoke(parent);
			}

			return value == null ? NullValue.NULL : value;
		}
		catch (Throwable ignore) {
			return NullValue.NULL;
		}
	}

	public boolean set(IArucasWrappedClass parent, Value value) {
		if (!this.isAssignable) {
			return false;
		}

		try {
			if (this.isStatic) {
				this.setter.invoke(value == null ? NullValue.NULL : value);
			}
			else {
				this.setter.invoke(parent, value == null ? NullValue.NULL : value);
			}
		}
		catch (Throwable ignore) {
			return false;
		}

		return true;
	}
}
