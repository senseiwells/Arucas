package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.wrappers.ArucasMemberHandle;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.values.Value;

import java.util.HashMap;
import java.util.Map;

public class ArucasWrapperClassValue extends ArucasClassValue {
	private final IArucasWrappedClass wrapperClass;
	private final Map<String, ArucasMemberHandle> fieldMap;

	public ArucasWrapperClassValue(WrapperArucasClassDefinition arucasClass, IArucasWrappedClass wrapperClass) {
		super(arucasClass);
		this.wrapperClass = wrapperClass;
		this.fieldMap = new HashMap<>();
	}

	@Override
	public boolean isAssignable(String name) {
		ArucasMemberHandle handle = this.fieldMap.get(name);
		return handle != null && !handle.isFinal();
	}

	@Override
	public boolean setMember(String name, Value<?> value) {
		ArucasMemberHandle handle = this.fieldMap.get(name);
		if (handle != null) {
			return handle.set(this.wrapperClass, value);
		}
		
		return false;
	}

	@Override
	public Value<?> getMember(String name) {
		ArucasMemberHandle handle = this.fieldMap.get(name);
		if (handle != null) {
			return handle.get(this.wrapperClass);
		}
		return this.getAllMembers().get(name);
	}
	
	public void addField(ArucasMemberHandle handle) {
		this.fieldMap.put(handle.getName(), handle);
	}
}
