package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.ArucasValueThread;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;
import java.util.Set;

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

	@Override
	protected Set<MemberFunction> getDefinedFunctions() {
		Set<MemberFunction> memberFunctions = super.getDefinedFunctions();
		memberFunctions.addAll(Set.of(
			new MemberFunction("isAlive", this::isAlive),
			new MemberFunction("getAge", this::getAge),
			new MemberFunction("getName", this::getName),
			new MemberFunction("stop", this::stop)
		));
		return memberFunctions;
	}

	private Value<?> isAlive(Context context, MemberFunction function) {
		return BooleanValue.of(this.value.isAlive());
	}

	private Value<?> getAge(Context context, MemberFunction function) {
		return NumberValue.of(System.currentTimeMillis() - this.value.getStartTime());
	}

	private Value<?> getName(Context context, MemberFunction function) {
		return this.name;
	}

	private Value<?> stop(Context context, MemberFunction function) throws RuntimeError {
		if (!this.value.isAlive()) {
			throw new RuntimeError("Thread is not alive", function.syntaxPosition, context);
		}
		this.value.interrupt();
		return NullValue.NULL;
	}

	public static class ArucasThreadClass extends ArucasClassExtension {
		public ArucasThreadClass() {
			super("Thread");
		}

		@Override
		public List<MemberFunction> getDefinedStaticMethods() {
			return List.of(
				new MemberFunction("getCurrentThread", this::getCurrentThread),
				new MemberFunction("runThreaded", List.of("function"), this::runThreaded$1),
				new MemberFunction("runThreaded", List.of("name", "function"), this::runThreaded$2)
			);
		}

		private Value<?> getCurrentThread(Context context, MemberFunction function) throws RuntimeError {
			Thread currentThread = Thread.currentThread();
			if (currentThread instanceof ArucasValueThread arucasValueThread) {
				return ThreadValue.of(arucasValueThread);
			}
			throw new RuntimeError("Thread is not safe to get", function.syntaxPosition, context);
		}

		private Value<?> runThreaded$1(Context context, MemberFunction function) throws CodeError {
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 0);
			ArucasValueThread thread = context.getThreadHandler().runAsyncFunctionInContext(
				context.createBranch(), (branchContext) -> functionValue.call(branchContext, new ArucasValueList()),
				"Unnamed Arucas Thread"
			);
			return ThreadValue.of(thread);
		}

		private Value<?> runThreaded$2(Context context, MemberFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
			ArucasValueThread thread = context.getThreadHandler().runAsyncFunctionInContext(
				context.createBranch(), (branchContext) -> functionValue.call(branchContext, new ArucasValueList()),
				stringValue.value
			);
			return ThreadValue.of(thread);
		}
	}
}
