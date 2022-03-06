package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.ArucasMemberHandle;
import me.senseiwells.arucas.api.wrappers.ArucasWrapperExtension;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

public class ArucasWrapperClassValue extends ArucasClassValue {
	private final IArucasWrappedClass wrapperClass;

	public ArucasWrapperClassValue(WrapperArucasClassDefinition arucasClass, IArucasWrappedClass wrapperClass) {
		super(arucasClass);
		this.wrapperClass = wrapperClass;
	}

	public <T extends IArucasWrappedClass> T getWrapper(Class<T> clazz) {
		if (!clazz.isInstance(this.wrapperClass)) {
			String wrapperName = ArucasWrapperExtension.getWrapperName(clazz);
			String thisWrapperName = ArucasWrapperExtension.getWrapperName(this.wrapperClass.getClass());
			throw new RuntimeException("Expected %s found %s".formatted(wrapperName, thisWrapperName));
		}
		return clazz.cast(this.wrapperClass);
	}

	public <T extends IArucasWrappedClass> T getWrapper(Class<T> clazz, ISyntax syntaxPosition, Context context) throws RuntimeError {
		if (!clazz.isInstance(this.wrapperClass)) {
			String wrapperName = ArucasWrapperExtension.getWrapperName(clazz);
			String thisWrapperName = ArucasWrapperExtension.getWrapperName(this.wrapperClass.getClass());
			throw new RuntimeError("Expected %s found %s".formatted(wrapperName, thisWrapperName), syntaxPosition, context);
		}
		return clazz.cast(this.wrapperClass);
	}

	@Override
	public boolean isAssignable(String name) {
		ArucasMemberHandle handle = this.getHandle(name);
		return handle != null && handle.isAssignable();
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
