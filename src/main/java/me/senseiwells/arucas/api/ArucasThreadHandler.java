package me.senseiwells.arucas.api;

import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.impl.ArucasThread;
import me.senseiwells.arucas.utils.Context;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ArucasThreadHandler {
	private final ThreadGroup arucasThreadGroup = new ThreadGroup("Arucas Thread Group");

	private Consumer<String> stopErrorHandler;
	private Consumer<String> errorHandler;
	private TriConsumer<Context, Throwable, String> fatalErrorHandler;
	private Runnable finalHandler;

	private boolean hasError;
	
	// This class can only be instantiated from this package
	protected ArucasThreadHandler() {
		this.stopErrorHandler = this.errorHandler;
		this.errorHandler = System.out::println;
		this.fatalErrorHandler = (c, t, s) -> t.printStackTrace();
		this.finalHandler = () -> { };
		this.hasError = false;
	}

	public ArucasThreadHandler setStopErrorHandler(Consumer<String> consumer) {
		this.stopErrorHandler = consumer;
		return this;
	}

	public ArucasThreadHandler setErrorHandler(Consumer<String> consumer) {
		this.errorHandler = consumer;
		return this;
	}

	public ArucasThreadHandler setFatalErrorHandler(TriConsumer<Context, Throwable, String> triConsumer) {
		this.fatalErrorHandler = triConsumer;
		return this;
	}

	public ArucasThreadHandler setFinalHandler(Runnable runnable) {
		this.finalHandler = runnable;
		return this;
	}

	public synchronized void stop() {
		if (this.isRunning()) {
			this.arucasThreadGroup.interrupt();
			this.finalHandler.run();
		}
	}

	private boolean isRunning() {
		return this.arucasThreadGroup.activeCount() > 0;
	}

	/**
	 * This method is to run the base script from
	 * @param context the base context
	 * @param fileName the name of the file you are running from
	 * @param fileContent the Arucas code you want to execute
	 * @param latch this can be null, counts down when thread has finished executing
	 */
	public synchronized ArucasThread runOnThread(Context context, String fileName, String fileContent, CountDownLatch latch) {
		// Make sure that this handler belongs to the provided context
		if (context.getThreadHandler() != this || this.isRunning()) {
			if (latch != null) {
				latch.countDown();
			}
			return null;
		}
		
		this.hasError = false;
		ArucasThread thread = new ArucasThread(this.arucasThreadGroup, () -> {
			try {
				Run.run(context, fileName, fileContent);
			}
			catch (ThrowStop stop) {
				this.stopErrorHandler.accept(stop.toString(context));
			}
			catch (CodeError codeError) {
				this.tryError(context, codeError);
			}
			catch (Throwable t) {
				this.fatalErrorHandler.accept(context, t, fileContent);
			}
			finally {
				if (latch != null) {
					latch.countDown();
				}
				this.stop();
			}
		}, "Arucas Main Thread");
		thread.setDaemon(true);
		thread.start();
		return thread;
	}

	/**
	 * @see #runAsyncFunctionInContext(Context, ThrowableConsumer, String) 
	 */
	public synchronized ArucasThread runAsyncFunctionInContext(Context context, ThrowableConsumer<Context> consumer) {
		return this.runAsyncFunction(context, consumer, "Arucas Runnable Thread");
	}

	/**
	 * This lets you run something on a different thread with
	 * a passed in context, this context is used in the consumer
	 * @param context the context you want to use in the consumer
	 * @param consumer the code you want to execute on the thread
	 * @param threadName the name of the thread
	 */
	public synchronized ArucasThread runAsyncFunctionInContext(Context context, ThrowableConsumer<Context> consumer, String threadName) {
		return this.runAsyncFunction(context, consumer, threadName);
	}

	private synchronized ArucasThread runAsyncFunction(final Context context, ThrowableConsumer<Context> consumer, String name) {
		// Make sure that this handler belongs to the provided context
		if (context.getThreadHandler() != this || !this.isRunning()) {
			return null;
		}

		ArucasThread thread = new ArucasThread(this.arucasThreadGroup, () -> {
			try {
				consumer.accept(context);
				return;
			}
			catch (CodeError codeError) {
				this.tryError(context, codeError);
			}
			catch (ThrowValue tv) {
				this.tryError(tv);
			}
			catch (Throwable t) {
				this.fatalErrorHandler.accept(context, t, "");
			}
			// If an exception happens in a thread it stops the program
			this.stop();
		}, name);
		thread.setDaemon(true);
		thread.start();
		return thread;
	}

	private synchronized void tryError(Context context, CodeError error) {
		if (this.hasError || error.errorType == CodeError.ErrorType.INTERRUPTED_ERROR) {
			return;
		}
		this.errorHandler.accept(error.toString(context));
		this.hasError = true;
	}

	private synchronized void tryError(ThrowValue tv) {
		if (this.hasError) {
			return;
		}
		this.errorHandler.accept(tv.getMessage());
		this.hasError = true;
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
