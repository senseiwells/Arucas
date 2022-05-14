package me.senseiwells.impl.wrappers;

import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.utils.Context;

@ArucasClass(name = "Child")
public class ChildWrapper extends BaseWrapper {
	@ArucasConstructor
	public void constructor(Context context) {

	}

	@FunctionDoc(
		name = "doSomethingElse",
		desc = "Does something",
		example = "child.doSomethingElse();"
	)
	@ArucasFunction
	public void doSomethingElse(Context context) {
		System.out.println("Did something else!");
	}

	@Override
	public void doSomething(Context context) {
		System.out.println("Child did something");;
	}
}
