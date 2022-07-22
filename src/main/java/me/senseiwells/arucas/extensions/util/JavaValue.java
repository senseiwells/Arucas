package me.senseiwells.arucas.extensions.util;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.IArucasAPI;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.JavaFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static me.senseiwells.arucas.utils.ValueTypes.*;

public class JavaValue extends GenericValue<Object> {
	private JavaValue(Object value) {
		super(value);
	}

	public static Value of(Object value) {
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
	public boolean isEquals(Context context, Value other) throws CodeError {
		return other instanceof JavaValue value && this.value.equals(value.value);
	}

	@Override
	public FunctionValue onMemberCall(Context context, String name, List<Value> arguments, ValueRef reference, ISyntax position) throws CodeError {
		FunctionValue function = context.getMemberFunction(this.getClass(), name, arguments.size() + 1);
		if (function == null) {
			// We check if there are any Java methods, we check this AFTER Arucas methods since
			// it's possible to call JavaMethods by using the Arucas function 'callJavaMethod'.
			String obfuscatedName = getObfuscatedMethodName(context, this.asJavaValue().getClass(), name);
			Value returnValue = ReflectionUtils.callMethodFromJavaValue(this, obfuscatedName, arguments, position, context);
			if (returnValue != null) {
				reference.set(returnValue);
				return null;
			}
		}
		arguments.add(0, this);
		return function;
	}

	@Override
	public Value onMemberAccess(Context context, String name, ISyntax position) throws CodeError {
		Object callingObject = this.asJavaValue();
		Class<?> callingClass = callingObject.getClass();
		String obfuscatedName = JavaValue.getObfuscatedFieldName(context, callingClass, name);

		Value value = ReflectionUtils.getFieldFromJavaValue(this, obfuscatedName, position, context);
		if (value != null) {
			return value;
		}

		obfuscatedName = JavaValue.getObfuscatedMethodName(context, callingClass, name);
		value = JavaFunction.of(
			ReflectionUtils.getMethod(callingClass, callingObject, obfuscatedName, -1),
			callingObject, position
		);
		if (value != null) {
			return value;
		}

		return super.onMemberAccess(context, name, position);
	}

	@Override
	public Value onMemberAssign(Context context, String name, Functions.UniFunction<Context, Value> valueGetter, ISyntax position) throws CodeError {
		Value value = valueGetter.apply(context);
		String obfuscatedFieldName = JavaValue.getObfuscatedFieldName(context, this.asJavaValue().getClass(), name);
		if (ReflectionUtils.setFieldFromJavaValue(this, value, obfuscatedFieldName, position, context)) {
			return value;
		}

		return super.onMemberAssign(context, name, valueGetter, position);
	}

	@Override
	public String getTypeName() {
		return JAVA;
	}

	@Override
	public GenericValue<Object> copy(Context context) throws CodeError {
		return new JavaValue(this.value);
	}

	@Override
	public final Object asJavaValue() {
		return this.value;
	}

	public static Class<?> getObfuscatedClass(Context context, ISyntax syntaxPosition, String name) throws RuntimeError {
		IArucasAPI api = context.getAPI();
		String remappedClassName = api.shouldObfuscate() ? api.obfuscateClassName(name) : name;
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
			return context.getAPI().obfuscateMethodName(clazz, name);
		}
		return name;
	}

	public static String getObfuscatedFieldName(Context context, Class<?> clazz, String name) {
		if (context.getAPI().shouldObfuscate()) {
			return context.getAPI().obfuscateFieldName(clazz, name);
		}
		return name;
	}


