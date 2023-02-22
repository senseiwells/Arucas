package me.senseiwells.arucas.utils.impl

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.functions.ArucasFunction
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Future

abstract class Task(interpreter: Interpreter) {
    private val tasks = ConcurrentLinkedQueue<DelayedFunction>()
    private var delay = 0

    protected val interpreter = interpreter.branch()

    fun addDelay(delay: Int) {
        this.delay += delay
    }

    fun addTask(delay: Int, function: ArucasFunction) {
        this.tasks.add(DelayedFunction(delay + this.delay, function))
        this.delay = 0
    }

    fun run(): Future<ClassInstance?> {
        return this.run(this.tasks.iterator())
    }

    protected abstract fun run(tasks: Iterator<DelayedFunction>): Future<ClassInstance?>
}

class ArucasTask(interpreter: Interpreter): Task(interpreter) {
    override fun run(tasks: Iterator<DelayedFunction>): Future<ClassInstance?> {
        return this.interpreter.runAsync {
            val branch = this.interpreter.branch()

            while (tasks.hasNext()) {
                val task = tasks.next()
                val time = task.time.toLong()
                if (time != 0L) {
                    Thread.sleep(time)
                }
                val result = task.function.invoke(branch, listOf())
                if (!tasks.hasNext()) {
                    return@runAsync result
                }
            }
            branch.getNull()
        }
    }
}

data class DelayedFunction(val time: Int, val function: ArucasFunction)