package me.senseiwells.arucas.api;

import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.utils.Functions.ThrowableRunnable;
import me.senseiwells.arucas.utils.Functions;
import me.senseiwells.arucas.utils.impl.ArucasThread;
import me.senseiwells.arucas.values.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public final class ArucasThreadHandler {
	private final ThreadGroup arucasThreadGroup;
	private final ScheduledThreadPoolExecutor asyncThreads;
	private final List<Runnable> shutdownEvents;

	private Functions.TriConsumer<Context, Throwable, String> fatalErrorHandler;
	private String currentFileContent;

	private boolean isRunning;
	private boolean hasError;

	// This class can only be instantiated from this package
	ArucasThreadHandler() {
		this.arucasThreadGroup = new ThreadGroup("Arucas Thread Group");
		this.shutdownEvents = new ArrayList<>();
		this.fatalErrorHandler = (c, t, s) -> t.printStackTrace();
		this.currentFileContent = "";
		this.isRunning = false;
		this.hasError = false;

		this.asyncThreads = new ScheduledThreadPoolExecutor(
			2, runnable -> new ArucasThread(this.arucasThreadGroup, runnable, "Arucas Async Thread")
		);
	}

	/**
	 * Sets the fatal error handler for when
	 * a Throwable is thrown when executing
	 * Arucas code. The consumer provides the
	 * context, throwable, and file contents
	 */
	public ArucasThreadHandler setFatalErrorHandler(Functions.TriConsumer<Context, Throwable, String> triConsumer) {
		this.fatalErrorHandler = triConsumer;
		return this;
	}

	/**
	 * Adds a runnable that will be called when
	 * the Main thread stops, events will then
	 * be cleared for when a new Main thread starts
	 */
	public synchronized void addShutdownEvent(Runnable runnable) {
		this.shutdownEvents.add(runnable);
	}

	/**
	 * This changes the current thread pool size
	 */
	public synchronized void setThreadPoolSize(int size) {
		this.asyncThreads.setCorePoolSize(size);
	}

	/**
	 * Checks whether the main Arucas thread is running
	 */
	public boolean isRunning() {
		return this.isRunning;
	}

	/**
	 * This allows you to try and submit an error to the thread
	 * which will stop the program unless it was controlled
	 */
	public synchronized void tryError(Context context, Throwable throwable) {
		if (!this.isRunning() || this.hasError || context.getThreadHandler() != this) {
			return;
		}
		try {
			if (throwable instanceof CodeError codeError) {
				if (codeError.errorType != CodeError.ErrorType.INTERRUPTED_ERROR) {
					context.getOutput().println(codeError.toString(context));
					this.hasError = true;
				}
				return;
			}
			this.hasError = true;
			if (throwable instanceof ThrowValue throwValue) {
				context.getOutput().println(throwValue.getMessage());
				return;
			}
			this.fatalErrorHandler.accept(context, throwable, this.currentFileContent);
		}
		finally {
			if (!(Thread.currentThread() instanceof ArucasThread arucasThread) || !arucasThread.isStopControlled()) {
				this.stop();
			}
		}
	}

	/**
	 * This method is to run the base script from
	 *
	 * @param context     the base context
	 * @param fileName    the name of the file you are running from
	 * @param fileContent the Arucas code you want to execute
	 */
	public ArucasThread runOnMainThread(Context context, String fileName, String fileContent) {
		// Make sure that this handler belongs to the provided context
		// Cannot have two main threads for the same program
		if (context.getThreadHandler() != this) {
			throw new IllegalStateException("Wrong context passed in");
		}
		if (this.isRunning()) {
			throw new IllegalStateException("Main thread is already running");
		}

		this.currentFileContent = fileContent;
		this.hasError = false;
		this.isRunning = true;

		return new ArucasThread(this.arucasThreadGroup, () -> {
			try {
				Run.run(context, fileName, fileContent, true);
				this.stop();
			}
			catch (Throwable t) {
				this.tryError(context, t);
			}
		}, "Arucas Main Thread").start(context);
	}

	/**
	 * This method is to run the base script returning a Future
	 *
	 * @param context     the base context
	 * @param fileName    the name of the file you are running from
	 * @param fileContent the Arucas code you want to execute
	 */
	public Future<Value> runOnMainThreadFuture(Context context, String fileName, String fileContent) {
		// Make sure that this handler belongs to the provided context
		// Cannot have two main threads for the same program
		if (context.getThreadHandler() != this) {
			throw new IllegalStateException("Wrong context passed in");
		}
		if (this.isRunning()) {
			throw new IllegalStateException("Main thread is already running");
		}

		this.currentFileContent = fileContent;
		this.hasError = false;
		this.isRunning = true;

		CompletableFuture<Value> futureValue = new CompletableFuture<>();
		new ArucasThread(this.arucasThreadGroup, () -> {
			Value value;
			try {
				value = Run.run(context, fileName, fileContent, true);
				this.stop();
			}
			catch (Throwable t) {
				this.tryError(context, t);
				value = null;
			}
			futureValue.complete(value);
		}, "Arucas Main Thread").start(context);
		return futureValue;
	}

	/**
	 * This method runs a script on a Main thread and
	 * will wait for the thread to finish returning the
	 * correct value and throwing any errors that the
	 * script would have thrown on the Main Thread
	 *
	 * @param context     the base context
	 * @param fileName    the name of the file you are running from
	 * @param fileContent the Arucas code you want to execute
	 */
	public Value runOnMainThreadAndWait(Context context, String fileName, String fileContent) throws CodeError {
		// Make sure that this handler belongs to the provided context
		// Cannot have two main threads for the same program
		if (context.getThreadHandler() != this) {
			throw new IllegalStateException("Wrong context passed in");
		}
		if (this.isRunning()) {
			throw new IllegalStateException("Main thread is already running");
		}

		this.currentFileContent = fileContent;
		this.hasError = false;
		this.isRunning = true;

		CompletableFuture<Throwable> futureThrowable = new CompletableFuture<>();
		CompletableFuture<Value> futureValue = new CompletableFuture<>();

		new ArucasThread(this.arucasThreadGroup, () -> {
			Value value;
			try {
				value = Run.run(context, fileName, fileContent, true);
			}
			catch (Throwable throwable) {
				futureThrowable.complete(throwable);
				value = null;
			}
			this.stop();
			futureValue.complete(value);
		}, "Arucas Main Thread").start(context);

		Value value = ExceptionUtils.catchAsNull(futureValue::get);
		if (value != null) {
			return value;
		}
		if (futureThrowable.isDone()) {
			Throwable throwable = ExceptionUtils.catchAsNull(futureThrowable::get);
			if (throwable == null) {
				throw new NullPointerException("Throwable was null");
			}
			if (throwable instanceof CodeError codeError) {
				throw codeError;
			}
			throw new RuntimeException(throwable);
		}
		throw new IllegalStateException("Result was neither an error or a value");
	}

	/**
	 * @see #runAsyncFunctionInContext(Context, ContextConsumer, String)
	 */
	public ArucasThread runAsyncFunctionInContext(Context context, ContextConsumer consumer) {
		return this.runAsyncFunction(context, consumer, "Arucas Async Thread");
	}

	/**
	 * This lets you run something on a different thread with
	 * a passed in context, this context is used in the consumer
	 *
	 * @param context    the context you want to use in the consumer
	 * @param consumer   the code you want to execute on the thread
	 * @param threadName the name of the thread
	 */
	public ArucasThread runAsyncFunctionInContext(Context context, ContextConsumer consumer, String threadName) {
		return this.runAsyncFunction(context, consumer, threadName);
	}

	private ArucasThread runAsyncFunction(Context context, ContextConsumer consumer, String name) {
		// Make sure that this handler belongs to the provided context
		// We also check that the Main thread is still running
		if (context.getThreadHandler() != this || !this.isRunning()) {
			return null;
		}

		return new ArucasThread(this.arucasThreadGroup, () -> {
			try {
				consumer.accept(context);
			}
			catch (Throwable t) {
				this.tryError(context, t);
			}
		}, name).start(context);
	}

	public void runAsyncFunctionInThreadPool(Context context, ContextConsumer consumer) {
		if (context.getThreadHandler() != this || !this.isRunning()) {
			return;
		}

		this.asyncThreads.execute(() -> {
			if (context.isDebug()) {
				context.getOutput().log("Running Async Thread\n");
			}
			try {
				consumer.accept(context);
			}
			catch (Throwable t) {
				this.tryError(context, t);
			}
		});
	}

	public CompletableFuture<Value> runAsyncFunctionInThreadPoolCompletable(Context context, ContextConsumerReturnable consumer) {
		if (context.getThreadHandler() != this || !this.isRunning()) {
			return CompletableFuture.completedFuture(null);
		}

		CompletableFuture<Value> future = new CompletableFuture<>();
		this.asyncThreads.execute(() -> {
			if (context.isDebug()) {
				context.getOutput().log("Running Async Thread\n");
			}
			try {
				future.complete(consumer.accept(context));
			}
			catch (Throwable t) {
				future.cancel(true);
				this.tryError(context, t);
			}
		});
		return future;
	}

	public ScheduledFuture<?> scheduleAsyncFunctionInThreadPool(int delay, TimeUnit timeUnit, Context context, ContextConsumer consumer) {
		if (context.getThreadHandler() != this || !this.isRunning()) {
			return null;
		}

		return this.asyncThreads.schedule(() -> {
			if (context.isDebug()) {
				context.getOutput().log("Running Scheduled Async Thread\n");
			}
			try {
				consumer.accept(context);
			}
			catch (Throwable t) {
				this.tryError(context, t);
			}
		}, delay, timeUnit);
	}

	public ScheduledFuture<?> loopAsyncFunctionInThreadPool(int period, TimeUnit timeUnit, Functions.ThrowableSupplier<Boolean> shouldContinue, ThrowableRunnable onFinish, Context context, ContextConsumer consumer) {
		AtomicReference<ScheduledFuture<?>> futureRef = new AtomicReference<>();
		futureRef.set(this.asyncThreads.scheduleAtFixedRate(() -> {
			try {
				consumer.accept(context);

				if (!shouldContinue.get()) {
					futureRef.get().cancel(false);
					onFinish.run();
				}
			}
			catch (Throwable t) {
				this.tryError(context, t);
			}
		}, 0, period, timeUnit));
		return futureRef.get();
	}

	private synchronized void stop() {
		if (this.isRunning()) {
			this.isRunning = false;
			this.shutdownEvents.forEach(Runnable::run);
			this.shutdownEvents.clear();

			this.arucasThreadGroup.interrupt();
			this.asyncThreads.shutdownNow();
			this.currentFileContent = "";
		}
	}

	@FunctionalInterface
	public interface ContextConsumer {
		void accept(Context obj) throws Throwable;
	}

	@FunctionalInterface
	public interface ContextConsumerReturnable {
		Value accept(Context obj) throws Throwable;
	}
}