	@ClassDoc(
		name = JAVA,
		desc = "This allows for direct interaction from Arucas to Java",
		importPath = "util.Internal"
	)
	public static class ArucasJavaClass extends ArucasClassExtension {
		public ArucasJavaClass() {
			super(JAVA);
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("doubleOf", 1, this::doubleOf),
				BuiltInFunction.of("floatOf", 1, this::floatOf),
				BuiltInFunction.of("longOf", 1, this::longOf),
				BuiltInFunction.of("intOf", 1, this::intOf),
				BuiltInFunction.of("shortOf", 1, this::shortOf),
				BuiltInFunction.of("byteOf", 1, this::byteOf),
				BuiltInFunction.of("charOf", 1, this::charOf),
				BuiltInFunction.of("booleanOf", 1, this::booleanOf),
				BuiltInFunction.of("valueOf", 1, this::of),
				BuiltInFunction.of("classFromName", 1, this::classFromName),
				BuiltInFunction.of("getStaticField", 2, this::getStaticField),
				BuiltInFunction.of("setStaticField", 3, this::setStaticField),
				BuiltInFunction.of("getStaticMethodDelegate", 3, this::getStaticMethodDelegate),
				BuiltInFunction.of("arrayWithSize", 1, this::arrayWithSize),
				BuiltInFunction.of("doubleArray", 1, this::doubleArray),
				BuiltInFunction.of("floatArray", 1, this::floatArray),
				BuiltInFunction.of("longArray", 1, this::longArray),
				BuiltInFunction.of("intArray", 1, this::intArray),
				BuiltInFunction.of("shortArray", 1, this::shortArray),
				BuiltInFunction.of("byteArray", 1, this::byteArray),
				BuiltInFunction.of("charArray", 1, this::charArray),
				BuiltInFunction.of("booleanArray", 1, this::booleanArray),
				BuiltInFunction.of("runnableOf", 1, this::runnableOf),
				BuiltInFunction.of("consumerOf", 1, this::consumerOf),
				BuiltInFunction.of("supplierOf", 1, this::supplierOf),
				BuiltInFunction.of("functionOf", 1, this::functionOf),
				BuiltInFunction.of("predicateOf", 1, this::predicateOf),
				BuiltInFunction.arbitrary("arrayOf", this::arrayOf),
				BuiltInFunction.arbitrary("callStaticMethod", this::callStaticMethod),
				BuiltInFunction.arbitrary("constructClass", this::constructClass)
			);
		}

		@FunctionDoc(
			isStatic = true,
			name = "doubleOf",
			desc = "Creates a Java value double, to be used in Java",
			params = {NUMBER, "num", "the number to convert to a Java double"},
			returns = {JAVA, "the double in Java wrapper"},
			example = "Java.doubleOf(1.0);"
		)
		private Value doubleOf(Arguments arguments) throws CodeError {
			NumberValue numberValue = arguments.getNext(NumberValue.class);
			return new JavaValue(numberValue.value);
		}

		@FunctionDoc(
			isStatic = true,
			name = "floatOf",
			desc = "Creates a Java value float, to be used in Java, since floats cannot be explicitly declared in Arucas",
			params = {NUMBER, "num", "the number to convert to a Java float"},
			returns = {JAVA, "the float in Java wrapper"},
			example = "Java.floatOf(1.0);"
		)
		private Value floatOf(Arguments arguments) throws CodeError {
			NumberValue numberValue = arguments.getNext(NumberValue.class);
			return new JavaValue(numberValue.value.floatValue());
		}

		@FunctionDoc(
			isStatic = true,
			name = "longOf",
			desc = "Creates a Java value long, to be used in Java since longs cannot be explicitly declared in Arucas",
			params = {NUMBER, "num", "the number to convert to a Java long"},
			returns = {JAVA, "the long in Java wrapper"},
			example = "Java.longOf(1000000000.0);"
		)
		private Value longOf(Arguments arguments) throws CodeError {
			NumberValue numberValue = arguments.getNext(NumberValue.class);
			return new JavaValue(numberValue.value.longValue());
		}

		@FunctionDoc(
			isStatic = true,
			name = "intOf",
			desc = "Creates a Java value int, to be used in Java since ints cannot be explicitly declared in Arucas",
			params = {NUMBER, "num", "the number to convert to a Java int"},
			returns = {JAVA, "the int in Java wrapper"},
			example = "Java.intOf(0xFF);"
		)
		private Value intOf(Arguments arguments) throws CodeError {
			NumberValue numberValue = arguments.getNext(NumberValue.class);
			return new JavaValue(numberValue.value.intValue());
		}

		@FunctionDoc(
			isStatic = true,
			name = "shortOf",
			desc = "Creates a Java value short, to be used in Java since shorts cannot be explicitly declared in Arucas",
			params = {NUMBER, "num", "the number to convert to a Java short"},
			returns = {JAVA, "the short in Java wrapper"},
			example = "Java.shortOf(0xFF);"
		)
		private Value shortOf(Arguments arguments) throws CodeError {
			NumberValue numberValue = arguments.getNext(NumberValue.class);
			return new JavaValue(numberValue.value.shortValue());
		}

