package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.wrappers.ArucasMemberHandle;
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
		ArucasMemberHandle handle = this.getHandle(name);
		return handle != null && !handle.isFinal();
	}

	@Override
	public boolean setMember(String name, Value<?> value) {
		ArucasMemberHandle handle = this.getHandle(name);
		if (handle != null) {
			return handle.set(this.wrapperClass, value);
		}
		
		return false;
	}

	@Override
	public Value<?> getMember(String name) {
		ArucasMemberHandle handle = this.getHandle(name);
		if (handle != null) {
			return handle.get(this.wrapperClass);
		}
		return this.getAllMembers().get(name);
	}

	private ArucasMemberHandle getHandle(String name) {
		return ((WrapperArucasClassDefinition) this.value).getMemberHandle(name);
	}
}
