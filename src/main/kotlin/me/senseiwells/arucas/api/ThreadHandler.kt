package me.senseiwells.arucas.api

import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.core.Arucas
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.ArucasError
import me.senseiwells.arucas.exceptions.FatalError
import me.senseiwells.arucas.exceptions.Propagator
import me.senseiwells.arucas.utils.InternalTrace
import me.senseiwells.arucas.utils.impl.ArucasThread
import org.jetbrains.annotations.TestOnly
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor

@Suppress("DeprecatedCallableAddReplaceWith")
open class ThreadHandler(
    val interpreter: Interpreter
) {
    private val shutdown = ArrayList<Runnable>()
    protected open val threadGroup = ThreadGroup("Arucas Thread Group")
    protected open val executor = this.createExecutor()

    private var errored = false
    var running: Boolean = false
        private set

    @Deprecated("You should use the ArucasErrorHandler instead")
    protected open fun handleArucasError(arucasError: ArucasError, interpreter: Interpreter) {
        interpreter.api.getErrorHandler().handleArucasError(arucasError, interpreter)
    }

    @Deprecated("You should use the ArucasErrorHandler instead")
    protected open fun handleInvalidPropagator(propagator: Propagator, interpreter: Interpreter) {
        interpreter.api.getErrorHandler().handleInvalidPropagator(propagator, interpreter)
    }

    @Deprecated("You should use the ArucasErrorHandler instead")
    protected open fun handleFatalError(fatalError: FatalError, interpreter: Interpreter) {
        interpreter.api.getErrorHandler().handleFatalError(fatalError, interpreter)
    }

    @Deprecated("You should use the ArucasErrorHandler instead")
    protected open fun handleFatalError(throwable: Throwable, interpreter: Interpreter) {
        interpreter.api.getErrorHandler().handleFatalError(throwable, interpreter)
    }

    @Deprecated("This will have no replacement - you should register a shutdown event instead.")
    protected open fun onStop() {

    }

    @Deprecated("This should be called from the interpreter instead.")
    /* internal */ fun runAsync(function: () -> ClassInstance): Future<ClassInstance?> {
        return this.async { function() }
    }

    @Deprecated("This should be called from the interpreter instead.")
    /* internal */ fun runThreaded(runnable: Runnable, name: String): ArucasThread {
        return ArucasThread(this.interpreter, this.threadGroup, runnable, name).also { it.start() }
    }

    @Synchronized
    @Deprecated("This should be called from the interpreter instead.", ReplaceWith("interpreter.addStopEvent()"))
    /* internal */ fun addShutdownEvent(runnable: Runnable) {
        this.shutdown.add(runnable)
    }

    @Synchronized
    @Deprecated("This should be called from the interpreter instead.", ReplaceWith("interpreter.stop()"))
    /* internal */ fun stop() {
        if (this.running) {
            var i = 0;
            while (i < this.shutdown.size) {
                this.shutdown[i++].run()
            }
            this.shutdown.clear()

            this.threadGroup.interrupt()
            this.onStop()
            this.running = false
        }
    }

    @JvmOverloads
    @Synchronized
    @Deprecated("This should not be called outside of the internals of Arucas")
    /* internal */ fun handleError(throwable: Throwable, interpreter: Interpreter = this.interpreter) {
        if (!this.running || this.errored) {
            return
        }
        try {
            when (throwable) {
                is Propagator.Stop -> return
                is Propagator -> this.handleInvalidPropagator(throwable, interpreter)
                is FatalError -> this.handleFatalError(throwable, interpreter)
                is ArucasError -> this.handleArucasError(throwable, interpreter)
                else -> this.handleFatalError(throwable, interpreter)
            }
        } finally {
            this.errored = true
            val currentThread = Thread.currentThread()
            if (currentThread !is ArucasThread || !currentThread.stopping) {
                this.stop()
            }
        }
    }

    @Deprecated("This should not be called outside of the internals of Arucas.")
    /* internal */ fun functionThreaded(callable: ClassInstance, interpreter: Interpreter, name: String = "Arucas Async Thread"): ArucasThread {
        val branch = interpreter.branch()
        return this.runThreaded(this.safe {
            branch.call(callable, listOf(), InternalTrace("Async Thread Function Call"))
        }, name)
    }

    @Deprecated("You should use method that are part of the interpreter instead.")
    /* internal */ fun safe(runnable: Runnable): () -> Unit {
        return {
            try {
                runnable.run()
            } catch (throwable: Throwable) {
                this.handleError(throwable)
            }
        }
    }

    @Deprecated("You should use methods that are part of the interpreter instead.")
    /* internal */ fun <T> safeReturnable(supplier: () -> T): () -> T? {
        return {
            try {
                supplier()
            } catch (throwable: Throwable) {
                this.handleError(throwable)
                null
            }
        }
    }

    @Deprecated("You should be executing from the interpreter instead!", ReplaceWith("interpreter.executeAsync()"))
    /* internal */ fun executeAsync(): Future<ClassInstance?> {
        if (this.running) {
            throw IllegalStateException("This handler is occupied")
        }
        this.running = true

        return this.async {
            Arucas.run(this.interpreter).also {
                this.stop()
            }
        }
    }

    @Deprecated("You should be executing from the interpreter instead!", ReplaceWith("interpreter.executeBlocking()"))
    /* internal */ fun executeBlocking(): ClassInstance? {
        return this.executeAsync().get()
    }

    @TestOnly
    internal fun testAsync(): Future<ClassInstance> {
        if (this.running) {
            throw IllegalStateException("This handler is occupied")
        }
        this.running = true

        return this.unsafeAsync {
            Arucas.run(this.interpreter).also {
                this.stop()
            }
        }
    }

    @TestOnly
    internal fun testBlocking(): ClassInstance {
        try {
            return this.testAsync().get()
        } catch (e: ExecutionException) {
            val cause = e.cause
            cause ?: throw e
            throw cause
        }
    }

    private fun <T> unsafeAsync(function: () -> T): Future<T> {
        return this.executor.submit(function)
    }

    private fun <T> async(function: () -> T): Future<T?> {
        return this.unsafeAsync(safeReturnable(function))
    }

    private fun createExecutor(): ScheduledExecutorService {
        return ScheduledThreadPoolExecutor(2) { runnable ->
            ArucasThread(this.interpreter, this.threadGroup, runnable, "Arucas Async Thread")
        }
    }
}