		@FunctionDoc(
			isStatic = true,
			name = "byteOf",
			desc = "Creates a Java value byte, to be used in Java since bytes cannot be explicitly declared in Arucas",
			params = {NUMBER, "num", "the number to convert to a Java byte"},
			returns = {JAVA, "the byte in Java wrapper"},
			example = "Java.byteOf(0xFF);"
		)
		private Value byteOf(Arguments arguments) throws CodeError {
			NumberValue numberValue = arguments.getNext(NumberValue.class);
			return new JavaValue(numberValue.value.byteValue());
		}

		@FunctionDoc(
			isStatic = true,
			name = "charOf",
			desc = "Creates a Java value char, to be used in Java since chars cannot be explicitly declared in Arucas",
			params = {STRING, "string", "the string with one character to convert to a Java char"},
			returns = {JAVA, "the char in Java wrapper"},
			throwMsgs = "String must be 1 character long",
			example = "Java.charOf('f');"
		)
		private Value charOf(Arguments arguments) throws CodeError {
			StringValue charValue = arguments.getNext(StringValue.class);
			if (charValue.value.length() != 1) {
				throw arguments.getError("String must be 1 character long");
			}
			return new JavaValue(charValue.value.charAt(0));
		}

		@FunctionDoc(
			isStatic = true,
			name = "booleanOf",
			desc = "Creates a Java value boolean, to be used in Java",
			params = {BOOLEAN, "bool", "the boolean to convert to a Java boolean"},
			returns = {JAVA, "the boolean in Java wrapper"},
			example = "Java.booleanOf(true);"
		)
		private Value booleanOf(Arguments arguments) throws CodeError {
			BooleanValue booleanValue = arguments.getNext(BooleanValue.class);
			return new JavaValue(booleanValue.value);
		}

		@FunctionDoc(
			isStatic = true,
			name = "valueOf",
			desc = "Converts any Arucas value into a Java value then wraps it in the Java wrapper and returns it",
			params = {ANY, "value", "any value to get the Java value of"},
			returns = {JAVA, "the Java wrapper value", "null if argument was null"},
			example = "Java.valueOf('Hello World!');"
		)
		private Value of(Arguments arguments) throws RuntimeError {
			Value value = arguments.getNext();
			return JavaValue.of(value.asJavaValue());
		}

		@FunctionDoc(
			isStatic = true,
			name = "classFromName",
			desc = "Gets a Java class from the name of the class",
			params = {STRING, "className", "the name of the class you want to get"},
			returns = {JAVA, "the Java Class<?> value wrapped in the Java wrapper"},
			throwMsgs = "No such class with ...",
			example = "Java.classFromName('java.util.ArrayList');"
		)
		private Value classFromName(Arguments arguments) throws CodeError {
			String name = arguments.getNextGeneric(StringValue.class);
			return new JavaValue(JavaValue.getObfuscatedClass(arguments.getContext(), arguments.getPosition(), name));
		}

		@FunctionDoc(
			isStatic = true,
			name = "getStaticField",
			desc = "Gets a static field Java value from a Java class",
			params = {
				STRING, "className", "the name of the class",
				STRING, "fieldName", "the name of the field"
			},
			returns = {JAVA, "the Java value of the field wrapped in the Java wrapper"},
			throwMsgs = "No such class with ...",
			example = "Java.getStaticField('java.lang.Integer', 'MAX_VALUE');"
		)
		private Value getStaticField(Arguments arguments) throws CodeError {
			Context context = arguments.getContext();
			ISyntax position = arguments.getPosition();
			String className = arguments.getNextGeneric(StringValue.class);
			String fieldName = arguments.getNextGeneric(StringValue.class);
			Class<?> clazz = JavaValue.getObfuscatedClass(context, position, className);
			fieldName = JavaValue.getObfuscatedFieldName(context, clazz, fieldName);
			return ReflectionUtils.getFieldFromName(clazz, null, fieldName, position, context);
		}

