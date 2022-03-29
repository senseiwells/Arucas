package me.senseiwells.arucas.extensions.discordapi;

import me.senseiwells.arucas.api.wrappers.ArucasDefinition;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.FileValue;
import me.senseiwells.arucas.values.MapValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassValue;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.channel.GenericChannelEvent;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import java.util.List;

/**
 * DiscordEvent class wrapper for Arucas. <br>
 * Fully Documented.
 * @author senseiwells
 */
@ArucasClass(name = "DiscordEvent")
public class DiscordEventWrapper implements IArucasWrappedClass {
	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION;

	private GenericEvent event;

	/**
	 * Name: <code>&lt;DiscordEvent>.getEventName()</code> <br>
	 * Description: This gets the name of the event <br>
	 * Returns - String: the name of the event <br>
	 * Example: <code>event.getEventName();</code>
	 */
	@ArucasFunction
	public StringValue getEventName(Context context) {
		return StringValue.of(this.event.getClass().getSimpleName());
	}

	/**
	 * Name: <code>&lt;DiscordEvent>.getMessage()</code> <br>
	 * Description: This gets the message that is related to the event <br>
	 * Returns - DiscordMessage: the message <br>
	 * Throws - Error: "... has no message" if the event does not have a message <br>
	 * Example: <code>event.getMessage();</code>
	 */
	@ArucasFunction
	public WrapperClassValue getMessage(Context context) throws CodeError {
		return DiscordMessageWrapper.newDiscordMessage(this.getMessage(), context);
	}

	/**
	 * Name: <code>&lt;DiscordEvent>.getUser()</code> <br>
	 * Description: This gets the user that is related to the event <br>
	 * Returns - DiscordUser: the user <br>
	 * Throws - Error: "... has no user" if the event does not have a user <br>
	 * Example: <code>event.getUser();</code>
	 */
	@ArucasFunction
	public WrapperClassValue getUser(Context context) throws CodeError {
		return DiscordUserWrapper.newDiscordUser(this.getUser(), context);
	}

	/**
	 * Name: <code>&lt;DiscordEvent>.getChannel()</code> <br>
	 * Description: This gets the channel that is related to the event <br>
	 * Returns - DiscordChannel: the channel <br>
	 * Throws - Error: "... has no channel" if the event does not have a channel <br>
	 * Example: <code>event.getChannel();</code>
	 */
	@ArucasFunction
	public WrapperClassValue getChannel(Context context) throws CodeError {
		return DiscordChannelWrapper.newDiscordChannel(this.getChannel(), context);
	}

	/**
	 * Name: <code>&lt;DiscordEvent>.getServer()</code> <br>
	 * Description: This gets the server that is related to the event <br>
	 * Returns - DiscordServer: the server <br>
	 * Throws - Error: "... has no server" if the event does not have a server <br>
	 * Example: <code>event.getServer();</code>
	 */
	@ArucasFunction
	public WrapperClassValue getServer(Context context) throws CodeError {
		return DiscordServerWrapper.newDiscordServer(this.getServer(), context);
	}

	/**
	 * Name: <code>&lt;DiscordEvent>.reply(message)</code> <br>
	 * Description: This replies to the event with the given message <br>
	 * Parameter - String: the message <br>
	 * Example: <code>event.reply("Reply!");</code>
	 */
	@ArucasFunction
	public void reply(Context context, StringValue message) {
		this.getReplyCallback().reply(message.value).complete();
	}

	/**
	 * Name: <code>&lt;DiscordEvent>.replyWithEmbed(embedMap)</code> <br>
	 * Description: This replies to the event with the given embed map <br>
	 * Parameter - Map: the embed map <br>
	 * Example: <code>event.replyWithEmbed({"title": "EMBED!", "description": ["Wow", "Nice"], "colour": 0xFFFFFF});</code>
	 */
	@ArucasFunction
	public void replyWithEmbed(Context context, MapValue mapValue) throws CodeError {
		this.getReplyCallback().replyEmbeds(DiscordUtils.parseMapAsEmbed(context, mapValue)).complete();
	}

	/**
	 * Name: <code>&lt;DiscordEvent>.replyWithFile(file)</code> <br>
	 * Description: This replies to the event with the given file <br>
	 * Parameter - File: the file <br>
	 * Example: <code>event.replyWithFile(new File("/path/to/file.txt"));</code>
	 */
	@ArucasFunction
	public void replyWithFile(Context context, FileValue fileValue) {
		this.getReplyCallback().replyFile(fileValue.value).complete();
	}

	private RuntimeException invalidEvent(String details) {
		return new RuntimeException("'%s' %s".formatted(this.event.getClass().getSimpleName(), details));
	}

	private IReplyCallback getReplyCallback() {
		if (this.event instanceof IReplyCallback iReplyCallback) {
			return iReplyCallback;
		}
		throw this.invalidEvent("cannot reply");
	}

	private Message getMessage() {
		return this.getMessage("has no message");
	}

	private Message getMessage(String error) {
		if (this.event instanceof MessageReceivedEvent receivedEvent) {
			return receivedEvent.getMessage();
		}
		if (this.event instanceof MessageUpdateEvent updateEvent) {
			return updateEvent.getMessage();
		}
		if (this.event instanceof GenericMessageReactionEvent reactionEvent) {
			return reactionEvent.retrieveMessage().complete();
		}
		throw this.invalidEvent(error);
	}

	private User getUser() {
		if (this.event instanceof GuildBanEvent banEvent) {
			return banEvent.getUser();
		}
		if (this.event instanceof Interaction interactionEvent) {
			return interactionEvent.getUser();
		}
		return this.getMessage("has no user").getAuthor();
	}

	private MessageChannel getChannel() {
		if (this.event instanceof GenericChannelEvent channelEvent && channelEvent.getChannel() instanceof MessageChannel messageChannel) {
			return messageChannel;
		}
		if (this.event instanceof Interaction interactionEvent) {
			return interactionEvent.getMessageChannel();
		}
		return this.getMessage("has no channel").getChannel();
	}

	private Guild getServer() {
		if (this.event instanceof GenericGuildEvent guildEvent) {
			return guildEvent.getGuild();
		}
		if (this.event instanceof Interaction interactionEvent) {
			return interactionEvent.getGuild();
		}
		return this.getMessage("has no server").getGuild();
	}

	public static WrapperClassValue newDiscordEvent(GenericEvent event, Context context) throws CodeError {
		DiscordEventWrapper eventWrapper = new DiscordEventWrapper();
		eventWrapper.event = event;
		return DEFINITION.createNewDefinition(eventWrapper, context, List.of());
	}
}
