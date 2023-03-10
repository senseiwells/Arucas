package me.senseiwells.arucas.interpreter

import me.senseiwells.arucas.api.ArucasExecutor
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.InternalTrace
import me.senseiwells.arucas.exceptions.ArucasError
import me.senseiwells.arucas.exceptions.FatalError
import me.senseiwells.arucas.exceptions.Propagator
import me.senseiwells.arucas.utils.impl.ArucasThread
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor

class ThreadHandler(val interpreter: Interpreter) {
    private val shutdown = ArrayList<Runnable>()
    private val threadGroup = ThreadGroup("Arucas Thread Group")
    private val async = ArucasExecutor.wrap(this.createExecutor())

    private var errored = false
    var running: Boolean = false
        private set

    internal fun execute(): ClassInstance {
        if (this.running) {
            throw IllegalStateException("This handler is occupied")
        }
        this.running = true

        val value = try {
            this.interpreter.interpret()
            this.interpreter.getNull()
        } catch (returnPropagator: Propagator.Return) {
            returnPropagator.returnValue
        }
        this.stop()
        return value
    }

    internal fun <T> async(interpreter: Interpreter, function: () -> T): Future<T?> {
       return this.async(interpreter, null, function)
    }

    internal fun <T> async(interpreter: Interpreter, executor: ArucasExecutor?, function: () -> T): Future<T?> {
        return (executor ?: this.async).submit {
            try {
                function()
            } catch (throwable: Throwable) {
                this@ThreadHandler.handleError(throwable, interpreter)
                null
            }
        }
    }

    internal fun <T> blocking(interpreter: Interpreter, function: () -> T): T {
        return this.blocking(interpreter, null, function)
    }

    internal fun <T> blocking(interpreter: Interpreter, executor: ArucasExecutor?, function: () -> T): T {
        val throwableFuture = CompletableFuture<Throwable?>()
        val future = (executor ?: this.async).submit {
            try {
                function().also {
                    throwableFuture.complete(null)
                }
            } catch (throwable: Throwable) {
                this@ThreadHandler.handleError(throwable, interpreter)
                throwableFuture.complete(throwable)
                null
            }
        }
        val throwable = throwableFuture.get()
        if (throwable != null) {
            throw throwable
        }
        return future.get()!!
    }

    internal fun runFunctionOnThread(callable: ClassInstance, interpreter: Interpreter, name: String): ArucasThread {
        val branch = interpreter.branch()
        return ArucasThread(interpreter, this.threadGroup, {
            interpreter.runSafe { branch.call(callable, listOf(), InternalTrace("Async Thread Function Call")) }
        }, name).also { it.start() }
    }

    @Synchronized
    internal fun addShutdownEvent(runnable: Runnable) {
        this.shutdown.add(runnable)
    }

    @Synchronized
    internal fun stop() {
        if (this.running) {
            var i = 0
            while (i < this.shutdown.size) {
                this.shutdown[i++].run()
            }
            this.shutdown.clear()

            this.threadGroup.interrupt()
            this.running = false
        }
    }

    @Synchronized
    internal fun handleError(throwable: Throwable, interpreter: Interpreter) {
        if (!this.running || this.errored) {
            return
        }
        try {
            val errorHandler = interpreter.api.getErrorHandler()
            when (throwable) {
                is Propagator.Stop -> return
                is Propagator -> errorHandler.handleInvalidPropagator(throwable, interpreter)
                is FatalError -> errorHandler.handleFatalError(throwable, interpreter)
                is ArucasError -> errorHandler.handleArucasError(throwable, interpreter)
                else -> errorHandler.handleFatalError(throwable, interpreter)
            }
        } finally {
            this.errored = true
            val currentThread = Thread.currentThread()
            if (currentThread !is ArucasThread || !currentThread.stopping) {
                this.stop()
            }
        }
    }

    private fun createExecutor(): ScheduledExecutorService {
        return ScheduledThreadPoolExecutor(2) { runnable ->
            ArucasThread(this.interpreter, this.threadGroup, runnable, "Arucas Async Thread")
        }
    }
}