		@FunctionDoc(
			isStatic = true,
			name = "setStaticField",
			desc = {
				"Sets a static field in a Java class with a new value, the type of the new value needs to match the type of the field, ",
				"you can pass in Java wrapped values to guarantee type matching, they will be unwrapped, regular values will be converted"
			},
			params = {
				STRING, "className", "the name of the class",
				STRING, "fieldName", "the name of the field",
				ANY, "newValue", "the new value"
			},
			throwMsgs = "No such class with ...",
			example = """
				// Obviously this won't work, but it's just an example
				Java.setStaticField('java.lang.Integer', 'MAX_VALUE', Java.intOf(100));"
				"""
		)
		private Value setStaticField(Arguments arguments) throws CodeError {
			Context context = arguments.getContext();
			ISyntax position = arguments.getPosition();
			String className = arguments.getNextGeneric(StringValue.class);
			String fieldName = arguments.getNextGeneric(StringValue.class);
			Value value = arguments.getNext();
			Class<?> clazz = JavaValue.getObfuscatedClass(context, position, className);
			fieldName = JavaValue.getObfuscatedFieldName(context, clazz, fieldName);
			ReflectionUtils.setFieldFromName(clazz, null, value.asJavaValue(), fieldName, position, context);
			return NullValue.NULL;
		}

		@FunctionDoc(
			isStatic = true,
			name = "getStaticMethodDelegate",
			desc = {
				"Gets a static method delegate from a Java class, delegating the method is much faster than directly calling it since it uses MethodHandles, ",
				"if you are repetitively calling a static method you should delegate it and call that delegate"
			},
			params = {
				STRING, "className", "the name of the class",
				STRING, "methodName", "the name of the method",
				NUMBER, "parameters", "the number of parameters"
			},
			returns = {FUNCTION, "the delegated Java method in an Arucas Function"},
			throwMsgs = {
				"No such class with ...",
				"No such method ... with ... parameters can be found"
			},
			example = "Java.getStaticMethodDelegate('java.lang.Integer', 'parseInt', 1);"
		)
		private Value getStaticMethodDelegate(Arguments arguments) throws CodeError {
			Context context = arguments.getContext();
			ISyntax position = arguments.getPosition();
			String className = arguments.getNextGeneric(StringValue.class);
			String methodName = arguments.getNextGeneric(StringValue.class);
			int parameters = arguments.getNextGeneric(NumberValue.class).intValue();
			Class<?> clazz = JavaValue.getObfuscatedClass(context, position, className);
			methodName = getObfuscatedMethodName(context, clazz, methodName);
			Method method = ReflectionUtils.getMethod(clazz, null, methodName, parameters);
			JavaFunction javaFunction = JavaFunction.of(method, null, position);
			if (javaFunction != null) {
				return javaFunction;
			}
			throw arguments.getError("No such method '%s' with %d parameters can be found", method, parameters);
		}

		@FunctionDoc(
			isStatic = true,
			name = "arrayWithSize",
			desc = {
				"Creates a Java Object array with a given size, the array is filled with null values ",
				"by default and can be filled with any Java values, this array cannot be expanded"
			},
			params = {NUMBER, "size", "the size of the array"},
			returns = {JAVA, "the Java Object array"},
			example = "Java.arrayWithSize(10);"
		)
		private Value arrayWithSize(Arguments arguments) throws CodeError {
			int size = arguments.getNextGeneric(NumberValue.class).intValue();
			this.checkArraySize(arguments.getContext(), arguments.getPosition(), size);
			return new JavaValue(new Object[size]);
		}

		@FunctionDoc(
			isStatic = true,
			name = "doubleArray",
			desc = {
				"Creates a Java double array with a given size, the array is filled with 0's ",
				"by default and can be filled with only doubles"
			},
			params = {NUMBER, "size", "the size of the array"},
			returns = {JAVA, "the Java double array"},
			example = "Java.doubleArray(10);"
		)
		private Value doubleArray(Arguments arguments) throws CodeError {
			int size = arguments.getNextGeneric(NumberValue.class).intValue();
			this.checkArraySize(arguments.getContext(), arguments.getPosition(), size);
			return new JavaValue(new double[size]);
		}

		@FunctionDoc(
			isStatic = true,
			name = "floatArray",
			desc = {
				"Creates a Java float array with a given size, the array is filled with 0's ",
				"by default and can be filled with only floats"
			},
			params = {NUMBER, "size", "the size of the array"},
			returns = {JAVA, "the Java float array"},
			example = "Java.floatArray(10);"
		)
		private Value floatArray(Arguments arguments) throws CodeError {
			int size = arguments.getNextGeneric(NumberValue.class).intValue();
			this.checkArraySize(arguments.getContext(), arguments.getPosition(), size);
			return new JavaValue(new float[size]);
		}

