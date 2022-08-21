package me.senseiwells.arucas.utils.impl

import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.ArucasFunction
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Future

abstract class Task(interpreter: Interpreter) {
    protected val interpreter = interpreter.branch()

    abstract fun canModify(): Boolean

    abstract fun addTask(function: ArucasFunction)

    abstract fun run(): Future<ClassInstance?>

    protected fun throwIfRunning() {
        if (this.canModify()) {
            runtimeError("Cannot modify a task after it has been run")
        }
    }
}

class ArucasTask(interpreter: Interpreter): Task(interpreter) {
    private val tasks = ConcurrentLinkedQueue<ArucasFunction>()
    private var hasRun = false

    override fun canModify(): Boolean {
        return this.hasRun
    }

    override fun addTask(function: ArucasFunction) {
        this.throwIfRunning()
        this.tasks.add(function)
    }

    override fun run(): Future<ClassInstance?> {
        this.hasRun = true
        return this.interpreter.threadHandler.runAsync {
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
        }
    }
}