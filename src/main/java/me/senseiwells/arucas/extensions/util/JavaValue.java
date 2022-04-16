package me.senseiwells.arucas.extensions.util;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.IArucasAPI;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.utils.ReflectionUtils;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.JavaFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.lang.reflect.Method;
import java.util.List;

public class JavaValue extends Value<Object> {
	private JavaValue(Object value) {
		super(value);
	}

	public static Value<?> of(Object value) {
		return value == null ? NullValue.NULL : new JavaValue(value);
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return this.value.toString();
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		return other instanceof JavaValue value && this.value.equals(value.value);
	}

	@Override
	public String getTypeName() {
		return "Java";
	}

	@Override
	public Value<Object> copy(Context context) throws CodeError {
		return new JavaValue(this.value);
	}

	@Override
	public final Object asJavaValue() {
		return this.value;
	}

	public static Class<?> getObfuscatedClass(Context context, ISyntax syntaxPosition, String name) throws RuntimeError {
		IArucasAPI api = context.getAPI();
		String remappedClassName = api.shouldObfuscate() ? api.obfuscate(name) : name;
		Class<?> clazz = ExceptionUtils.catchAsNull(() -> Class.forName(remappedClassName));
		if (clazz == null) {
			throw new RuntimeError(
				"No such class with '%s'".formatted(name),
				syntaxPosition, context
			);
		}
		return clazz;
	}

	public static String getObfuscatedMethodName(Context context, Class<?> clazz, String name) {
		if (context.getAPI().shouldObfuscate()) {
			String deobfuscatedClassName = context.getAPI().deobfuscate(clazz.getName());
			String fullMethodName = deobfuscatedClassName + "#" + name + "()";
			String obfuscatedMethod = context.getAPI().obfuscate(fullMethodName);
			return obfuscatedMethod.substring(obfuscatedMethod.lastIndexOf('#') + 1, obfuscatedMethod.length() - 2);
		}
		return name;
	}

	public static String getObfuscatedFieldName(Context context, Class<?> clazz, String name) {
		if (context.getAPI().shouldObfuscate()) {
			String deobfuscatedClassName = context.getAPI().deobfuscate(clazz.getName());
			String fullFieldName = deobfuscatedClassName + "#" + name;
			String obfuscatedField = context.getAPI().obfuscate(fullFieldName);
			return obfuscatedField.substring(obfuscatedField.lastIndexOf('#') + 1);
		}
		return name;
	}

	/**
	 * Java class for Arucas. This allows for direct interaction from Arucas to Java <br>
	 * Import the class with <code>import Java from util.Internal;</code>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasJavaClass extends ArucasClassExtension {
		public ArucasJavaClass() {
			super("Java");
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("doubleOf", "value", this::doubleOf),
				new BuiltInFunction("floatOf", "value", this::floatOf),
				new BuiltInFunction("longOf", "value", this::longOf),
				new BuiltInFunction("intOf", "value", this::intOf),
				new BuiltInFunction("shortOf", "value", this::shortOf),
				new BuiltInFunction("byteOf", "value", this::byteOf),
				new BuiltInFunction("charOf", "value", this::charOf),
				new BuiltInFunction("booleanOf", "value", this::booleanOf),
				new BuiltInFunction("valueOf", "value", this::of),
				new BuiltInFunction("classFromName", "className", this::classFromName),
				new BuiltInFunction("getStaticField", List.of("className", "fieldName"), this::getStaticField),
				new BuiltInFunction("setStaticField", List.of("className", "fieldName", "value"), this::setStaticField),
				new BuiltInFunction("getStaticMethodDelegate", List.of("className", "methodName", "parameters"), this::getStaticMethodDelegate),
				new BuiltInFunction("arrayWithSize", "size", this::arrayWithSize),
				new BuiltInFunction.Arbitrary("arrayOf", this::arrayOf),
				new BuiltInFunction.Arbitrary("callStaticMethod", this::callStaticMethod),
				new BuiltInFunction.Arbitrary("constructClass", this::constructClass)
			);
		}

		/**
		 * Name: <code>Java.doubleOf(num)</code> <br>
		 * Description: Creates a Java value double, to be used in Java <br>
		 * Parameter - Number: the number to convert to a Java double <br>
		 * Returns - Java: the double in Java wrapper <br>
		 * Example: <code>Java.doubleOf(1.0);</code>
		 */
		private Value<?> doubleOf(Context context, BuiltInFunction function) throws CodeError {
			NumberValue numberValue = function.getFirstParameter(context, NumberValue.class);
			return new JavaValue(numberValue.value);
		}