		@FunctionDoc(
			isStatic = true,
			name = "longArray",
			desc = {
				"Creates a Java long array with a given size, the array is filled with 0's ",
				"by default and can be filled with only longs"
			},
			params = {NUMBER, "size", "the size of the array"},
			returns = {JAVA, "the Java long array"},
			example = "Java.longArray(10);"
		)
		private Value longArray(Arguments arguments) throws CodeError {
			int size = arguments.getNextGeneric(NumberValue.class).intValue();
			this.checkArraySize(arguments.getContext(), arguments.getPosition(), size);
			return new JavaValue(new long[size]);
		}

		@FunctionDoc(
			isStatic = true,
			name = "intArray",
			desc = {
				"Creates a Java int array with a given size, the array is filled with 0's ",
				"by default and can be filled with only ints"
			},
			params = {NUMBER, "size", "the size of the array"},
			returns = {JAVA, "the Java int array"},
			example = "Java.intArray(10);"
		)
		private Value intArray(Arguments arguments) throws CodeError {
			int size = arguments.getNextGeneric(NumberValue.class).intValue();
			this.checkArraySize(arguments.getContext(), arguments.getPosition(), size);
			return new JavaValue(new int[size]);
		}

		@FunctionDoc(
			isStatic = true,
			name = "shortArray",
			desc = {
				"Creates a Java short array with a given size, the array is filled with 0's ",
				"by default and can be filled with only shorts"
			},
			params = {NUMBER, "size", "the size of the array"},
			returns = {JAVA, "the Java short array"},
			example = "Java.shortArray(10);"
		)
		private Value shortArray(Arguments arguments) throws CodeError {
			int size = arguments.getNextGeneric(NumberValue.class).intValue();
			this.checkArraySize(arguments.getContext(), arguments.getPosition(), size);
			return new JavaValue(new short[size]);
		}

		@FunctionDoc(
			isStatic = true,
			name = "byteArray",
			desc = {
				"Creates a Java byte array with a given size, the array is filled with 0's ",
				"by default and can be filled with only bytes"
			},
			params = {NUMBER, "size", "the size of the array"},
			returns = {JAVA, "the Java byte array"},
			example = "Java.byteArray(10);"
		)
		private Value byteArray(Arguments arguments) throws CodeError {
			int size = arguments.getNextGeneric(NumberValue.class).intValue();
			this.checkArraySize(arguments.getContext(), arguments.getPosition(), size);
			return new JavaValue(new byte[size]);
		}

		@FunctionDoc(
			isStatic = true,
			name = "charArray",
			desc = {
				"Creates a Java char array with a given size, the array is filled with \u0000's ",
				"(null characters) by default and can be filled with only chars"
			},
			params = {NUMBER, "size", "the size of the array"},
			returns = {JAVA, "the Java char array"},
			example = "Java.charArray(10);"
		)
		private Value charArray(Arguments arguments) throws CodeError {
			int size = arguments.getNextGeneric(NumberValue.class).intValue();
			this.checkArraySize(arguments.getContext(), arguments.getPosition(), size);
			return new JavaValue(new char[size]);
		}

		@FunctionDoc(
			isStatic = true,
			name = "booleanArray",
			desc = {
				"Creates a Java boolean array with a given size, the array is filled with false ",
				"by default and can be filled with only booleans"
			},
			params = {NUMBER, "size", "the size of the array"},
			returns = {JAVA, "the Java boolean array"},
			example = "Java.booleanArray(10);"
		)
		private Value booleanArray(Arguments arguments) throws CodeError {
			int size = arguments.getNextGeneric(NumberValue.class).intValue();
			this.checkArraySize(arguments.getContext(), arguments.getPosition(), size);
			return new JavaValue(new boolean[size]);
		}

		@FunctionDoc(
			isStatic = true,
			name = "runnableOf",
			desc = "Creates a Java Runnable object from a given function",
			params = {
				FUNCTION, "function", "the function to be executed, this must have no parameters and any return values will be ignored"
			},
			returns = {JAVA, "the Java Runnable object"},
			example = """
				Java.runnableOf(fun() {
				    print('runnable');
				});
				"""
		)
		private Value runnableOf(Arguments arguments) throws CodeError {
			FunctionValue functionValue = arguments.getNext(FunctionValue.class);
			Context branchContext = arguments.getContext().createBranch();
			return new JavaValue((Runnable) () -> {
				functionValue.callSafe(branchContext.createBranch(), ArrayList::new);
			});
		}

