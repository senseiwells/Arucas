package me.senseiwells.impl.wrappers;

import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.utils.Context;

public interface InterfaceWrapper {
	@FunctionDoc(
		name = "doSomethingInterface",
		desc = "Does something in an interface",
		example	= "interface.doSomethingInterface();"
	)
	@ArucasFunction
	default void doSomethingInterface(Context context) {
		System.out.println("doSomethingInterface");
	}
}
