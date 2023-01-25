package me.senseiwells.arucas.api

import java.util.concurrent.CompletableFuture

/**
 * Interface to handle input for the interpreter.
 */
interface ArucasInput {
    /**
     * This method should provide a future
     * giving access to the users input.
     *
     * @return the completable future for when input is complete.
     */
    fun takeInput(): CompletableFuture<String>
}