		@FunctionDoc(
			isStatic = true,
			name = "consumerOf",
			desc = "Creates a Java Consumer object from a given function",
			params = {
				FUNCTION, "function", "the function to be executed, this must have one parameter and " +
				"any return values will be ignored, the parameter type is unknown at compile time"
			},
			returns = {JAVA, "the Java Consumer object"},
			example = """
				Java.consumerOf(fun(something) {
				    print(something);
				});
				"""
		)
		private Value consumerOf(Arguments arguments) throws CodeError {
			FunctionValue functionValue = arguments.getNext(FunctionValue.class);
			Context branchContext = arguments.getContext().createBranch();
			return new JavaValue((Consumer<Object>) o -> {
				functionValue.callSafe(branchContext.createBranch(), () -> ArucasList.arrayListOf(branchContext.convertValue(o)));
			});
		}

		@FunctionDoc(
			isStatic = true,
			name = "supplierOf",
			desc = "Creates a Java Supplier object from a given function",
			params = {
				FUNCTION, "function", "the function to be executed, this must have no parameters and must return (supply) a value"
			},
			returns = {JAVA, "the Java Supplier object"},
			example = """
				Java.supplierOf(fun() {
				    return "supplier";
				});
				"""
		)
		private Value supplierOf(Arguments arguments) throws CodeError {
			FunctionValue functionValue = arguments.getNext(FunctionValue.class);
			Context branchContext = arguments.getContext().createBranch();
			return new JavaValue((Supplier<Object>) () -> {
				Value returnValue = functionValue.callSafe(branchContext.createBranch(), ArrayList::new);
				return returnValue == null ? null : returnValue.asJavaValue();
			});
		}

		@FunctionDoc(
			isStatic = true,
			name = "functionOf",
			desc = "Creates a Java Function object from a given function",
			params = {
				FUNCTION, "function", "the function to be executed, this must have one parameter and must return a value"
			},
			returns = {JAVA, "the Java Function object"},
			example = """
				Java.functionOf(fun(num) {
				    return num + 10;
				});
				"""
		)
		private Value functionOf(Arguments arguments) throws CodeError {
			FunctionValue functionValue = arguments.getNext(FunctionValue.class);
			Context branchContext = arguments.getContext().createBranch();
			return new JavaValue((Function<Object, Object>) o -> {
				Value returnValue = functionValue.callSafe(branchContext.createBranch(), () -> ArucasList.arrayListOf(branchContext.convertValue(o)));
				return returnValue == null ? null : returnValue.asJavaValue();
			});
		}

		@FunctionDoc(
			isStatic = true,
			name = "predicateOf",
			desc = "Creates a Java Predicate object from a given function",
			params = {
				FUNCTION, "function", "the function to be executed, this must have one parameter and must return a boolean"
			},
			returns = {JAVA, "the Java Predicate object"},
			example = """
				Java.predicateOf(fun(num) {
				    return num == 42;
				});
				"""
		)
		private Value predicateOf(Arguments arguments) throws CodeError {
			FunctionValue functionValue = arguments.getNext(FunctionValue.class);
			Context branchContext = arguments.getContext().createBranch();
			return new JavaValue((Predicate<Object>) o -> {
				Context branch = branchContext.createBranch();
				Value returnValue = functionValue.callSafe(branch, () -> ArucasList.arrayListOf(branchContext.convertValue(o)));
				if (returnValue != null && returnValue.getValue() instanceof Boolean ret) {
					return ret;
				}
				branch.getThreadHandler().tryError(branch, new RuntimeError("Predicate function must return a boolean", functionValue.getPosition(), branch));
				return false;
			});
		}

		@FunctionDoc(
			isVarArgs = true,
			isStatic = true,
			name = "arrayOf",
			desc = {
				"Creates a Java Object array with a given values, this will be the size of the array, ",
				"again this cannot be used to create primitive arrays"
			},
			params = {ANY, "values...", "the values to add to the array"},
			returns = {JAVA, "the Java Object array"},
			example = "Java.arrayOf(1, 2, 3, 'string!', false);"
		)
		private Value arrayOf(Arguments arguments) throws CodeError {
			Object[] array = new Object[arguments.size()];
			for (int i = 0; i < arguments.size(); i++) {
				array[i] = arguments.get(i).asJavaValue();
			}
			return new JavaValue(array);
		}

