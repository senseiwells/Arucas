package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.throwables.CodeError;

public final class Functions {
	@FunctionalInterface
	public static interface Bi<F, S, R> {
		R apply(F first, S second) throws CodeError;
	}

	@FunctionalInterface
	public interface Uni<F, R> {
		R apply(F first) throws CodeError;
	}
}
