package me.senseiwells.arucas.utils.impl

import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.ArucasFunction
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Future

abstract class Task(interpreter: Interpreter) {
    protected val interpreter = interpreter.branch()

    abstract fun isRunning(): Boolean

    abstract fun addTask(function: ArucasFunction)

    abstract fun run(): Future<ClassInstance?>

    protected fun throwIfRunning() {
        if (this.isRunning()) {
            runtimeError("Cannot modify a task while its running")
        }
    }
}

class ArucasTask(interpreter: Interpreter): Task(interpreter) {
    private val tasks = ConcurrentLinkedQueue<ArucasFunction>()
    @Volatile
    private var running = 0

    override fun isRunning(): Boolean {
        return this.running > 0
    }

    override fun addTask(function: ArucasFunction) {
        this.throwIfRunning()
        this.tasks.add(function)
    }

    override fun run(): Future<ClassInstance?> {
        return this.interpreter.threadHandler.runAsync {
            try {
                this.running++
                val branch = this.interpreter.branch()
                val iterator = this.tasks.iterator()

                while (iterator.hasNext()) {
                    val task = iterator.next()
                    if (!iterator.hasNext()) {
                        return@runAsync task.invoke(branch, listOf())
                    }
                    task.invoke(branch, listOf())
                }
                branch.getNull()
            } finally {
                this.running--
            }
        }
    }
}