		@FunctionDoc(
			isVarArgs = true,
			isStatic = true,
			name = "callStaticMethod",
			desc = "Calls a static method of a Java class, this is slower than delegating a method, but better for a one off call",
			params = {
				STRING, "className", "the name of the class",
				STRING, "methodName", "the name of the method",
				ANY, "parameters...", "any parameters to call the method with, this can be none, " +
				"a note - if you are calling a VarArg method then you must have your VarArg parameters in a Java Object array"
			},
			returns = {JAVA, "the return value of the method wrapped in the Java wrapper"},
			throwMsgs = {
				"First parameter must be a class name and the second parameter must be a method name",
				"No such class with ...",
				"No such method ... with ... parameters exists for ...",
			},
			example = "Java.callStaticMethod('java.lang.Integer', 'parseInt', '123');"
		)
		private Value callStaticMethod(Arguments arguments) throws CodeError {
			if (arguments.size() < 2) {
				throw arguments.getError("First parameter must be a class name and second parameter must be a method name");
			}
			Context context = arguments.getContext();
			ISyntax position = arguments.getPosition();

			String className = arguments.getNextGeneric(StringValue.class);
			String methodName = arguments.getNextGeneric(StringValue.class);

			Class<?> clazz = JavaValue.getObfuscatedClass(context, position, className);
			String name = getObfuscatedMethodName(context, clazz, methodName);
			return ReflectionUtils.callMethodFromNameAndArgs(clazz, null, name, arguments.getRemaining(), position, context);
		}

