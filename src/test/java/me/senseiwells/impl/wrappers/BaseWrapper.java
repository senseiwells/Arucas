package me.senseiwells.impl.wrappers;

import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.utils.Context;

public abstract class BaseWrapper implements IArucasWrappedClass, InterfaceWrapper {
	@FunctionDoc(
		name = "doSomething",
		desc = "Does something",
		example = "base.doSomething();"
	)
	@ArucasFunction
	public void doSomething(Context context) {
		System.out.println("Did something!");
	}
}
