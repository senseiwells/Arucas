package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.extensions.util.JavaValue;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.values.Value;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReflectionUtils {
	private static final Map<MethodId, Method> methodCache = new HashMap<>();

	public static Value callMethodFromNameAndArgs(Class<?> callingClass, Object callingObject, String methodName, List<Value> arguments, ISyntax syntaxPosition, Context context) throws CodeError {
		MethodId methodId = new MethodId(callingClass, methodName, arguments.size());
		Object[] parameters = new Object[arguments.size()];

		Method wantedMethod = methodCache.get(methodId);
		if (wantedMethod == null) {
			Class<?>[] types = new Class[arguments.size()];

			for (int i = 0; i < arguments.size(); i++) {
				parameters[i] = arguments.get(i).asJavaValue();
				if (parameters[i] != null) {
					types[i] = parameters[i].getClass();
					continue;
				}
				types[i] = Object.class;
			}

			// This won't work if the parameter has primitive parameters since it converted to Wrappers
			// This will be fine since #getMethodSlow will still resolve the correct method...
			wantedMethod = ExceptionUtils.catchAsNull(() -> callingClass.getMethod(methodName, types));
			if (wantedMethod == null) {
				wantedMethod = ExceptionUtils.catchAsNull(() -> callingClass.getMethod(methodName, Object[].class));
				if (wantedMethod == null) {
					wantedMethod = getMethodSlow(callingClass, callingObject, methodName, parameters.length);
					if (wantedMethod == null) {
						throw new RuntimeError(
							"No such method '%s' with %d parameters exists for '%s'".formatted(methodName, parameters.length, callingClass.getSimpleName()),
							syntaxPosition, context
						);
					}
				}
			}
			methodCache.put(methodId, wantedMethod);
		}
		else {
			for (int i = 0; i < arguments.size(); i++) {
				parameters[i] = arguments.get(i).asJavaValue();
			}
		}

		return invokeMethod(context, syntaxPosition, wantedMethod, callingObject, parameters);
	}

	public static Value callMethodFromJavaValue(JavaValue javaValue, String methodName, List<Value> arguments, ISyntax syntaxPosition, Context context) throws CodeError {
		Object callingObject = javaValue.asJavaValue();
		Class<?> callingClass = callingObject.getClass();

		Method wantedMethod = getMethod(callingClass, callingObject, methodName, arguments.size());
		if (wantedMethod == null) {
			return null;
		}

		// This method doesn't visit arguments unless it knows that there is a valid method
		// that can be called, we don't want arguments to be visited for no reason
		Object[] objects = new Object[arguments.size()];
		for (int i = 0; i < arguments.size(); i++) {
			objects[i] = arguments.get(i).asJavaValue();
		}

		return invokeMethod(context, syntaxPosition, wantedMethod, callingObject, objects);
	}

	public static MethodHandle getMethodHandle(Method method) {
		return ExceptionUtils.catchAsNull(() -> {
			MethodHandles.Lookup lookup = MethodHandles.publicLookup();
			MethodType methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());

			return Modifier.isStatic(method.getModifiers()) ?
				lookup.findStatic(method.getDeclaringClass(), method.getName(), methodType) :
				lookup.findVirtual(method.getDeclaringClass(), method.getName(), methodType);
		});
	}

	public static Value constructFromArgs(Class<?> constructingClass, List<Value> arguments, ISyntax syntaxPosition, Context context) throws CodeError {
		Object[] objects = new Object[arguments.size()];
		Class<?>[] parameters = new Class[arguments.size()];

		for (int i = 0; i < arguments.size(); i++) {
			objects[i] = arguments.get(i).asJavaValue();
			if (objects[i] != null) {
				parameters[i] = objects[i].getClass();
				continue;
			}
			parameters[i] = Object.class;
		}

		Constructor<?> constructor = ExceptionUtils.catchAsNull(() -> constructingClass.getConstructor(parameters));
		if (constructor == null) {
			constructor = ExceptionUtils.catchAsNull(() -> constructingClass.getConstructor(Object[].class));
			if (constructor == null) {
				constructor = getConstructorSlow(constructingClass, objects.length);
				if (constructor == null) {
					throw new RuntimeError(
						"No such constructor with %d parameters exists for '%s'".formatted(objects.length, constructingClass.getSimpleName()),
						syntaxPosition, context
					);
				}
			}
		}

		return invokeConstructor(context, syntaxPosition, constructor, objects);
	}

	public static Value getFieldFromName(Class<?> callingClass, Object callingObject, String fieldName, ISyntax syntaxPosition, Context context) throws RuntimeError {
		Field field = ExceptionUtils.catchAsNull(() -> callingClass.getField(fieldName));
		if (field != null) {
			return getField(context, syntaxPosition, field, callingObject);
		}
		throw new RuntimeError(
			"No such field '%s'".formatted(fieldName),
			syntaxPosition, context
		);
	}

	public static Value getFieldFromJavaValue(JavaValue javaValue, String fieldName, ISyntax syntaxPosition, Context context) {
		Object callingObject = javaValue.asJavaValue();
		Class<?> callingClass = callingObject.getClass();
		Field field = ExceptionUtils.catchAsNull(() -> callingClass.getField(fieldName));
		if (field != null) {
			return ExceptionUtils.catchAsNull(() -> getField(context, syntaxPosition, field, callingObject));
		}
		return null;
	}

	public static void setFieldFromName(Class<?> callingClass, Object callingObject, Object newValue, String fieldName, ISyntax syntaxPosition, Context context) throws RuntimeError {
		Field field = ExceptionUtils.catchAsNull(() -> callingClass.getField(fieldName));
		if (field != null) {
			setField(context, syntaxPosition, field, callingObject, newValue);
			return;
		}
		throw new RuntimeError(
			"No such field '%s'".formatted(fieldName),
			syntaxPosition, context
		);
	}

	public static boolean setFieldFromJavaValue(JavaValue javaValue, Value newValue, String fieldName, ISyntax syntaxPosition, Context context) {
		Object callingObject = javaValue.asJavaValue();
		Class<?> callingClass = callingObject.getClass();
		Field field = ExceptionUtils.catchAsNull(() -> callingClass.getField(fieldName));
		if (field != null) {
			return ExceptionUtils.runSafe(() -> setField(context, syntaxPosition, field, callingObject, newValue.asJavaValue()));
		}
		return false;
	}

	public static Method getMethod(Class<?> callingClass, Object callingObject, String methodName, int arguments) {
		MethodId methodId = new MethodId(callingClass, methodName, arguments);
		Method cachedMethod = methodCache.get(methodId);
		if (cachedMethod == null) {
			Method method = getMethodSlow(callingClass, callingObject, methodName, arguments);
			if (method != null) {
				methodCache.put(methodId, method);
			}
			return method;
		}
		return cachedMethod;
	}

	private static Method getMethodSlow(Class<?> callingClass, Object callingObject, String methodName, int arguments) {
		boolean isStatic = callingObject == null;
		for (Method method : callingClass.getMethods()) {
			boolean matchingStatic = Modifier.isStatic(method.getModifiers()) == isStatic;
			if (matchingStatic && (arguments < 0 || method.getParameterCount() == arguments) && method.getName().equals(methodName)) {
				Method accessible = getAccessibleMethod(callingClass, callingObject, method);
				return Objects.requireNonNullElse(accessible, method);
			}
		}
		return null;
	}

	private static Method getAccessibleMethod(Class<?> callingClass, Object callingObject, Method method) {
		// The direct method may be inaccessible (for example it's a member of an inaccessible inner class)
		// but accessible via an interface or a superclass. Notably this happens for synthetic lambda classes.
		// This is a workaround which looks for an accessible method in a superclass or interface.

		if (method.canAccess(callingObject)) {
			return method;
		}

		try {
			method = callingClass.getMethod(method.getName(), method.getParameterTypes());
			if (method.canAccess(callingObject)) {
				return method;
			}
		} catch (NoSuchMethodException e) {
			return null;
		}

		if (!callingClass.isInterface()) {
			Class<?> superClass = callingClass.getSuperclass();
			if (superClass != null) {
				Method accessible = getAccessibleMethod(superClass, callingObject, method);
				if (accessible != null) {
					return accessible;
				}
			}
		}

		for (Class<?> itf : callingClass.getInterfaces()) {
			Method accessible = getAccessibleMethod(itf, callingObject, method);
			if (accessible != null) {
				return accessible;
			}
		}

		return null;
	}

	private static Constructor<?> getConstructorSlow(Class<?> callingClass, int arguments) {
		for (Constructor<?> constructor : callingClass.getConstructors()) {
			if (constructor.getParameterCount() == arguments) {
				return constructor;
			}
		}
		return null;
	}

	private static Value invokeMethod(Context context, ISyntax syntaxPosition, Method method, Object object, Object... arguments) throws CodeError {
		try {
			return JavaValue.of(method.invoke(object, arguments));
		}
		catch (InvocationTargetException e) {
			Throwable throwable = e.getCause();
			if (throwable instanceof CodeError codeError) {
				throw codeError;
			}
			throw new RuntimeError(
				"Failed to call '%s': %s".formatted(method.getName(), throwable),
				syntaxPosition, context
			);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeError(
				"No access to the method '%s'".formatted(method.getName()),
				syntaxPosition, context
			);
		}
		catch (IllegalArgumentException e) {
			throw new RuntimeError(
				"Illegal Arguments for the method '%s': %s".formatted(method.getName(), e),
				syntaxPosition, context
			);
		}
	}

	private static Value invokeConstructor(Context context, ISyntax syntaxPosition, Constructor<?> constructor, Object... arguments) throws CodeError {
		try {
			return JavaValue.of(constructor.newInstance(arguments));
		}
		catch (InvocationTargetException e) {
			Throwable throwable = e.getCause();
			if (throwable instanceof CodeError codeError) {
				throw codeError;
			}
			throw new RuntimeError(
				"Failed to call '%s': %s".formatted(constructor.getName(), throwable),
				syntaxPosition, context
			);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeError(
				"No access to the constructor '%s'".formatted(constructor.getName()),
				syntaxPosition, context
			);
		}
		catch (IllegalArgumentException e) {
			throw new RuntimeError(
				"Illegal Arguments for the constructor '%s': %s".formatted(constructor.getName(), e),
				syntaxPosition, context
			);
		}
		catch (InstantiationException e) {
			throw new RuntimeError(
				"Failed to construct '%s': %s".formatted(constructor.getName(), e),
				syntaxPosition, context
			);
		}
	}

	private static Value getField(Context context, ISyntax syntaxPosition, Field field, Object object) throws RuntimeError {
		try {
			// Cannot do catchAsNull since actual return value may be null
			return JavaValue.of(field.get(object));
		}
		catch (IllegalAccessException e) {
			throw new RuntimeError(
				"No access to the field '%s'".formatted(field.getName()),
				syntaxPosition, context
			);
		}
	}

	private static void setField(Context context, ISyntax syntaxPosition, Field field, Object object, Object newValue) throws RuntimeError {
		Throwable throwable = ExceptionUtils.returnThrowable(() -> field.set(object, newValue));
		if (throwable != null) {
			throw new RuntimeError(
				"Failed to set field '%s' with value: %s".formatted(field.getName(), throwable),
				syntaxPosition, context
			);
		}
	}

	private static final class MethodId {
		private final Class<?> clazz;
		private final String name;
		private final int parameters;
		private final int hash;

		private MethodId(Class<?> clazz, String name, int parameters) {
			this.clazz = clazz;
			this.name = name;
			this.parameters = parameters;
			this.hash = (clazz.hashCode() - this.name.hashCode() * this.parameters) * 31;
		}

		@Override
		public int hashCode() {
			return this.hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof MethodId id) {
				return this.clazz == id.clazz && this.parameters == id.parameters && this.name.equals(id.name);
			}
			return false;
		}
	}
}