		@FunctionDoc(
			isVarArgs = true,
			isStatic = true,
			name = "constructClass",
			desc = "This constructs a Java class with specified class name and parameters",
			params = {
				STRING, "className", "the name of the class",
				ANY, "parameters...", "any parameters to pass to the constructor, there may be no parameters, " +
				"again if calling VarArgs constructor you must have your VarArg parameters in a Java Object array"
			},
			returns = {JAVA, "the constructed Java Object wrapped in the Java wrapper"},
			throwMsgs = {
				"First parameter must be a class name",
				"No such class with ...",
				"No such constructor with ... parameters exists for ..."
			},
			example = "Java.constructClass('java.util.ArrayList');"
		)
		private Value constructClass(Arguments arguments) throws CodeError {
			if (arguments.size() < 1) {
				throw arguments.getError("First parameter must be a class name");
			}
			Context context = arguments.getContext();
			ISyntax position = arguments.getPosition();

			String className = arguments.getNextGeneric(StringValue.class);

			Class<?> clazz = JavaValue.getObfuscatedClass(context, position, className);
			return ReflectionUtils.constructFromArgs(clazz, arguments.getRemaining(), position, context);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("toArucas", this::toValue),
				MemberFunction.of("getMethodDelegate", 2, this::getMethodDelegate),
				MemberFunction.of("getField", 1, this::getJavaField, "You should get the field directly"),
				MemberFunction.of("setField", 2, this::setJavaField, "You should set the field directly"),
				MemberFunction.arbitrary("callMethod", this::callMethodArbitrary, "You should call the method directly")
			);
		}

		@FunctionDoc(
			name = "toArucas",
			desc = "This converts the Java value to an Arucas Value",
			returns = {
				ANY, "the Value in Arucas, this may still be of Java value if the value cannot be " +
				"converted into an Arucas value, values like Strings, Numbers, Lists, etc... will be converted"
			},
			example = "Java.valueOf([1, 2, 3]).toArucas();"
		)
		private Value toValue(Arguments arguments) throws CodeError {
			JavaValue thisValue = arguments.getNext(JavaValue.class);
			return arguments.getContext().convertValue(thisValue.asJavaValue());
		}

		@FunctionDoc(
			name = "getMethodDelegate",
			desc = {
				"This returns a method delegate for the specified method name and parameters, ",
				"delegating the method is much faster since it uses MethodHandles, so if you are calling ",
				"a method repetitively it is faster to delegate the method and then call the delegate"
			},
			params = {
				STRING, "methodName", "the name of the method",
				NUMBER, "parameters", "the number of parameters"
			},
			returns = {FUNCTION, "the function containing the Java method delegate"},
			throwMsgs = "No such method ... with ... parameters can be found",
			example = "Java.valueOf('string!').getMethodDelegate('isBlank', 0);"
		)
		private Value getMethodDelegate(Arguments arguments) throws CodeError {
			JavaValue thisValue = arguments.getNext(JavaValue.class);
			String methodName = arguments.getNextGeneric(StringValue.class);
			int parameters = arguments.getNextGeneric(NumberValue.class).intValue();
			Object callingObject = thisValue.asJavaValue();
			Class<?> callingClass = callingObject.getClass();
			methodName = getObfuscatedMethodName(arguments.getContext(), callingClass, methodName);
			Method method = ReflectionUtils.getMethod(callingClass, callingObject, methodName, parameters);
			JavaFunction javaFunction = JavaFunction.of(method, callingObject, arguments.getPosition());
			if (javaFunction != null) {
				return javaFunction;
			}
			throw arguments.getError("No such method '%s' with %d parameters can be found", method, parameters);
		}

		@FunctionDoc(
			isVarArgs = true,
			deprecated = "You should call the method directly on the value: Java.valueOf('').isBlank();",
			name = "callMethod",
			desc = {
				"This calls the specified method with the specified parameters, this is slower ",
				"than calling a delegate, this is the same speed as calling the method directly on the value however"
			},
			params = {
				STRING, "methodName", "the name of the method",
				ANY, "parameters...", "the parameters to call the method with, " +
				"this may be none, a note - if you are calling a VarArgs method you must pass a Java " +
				"Object array with your VarArg arguments"
			},
			returns = {JAVA, "the return value of the method call wrapped in the Java wrapper"},
			throwMsgs = {
				"No such method ... with ... parameters exists for ...",
				"First parameter must be name of method"
			},
			example = "Java.valueOf('').callMethod('isBlank');"
		)
		private Value callMethodArbitrary(Arguments arguments) throws CodeError {
			JavaValue thisValue = arguments.getNext(JavaValue.class);
			if (arguments.size() < 2) {
				throw arguments.getError("First parameter must be name of method");
			}
			Context context = arguments.getContext();
			String methodName = arguments.getNextGeneric(StringValue.class);

			Object callingObject = thisValue.asJavaValue();
			String name = getObfuscatedMethodName(context, callingObject.getClass(), methodName);
			return ReflectionUtils.callMethodFromNameAndArgs(
				callingObject.getClass(), callingObject,
				name, arguments.getRemaining(),
				arguments.getPosition(), context
			);
		}

		@FunctionDoc(
			deprecated = "You should call the method directly on the value: Java.constructClass('me.senseiwells.impl.Test').A;",
			name = "getField",
			desc = "This returns the Java wrapped value of the specified field",
			params = {STRING, "fieldName", "the name of the field"},
			returns = {JAVA, "the Java wrapped value of the field"},
			example = "Java.constructClass('me.senseiwells.impl.Test').getField('A');"
		)
		private Value getJavaField(Arguments arguments) throws CodeError {
			JavaValue thisValue = arguments.getNext(JavaValue.class);
			String fieldName = arguments.getNextGeneric(StringValue.class);
			Context context = arguments.getContext();
			Object callingObject = thisValue.asJavaValue();
			fieldName = getObfuscatedFieldName(context, callingObject.getClass(), fieldName);
			return ReflectionUtils.getFieldFromName(callingObject.getClass(), callingObject, fieldName, arguments.getPosition(), context);
		}

		@FunctionDoc(
			deprecated = "You should assign the value directly on the value: Java.constructClass('me.senseiwells.impl.Test').A = 'Hello';",
			name = "setField",
			desc = "This sets the specified field to the specified value",
			params = {
				STRING, "fieldName", "the name of the field",
				ANY, "value", "the value to set the field to, the value type must match the type of the field"
			},
			example = "Java.constructClass('me.senseiwells.impl.Test').setField('A', 'Hello');"
		)
		private Value setJavaField(Arguments arguments) throws CodeError {
			JavaValue thisValue = arguments.getNext(JavaValue.class);
			String fieldName = arguments.getNextGeneric(StringValue.class);
			Value newValue = arguments.getNext();
			Context context = arguments.getContext();
			Object callingObject = thisValue.asJavaValue();
			fieldName = getObfuscatedFieldName(context, callingObject.getClass(), fieldName);
			ReflectionUtils.setFieldFromName(callingObject.getClass(), callingObject, newValue.asJavaValue(), fieldName, arguments.getPosition(), context);
			return NullValue.NULL;
		}

		private void checkArraySize(Context context, ISyntax syntaxPosition, int size) throws RuntimeError {
			if (size < 0) {
				throw new RuntimeError("Cannot have a negative size array", syntaxPosition, context);
			}
		}

		@Override
		public Class<JavaValue> getValueClass() {
			return JavaValue.class;
		}
	}
}