		/**
		 * Name: <code>Java.floatOf(num)</code> <br>
		 * Description: Creates a Java value float, to be used in Java, since
		 * floats cannot be explicitly declared in Arucas <br>
		 * Parameter - Number: the number to convert to a Java float <br>
		 * Returns - Java: the float in Java wrapper <br>
		 * Example: <code>Java.floatOf(1.0);</code>
		 */
		private Value<?> floatOf(Context context, BuiltInFunction function) throws CodeError {
			NumberValue numberValue = function.getFirstParameter(context, NumberValue.class);
			return new JavaValue(numberValue.value.floatValue());
		}

		/**
		 * Name: <code>Java.longOf(num)</code> <br>
		 * Description: Creates a Java value long, to be used in Java since
		 * longs cannot be explicitly declared in Arucas <br>
		 * Parameter - Number: the number to convert to a Java long <br>
		 * Returns - Java: the long in Java wrapper <br>
		 * Example: <code>Java.longOf(1000000000.0);</code>
		 */
		private Value<?> longOf(Context context, BuiltInFunction function) throws CodeError {
			NumberValue numberValue = function.getFirstParameter(context, NumberValue.class);
			return new JavaValue(numberValue.value.longValue());
		}

		/**
		 * Name: <code>Java.intOf(num)</code> <br>
		 * Description: Creates a Java value int, to be used in Java since
		 * ints cannot be explicitly declared in Arucas <br>
		 * Parameter - Number: the number to convert to a Java int <br>
		 * Returns - Java: the int in Java wrapper <br>
		 * Example: <code>Java.intOf(0xFF);</code>
		 */
		private Value<?> intOf(Context context, BuiltInFunction function) throws CodeError {
			NumberValue numberValue = function.getFirstParameter(context, NumberValue.class);
			return new JavaValue(numberValue.value.intValue());
		}

		/**
		 * Name: <code>Java.shortOf(num)</code> <br>
		 * Description: Creates a Java value short, to be used in Java since
		 * shorts cannot be explicitly declared in Arucas <br>
		 * Parameter - Number: the number to convert to a Java short <br>
		 * Returns - Java: the short in Java wrapper <br>
		 * Example: <code>Java.shortOf(0xFF);</code>
		 */
		private Value<?> shortOf(Context context, BuiltInFunction function) throws CodeError {
			NumberValue numberValue = function.getFirstParameter(context, NumberValue.class);
			return new JavaValue(numberValue.value.shortValue());
		}

		/**
		 * Name: <code>Java.byteOf(num)</code> <br>
		 * Description: Creates a Java value byte, to be used in Java since
		 * bytes cannot be explicitly declared in Arucas <br>
		 * Parameter - Number: the number to convert to a Java byte <br>
		 * Returns - Java: the byte in Java wrapper <br>
		 * Example: <code>Java.byteOf(0xFF);</code>
		 */
		private Value<?> byteOf(Context context, BuiltInFunction function) throws CodeError {
			NumberValue numberValue = function.getFirstParameter(context, NumberValue.class);
			return new JavaValue(numberValue.value.byteValue());
		}

		/**
		 * Name: <code>Java.charOf(string)</code> <br>
		 * Description: Creates a Java value char, to be used in Java since
		 * chars cannot be explicitly declared in Arucas <br>
		 * Parameter - String: the string with one character to convert to a Java char <br>
		 * Returns - Java: the char in Java wrapper <br>
		 * Throws - CodeError: if the string is not exactly one character long <br>
		 * Example: <code>Java.charOf("f");</code>
		 */
		private Value<?> charOf(Context context, BuiltInFunction function) throws CodeError {
			StringValue charValue = function.getFirstParameter(context, StringValue.class);
			if (charValue.value.length() != 1) {
				throw new RuntimeError("String must be 1 character long", function.syntaxPosition, context);
			}
			return new JavaValue(charValue.value.charAt(0));
		}

