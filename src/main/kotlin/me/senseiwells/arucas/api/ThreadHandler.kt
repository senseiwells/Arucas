package me.senseiwells.arucas.api

import kotlinx.coroutines.*
import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.core.Arucas
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.ArucasError
import me.senseiwells.arucas.exceptions.Propagator
import me.senseiwells.arucas.utils.InternalTrace
import me.senseiwells.arucas.utils.impl.ArucasThread
import java.lang.Runnable
import java.util.concurrent.ScheduledThreadPoolExecutor

open class ThreadHandler(
    val interpreter: Interpreter
) {
    private val shutdown = ArrayList<Runnable>()
    protected open val threadGroup = ThreadGroup("Arucas Thread Group")
    protected open val dispatcher = this.createDispatcher()

    private var errored = false
    var running: Boolean = false
        private set

    fun executeAsync(): Deferred<ClassInstance?> {
        if (this.running) {
            throw IllegalStateException("This handler is occupied")
        }
        this.running = true

        return this.coroutineAsync {
            Arucas.run(this.interpreter).also {
                this.stop()
            }
        }
    }

    fun executeBlocking(): ClassInstance? {
        return runBlocking { this@ThreadHandler.executeAsync().await() }
    }

    fun runAsync(function: () -> ClassInstance): Deferred<ClassInstance?> {
        return this.coroutineAsync { function() }
    }

    fun runThreaded(runnable: Runnable, name: String): ArucasThread {
        return ArucasThread(this.interpreter, this.threadGroup, runnable, name).also { it.start() }
    }

    fun functionThreaded(callable: ClassInstance, interpreter: Interpreter, name: String = "Arucas Async Thread"): ArucasThread {
        val branch = interpreter.branch()
        return this.runThreaded(this.runnableSafe {
            branch.call(callable, listOf(), InternalTrace("Async Thread Function Call"))
        }, name)
    }

    fun <T> wrapSafe(function: () -> T): T? {
        return try {
            function()
        } catch (throwable: Throwable) {
            this.handleError(throwable)
            null
        }
    }

    fun runnableSafe(runnable: Runnable): () -> Unit {
        return {
            try {
                runnable.run()
            } catch (throwable: Throwable) {
                this.handleError(throwable)
            }
        }
    }

    private fun <T> coroutineAsync(function: () -> T): Deferred<T?> {
        return CoroutineScope(this@ThreadHandler.dispatcher).async {
            return@async this@ThreadHandler.wrapSafe(function)
        }
    }

    // Intended for testing purposes only!

    fun testAsync(): Deferred<ClassInstance> {
        if (this.running) {
            throw IllegalStateException("This handler is occupied")
        }
        this.running = true

        return this.coroutineUnsafeAsync {
            Arucas.run(this.interpreter).also {
                this.stop()
            }
        }
    }

    fun testBlocking(): ClassInstance {
        return runBlocking { this@ThreadHandler.testAsync().await() }
    }

    private fun <T> coroutineUnsafeAsync(function: () -> T): Deferred<T> {
        return CoroutineScope(this@ThreadHandler.dispatcher).async {
            return@async function()
        }
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
    protected fun stop() {
        if (this.running) {
            this.shutdown.forEach(Runnable::run)
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

    protected open fun handleFatalError(throwable: Throwable) {
        throwable.printStackTrace()
    }

    protected open fun onStop() {

    }

    private fun createDispatcher(): ExecutorCoroutineDispatcher {
        return ScheduledThreadPoolExecutor(2) { runnable ->
            ArucasThread(this.interpreter, this.threadGroup, runnable, "Arucas Async Thread")
        }.asCoroutineDispatcher()
    }
}