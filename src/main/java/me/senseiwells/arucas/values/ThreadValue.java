package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
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

import static me.senseiwells.arucas.utils.ValueTypes.*;

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
		return THREAD;
	}

	@ClassDoc(
		name = THREAD,
		desc = "This class allows you to create threads for asynchronous execution."
	)
	public static class ArucasThreadClass extends ArucasClassExtension {
		public ArucasThreadClass() {
			super(THREAD);
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

		@FunctionDoc(
			isStatic = true,
			name = "getCurrentThread",
			desc = "This gets the current thread that the code is running on",
			returns = {THREAD, "the current thread"},
			throwMsgs = "Thread is not safe to get",
			example = "Thread.getCurrentThread();"
		)
		private Value<?> getCurrentThread(Context context, BuiltInFunction function) throws RuntimeError {
			Thread currentThread = Thread.currentThread();
			if (currentThread instanceof ArucasThread arucasValueThread) {
				return ThreadValue.of(arucasValueThread);
			}
			throw new RuntimeError("Thread is not safe to get", function.syntaxPosition, context);
		}

		@FunctionDoc(
			isStatic = true,
			name = "runThreaded",
			desc = {
				"This starts a new thread and runs a function on it, the thread will ",
				"terminate when it finishes executing the function, threads will stop automatically ",
				"when the program stops, you are also able to stop threads by using the Thread value"
			},
			params = {FUNCTION, "function", "the function you want to run on a new thread"},
			returns = {THREAD, "the new thread"},
			example = """
			Thread.runThreaded(fun() {
			    print("Running asynchronously!");
			});
			"""
		)
		private Value<?> runThreaded1(Context context, BuiltInFunction function) throws CodeError {
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 0);
			ArucasThread thread = context.getThreadHandler().runAsyncFunctionInContext(
				context.createBranch(), branchContext -> functionValue.call(branchContext, new ArucasList()),
				"Unnamed Arucas Thread"
			);
			return ThreadValue.of(thread);
		}

		@FunctionDoc(
			isStatic = true,
			name = "runThreaded",
			desc = "This starts a new thread with a specific name and runs a function on it",
			params = {
				STRING, "name", "the name of the thread",
				FUNCTION, "function", "the function you want to run on a new thread"
			},
			returns = {THREAD, "the new thread"},
			example = """
			Thread.runThreaded("MyThread", fun() {
			    print("Running asynchronously on MyThread!");
			});
			"""
		)
		private Value<?> runThreaded2(Context context, BuiltInFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
			ArucasThread thread = context.getThreadHandler().runAsyncFunctionInContext(
				context.createBranch(), branchContext -> functionValue.call(branchContext, new ArucasList()),
				stringValue.value
			);
			return ThreadValue.of(thread);
		}

		@FunctionDoc(
			isStatic = true,
			name = "freeze",
			desc = "This freezes the current thread, stops anything else from executing on the thread",
			example = "Thread.freeze();"
		)
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

		@FunctionDoc(
			name = "isAlive",
			desc = "This checks if the thread is alive (still running)",
			returns = {BOOLEAN, "true if the thread is alive, false if not"},
			example = "Thread.getCurrentThread().isAlive();"
		)
		private Value<?> isAlive(Context context, MemberFunction function) throws CodeError {
			ThreadValue thisValue = function.getParameterValueOfType(context, ThreadValue.class, 0);
			return BooleanValue.of(thisValue.value.isAlive());
		}

		@FunctionDoc(
			name = "getAge",
			desc = "This gets the age of the thread in milliseconds",
			returns = {NUMBER, "the age of the thread"},
			example = "Thread.getCurrentThread().getAge();"
		)
		private Value<?> getAge(Context context, MemberFunction function) throws CodeError {
			ThreadValue thisValue = function.getParameterValueOfType(context, ThreadValue.class, 0);
			return NumberValue.of(System.currentTimeMillis() - thisValue.value.getStartTime());
		}

		@FunctionDoc(
			name = "getName",
			desc = "This gets the name of the thread",
			returns = {STRING, "the name of the thread"},
			example = "Thread.getCurrentThread().getName();"
		)
		private Value<?> getName(Context context, MemberFunction function) throws CodeError {
			ThreadValue thisValue = function.getParameterValueOfType(context, ThreadValue.class, 0);
			return thisValue.name;
		}

		@FunctionDoc(
			name = "stop",
			desc = "This stops the thread from executing, anything that was running will be instantly stopped",
			throwMsgs = "Thread is not alive",
			example = "Thread.getCurrentThread().stop();"
		)
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
