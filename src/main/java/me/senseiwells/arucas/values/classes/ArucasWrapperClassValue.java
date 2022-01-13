package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.values.Value;

import java.util.HashMap;
import java.util.Map;

public class ArucasWrapperClassValue extends ArucasClassValue {
	private final IArucasWrappedClass wrapperClass;
	private final Map<String, WrapperArucasClassDefinition.FieldBoolean> fieldMap;

	public ArucasWrapperClassValue(WrapperArucasClassDefinition arucasClass, IArucasWrappedClass wrapperClass) {
		super(arucasClass);
		this.wrapperClass = wrapperClass;
		this.fieldMap = new HashMap<>();
	}

	public void addField(String name, WrapperArucasClassDefinition.FieldBoolean fieldBoolean) {
		this.fieldMap.put(name, fieldBoolean);
	}

	@Override
	public boolean isAssignable(String name) {
		WrapperArucasClassDefinition.FieldBoolean fieldBoolean = this.fieldMap.get(name);
		return fieldBoolean != null && !fieldBoolean.isFinal();
	}

	@Override
	public boolean setMember(String name, Value<?> value) {
		if (this.fieldMap.containsKey(name)) {
			WrapperArucasClassDefinition.FieldBoolean fieldBoolean = this.fieldMap.get(name);
			if (fieldBoolean.isFinal()) {
				return false;
			}
			try {
				fieldBoolean.field().set(this.wrapperClass, value);
				return true;
			}
			catch (IllegalAccessException e) {
				this.fieldMap.remove(name);
			}
		}
		return false;
	}

	@Override
	public Value<?> getMember(String name) {
		if (this.fieldMap.containsKey(name)) {
			try {
				return (Value<?>) this.fieldMap.get(name).field().get(this.wrapperClass);
			}
			catch (IllegalAccessException e) {
				this.fieldMap.remove(name);
			}
		}
		return this.getAllMembers().get(name);
	}
}
