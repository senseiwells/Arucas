package me.senseiwells.arucas.api

import me.senseiwells.arucas.interpreter.Interpreter

/**
 * This class provides a polling method which gets called
 * while the interpreter is running periodically.
 * The poll can happen at *random intervals* and there
 * is no guarantee between intervals.
 *
 * The purpose for this is to be able to execute other
 * things during the [Interpreter]'s execution.
 */
interface ArucasPoller {
    /**
     * Polling method to execute anything while the
     * interpreter is running and ensure that the
     * interpreter can keep running.
     *
     * @param interpreter the interpreter that is being polled.
     */
    fun poll(interpreter: Interpreter)
}