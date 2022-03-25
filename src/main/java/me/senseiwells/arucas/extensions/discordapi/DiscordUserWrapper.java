package me.senseiwells.arucas.extensions.discordapi;

import me.senseiwells.arucas.api.wrappers.ArucasDefinition;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassValue;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

@ArucasClass(name = "DiscordUser")
public class DiscordUserWrapper implements IArucasWrappedClass {
	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION;

	private User user;

	@ArucasFunction
	public StringValue getName(Context context) {
		return StringValue.of(this.user.getName());
	}

	@ArucasFunction
	public StringValue getTag(Context context) {
		return StringValue.of(this.user.getDiscriminator());
	}

	@ArucasFunction
	public StringValue getNameAndTag(Context context) {
		return StringValue.of(this.user.getAsTag());
	}

	@ArucasFunction
	public StringValue getId(Context context) {
		return DiscordUtils.getId(this.user);
	}

	public User getUser() {
		return this.user;
	}

	public static WrapperClassValue createNewDefinition(User user, Context context) throws CodeError {
		DiscordUserWrapper userWrapper = new DiscordUserWrapper();
		userWrapper.user = user;
		return DEFINITION.createNewDefinition(userWrapper, context, List.of());
	}
}
