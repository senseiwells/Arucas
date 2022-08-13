package me.senseiwells.arucas.api

import java.util.concurrent.CompletableFuture

/**
 * Interface to handle input for the interpreter.
 */
interface ArucasInput {
    /**
     * This method should provide a future
     * giving access to the users input.
     */
    fun takeInput(): CompletableFuture<String>
}