		/**
		 * Name: <code>Java.booleanOf(bool)</code> <br>
		 * Description: Creates a Java value boolean, to be used in Java <br>
		 * Parameter - Boolean: the boolean to convert to a Java boolean <br>
		 * Returns - Java: the boolean in Java wrapper <br>
		 * Example: <code>Java.booleanOf(true);</code>
		 */
		private Value<?> booleanOf(Context context, BuiltInFunction function) throws CodeError {
			BooleanValue booleanValue = function.getFirstParameter(context, BooleanValue.class);
			return new JavaValue(booleanValue.value);
		}

		/**
		 * Name: <code>Java.valueOf(value)</code> <br>
		 * Description: Converts any Arucas value into a Java value then wraps it
		 * in the Java wrapper and returns it <br>
		 * Parameter - Value: any value to get the Java value of <br>
		 * Returns - Java/Null: the Java wrapper value, null if argument was null <br>
		 * Example: <code>Java.valueOf("Hello World!");</code>
		 */
		private Value<?> of(Context context, BuiltInFunction function) {
			Value<?> value = function.getParameterValue(context, 0);
			return JavaValue.of(value.asJavaValue());
		}

		/**
		 * Name: <code>Java.classFromName(className)</code> <br>
		 * Description: Gets a Java class from the name of the class <br>
		 * Parameter - String: the name of the class you want to get <br>
		 * Returns - Java: the Java Class<?> value wrapped in the Java wrapper <br>
		 * Throws - Error: <code>"No such class with ..."</code> if the class is not found <br>
		 * Example: <code>Java.classFromName("java.util.ArrayList");</code>
		 */
		private Value<?> classFromName(Context context, BuiltInFunction function) throws CodeError {
			String name = function.getFirstParameter(context, StringValue.class).value;
			return new JavaValue(JavaValue.getObfuscatedClass(context, function.syntaxPosition, name));
		}

		/**
		 * Name: <code>Java.getStaticField(className, fieldName)</code> <br>
		 * Description: Gets a static field Java value from a Java class <br>
		 * Parameters - String, String: the name of the class, the name of the field <br>
		 * Returns - Java: the Java value of the field wrapped in the Java wrapper <br>
		 * Throws - Error: <code>"No such class with ..."</code> if the class is not found <br>
		 * Example: <code>Java.getStaticField("java.lang.Integer", "MAX_VALUE");</code>
		 */
		private Value<?> getStaticField(Context context, BuiltInFunction function) throws CodeError {
			String className = function.getFirstParameter(context, StringValue.class).value;
			String fieldName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			Class<?> clazz = JavaValue.getObfuscatedClass(context, function.syntaxPosition, className);
			fieldName = JavaValue.getObfuscatedFieldName(context, clazz, fieldName);
			return ReflectionUtils.getFieldFromName(clazz, null, fieldName, function.syntaxPosition, context);
		}

		/**
		 * Name: <code>Java.setStaticField(className, fieldName, newValue)</code> <br>
		 * Description: Sets a static field in a Java class with a new value, the type of the new
		 * value needs to match the type of the field, you can pass in Java wrapped values to
		 * guarantee type matching, they will be unwrapped, regular values will be converted <br>
		 * Parameters - String, String, Value: the name of the class, the name of the field, the new value <br>
		 * Throw - Error: <code>"No such class with ..."</code> if the class is not found <br>
		 * Example: <code>Java.setStaticField("java.lang.Integer", "MAX_VALUE", Java.intOf(100));</code>
		 */
		private Value<?> setStaticField(Context context, BuiltInFunction function) throws CodeError {
			String className = function.getFirstParameter(context, StringValue.class).value;
			String fieldName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			Value<?> value = function.getParameterValue(context, 2);
			Class<?> clazz = JavaValue.getObfuscatedClass(context, function.syntaxPosition, className);
			fieldName = JavaValue.getObfuscatedFieldName(context, clazz, fieldName);
			ReflectionUtils.setFieldFromName(clazz, null, value.asJavaValue(), fieldName, function.syntaxPosition, context);
			return NullValue.NULL;
		}

