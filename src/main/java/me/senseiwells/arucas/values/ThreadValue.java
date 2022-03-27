package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasThread;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;

public class ThreadValue extends Value<ArucasThread> {
	private final StringValue name;

	private ThreadValue(ArucasThread value) {
		super(value);
		this.name = StringValue.of(value.getName());
	}

	public static ThreadValue of(ArucasThread thread) {
		return new ThreadValue(thread);
	}

	@Override
	public ThreadValue copy(Context context) {
		return this;
	}

	@Override
	public int getHashCode(Context context) {
		return this.value.hashCode();
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "<Thread - " + this.name.value + ">";
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) {
		return other instanceof ThreadValue that && this.value == that.value;
	}

	@Override
	public String getTypeName() {
		return "Thread";
	}

	public static class ArucasThreadClass extends ArucasClassExtension {
		public ArucasThreadClass() {
			super("Thread");
		}

		@Override
		public Class<ThreadValue> getValueClass() {
			return ThreadValue.class;
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("getCurrentThread", this::getCurrentThread),
				new BuiltInFunction("runThreaded", List.of("function"), this::runThreaded1),
				new BuiltInFunction("runThreaded", List.of("name", "function"), this::runThreaded2),
				new BuiltInFunction("freeze", this::freeze)
			);
		}

		private Value<?> getCurrentThread(Context context, BuiltInFunction function) throws RuntimeError {
			Thread currentThread = Thread.currentThread();
			if (currentThread instanceof ArucasThread arucasValueThread) {
				return ThreadValue.of(arucasValueThread);
			}
			throw new RuntimeError("Thread is not safe to get", function.syntaxPosition, context);
		}

		private Value<?> runThreaded1(Context context, BuiltInFunction function) throws CodeError {
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 0);
			ArucasThread thread = context.getThreadHandler().runAsyncFunctionInContext(
				context.createBranch(), branchContext -> functionValue.call(branchContext, new ArucasList()),
				"Unnamed Arucas Thread"
			);
			return ThreadValue.of(thread);
		}

		private Value<?> runThreaded2(Context context, BuiltInFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
			ArucasThread thread = context.getThreadHandler().runAsyncFunctionInContext(
				context.createBranch(), branchContext -> functionValue.call(branchContext, new ArucasList()),
				stringValue.value
			);
			return ThreadValue.of(thread);
		}

		private Value<?> freeze(Context context, BuiltInFunction function) throws CodeError {
			Thread currentThread = Thread.currentThread();
			if (!(currentThread instanceof ArucasThread)) {
				throw new RuntimeError("Thread is not safe to freeze", function.syntaxPosition, context);
			}
			try {
				Thread.sleep(Long.MAX_VALUE);
				return NullValue.NULL;
			}
			catch (InterruptedException e) {
				throw new CodeError(CodeError.ErrorType.INTERRUPTED_ERROR, "", function.syntaxPosition);
			}
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("isAlive", this::isAlive),
				new MemberFunction("getAge", this::getAge),
				new MemberFunction("getName", this::getName),
				new MemberFunction("stop", this::stop)
			);
		}

		private Value<?> isAlive(Context context, MemberFunction function) throws CodeError {
			ThreadValue thisValue = function.getParameterValueOfType(context, ThreadValue.class, 0);
			return BooleanValue.of(thisValue.value.isAlive());
		}

		private Value<?> getAge(Context context, MemberFunction function) throws CodeError {
			ThreadValue thisValue = function.getParameterValueOfType(context, ThreadValue.class, 0);
			return NumberValue.of(System.currentTimeMillis() - thisValue.value.getStartTime());
		}

		private Value<?> getName(Context context, MemberFunction function) throws CodeError {
			ThreadValue thisValue = function.getParameterValueOfType(context, ThreadValue.class, 0);
			return thisValue.name;
		}

		private Value<?> stop(Context context, MemberFunction function) throws CodeError {
			ThreadValue thisValue = function.getParameterValueOfType(context, ThreadValue.class, 0);
			if (!thisValue.value.isAlive()) {
				throw new RuntimeError("Thread is not alive", function.syntaxPosition, context);
			}
			thisValue.value.controlledStop(context);
			return NullValue.NULL;
		}
	}
}
