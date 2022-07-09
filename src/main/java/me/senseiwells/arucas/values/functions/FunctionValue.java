package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.BuiltInException;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

import java.util.List;
import java.util.Objects;

import static me.senseiwells.arucas.utils.ValueTypes.*;

public abstract class FunctionValue extends GenericValue<String> {
	private final ISyntax syntaxPosition;
	private final String deprecationMessage;
	private final int parameters;

	protected FunctionValue(String name, ISyntax position, int parameters, String deprecationMessage) {
		super(name);
		this.syntaxPosition = position;
		this.deprecationMessage = deprecationMessage;
		this.parameters = parameters;
	}

	public String getName() {
		return this.value;
	}

	public int getCount() {
		return this.parameters;
	}

	public String getDeprecationMessage() {
		return this.deprecationMessage;
	}

	public ISyntax getPosition() {
		return this.syntaxPosition;
	}

	public RuntimeError getError(Context context, String details) {
		return new RuntimeError(details, this.syntaxPosition, context);
	}

	public RuntimeError getError(Context context, String details, Object... objects) {
		return new RuntimeError(details.formatted(objects), this.syntaxPosition, context);
	}

	public RuntimeError getError(Context context, Throwable throwable) {
		return new RuntimeError(throwable, this.syntaxPosition, context);
	}

	protected Context getContext(Context context) {
		return context;
	}

	protected void onReturnValue(Context context, Value returnValue) throws CodeError { }

	protected abstract Value execute(Context context, List<Value> arguments) throws CodeError;

	public final Value callSafe(Context context, ExceptionUtils.ThrowableSupplier<List<Value>> arguments) {
		try {
			return this.call(context, arguments.get());
		}
		catch (Throwable throwable) {
			context.getThreadHandler().tryError(context, throwable);
			return null;
		}
	}

	public final Value call(Context context, List<Value> arguments) throws CodeError {
		return this.call(context, arguments, true);
	}

	public final Value call(Context context, List<Value> arguments, boolean returnable) throws CodeError {
		context = this.getContext(context);
		context.pushFunctionScope(this.syntaxPosition);
		Value returnValue;
		try {
			returnValue = this.execute(context, arguments);
			context.popScope();
		}
		catch (ThrowValue.Return throwValue) {
			if (!returnable) {
				throw this.getError(context, throwValue.getMessage());
			}
			context.moveScope(context.getReturnScope());
			context.popScope();
			returnValue = throwValue.getReturnValue();
		}
		catch (ThrowValue throwValue) {
			throw new RuntimeError(throwValue.getMessage(), this.syntaxPosition, context);
		}
		catch (BuiltInException exception) {
			throw exception.asRuntimeError(context, this.syntaxPosition);
		}
		catch (StackOverflowError e) {
			throw this.getError(context, "StackOverflow: Call stack went too deep");
		}

		this.onReturnValue(context, returnValue);
		return returnValue;
	}

	@Override
	public GenericValue<String> copy(Context context) throws CodeError {
		return this;
	}

	@Override
	public Object asJavaValue() {
		return this;
	}

	@Override
	public boolean isEquals(Context context, Value other) throws CodeError {
		return this == other;
	}

	@Override
	public String getTypeName() {
		return FUNCTION;
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "<function - " + this.getName() + ">";
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return Objects.hash(this.getName(), this.getCount());
	}

	@ClassDoc(
		name = FUNCTION,
		desc = "Adds utilities for delegating and calling functions."
	)
	public static class ArucasFunctionClass extends ArucasClassExtension {
		public ArucasFunctionClass() {
			super(FUNCTION);
		}

		@Override
		public Class<FunctionValue> getValueClass() {
			return FunctionValue.class;
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("getBuiltIn", 2, this::getBuiltInDelegate),
				BuiltInFunction.of("getMethod", 3, this::getMethodDelegate),
				BuiltInFunction.of("callWithList", 2, this::callDelegateWithList),
				BuiltInFunction.arbitrary("call", this::callDelegate)
			);
		}

		@FunctionDoc(
			isStatic = true,
			name = "getBuiltIn",
			desc = "Returns a built-in function delegate with the given name and parameter count",
			params = {
				STRING, "functionName", "the name of the function",
				NUMBER, "parameterCount", "the parameter count of the function"
			},
			returns = {FUNCTION, "the built-in function delegate"},
			example = "Function.getBuiltIn('print', 1);"
		)
		private Value getBuiltInDelegate(Arguments arguments) throws CodeError {
			String functionName = arguments.getNextGeneric(StringValue.class);
			int parameters = arguments.getNextGeneric(NumberValue.class).intValue();
			FunctionValue functionValue = arguments.getContext().getBuiltInFunction(functionName, parameters);
			if (functionValue == null) {
				throw arguments.getError("No such built in function '%s' with %d parameters", functionName, parameters);
			}
			return functionValue;
		}

		@FunctionDoc(
			isStatic = true,
			name = "getMethod",
			desc = "Returns a method delegate with the given name and parameter count",
			params = {
				ANY, "value", "the value to call the method on",
				STRING, "methodName", "the name of the method",
				NUMBER, "parameterCount", "the parameter count of the method"
			},
			returns = {FUNCTION, "the method delegate"},
			example = "Function.getMethod('String', 'contains', 1);"
		)
		private Value getMethodDelegate(Arguments arguments) throws CodeError {
			Value callingValue = arguments.getNext();
			String functionName = arguments.getNextGeneric(StringValue.class);
			int parameters = arguments.getNextGeneric(NumberValue.class).intValue();

			FunctionValue delegate = null;
			if (callingValue instanceof ArucasClassValue classValue) {
				delegate = classValue.getMember(functionName, parameters + 1);
			}
			if (delegate == null) {
				delegate = arguments.getContext().getMemberFunction(callingValue.getClass(), functionName, parameters + 1);
			}
			if (delegate == null) {
				throw arguments.getError("No such method '%s' with %d parameters", functionName, parameters);
			}
			return delegate;
		}

		@FunctionDoc(
			isStatic = true,
			name = "callWithList",
			desc = "Calls the given delegate with the given parameters",
			params = {
				FUNCTION, "delegate", "the delegate to call",
				LIST, "parameters", "the parameters to pass to the delegate"
			},
			returns = {ANY, "the return value of the delegate"},
			example = "Function.callWithList(fun(m1, m2) { }, ['Hello', 'World']);"
		)
		private Value callDelegateWithList(Arguments arguments) throws CodeError {
			FunctionValue delegate = arguments.getNextFunction();
			ListValue listValue = arguments.getNextList();
			return delegate.call(arguments.getContext(), listValue.value);
		}

		@FunctionDoc(
			isVarArgs = true,
			isStatic = true,
			name = "call",
			desc = "Calls the given delegate with the given arbitrary parameters",
			params = {
				FUNCTION, "delegate", "the delegate to call",
				ANY, "parameters...", "the parameters to pass to the delegate"
			},
			returns = {ANY, "the return value of the delegate"},
			example = "Function.call(Function.getBuiltIn('print', 1), 'Hello World!');"
		)
		private Value callDelegate(Arguments arguments) throws CodeError {
			if (arguments.size() < 1) {
				throw arguments.getError("First parameter must be of function value");
			}
			FunctionValue function = arguments.getNextFunction();
			return function.call(arguments.getContext(), arguments.getRemaining());
		}
	}
}
