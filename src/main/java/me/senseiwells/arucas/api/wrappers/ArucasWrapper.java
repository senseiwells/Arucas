package me.senseiwells.arucas.api.wrappers;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.WrapperArucasClassDefinition;
import me.senseiwells.arucas.values.functions.WrapperClassMemberFunction;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
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
		this.clazz = this.value.getClass();
		this.classDefinition = new WrapperArucasClassDefinition(this.value, supplier);
		
		for (Method method : this.clazz.getMethods()) {
			ArucasFunction functionAnnotation = method.getAnnotation(ArucasFunction.class);
			if (functionAnnotation != null) {
				if (!this.addMethod(method)) {
					throw invalidWrapperMethod(this.clazz, method, "Invalid method signature");
				}
				continue;
			}
			ArucasConstructor constructorAnnotation = method.getAnnotation(ArucasConstructor.class);
			if (constructorAnnotation != null) {
				if (!this.addConstructor(method)) {
					throw invalidWrapperMethod(this.clazz, method, "Invalid constructor signature");
				}
			}
		}

		for (Field field : this.clazz.getFields()) {
			ArucasMember memberAnnotation = field.getAnnotation(ArucasMember.class);
			if (memberAnnotation != null) {
				if (!this.addMemberVariable(field)) {
					throw invalidWrapperField(this.clazz, field, "Invalid field signature");
				}
			}
		}
	}
	
	/**
	 * Returns the method handle if the method was valid.
	 */
	private MethodHandle getMethodHandle(Class<?> clazz, Method method, boolean isStatic, boolean isConstructor) {
		Class<?>[] parameters = method.getParameterTypes();
		
		// Make sure that the first parameter is Context
		if (parameters.length < 1 || parameters[0] != Context.class) {
			throw invalidWrapperMethod(clazz, method, "First parameter was not Context");
		}

		if (isConstructor) {
			if (method.getReturnType() != void.class) {
				throw invalidWrapperMethod(clazz, method, "Constructors must return void");
			}
		}
		else if (!Value.class.isAssignableFrom(method.getReturnType())) {
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
			}
			else {
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

	private static RuntimeException invalidWrapperField(Class<?> clazz, Field field, String message) {
		return new RuntimeException("Invalid wrapper field '%s:%s'. %s".formatted(clazz, field.getName(), message));
	}

	/**
	 * Returns true if this method was added.
	 */
	private boolean addMethod(Method method) {
		final boolean isStatic = Modifier.isStatic(method.getModifiers());
		MethodHandle handle = this.getMethodHandle(this.clazz, method, isStatic, false);
		if (handle == null) {
			throw invalidWrapperMethod(this.clazz, method, "Failed to get method handle");
		}
		
		Class<?>[] parameters = method.getParameterTypes();
		final int parameterLength = parameters.length - (isStatic ? 1 : 0);
		
		System.out.printf("Method: %s%s::%s (%s)\n", isStatic ? "static " : "", this.clazz.getSimpleName(), method.getName(), handle);
		WrapperClassMemberFunction function = new WrapperClassMemberFunction(method.getName(), parameterLength, isStatic, handle);
		
		if (isStatic) {
			this.classDefinition.addStaticMethod(function);
		}
		else {
			this.classDefinition.addMethod(function);
		}
		
		return true;
	}

	private boolean addConstructor(Method method) {
		if (Modifier.isStatic(method.getModifiers())) {
			throw invalidWrapperMethod(this.clazz, method, "Constructors cannot be static");
		}
		MethodHandle handle = this.getMethodHandle(this.clazz, method, false, true);
		if (handle == null) {
			throw invalidWrapperMethod(this.clazz, method, "Failed to get method handle");
		}

		Class<?>[] parameters = method.getParameterTypes();
		final int parameterLength = parameters.length;

		System.out.printf("Constructor: %s::%s (%s)\n", this.clazz.getSimpleName(), method.getName(), handle);
		WrapperClassMemberFunction function = new WrapperClassMemberFunction(this.value.getName(), parameterLength, false, handle);

		this.classDefinition.addConstructor(function);
		return true;
	}

	private boolean addMemberVariable(Field field) {
		int modifiers = field.getModifiers();
		if (!Modifier.isPublic(modifiers)) {
			throw invalidWrapperField(this.clazz, field, "Field is not public");
		}
		final boolean isStatic = Modifier.isStatic(modifiers);
		final boolean isFinal = Modifier.isFinal(modifiers);
		try {
			Object object = field.get(isStatic ? null : this.value);
			if (!(object instanceof Value<?>)) {
				throw invalidWrapperField(this.clazz, field, "Field is not Value type");
			}
			if (isStatic) {
				this.classDefinition.addStaticField(field, isFinal);
			}
			else {
				this.classDefinition.addField(field, isFinal);
			}
		}
		catch (IllegalAccessException e) {
			throw invalidWrapperField(this.clazz, field, "Field is not accessible");
		}
		return true;
	}
	
	public WrapperArucasClassDefinition getClassDefinition() {
		return this.classDefinition;
	}
	
	public static WrapperArucasClassDefinition createWrapper(Supplier<IArucasWrappedClass> value) {
		ArucasWrapper wrapper = new ArucasWrapper(value);
		return wrapper.getClassDefinition();
	}
}
