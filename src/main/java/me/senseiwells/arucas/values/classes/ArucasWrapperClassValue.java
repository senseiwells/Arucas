package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.values.Value;

public class ArucasWrapperClassValue extends ArucasClassValue {
	private final IArucasWrappedClass wrapperClass;

	public ArucasWrapperClassValue(WrapperArucasClassDefinition arucasClass, IArucasWrappedClass wrapperClass) {
		super(arucasClass);
		this.wrapperClass = wrapperClass;
	}

	@Override
	public boolean isAssignable(String name) {
		WrapperArucasClassDefinition.FieldBoolean fieldBoolean = this.getField(name);
		return fieldBoolean != null && !fieldBoolean.isFinal();
	}

	@Override
	public boolean setMember(String name, Value<?> value) {
		WrapperArucasClassDefinition.FieldBoolean fieldBoolean = this.getField(name);
		if (fieldBoolean != null) {
			if (fieldBoolean.isFinal()) {
				return false;
			}
			try {
				fieldBoolean.field().set(this.wrapperClass, value);
				return true;
			}
			catch (IllegalAccessException ignored) { }
		}
		return false;
	}

	@Override
	public Value<?> getMember(String name) {
		WrapperArucasClassDefinition.FieldBoolean fieldBoolean = this.getField(name);
		if (fieldBoolean != null) {
			try {
				return (Value<?>) fieldBoolean.field().get(this.wrapperClass);
			}
			catch (IllegalAccessException ignored) { }
		}
		return this.getAllMembers().get(name);
	}

	private WrapperArucasClassDefinition.FieldBoolean getField(String name) {
		return ((WrapperArucasClassDefinition) this.value).getField(name);
	}
}