		/**
		 * Name: <code>Java.getStaticMethodDelegate(className, methodName, parameters)</code> <br>
		 * Description: Gets a static method delegate from a Java class, delegating the method is
		 * much faster than directly calling it since it uses MethodHandles, if you are repetitively
		 * calling a static method you should delegate it and call that delegate <br>
		 * Parameters - String, String, Number: the name of the class, the name of the method, the number of parameters <br>
		 * Returns - Function: the delegated Java method in an Arucas Function <br>
		 * Throws - Error: <code>"..."</code> if the class is not found or method cannot be found <br>
		 * Example: <code>Java.getStaticMethodDelegate("java.lang.Integer", "parseInt", 1);</code>
		 */
		private Value<?> getStaticMethodDelegate(Context context, BuiltInFunction function) throws CodeError {
			String className = function.getFirstParameter(context, StringValue.class).value;
			String methodName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			int parameters = function.getParameterValueOfType(context, NumberValue.class, 2).value.intValue();
			Class<?> clazz = JavaValue.getObfuscatedClass(context, function.syntaxPosition, className);
			methodName = getObfuscatedMethodName(context, clazz, methodName);
			Method method = ReflectionUtils.getMethodSlow(clazz, null, methodName, parameters);
			JavaFunction javaFunction = JavaFunction.of(method, null, function.syntaxPosition);
			if (javaFunction != null) {
				return javaFunction;
			}
			throw new RuntimeError(
				"No such method '%s' with %d parameters can be found".formatted(method, parameters),
				function.syntaxPosition, context
			);
		}

		/**
		 * Name: <code>Java.arrayWithSize(size)</code> <br>
		 * Description: Creates a Java Object array with a given size, the array is filled with null values
		 * by default and can be filled with any Java values, arrays cannot be expanded or shrunk, you cannot
		 * create a primitive array, you will need some more reflection to do that <br>
		 * Parameter - Number: the size of the array <br>
		 * Returns - Java: the Java Object array <br>
		 * Example: <code>Java.arrayWithSize(10);</code>
		 */
		private Value<?> arrayWithSize(Context context, BuiltInFunction function) throws CodeError {
			NumberValue size = function.getFirstParameter(context, NumberValue.class);
			if (size.value.intValue() < 0) {
				throw new RuntimeError("Cannot have a negative size array", function.syntaxPosition, context);
			}
			return new JavaValue(new Object[size.value.intValue()]);
		}

		/**
		 * Name: <code>Java.arrayOf(values...)</code> <br>
		 * Description: Creates a Java Object array with a given values, this will be the size of the array,
		 * again this cannot be used to create primitive arrays <br>
		 * Parameters - Value...: the values to add to the array <br>
		 * Returns - Java: the Java Object array <br>
		 * Example: <code>Java.arrayOf(1, 2, 3, "string!", false);</code>
		 */
		private Value<?> arrayOf(Context context, BuiltInFunction function) throws CodeError {
			ArucasList arucasList = function.getFirstParameter(context, ListValue.class).value;
			Object[] array = new Object[arucasList.size()];
			for (int i = 0; i < arucasList.size(); i++) {
				array[i] = arucasList.get(i).asJavaValue();
			}
			return new JavaValue(array);
		}

		/**
		 * Name: <code>Java.callStaticMethod(className, methodName, parameters...)</code> <br>
		 * Description: Calls a static method of a Java class, this is slower than delegating a method,
		 * but better for a one off call <br>
		 * Parameters - String, String, Value...: the name of the class, the name of the method, any parameters
		 * to call the method with, this can be none, a note - if you are calling a VarArg method then you must
		 * have your VarArg parameters in a Java Object array <br>
		 * Returns - Java: the return value of the method wrapped in the Java wrapper <br>
		 * Throws - Error: <code>"..."</code> if the class is not found or the parameters are incorrect <br>
		 * Example: <code>Java.callStaticMethod("java.lang.Integer", "parseInt", "123");</code>
		 */
		private Value<?> callStaticMethod(Context context, BuiltInFunction function) throws CodeError {
			ArucasList arguments = function.getFirstParameter(context, ListValue.class).value;
			if (arguments.size() < 2 || !(arguments.get(0) instanceof StringValue className) || !(arguments.get(1) instanceof StringValue methodName)) {
				throw new RuntimeError(
					"First parameter must be a class name and second parameter must be a method name",
					function.syntaxPosition, context
				);
			}
			Class<?> clazz = JavaValue.getObfuscatedClass(context, function.syntaxPosition, className.value);
			String name = getObfuscatedMethodName(context, clazz, methodName.value);
			arguments.remove(0);
			arguments.remove(0);
			return ReflectionUtils.callMethodFromNameAndArgs(clazz, null, name, arguments, function.syntaxPosition, context);
		}

