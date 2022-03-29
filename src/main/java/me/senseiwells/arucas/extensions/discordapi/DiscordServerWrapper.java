package me.senseiwells.arucas.extensions.discordapi;

import me.senseiwells.arucas.api.wrappers.ArucasDefinition;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassValue;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;

/**
 * DiscordServer class wrapper for Arucas. <br>
 * Fully Documented.
 * @author senseiwells
 */
@ArucasClass(name = "DiscordServer")
public class DiscordServerWrapper implements IArucasWrappedClass {
	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION;

	private Guild guild;

	/**
	 * Name: <code>&lt;DiscordServer>.ban(user, reason)</code> <br>
	 * Description: This bans a user from the server, with a reason <br>
	 * Parameters - DiscordUser, String: the user to ban, the reason for the ban <br>
	 * Example: <code>server.ban(user, "The ban hammer has struck!");</code>
	 */
	@ArucasFunction
	public void ban(Context context, WrapperClassValue wrapperClassValue, StringValue reason) {
		DiscordUserWrapper userWrapper = wrapperClassValue.getWrapper(DiscordUserWrapper.class);
		this.guild.ban(userWrapper.getUser(), 0, reason.value).complete();
	}

	/**
	 * Name: <code>&lt;DiscordServer>.ban(user)</code> <br>
	 * Description: This bans a user from the server <br>
	 * Parameter - DiscordUser: the user to ban <br>
	 * Example: <code>server.ban(user);</code>
	 */
	@ArucasFunction
	public void ban(Context context, WrapperClassValue wrapperClassValue) {
		DiscordUserWrapper userWrapper = wrapperClassValue.getWrapper(DiscordUserWrapper.class);
		this.guild.ban(userWrapper.getUser(), 0).complete();
	}

	/**
	 * Name: <code>&lt;DiscordServer>.kick(user)</code> <br>
	 * Description: This kicks a user from the server <br>
	 * Parameter - DiscordUser: the user to kick <br>
	 * Throws - Error: "Member was null" if the user was null <br>
	 * Example: <code>server.kick(user);</code>
	 */
	@ArucasFunction
	public void kick(Context context, WrapperClassValue wrapperClassValue) {
		DiscordUserWrapper userWrapper = wrapperClassValue.getWrapper(DiscordUserWrapper.class);
		Member member = this.guild.getMember(userWrapper.getUser());
		if (member == null) {
			throw new RuntimeException("Member was null");
		}
		this.guild.kick(member).complete();
	}

	/**
	 * Name: <code>&lt;DiscordServer>.unban(user)</code> <br>
	 * Description: This unbans a user from the server <br>
	 * Parameter - DiscordUser: the user to unban <br>
	 * Example: <code>server.unban(user);</code>
	 */
	@ArucasFunction
	public void unban(Context context, WrapperClassValue wrapperClassValue) {
		DiscordUserWrapper userWrapper = wrapperClassValue.getWrapper(DiscordUserWrapper.class);
		this.guild.unban(userWrapper.getUser()).complete();
	}

	/**
	 * Name: <code>&lt;DiscordServer>.getOwnerId()</code> <br>
	 * Description: This gets the id of the owner of the server <br>
	 * Returns - String: the id of the owner <br>
	 * Example: <code>server.getOwnerId();</code>
	 */
	@ArucasFunction
	public StringValue getOwnerId(Context context) {
		return StringValue.of(this.guild.getOwnerId());
	}

	/**
	 * Name: <code>&lt;DiscordServer>.getMemberCount()</code> <br>
	 * Description: This gets the amount of members in the server <br>
	 * Returns - Number: the amount of members <br>
	 * Example: <code>server.getMemberCount();</code>
	 */
	@ArucasFunction
	public NumberValue getMemberCount(Context context) {
		return NumberValue.of(this.guild.getMemberCount());
	}

	/**
	 * Name: <code>&lt;DiscordServer>.getUserFromId(userId)</code> <br>
	 * Description: This gets a user from the server by their id <br>
	 * Parameter - String: the id of the user <br>
	 * Returns - DiscordUser/Null: the user, if the user cannot be found returns null <br>
	 * Example: <code>server.getUserFromId("12345678901234567890123456789012");</code>
	 */
	@ArucasFunction
	public Value<?> getUserFromId(Context context, StringValue stringValue) throws CodeError {
		Member member = this.guild.retrieveMemberById(stringValue.value).complete();
		return member == null ? NullValue.NULL : DiscordUserWrapper.newDiscordUser(member.getUser(), context);
	}

	/**
	 * Name: <code>&lt;DiscordServer>.createRole(roleMap)</code> <br>
	 * Description: This creates a role in the server <br>
	 * Parameter - Map: the map of the role <br>
	 * Throws - Error: "..." if the role map was invalid <br>
	 * Example: <code>server.createRole({"name": "new role", "colour": 0xFFFFFF, "permissions": ["Manage Permissions", "Ban Members"]});</code>
	 */
	@ArucasFunction
	public void createRole(Context context, MapValue mapValue) throws CodeError {
		DiscordUtils.parseMapAsRole(context, this.guild.createRole(), mapValue);
	}

	public static WrapperClassValue newDiscordServer(Guild guild, Context context) throws CodeError {
		DiscordServerWrapper channelWrapper = new DiscordServerWrapper();
		channelWrapper.guild = guild;
		return DEFINITION.createNewDefinition(channelWrapper, context, List.of());
	}
}
