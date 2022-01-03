package me.senseiwells.arucas.api;

import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ArucasThreadHandler {
	private final ThreadGroup arucasThreadGroup = new ThreadGroup("Arucas Thread Group");
	private final Map<Integer, Thread> threadMap;
	private final AtomicInteger threadCounter;

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
		this.threadMap = new HashMap<>();
		this.threadCounter = new AtomicInteger();
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

	@Deprecated
	public synchronized void stop() {
		if (this.isRunning()) {
			this.arucasThreadGroup.interrupt();
			this.finalHandler.run();
		}
	}

	private boolean isRunning() {
		return this.arucasThreadGroup.activeCount() > 0;
	}
	
	@Deprecated
	public boolean isScriptThread() {
		// TODO: Implement this check when running scripts!
		return Thread.currentThread().getThreadGroup() == this.arucasThreadGroup;
	}
	
	public synchronized int runOnThread(Context context, String fileName, String fileContent) {
		// Make sure that this handler belongs to the provided context
		if (context.getThreadHandler() != this) {
			return -1;
		}
//		if (!this.isRunning()) {
//			return -1;
//		}
		
		this.hasError = false;
		final int threadId = this.threadCounter.getAndIncrement();
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
				// Make sure we remove this thread from the thread map
				this.threadMap.remove(threadId);
			}
		});
		thread.setDaemon(true);
		thread.start();
		this.threadMap.put(threadId, thread);
		return threadId;
	}

	public synchronized int runAsyncFunctionInContext(Context context, ThrowableConsumer<Context> consumer) {
		return this.runAsyncFunction(context, consumer);
	}

	public synchronized int runBranchAsyncFunction(Context context, ThrowableConsumer<Context> consumer) {
		return this.runAsyncFunctionInContext(context.createBranch(), consumer);
	}
	
	public synchronized Thread getThreadById(int threadId) {
		return this.threadMap.get(threadId);
	}

	private synchronized int runAsyncFunction(final Context context, ThrowableConsumer<Context> consumer) {
		// Make sure that this handler belongs to the provided context
		if (context.getThreadHandler() != this) {
			return -1;
		}
//		if (!this.isRunning()) {
//			return -1;
//		}
		
		final int threadId = this.threadCounter.getAndIncrement();
		Thread thread = new Thread(this.arucasThreadGroup, () -> {
			try {
				consumer.accept(context);
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
			finally {
				// Make sure we remove this thread from the thread map
				this.threadMap.remove(threadId);
			}
		}, "Arucas Runnable Thread");
		thread.setDaemon(true);
		thread.start();
		this.threadMap.put(threadId, thread);
		return threadId;
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
