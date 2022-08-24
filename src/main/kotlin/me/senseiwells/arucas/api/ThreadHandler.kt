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

open class ThreadHandler(
    val interpreter: Interpreter
) {
    private val shutdown = ArrayList<Runnable>()
    protected open val threadGroup = ThreadGroup("Arucas Thread Group")
    protected open val executor = this.createExecutor()

    private var errored = false
    var running: Boolean = false
        private set

    fun executeAsync(): Future<ClassInstance?> {
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

    fun executeBlocking(): ClassInstance? {
        return this.executeAsync().get()
    }

    fun runAsync(function: () -> ClassInstance): Future<ClassInstance?> {
        return this.async { function() }
    }

    fun runThreaded(runnable: Runnable, name: String): ArucasThread {
        return ArucasThread(this.interpreter, this.threadGroup, runnable, name).also { it.start() }
    }

    fun functionThreaded(callable: ClassInstance, interpreter: Interpreter, name: String = "Arucas Async Thread"): ArucasThread {
        val branch = interpreter.branch()
        return this.runThreaded(this.safe {
            branch.call(callable, listOf(), InternalTrace("Async Thread Function Call"))
        }, name)
    }

    fun safe(runnable: Runnable): () -> Unit {
        return {
            try {
                runnable.run()
            } catch (throwable: Throwable) {
                this.handleError(throwable)
            }
        }
    }

    fun <T> safeReturnable(supplier: () -> T): () -> T? {
        return {
            try {
                supplier()
            } catch (throwable: Throwable) {
                this.handleError(throwable)
                null
            }
        }
    }

    private fun <T> async(function: () -> T): Future<T?> {
        return this.unsafeAsync(safeReturnable(function))
    }

    @TestOnly
    fun testAsync(): Future<ClassInstance> {
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
    fun testBlocking(): ClassInstance {
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

    @Synchronized
    fun addShutdownEvent(runnable: Runnable) {
        this.shutdown.add(runnable)
    }

    @Synchronized
    fun handleError(throwable: Throwable) {
        if (!this.running || this.errored) {
            return
        }
        try {
            when (throwable) {
                is Propagator.Stop -> return
                is Propagator -> this.handleInvalidPropagator(throwable)
                is FatalError -> this.handleFatalError(throwable)
                is ArucasError -> this.handleArucasError(throwable)
                else -> this.handleFatalError(throwable)
            }
        } finally {
            this.errored = true
            val currentThread = Thread.currentThread()
            if (currentThread !is ArucasThread || !currentThread.stopping) {
                this.stop()
            }
        }
    }

    @Synchronized
    fun stop() {
        if (this.running) {
            val i = 0;
            while (i < this.shutdown.size) {
                this.shutdown[i].run()
            }
            this.shutdown.clear()

            this.threadGroup.interrupt()
            this.onStop()
            this.running = false
        }
    }

    protected open fun handleArucasError(arucasError: ArucasError) {
        this.interpreter.api.getOutput().printError(arucasError.format(this.interpreter))
    }

    protected open fun handleInvalidPropagator(propagator: Propagator) {
        this.interpreter.api.getOutput().println(propagator.message)
    }

    protected open fun handleFatalError(fatalError: FatalError) {
        this.interpreter.api.getOutput().printError(fatalError.format(this.interpreter))
    }

    protected open fun handleFatalError(throwable: Throwable) {
        throwable.printStackTrace()
    }

    protected open fun onStop() {

    }

    private fun createExecutor(): ScheduledExecutorService {
        return ScheduledThreadPoolExecutor(2) { runnable ->
            ArucasThread(this.interpreter, this.threadGroup, runnable, "Arucas Async Thread")
        }
    }
}