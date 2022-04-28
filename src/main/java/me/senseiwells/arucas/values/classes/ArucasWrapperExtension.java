package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.wrappers.*;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.WrapperClassMemberFunction;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ArucasWrapperExtension {
	private final WrapperClassDefinition classDefinition;
	private final Class<? extends IArucasWrappedClass> clazz;

	private ArucasWrapperExtension(Supplier<IArucasWrappedClass> supplier) {
		IArucasWrappedClass value = supplier.get();
		this.clazz = value.getClass();
		this.classDefinition = new WrapperClassDefinition(getWrapperName(this.clazz), supplier);
		this.init();
	}

	private void init() {
		Class<?> clazz = this.clazz;
		if (clazz.getAnnotation(ArucasClass.class) == null) {
			throw new RuntimeException("Wrapper class '%s' was not annotated with @ArucasClass".formatted(clazz.getSimpleName()));
		}

		List<Class<?>> classList = new ArrayList<>();
		while (clazz != Object.class) {
			classList.add(0, clazz);
			clazz = clazz.getSuperclass();
		}
		for (Class<?> each : classList) {
			this.define(each);
		}
	}

	private void define(Class<?> clazz) {
		boolean isThisClass = clazz == this.clazz;
		for (Method method : clazz.getMethods()) {
			ArucasFunction functionAnnotation = method.getAnnotation(ArucasFunction.class);
			if (functionAnnotation != null) {
				if (!this.addMethod(method)) {
					throw invalidWrapperMethod(clazz, method, "Invalid method signature");
				}
				continue;
			}

			ArucasOperator operatorAnnotation = method.getAnnotation(ArucasOperator.class);
			if (operatorAnnotation != null) {
				if (!this.addOperator(method, operatorAnnotation)) {
					throw invalidWrapperMethod(clazz, method, "Invalid operator signature");
				}
				continue;
			}

			if (!isThisClass) {
				continue;
			}

			ArucasConstructor constructorAnnotation = method.getAnnotation(ArucasConstructor.class);
			if (constructorAnnotation != null) {
				if (!this.addConstructor(method)) {
					throw invalidWrapperMethod(clazz, method, "Invalid constructor signature");
				}
			}
		}

		boolean hasDefinition = false;
		for (Field field : clazz.getFields()) {
			ArucasMember memberAnnotation = field.getAnnotation(ArucasMember.class);
			if (memberAnnotation != null) {
				if (!this.addMemberVariable(field, memberAnnotation)) {
					throw invalidWrapperField(clazz, field, "Invalid field signature");
				}
				continue;
			}

			if (!isThisClass) {
				continue;
			}
			ArucasDefinition definitionAnnotation = field.getAnnotation(ArucasDefinition.class);
			if (definitionAnnotation != null) {
				if (hasDefinition) {
					throw invalidWrapperField(clazz, field, "Already have definition reference");
				}
				int mods = field.getModifiers();
				if (!Modifier.isStatic(mods) || Modifier.isFinal(mods)) {
					throw invalidWrapperField(clazz, field, "Definition reference must be static and not final");
				}
				if (field.getType() != WrapperClassDefinition.class) {
					throw invalidWrapperField(clazz, field, "Definition must be of type 'WrapperClassDefinition'");
				}

				if (ExceptionUtils.runSafe(() -> field.set(null, this.classDefinition))) {
					hasDefinition = true;
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

		Class<?> returnType = method.getReturnType();
		if (isConstructor) {
			if (returnType != void.class) {
				throw invalidWrapperMethod(clazz, method, "Constructors must return void");
			}
		}
		else if (getMethodReturnType(clazz, method) == null) {
			throw invalidWrapperMethod(clazz, method, "Return type was not a subclass of Value, or void, or %s".formatted(clazz.getName()));
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
			return lookup.findVirtual(clazz, method.getName(), methodType);
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

	private static ArucasMethodHandle.ReturnType getMethodReturnType(Class<?> clazz, Method method) {
		Class<?> returnType = method.getReturnType();
		if (returnType == void.class) {
			return ArucasMethodHandle.ReturnType.VOID;
		}
		if (returnType == clazz) {
			return ArucasMethodHandle.ReturnType.THIS;
		}
		if (Value.class.isAssignableFrom(returnType)) {
			return ArucasMethodHandle.ReturnType.VALUE;
		}
		return null;
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

		ArucasMethodHandle methodHandle = new ArucasMethodHandle(handle, getMethodReturnType(this.clazz, method));
		WrapperClassMemberFunction function = new WrapperClassMemberFunction(method.getName(), parameterLength, isStatic, methodHandle);

		if (isStatic) {
			this.classDefinition.addStaticMethod(function);
		}
		else {
			this.classDefinition.addMethod(function);
		}

		return true;
	}

	/**
	 * Returns true if constructor was added.
	 */
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

		WrapperClassMemberFunction function = new WrapperClassMemberFunction("", parameterLength, false, new ArucasMethodHandle(handle, null));

		this.classDefinition.addConstructor(function);
		return true;
	}

	/**
	 * Returns true if the operator was added.
	 */
	private boolean addOperator(Method method, ArucasOperator operatorAnnotation) {
		if (Modifier.isStatic(method.getModifiers())) {
			throw invalidWrapperMethod(this.clazz, method, "Operator methods cannot be static");
		}
		MethodHandle handle = this.getMethodHandle(this.clazz, method, false, false);
		if (handle == null) {
			throw invalidWrapperMethod(this.clazz, method, "Failed to get method handle");
		}

		Class<?>[] parameters = method.getParameterTypes();
		final int parameterLength = parameters.length;

		Token.Type operatorToken = operatorAnnotation.value();

		RuntimeException noSuchOperator = invalidWrapperMethod(
			this.clazz,
			method,
			"No such operator %s with %d parameters".formatted(operatorToken, parameterLength)
		);

		switch (parameterLength) {
			case 1 -> {
				if (!Token.Type.OVERRIDABLE_UNARY_OPERATORS.contains(operatorToken)) {
					throw noSuchOperator;
				}
			}
			case 2 -> {
				if (!Token.Type.OVERRIDABLE_BINARY_OPERATORS.contains(operatorToken)) {
					throw noSuchOperator;
				}
			}
			default -> throw noSuchOperator;
		}

		ArucasMethodHandle methodHandle = new ArucasMethodHandle(handle, getMethodReturnType(this.clazz, method));
		WrapperClassMemberFunction function = new WrapperClassMemberFunction(method.getName(), parameterLength, false, methodHandle);

		this.classDefinition.addOperatorMethod(operatorToken, function);
		return true;
	}

	/**
	 * Returns the field handle if the field was valid.
	 */
	private ArucasMemberHandle getFieldHandle(Class<?> clazz, Field field, boolean isStatic, boolean isFinal, boolean isAssignable) {
		// We must force fields to be Value if they can be assigned in the language

		if (isAssignable) {
			if (field.getType() != Value.class) {
				throw invalidWrapperField(clazz, field, "Field type must be type Value");
			}
		}
		else if (!Value.class.isAssignableFrom(field.getType())) {
			throw invalidWrapperField(clazz, field, "Return type was not a subclass of Value");
		}

		try {
			MethodHandles.Lookup lookup = MethodHandles.publicLookup();
			return new ArucasMemberHandle(
				field.getName(),
				lookup.unreflectGetter(field),
				isFinal ? null : lookup.unreflectSetter(field),
				isStatic,
				!isFinal && isAssignable
			);
		}
		catch (IllegalAccessException ignored) {
			throw invalidWrapperField(clazz, field, "Failed to get field handle");
		}
	}

	/**
	 * Returns true if the member was added.
	 */
	private boolean addMemberVariable(Field field, ArucasMember memberAnnotation) {
		int modifiers = field.getModifiers();
		if (!Modifier.isPublic(modifiers)) {
			throw invalidWrapperField(this.clazz, field, "Field is not public");
		}

		final boolean isStatic = Modifier.isStatic(modifiers);
		final boolean isFinal = Modifier.isFinal(modifiers);

		ArucasMemberHandle handle = this.getFieldHandle(this.clazz, field, isStatic, isFinal, memberAnnotation.assignable());

		if (isStatic) {
			this.classDefinition.addStaticField(handle);
		}
		else {
			this.classDefinition.addField(handle);
		}

		return true;
	}

	private WrapperClassDefinition getClassDefinition() {
		return this.classDefinition;
	}

	public static WrapperClassDefinition createWrapper(Supplier<IArucasWrappedClass> value) {
		ArucasWrapperExtension wrapper = new ArucasWrapperExtension(value);
		return wrapper.getClassDefinition();
	}

	public static String getWrapperName(Class<? extends IArucasWrappedClass> clazz) {
		ArucasClass wrapper = clazz.getAnnotation(ArucasClass.class);
		return wrapper == null ? null : wrapper.name();
	}
}
