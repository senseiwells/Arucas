package me.senseiwells.impl.wrappers;

import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.utils.Context;

public interface InterfaceWrapper {
	@ArucasFunction
	default void doSomethingInterface(Context context) {
		System.out.println("doSomethingInterface");
	}
}
