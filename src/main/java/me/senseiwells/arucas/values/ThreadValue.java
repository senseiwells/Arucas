package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.ArucasValueThread;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;

public class ThreadValue extends Value<ArucasValueThread> {
	private final StringValue name;

	private ThreadValue(ArucasValueThread value) {
		super(value);
		this.name = StringValue.of(value.getName());
	}

	public static ThreadValue of(ArucasValueThread thread) {
		return new ThreadValue(thread);
	}

	@Override
	public Value<ArucasValueThread> copy() {
		return this;
	}

	@Override
	public String getStringValue(Context context) throws CodeError {
		return "<Thread - %s>".formatted(this.name.value);
	}

	public static class ArucasThreadClass extends ArucasClassExtension {
		public ArucasThreadClass() {
			super("Thread");
		}

		@Override
		public Class<?> getValueClass() {
			return ThreadValue.class;
		}

		@Override
		public List<BuiltInFunction> getDefinedStaticMethods() {
			return List.of(
				new BuiltInFunction("getCurrentThread", this::getCurrentThread),
				new BuiltInFunction("runThreaded", List.of("function"), this::runThreaded$1),
				new BuiltInFunction("runThreaded", List.of("name", "function"), this::runThreaded$2)
			);
		}

		private Value<?> getCurrentThread(Context context, BuiltInFunction function) throws RuntimeError {
			Thread currentThread = Thread.currentThread();
			if (currentThread instanceof ArucasValueThread arucasValueThread) {
				return ThreadValue.of(arucasValueThread);
			}
			throw new RuntimeError("Thread is not safe to get", function.syntaxPosition, context);
		}

		private Value<?> runThreaded$1(Context context, BuiltInFunction function) throws CodeError {
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 0);
			ArucasValueThread thread = context.getThreadHandler().runAsyncFunctionInContext(
				context.createBranch(), (branchContext) -> functionValue.call(branchContext, new ArucasValueList()),
				"Unnamed Arucas Thread"
			);
			return ThreadValue.of(thread);
		}

		private Value<?> runThreaded$2(Context context, BuiltInFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
			ArucasValueThread thread = context.getThreadHandler().runAsyncFunctionInContext(
				context.createBranch(), (branchContext) -> functionValue.call(branchContext, new ArucasValueList()),
				stringValue.value
			);
			return ThreadValue.of(thread);
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
			thisValue.value.interrupt();
			return NullValue.NULL;
		}
	}
}
