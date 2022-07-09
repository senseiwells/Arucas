package me.senseiwells.arucas.api;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public interface IArucasInput {
	/**
	 * This should provide a future giving
	 * access to the users input.
	 */
	CompletableFuture<String> takeInput();
}
