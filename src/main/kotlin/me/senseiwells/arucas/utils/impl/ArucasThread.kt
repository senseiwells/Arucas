package me.senseiwells.arucas.utils.impl

import me.senseiwells.arucas.exceptions.Propagator
import me.senseiwells.arucas.interpreter.Interpreter
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ArucasThread internal constructor(
    private val interpreter: Interpreter,
    group: ThreadGroup,
    runnable: Runnable,
    name: String
): Thread(group, runnable, name) {
    private val freezeLock = ReentrantLock()
    private val condition = this.freezeLock.newCondition()

    var isFrozen = false
    var startTime: Long? = null
        private set
    var stopping: Boolean = false
        private set

    init {
        this.isDaemon = true
    }

    fun freeze() {
        this.isFrozen = true
        try {
            this.freezeLock.withLock {
                while (this.isFrozen) {
                    this.condition.await()
                }
            }
        } catch (e: InterruptedException) {
            throw Propagator.Stop.INSTANCE
        }
    }

    fun thaw() {
        this.freezeLock.withLock {
            this.isFrozen = false
            this.condition.signal()
        }
    }

    @Synchronized
    fun stopThread() {
        if (!this.stopping) {
            this.stopping = true
            this.interpreter.logDebug("Manually stopping thread: ${this.name}")
            this.interrupt()
        }
    }

    override fun start() {
        super.start()
        this.startTime = System.nanoTime()
        this.interpreter.logDebug("Starting thread: ${this.name}")
    }
}