		/**
		 * Name: <code>Java.constructClass(className, parameters...)</code> <br>
		 * Description: This constructs a Java class with specified class name and parameters <br>
		 * Parameters - String, Value...: the name of the class, any parameters to pass to the constructor,
		 * there may be no parameters, again if calling VarArgs constructor you must have your VarArg
		 * parameters in a Java Object array <br>
		 * Returns - Java: the constructed Java Object wrapped in the Java wrapper <br>
		 * Throws - Error: <code>"..."</code> if the class is not found or the parameters are incorrect <br>
		 * Example: <code>Java.constructClass("java.util.ArrayList");</code>
		 */
		private Value<?> constructClass(Context context, BuiltInFunction function) throws CodeError {
			ArucasList arguments = function.getFirstParameter(context, ListValue.class).value;
			if (arguments.size() < 1 || !(arguments.get(0) instanceof StringValue className)) {
				throw new RuntimeError(
					"First parameter must be a class name",
					function.syntaxPosition, context
				);
			}
			Class<?> clazz = JavaValue.getObfuscatedClass(context, function.syntaxPosition, className.value);
			arguments.remove(0);
			return ReflectionUtils.constructFromArgs(clazz, arguments, function.syntaxPosition, context);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("toArucas", this::toValue),
				new MemberFunction("getMethodDelegate", List.of("methodName", "parameters"), this::getMethodDelegate),
				new MemberFunction("getField", "fieldName", this::getJavaField, "You should get the field directly"),
				new MemberFunction("setField", List.of("fieldName", "value"), this::setJavaField, "You should set the field directly"),
				new MemberFunction.Arbitrary("callMethod", this::callMethodArbitrary, "You should call the method directly")
			);
		}

		/**
		 * Name: <code>&lt;Java>.toArucas()</code> <br>
		 * Description: This converts the Java value to an Arucas Value <br>
		 * Returns - Value: the Value in Arucas, this may still be of Java value if the value cannot be
		 * converted into an Arucas value, values like Strings, Numbers, Lists, etc... will be converted <br>
		 * Example: <code>Java.valueOf([1, 2, 3]).toArucas();</code>
		 */
		private Value<?> toValue(Context context, MemberFunction function) throws CodeError {
			JavaValue thisValue = function.getThis(context, JavaValue.class);
			return context.convertValue(thisValue.asJavaValue());
		}

		/**
		 * Name: <code>&lt;Java>.getMethodDelegate(methodName, parameters)</code> <br>
		 * Description: This returns a method delegate for the specified method name and parameters,
		 * delegating the method is much faster since it uses MethodHandles, so if you are calling
		 * a method repetitively it is faster to delegate the method and then call the delegate <br>
		 * Parameters - String, Number: the name of the method, the number of parameters <br>
		 * Returns - Function: the function containing the Java method delegate <br>
		 * Throws - Error: <code>"..."</code> if the method is not found <br>
		 * Example: <code>Java.valueOf("string!").getMethodDelegate("isBlank", 0);</code>
		 */
		private Value<?> getMethodDelegate(Context context, MemberFunction function) throws CodeError {
			JavaValue thisValue = function.getThis(context, JavaValue.class);
			String methodName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			int parameters = function.getParameterValueOfType(context, NumberValue.class, 2).value.intValue();
			Object callingObject = thisValue.asJavaValue();
			Class<?> callingClass = callingObject.getClass();
			methodName = getObfuscatedMethodName(context, callingClass, methodName);
			Method method = ReflectionUtils.getMethodSlow(callingClass, callingObject, methodName, parameters);
			JavaFunction javaFunction = JavaFunction.of(method, callingObject, function.syntaxPosition);
			if (javaFunction != null) {
				return javaFunction;
			}
			throw new RuntimeError(
				"No such method '%s' with %d parameters can be found".formatted(method, parameters),
				function.syntaxPosition, context
			);
		}

		/**
		 * Deprecated: You should call the method directly on the value: <code>Java.valueOf("").isBlank();</code> <br>
		 * Name: <code>&lt;Java>.callMethod(methodName, parameters...)</code> <br>
		 * Description: This calls the specified method with the specified parameters, this is slower
		 * than calling a delegate, this is the same speed as calling the method directly on the value however <br>
		 * Parameters - String, Value...: the name of the method, the parameters to call the method with,
		 * this may be none, a note - if you are calling a VarArgs method you must pass a Java
		 * Object array with your VarArg arguments <br>
		 * Returns - Java: the return value of the method call wrapped in the Java wrapper <br>
		 * Throws - Error: <code>"..."</code> if the method is not found <br>
		 * Example: <code>Java.valueOf("").callMethod("isBlank");</code>
		 */
		private Value<?> callMethodArbitrary(Context context, MemberFunction function) throws CodeError {
			JavaValue thisValue = function.getThis(context, JavaValue.class);
			ArucasList arguments = function.getParameterValueOfType(context, ListValue.class, 1).value;
			if (arguments.size() < 1 || !(arguments.get(0) instanceof StringValue methodName)) {
				throw new RuntimeError(
					"First parameter must be name of method",
					function.syntaxPosition, context
				);
			}
			// We don't have to copy the list since the list was never passed in, it was generated
			// Meaning nothing in Arucas should be pointing to this list, should be safe to just remove
			arguments.remove(0);

			Object callingObject = thisValue.asJavaValue();
			String name = getObfuscatedMethodName(context, callingObject.getClass(), methodName.value);
			return ReflectionUtils.callMethodFromNameAndArgs(callingObject.getClass(), callingObject, name, arguments, function.syntaxPosition, context);
		}

		/**
		 * Deprecated: You should call the method directly on the value:
		 * <code>Java.constructClass("me.senseiwells.impl.Test").A;</code> <br>
		 * Name: <code>&lt;Java>.getField(fieldName)</code> <br>
		 * Description: This returns the Java wrapped value of the specified field <br>
		 * Parameters - String: the name of the field <br>
		 * Returns - Java: the Java wrapped value of the field <br>
		 * Example: <code>Java.constructClass("me.senseiwells.impl.Test").getField("A");</code>
		 */
		private Value<?> getJavaField(Context context, MemberFunction function) throws CodeError {
			JavaValue thisValue = function.getThis(context, JavaValue.class);
			String fieldName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			Object callingObject = thisValue.asJavaValue();
			fieldName = getObfuscatedFieldName(context, callingObject.getClass(), fieldName);
			return ReflectionUtils.getFieldFromName(callingObject.getClass(), callingObject, fieldName, function.syntaxPosition, context);
		}

		/**
		 * Deprecated: You should assign the value directly on the value:
		 * <code>Java.constructClass("me.senseiwells.impl.Test").A = "Hello";</code> <br>
		 * Name: <code>&lt;Java>.setField(fieldName, value)</code> <br>
		 * Description: This sets the specified field to the specified value <br>
		 * Parameters - String, Value: the name of the field, the value to set the field to,
		 * the value type must match the type of the field <br>
		 * Example: <code>Java.constructClass("me.senseiwells.impl.Test").setField("A", "Hello");</code>
		 */
		private Value<?> setJavaField(Context context, MemberFunction function) throws CodeError {
			JavaValue thisValue = function.getThis(context, JavaValue.class);
			String fieldName = function.getParameterValueOfType(context, StringValue.class, 1).value;
			Value<?> newValue = function.getParameterValue(context, 2);
			Object callingObject = thisValue.asJavaValue();
			fieldName = getObfuscatedFieldName(context, callingObject.getClass(), fieldName);
			ReflectionUtils.setFieldFromName(callingObject.getClass(), callingObject, newValue.asJavaValue(), fieldName, function.syntaxPosition, context);
			return NullValue.NULL;
		}

		@Override
		public Class<JavaValue> getValueClass() {
			return JavaValue.class;
		}
	}
}
