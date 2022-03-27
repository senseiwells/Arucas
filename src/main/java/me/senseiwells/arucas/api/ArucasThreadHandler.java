package me.senseiwells.arucas.api;

import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasThread;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public final class ArucasThreadHandler {
	private final ThreadGroup arucasThreadGroup;
	private final List<Runnable> shutdownEvents;

	private TriConsumer<Context, Throwable, String> fatalErrorHandler;

	private boolean isRunning;
	private boolean hasError;
	
	// This class can only be instantiated from this package
	ArucasThreadHandler() {
		this.arucasThreadGroup = new ThreadGroup("Arucas Thread Group");
		this.shutdownEvents = new ArrayList<>();
		this.fatalErrorHandler = (c, t, s) -> t.printStackTrace();
		this.isRunning = false;
		this.hasError = false;
	}

	public ArucasThreadHandler setFatalErrorHandler(TriConsumer<Context, Throwable, String> triConsumer) {
		this.fatalErrorHandler = triConsumer;
		return this;
	}

	public synchronized void addShutdownEvent(Runnable runnable) {
		this.shutdownEvents.add(runnable);
	}

	public synchronized void stop() {
		if (this.isRunning()) {
			this.isRunning = false;
			this.shutdownEvents.forEach(Runnable::run);
			this.shutdownEvents.clear();

			this.arucasThreadGroup.interrupt();
		}
	}

	public boolean isRunning() {
		return this.isRunning;
	}

	/**
	 * This method is to run the base script from
	 * @param context the base context
	 * @param fileName the name of the file you are running from
	 * @param fileContent the Arucas code you want to execute
	 * @param latch this can be null, counts down when thread has finished executing
	 */
	public ArucasThread runOnThread(Context context, String fileName, String fileContent, CountDownLatch latch) {
		// Make sure that this handler belongs to the provided context
		if (context.getThreadHandler() != this || this.isRunning()) {
			if (latch != null) {
				latch.countDown();
			}
			return null;
		}
		
		this.hasError = false;
		this.isRunning = true;
		ArucasThread thread = new ArucasThread(this.arucasThreadGroup, () -> {
			try {
				Run.run(context, fileName, fileContent);
			}
			catch (ThrowStop stop) {
				context.getOutput().println(stop.toString(context));
			}
			catch (CodeError codeError) {
				this.tryError(context, codeError);
			}
			catch (Throwable t) {
				this.fatalErrorHandler.accept(context, t, fileContent);
			}
			finally {
				this.stop();
				if (latch != null) {
					latch.countDown();
				}
			}
		}, "Arucas Main Thread");
		return thread.start(context);
	}

	public Value<?> runOnThreadReturnable(Context context, String fileName, String fileContent) throws CodeError {
		if (context.getThreadHandler() != this || this.isRunning()) {
			return null;
		}

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<CodeError> atomicError = new AtomicReference<>(null);
		final AtomicReference<Value<?>> atomicValue = new AtomicReference<>(NullValue.NULL);

		this.hasError = false;
		this.isRunning = true;
		new ArucasThread(this.arucasThreadGroup, () -> {
			try {
				atomicValue.set(Run.run(context, fileName, fileContent));
			}
			catch (CodeError thrownCodeError) {
				atomicError.set(thrownCodeError);
			}
			finally {
				this.stop();
				latch.countDown();
			}
		}, "Arucas Test Thread").start(context);

		try {
			latch.await();
			// This sleep statement is required
			Thread.sleep(1);
		}
		catch (InterruptedException ignored) { }

		if (atomicError.get() != null) {
			throw atomicError.get();
		}
		return atomicValue.get();
	}

	/**
	 * @see #runAsyncFunctionInContext(Context, ThrowableConsumer, String) 
	 */
	public ArucasThread runAsyncFunctionInContext(Context context, ThrowableConsumer<Context> consumer) {
		return this.runAsyncFunction(context, consumer, "Arucas Runnable Thread");
	}

	/**
	 * This lets you run something on a different thread with
	 * a passed in context, this context is used in the consumer
	 * @param context the context you want to use in the consumer
	 * @param consumer the code you want to execute on the thread
	 * @param threadName the name of the thread
	 */
	public ArucasThread runAsyncFunctionInContext(Context context, ThrowableConsumer<Context> consumer, String threadName) {
		return this.runAsyncFunction(context, consumer, threadName);
	}

	private ArucasThread runAsyncFunction(final Context context, ThrowableConsumer<Context> consumer, String name) {
		// Make sure that this handler belongs to the provided context
		if (context.getThreadHandler() != this || !this.isRunning()) {
			return null;
		}

		return new ArucasThread(this.arucasThreadGroup, () -> {
			try {
				consumer.accept(context);
				return;
			}
			catch (CodeError codeError) {
				this.tryError(context, codeError);
			}
			catch (ThrowValue tv) {
				this.tryError(context, tv);
			}
			catch (Throwable t) {
				this.fatalErrorHandler.accept(context, t, "");
			}
			if (Thread.currentThread() instanceof ArucasThread arucasThread && arucasThread.isStopControlled()) {
				return;
			}
			// If an exception happens in a thread it stops the program
			this.stop();
		}, name).start(context);
	}

	public synchronized void tryError(Context context, CodeError error) {
		if (!this.hasError && error.errorType != CodeError.ErrorType.INTERRUPTED_ERROR) {
			context.getOutput().println(error.toString(context));
			this.hasError = true;
		}
	}

	private synchronized void tryError(Context context, ThrowValue tv) {
		if (!this.hasError) {
			context.getOutput().println(tv.getMessage());
			this.hasError = true;
		}
	}
	
	@FunctionalInterface
	public interface TriConsumer<A, B, C> {
		void accept(A a, B b, C c);
	}
	
	@FunctionalInterface
	public interface ThrowableConsumer<T> {
		void accept(T obj) throws Throwable;
	}
}
