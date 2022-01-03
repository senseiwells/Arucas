package me.senseiwells.arucas.api;

import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;

import java.util.function.Consumer;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ArucasThreadHandler {
	public static ArucasThreadHandler instance = new ArucasThreadHandler();

	private final ThreadGroup arucasThreadGroup = new ThreadGroup("Arucas Thread Group");

	private Consumer<String> stopErrorHandler;
	private Consumer<String> errorHandler;
	private TriConsumer<Context, Throwable, String> fatalErrorHandler;
	private Runnable finalHandler;

	private Context currentContext;
	private boolean hasErrored;

	private ArucasThreadHandler() {
		this.stopErrorHandler = this.errorHandler;
		this.errorHandler = System.out::println;
		this.fatalErrorHandler = (c, t, s) -> t.printStackTrace();
		this.finalHandler = () -> { };
		this.currentContext = null;
		this.hasErrored = false;
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
			this.currentContext = null;
			this.arucasThreadGroup.interrupt();
			this.finalHandler.run();
		}
	}

	private boolean isRunning() {
		return this.arucasThreadGroup.activeCount() > 0 && this.currentContext != null;
	}

	public synchronized Thread runOnThread(Context context, String fileName, String fileContent) {
		if (this.isRunning()) {
			return null;
		}
		this.hasErrored = false;
		this.currentContext = context;
		Thread thread = new Thread(this.arucasThreadGroup, () -> {
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
				this.stop();
			}
		});
		thread.setDaemon(true);
		thread.start();
		return thread;
	}

	public synchronized Thread runAsyncFunctionInContext(Context context, ThrowableConsumer<Context> consumer) {
		return this.runAsyncFunction(context, consumer);
	}

	public synchronized Thread runBranchAsyncFunction(ThrowableConsumer<Context> consumer) {
		return this.runAsyncFunctionInContext(this.currentContext.createBranch(), consumer);
	}

	private synchronized Thread runAsyncFunction(final Context context, ThrowableConsumer<Context> consumer) {
		if (!this.isRunning()) {
			return null;
		}
		Thread thread = new Thread(this.arucasThreadGroup, () -> {
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
			this.stop();
		}, "Arucas Runnable Thread");
		thread.setDaemon(true);
		thread.start();
		return thread;
	}

	private synchronized void tryError(Context context, CodeError error) {
		if (this.hasErrored || error.errorType == CodeError.ErrorType.INTERRUPTED_ERROR) {
			return;
		}
		this.errorHandler.accept(error.toString(context));
		this.hasErrored = true;
	}

	private synchronized void tryError(ThrowValue tv) {
		if (this.hasErrored) {
			return;
		}
		this.errorHandler.accept(tv.getMessage());
		this.hasErrored = true;
	}

	public interface TriConsumer<A, B, C> {
		void accept(A a, B b, C c);
	}

	public interface ThrowableConsumer<T> {
		void accept(T obj) throws Throwable;
	}
}
