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

/**
 * DiscordUser class wrapper for Arucas. <br>
 * Fully Documented.
 * @author senseiwells
 */
@ArucasClass(name = "DiscordUser")
public class DiscordUserWrapper implements IArucasWrappedClass {
	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION;

	private User user;

	/**
	 * Name: <code>&lt;DiscordUser>.getName()</code> <br>
	 * Description: This gets the name of the user <br>
	 * Returns - String: the name of the user <br>
	 * Example: <code>user.getName();</code>
	 */
	@ArucasFunction
	public StringValue getName(Context context) {
		return StringValue.of(this.user.getName());
	}

	/**
	 * Name: <code>&lt;DiscordUser>.getTag()</code> <br>
	 * Description: This gets the tag of the user, the numbers after the # <br>
	 * Returns - String: the tag of the user <br>
	 * Example: <code>user.getTag();</code>
	 */
	@ArucasFunction
	public StringValue getTag(Context context) {
		return StringValue.of(this.user.getDiscriminator());
	}

	/**
	 * Name: <code>&lt;DiscordUser>.getNameAndTag()</code> <br>
	 * Description: This gets the name and tag of the user <br>
	 * Returns - String: the name and tag of the user <br>
	 * Example: <code>user.getNameAndTag();</code>
	 */
	@ArucasFunction
	public StringValue getNameAndTag(Context context) {
		return StringValue.of(this.user.getAsTag());
	}

	/**
	 * Name: <code>&lt;DiscordUser>.getId()</code> <br>
	 * Description: This gets the id of the user <br>
	 * Returns - String: the id of the user <br>
	 * Example: <code>user.getId();</code>
	 */
	@ArucasFunction
	public StringValue getId(Context context) {
		return DiscordUtils.getId(this.user);
	}

	public User getUser() {
		return this.user;
	}

	public static WrapperClassValue newDiscordUser(User user, Context context) throws CodeError {
		DiscordUserWrapper userWrapper = new DiscordUserWrapper();
		userWrapper.user = user;
		return DEFINITION.createNewDefinition(userWrapper, context, List.of());
	}
}
