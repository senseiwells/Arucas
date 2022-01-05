package me.senseiwells.arucas.values.wrapper;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.nodes.DirectAccessNode;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassDefinition;
import me.senseiwells.arucas.values.functions.WrapperClassMemberFunction;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ArucasWrapper {
	private final IArucasWrappedClass value;
	private final ArucasClassDefinition classDefinition;
	private final Class<?> clazz;
	
	private ArucasWrapper(IArucasWrappedClass value) {
		this.value = value;
		this.clazz = value.getClass();
		this.classDefinition = new ArucasClassDefinition(value.getName());
		
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
		System.out.printf("     p: %d\n", parameterLength);
		WrapperClassMemberFunction function = new WrapperClassMemberFunction(this.value, method.getName(), parameterLength, isStatic, handle);
		
		if (isStatic) {
//			UserDefinedFunction function = new UserDefinedFunction(method.getName(), stringParameters, ISyntax.empty());
//			function.complete(createWrapperNode(method.getName(), handle, parameterLength, true));
			this.classDefinition.addStaticMethod(function);
		} else {
//			ClassMemberFunction function = new ClassMemberFunction(method.getName(), stringParameters, ISyntax.empty());
//			function.complete(createWrapperNode(method.getName(), handle, parameterLength, false));
			this.classDefinition.addMethod(function);
		}
		
		return true;
	}
	
	private Node createWrapperNode(String methodName, MethodHandle handle, final int parameters, boolean isStatic) {
		Token methodToken = new Token(Token.Type.IDENTIFIER, methodName, ISyntax.empty());
		return new DirectAccessNode(methodToken, NullValue.NULL) {
			@Override
			public Value<?> visit(Context context) throws CodeError, ThrowValue {
				// TODO: Figure out a better solution to get the parameters
				//       If we can get direct access to the parameters buffer
				//       we could skip some branches and context lookups. This
				//       would make wrapper classes much faster than other types
				//       of calls. The only problem is that we need to make
				//       wrapper classes a subclass of `ArucasClassValue` for
				//       them to be completely unique.
				//
				// TODO: To fix this we need to make a more robust system for
				//       class creation.
				//
				
				Object[] args = new Object[1 + parameters];
				if (isStatic) {
					args[0] = context;
					for (int i = 1; i < parameters; i++) {
						args[i] = context.getVariable(Integer.toString(i));
					}
				} else {
					args[0] = ArucasWrapper.this.value;
					args[1] = context;
					for (int i = 1; i < parameters; i++) {
						args[i + 1] = context.getVariable(Integer.toString(i));
					}
				}
				
				try {
					throw new ThrowValue.Return((Value<?>)handle.invokeWithArguments(args));
				}
				catch (CodeError | ThrowValue t) {
					throw t;
				}
				catch (Throwable t) {
					throw new RuntimeError(t.getMessage(), this.syntaxPosition, context);
				}
			}
		};
	}
	
	public ArucasClassDefinition getClassDefinition() {
		return this.classDefinition;
	}
	
	public static ArucasClassDefinition createWrapper(IArucasWrappedClass value) {
		ArucasWrapper wrapper = new ArucasWrapper(value);
		return wrapper.getClassDefinition();
	}
}
