package me.senseiwells.arucas.api.impl

import me.senseiwells.arucas.api.ArucasPoller
import me.senseiwells.arucas.core.Interpreter
import java.util.*

/**
 * Implementation of [ArucasPoller] which provides the ability
 * to have multiple [ArucasPoller]s. The behaviour of this class is
 * defined by the order in which pollers are added.
 */
class MultiArucasPoller: ArucasPoller {
    /**
     * The list of pollers.
     */
    private val pollers = LinkedList<ArucasPoller>()

    /**
     * This adds a poller to [pollers].
     *
     * @param poller the poller to add.
     */
    fun addPoller(poller: ArucasPoller) {
        this.pollers.add(poller)
    }

    /**
     * The polling method that gets executed periodically
     * while the interpreter is running.
     *
     * @param interpreter the interpreter being polled.
     * @return whether the interpreter should keep running.
     * @see [ArucasPoller.poll]
     */
    override fun poll(interpreter: Interpreter): Boolean {
        for (poller in this.pollers) {
            if (!poller.poll(interpreter)) {
                return false
            }
        }
        return true
    }
}