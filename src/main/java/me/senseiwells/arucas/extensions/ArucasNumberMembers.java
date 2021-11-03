package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.Set;

public class ArucasNumberMembers implements IArucasExtension {
	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.numberFunctions;
	}

	@Override
	public String getName() {
		return "NumberMemberFunctions";
	}

	private final Set<MemberFunction> numberFunctions = Set.of(

	);
}
