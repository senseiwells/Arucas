package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.Set;

public class ArucasStringMembers implements IArucasExtension {
	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.stringFunctions;
	}

	@Override
	public String getName() {
		return "StringMemberFunctions";
	}

	private final Set<MemberFunction> stringFunctions = Set.of(

	);
}
