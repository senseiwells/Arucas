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

@ArucasClass(name = "DiscordServer")
public class DiscordServerWrapper implements IArucasWrappedClass {
	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION;

	private Guild guild;

	@ArucasFunction
	public void ban(Context context, WrapperClassValue wrapperClassValue, StringValue reason) {
		DiscordUserWrapper userWrapper = wrapperClassValue.getWrapper(DiscordUserWrapper.class);
		this.guild.ban(userWrapper.getUser(), 0, reason.value).complete();
	}

	@ArucasFunction
	public void ban(Context context, WrapperClassValue wrapperClassValue) {
		DiscordUserWrapper userWrapper = wrapperClassValue.getWrapper(DiscordUserWrapper.class);
		this.guild.ban(userWrapper.getUser(), 0).complete();
	}

	@ArucasFunction
	public void kick(Context context, WrapperClassValue wrapperClassValue) {
		DiscordUserWrapper userWrapper = wrapperClassValue.getWrapper(DiscordUserWrapper.class);
		Member member = this.guild.getMember(userWrapper.getUser());
		if (member == null) {
			throw new RuntimeException("Member was null");
		}
		this.guild.kick(member).complete();
	}

	@ArucasFunction
	public void unban(Context context, WrapperClassValue wrapperClassValue) {
		DiscordUserWrapper userWrapper = wrapperClassValue.getWrapper(DiscordUserWrapper.class);
		this.guild.unban(userWrapper.getUser()).complete();
	}

	@ArucasFunction
	public StringValue getOwnerId(Context context) {
		return StringValue.of(this.guild.getOwnerId());
	}

	@ArucasFunction
	public NumberValue getMemberCount(Context context) {
		return NumberValue.of(this.guild.getMemberCount());
	}

	@ArucasFunction
	public Value<?> getUserFromId(Context context, StringValue stringValue) throws CodeError {
		Member member = this.guild.retrieveMemberById(stringValue.value).complete();
		return member == null ? NullValue.NULL : DiscordUserWrapper.createNewDefinition(member.getUser(), context);
	}

	@ArucasFunction
	public void createRole(Context context, MapValue mapValue) throws CodeError {
		DiscordUtils.parseMapAsRole(context, this.guild.createRole(), mapValue);
	}

	public static WrapperClassValue createNewChannelWrapper(Guild guild, Context context) throws CodeError {
		DiscordServerWrapper channelWrapper = new DiscordServerWrapper();
		channelWrapper.guild = guild;
		return DEFINITION.createNewDefinition(channelWrapper, context, List.of());
	}
}
