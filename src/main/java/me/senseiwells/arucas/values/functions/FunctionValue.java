package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

import java.util.List;
import java.util.Objects;

public abstract class FunctionValue extends Value<String> {
	public final List<String> argumentNames;
	public final ISyntax syntaxPosition;
	public final String deprecatedMessage;

	public FunctionValue(String name, ISyntax syntaxPosition, List<String> argumentNames, String deprecatedMessage) {
		super(name);
		this.syntaxPosition = syntaxPosition;
		this.argumentNames = argumentNames;
		this.deprecatedMessage = deprecatedMessage;
	}

	public String getName() {
		return this.value;
	}

	public int getParameterCount() {
		return this.argumentNames.size();
	}

	protected void checkArguments(Context context, List<Value<?>> arguments, List<String> argumentNames) throws CodeError {
		int argumentSize = arguments == null ? 0 : arguments.size();
		if (argumentSize > argumentNames.size()) {
			throw new RuntimeError(
				"%s too many arguments passed into %s".formatted(arguments.size() - argumentNames.size(), this.value),
				this.syntaxPosition,
				context
			);
		}
		if (argumentSize < argumentNames.size()) {
			throw new RuntimeError(
				"%s too few arguments passed into %s".formatted(argumentNames.size() - argumentSize, this.value),
				this.syntaxPosition,
				context
			);
		}
	}

	protected void populateArguments(Context context, List<Value<?>> arguments, List<String> argumentNames) {
		for (int i = 0; i < argumentNames.size(); i++) {
			String argumentName = argumentNames.get(i);
			Value<?> argumentValue = arguments.get(i);
			context.setLocal(argumentName, argumentValue);
		}
	}

	public void checkAndPopulateArguments(Context context, List<Value<?>> arguments, List<String> argumentNames) throws CodeError {
		this.checkArguments(context, arguments, argumentNames);
		this.populateArguments(context, arguments, argumentNames);
	}

	public CodeError throwInvalidParameterError(String details, Context context) {
		return new RuntimeError(details, this.syntaxPosition, context);
	}

	protected abstract Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError, ThrowValue;

