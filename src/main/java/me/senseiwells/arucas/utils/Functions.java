package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.throwables.CodeError;

public final class Functions {
	private Functions() { }

	@FunctionalInterface
	public interface BiFunction<F, S, R> {
		R apply(F first, S second) throws CodeError;
	}

	@FunctionalInterface
	public interface UniFunction<F, R> {
		R apply(F first) throws CodeError;
	}

	@FunctionalInterface
	public interface TriConsumer<A, B, C> {
		void accept(A first, B second, C third);
	}
}
