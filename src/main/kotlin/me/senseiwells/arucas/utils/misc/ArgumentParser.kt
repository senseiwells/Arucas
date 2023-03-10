package me.senseiwells.arucas.utils.misc

/**
 * Utility class to parse arguments passed into
 * the main function.
 *
 * This allows for different arguments to be defined
 * with defined argument types and behaviours.
 */
class ArgumentParser {
    private val map = HashMap<String, Action<*>>()

    /**
     * Adds a string argument.
     *
     * @param flag the argument name.
     * @param action the action for if the argument is passed.
     */
    fun addString(flag: String, action: (String) -> Unit) {
        this.map[flag] = Action.Str(action)
    }

    /**
     * Adds a boolean argument.
     *
     * @param flag the argument name.
     * @param action the action for if the argument is passed.
     */
    fun addBoolean(flag: String, action: (Boolean) -> Unit) {
        this.map[flag] = Action.Bool(action)
    }

    /**
     * Adds a integer argument.
     *
     * @param flag the argument name.
     * @param action the action for if the argument is passed.
     */
    fun addInt(flag: String, action: (Int) -> Unit) {
        this.map[flag] = Action.IntZ(action)
    }

    /**
     * Parses the array of arguments into the defined accepted arguments.
     *
     * @param args the given arguments.
     */
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