	/**
	 * API overridable method.
	 */
	protected Value<?> callOverride(Context context, List<Value<?>> arguments, boolean returnable) throws CodeError {
		context.pushFunctionScope(this.syntaxPosition);
		try {
			Value<?> value = this.execute(context, arguments);
			context.popScope();
			return value;
		}
		catch (ThrowValue.Return tv) {
			if (!returnable) {
				throw new CodeError(
					CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
					tv.getMessage(),
					this.syntaxPosition
				);
			}
			context.moveScope(context.getReturnScope());
			context.popScope();
			return tv.getReturnValue();
		}
		catch (ThrowValue tv) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				tv.getMessage(),
				this.syntaxPosition
			);
		}
		catch (StackOverflowError e) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"StackOverflow: Call stack went too deep",
				this.syntaxPosition
			);
		}
	}

	public final Value<?> call(Context context, List<Value<?>> arguments) throws CodeError {
		return this.call(context, arguments, true);
	}

	public final Value<?> call(Context context, List<Value<?>> arguments, boolean returnable) throws CodeError {
		return this.callOverride(context, arguments, returnable);
	}

	@Override
	public final FunctionValue copy(Context context) {
		return this;
	}

	@Override
	public FunctionValue asJavaValue() {
		return this;
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return Objects.hash(this.value, this.getParameterCount());
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "<function " + this.value + ">";
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) {
		// The problem here is that it is not enough to check the parameter count and name
		// If this function was a delegate of a class, and then we compared it to a delegate
		// of the same class but another instance it should always return false.
		return this == other;
	}

	@Override
	public String getTypeName() {
		return "Function";
	}

	/**
	 * Function class for Arucas. <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasFunctionClass extends ArucasClassExtension {
		public ArucasFunctionClass() {
			super("Function");
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("getBuiltIn", List.of("functionName", "parameters"), this::getBuiltInDelegate),
				new BuiltInFunction("getMethod", List.of("object", "methodname", "parameters"), this::getMethodDelegate),
				new BuiltInFunction("callWithList", List.of("delegate", "parameters"), this::callDelegateWithList),
				new BuiltInFunction.Arbitrary("call", this::callDelegate)
			);
		}

		/**
		 * Name: <code>Function.getBuiltIn(functionName, parameterCount)</code> <br>
		 * Description: Returns a built-in function delegate with the given name and parameter count. <br>
		 * Parameters: String, Number: the name of the function, the parameter count of the function <br>
		 * Returns - Function: the built-in function delegate <br>
		 * Example: <code>Function.getBuiltIn("print", 1);</code>
		 */
		private Value<?> getBuiltInDelegate(Context context, BuiltInFunction function) throws CodeError {
			StringValue functionName = function.getParameterValueOfType(context, StringValue.class, 0);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			FunctionValue functionValue = context.getBuiltInFunction(functionName.value, numberValue.value.intValue());
			if (functionValue == null) {
				throw new RuntimeError(
					"No such built in function '%s' with %d parameters".formatted(functionName.value, numberValue.value.intValue()),
					function.syntaxPosition,
					context
				);
			}
			return functionValue;
		}

		/**
		 * Name: <code>Function.getMethod(object, methodName, parameterCount)</code> <br>
		 * Description: Returns a method delegate with the given name and parameter count. <br>
		 * Parameters: Value, String, Number: the object to call the method on, the name of the method, the parameter count of the method <br>
		 * Returns - Function: the method delegate <br>
		 * Example: <code>Function.getMethod(this, "print", 1);</code>
		 */
		private Value<?> getMethodDelegate(Context context, BuiltInFunction function) throws CodeError {
			Value<?> callingValue = function.getParameterValue(context, 0);
			StringValue methodNameValue = function.getParameterValueOfType(context, StringValue.class, 1);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 2);

			FunctionValue delegate = null;
			if (callingValue instanceof ArucasClassValue classValue) {
				delegate = classValue.getMember(methodNameValue.value, numberValue.value.intValue() + 1);
			}
			if (delegate == null) {
				delegate = context.getMemberFunction(callingValue.getClass(), methodNameValue.value, numberValue.value.intValue() + 1);
			}
			if (delegate == null) {
				throw new RuntimeError(
					"No such method '%s' with %d parameters".formatted(methodNameValue.value, numberValue.value.intValue()),
					function.syntaxPosition,
					context
				);
			}
			return delegate;
		}

		/**
		 * Name: <code>Function.callWithList(delegate, parameters)</code> <br>
		 * Description: Calls the given delegate with the given parameters. <br>
		 * Parameters: Function, List: the delegate to call, the parameters to pass to the delegate <br>
		 * Returns - Value: the return value of the delegate <br>
		 * Example: <code>Function.callWithList(this.print, List.of("Hello World!"));</code>
		 */
		private Value<?> callDelegateWithList(Context context, BuiltInFunction function) throws CodeError {
			FunctionValue delegate = function.getParameterValueOfType(context, FunctionValue.class, 0);
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 1);
			return delegate.call(context, listValue.value);
		}

		/**
		 * Name: <code>Function.call(delegate, parameters)</code> <br>
		 * Description: Calls the given delegate with the given arbitrary parameters. <br>
		 * Parameters: Function, Value...: the delegate to call, the parameters to pass to the delegate <br>
		 * Returns - Value: the return value of the delegate <br>
		 * Example: <code>Function.call(Function.getBuiltIn("print", 1), "Hello World!");</code>
		 */
		private Value<?> callDelegate(Context context, BuiltInFunction function) throws CodeError {
			ListValue arguments = function.getParameterValueOfType(context, ListValue.class, 0);
			ArucasList list = arguments.value;
			if (list.size() < 1 || !(list.remove(0) instanceof FunctionValue functionValue)) {
				throw new RuntimeError("First parameter must be of function value", function.syntaxPosition, context);
			}
			return functionValue.call(context, list);
		}

		@Override
		public Class<FunctionValue> getValueClass() {
			return FunctionValue.class;
		}
	}
}
