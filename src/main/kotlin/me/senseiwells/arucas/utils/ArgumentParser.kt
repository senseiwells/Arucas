package me.senseiwells.arucas.utils

class ArgumentParser {
    private val map = HashMap<String, Action<*>>()

    fun addStr(flag: String, action: (String) -> Unit) {
        this.map[flag] = Action.Str(action)
    }

    fun addBool(flag: String, action: (Boolean) -> Unit) {
        this.map[flag] = Action.Bool(action)
    }

    fun addInt(flag: String, action: (Int) -> Unit) {
        this.map[flag] = Action.IntZ(action)
    }

    fun parse(args: Array<String>) {
        if (args.size % 2 != 0) {
            throw IllegalArgumentException("Incorrect number of arguments, each flag must have a parameter")
        }
        for (i in args.indices step 2) {
            this.map[args[i]]?.run(args[i + 1])
        }
    }

    private abstract class Action<T>(val consumer: (T) -> Unit) {
        abstract fun convert(string: String): T

        fun run(input: String) {
            this.consumer(this.convert(input))
        }

        class Bool(consumer: (Boolean) -> Unit): Action<Boolean>(consumer) {
            override fun convert(string: String) = string == "true"
        }

        class Str(consumer: (String) -> Unit): Action<String>(consumer) {
            override fun convert(string: String) = string
        }

        class IntZ(consumer: (Int) -> Unit): Action<Int>(consumer) {
            override fun convert(string: String) = string.toInt()
        }
    }
}