package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.throwables.CodeError;

@FunctionalInterface
public interface ArucasBiFunction<F, S, R> {
	R apply(F first, S second) throws CodeError;
}