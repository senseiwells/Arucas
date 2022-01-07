package me.senseiwells.arucas.values.wrapper;

import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperArucasClassDefinition;
import me.senseiwells.arucas.values.functions.WrapperClassMemberFunction;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

public class ArucasWrapper {
	private final IArucasWrappedClass value;
	private final WrapperArucasClassDefinition classDefinition;
	private final Class<?> clazz;
	
	private ArucasWrapper(Supplier<IArucasWrappedClass> supplier) {
		// TODO: Figure out how to get the class type from the wrapper
		this.value = supplier.get();
		this.clazz = value.getClass();
		this.classDefinition = new WrapperArucasClassDefinition(this.value.getName(), supplier);
		
		for (Method method : this.clazz.getMethods()) {
			ArucasFunction annotation = method.getAnnotation(ArucasFunction.class);
			if (annotation == null) continue;
			
			if (!this.addMethod(this.clazz, method, annotation)) {
				throw invalidWrapperMethod(this.clazz, method, "Invalid method signature");
			}
		}
	}
	
	/**
	 * Returns the method handle if the method was valid
	 */
	private MethodHandle getMethodHandle(Class<?> clazz, Method method, boolean isStatic) {
		Class<?>[] parameters = method.getParameterTypes();
		
		// Make sure that the first parameter is Context
		if (parameters.length < 1 || parameters[0] != Context.class) {
			throw invalidWrapperMethod(clazz, method, "First parameter was not Context");
		}
		
		if (!Value.class.isAssignableFrom(method.getReturnType())) {
			throw invalidWrapperMethod(clazz, method, "Return type was not a subclass of Value");
		}
		
		// Make sure that all parameters after the first one extends Value<?>
		for (int i = 1; i < parameters.length; i++) {
			Class<?> param = parameters[i];
			
			if (!Value.class.isAssignableFrom(param)) {
				throw invalidWrapperMethod(clazz, method, "Invalid parameter %d '%s' is not a subclass of Value".formatted(i - 1, param.getSimpleName()));
			}
		}
		
		if (this.classDefinition.hasMember(method.getName(), parameters.length - 1)) {
			throw invalidWrapperMethod(clazz, method, "This method has already been overloaded");
		}
		
		try {
			MethodHandles.Lookup lookup = MethodHandles.publicLookup();
			MethodType methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
			
			if (isStatic) {
				return lookup.findStatic(clazz, method.getName(), methodType);
			} else {
				return lookup.findVirtual(clazz, method.getName(), methodType);
			}
		}
		catch (NoSuchMethodException | IllegalAccessException ignored) {
			throw invalidWrapperMethod(clazz, method, "Failed to get method handle");
		}
	}
	
	private static RuntimeException invalidWrapperMethod(Class<?> clazz, Method method, String message) {
		return new RuntimeException("Invalid wrapper method '%s:%s'. %s".formatted(clazz, method.getName(), message));
	}
	
	/**
	 * Returns true if this method was added
	 */
	private boolean addMethod(Class<?> clazz, Method method, ArucasFunction annotation) {
		boolean isStatic = Modifier.isStatic(method.getModifiers());
		MethodHandle handle = this.getMethodHandle(clazz, method, isStatic);
		if (handle == null) {
			throw invalidWrapperMethod(clazz, method, "Failed to get method handle");
		}
		
		Class<?>[] parameters = method.getParameterTypes();
		final int parameterLength = parameters.length - (isStatic ? 1 : 0);
		
		System.out.printf("Method: %s%s::%s (%s)\n", isStatic ? "static " : "", clazz.getSimpleName(), method.getName(), handle);
		WrapperClassMemberFunction function = new WrapperClassMemberFunction(method.getName(), parameterLength, isStatic, handle);
		
		if (isStatic) {
			this.classDefinition.addStaticMethod(function);
		} else {
			this.classDefinition.addMethod(function);
		}
		
		return true;
	}
	
	public ArucasClassDefinition getClassDefinition() {
		return this.classDefinition;
	}
	
	public static ArucasClassDefinition createWrapper(Supplier<IArucasWrappedClass> value) {
		ArucasWrapper wrapper = new ArucasWrapper(value);
		return wrapper.getClassDefinition();
	}
}
