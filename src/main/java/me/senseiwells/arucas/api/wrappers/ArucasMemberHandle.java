package me.senseiwells.arucas.api.wrappers;

import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.lang.invoke.MethodHandle;

public class ArucasMemberHandle {
	final String name;
	final MethodHandle setter;
	final MethodHandle getter;
	final boolean isStatic;
	final boolean isFinal;
	
	public ArucasMemberHandle(String name, MethodHandle getter, MethodHandle setter, boolean isStatic, boolean isFinal) {
		this.name = name;
		this.getter = getter;
		this.setter = setter;
		this.isStatic = isStatic;
		this.isFinal = isFinal;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isStatic() {
		return this.isStatic;
	}
	
	public boolean isFinal() {
		return this.isFinal;
	}
	
	public Value<?> get(IArucasWrappedClass parent) {
		try {
			Value<?> value;
			if (this.isStatic) {
				value = (Value<?>) this.getter.invoke();
			}
			else {
				value = (Value<?>) this.getter.invoke(parent);
			}
			
			return value == null ? NullValue.NULL : value;
		}
		catch (Throwable ignore) {
			return NullValue.NULL;
		}
	}
	
	public boolean set(IArucasWrappedClass parent, Value<?> value) {
		if (this.isFinal) {
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
