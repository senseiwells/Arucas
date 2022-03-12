package me.senseiwells.arucas.extensions.discordapi;

import me.senseiwells.arucas.api.wrappers.ArucasDefinition;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.ArucasWrapper;
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

@ArucasWrapper(name = "DiscordEvent")
public class DiscordEventWrapper implements IArucasWrappedClass {
	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION;

	private GenericEvent event;

	@ArucasFunction
	public WrapperClassValue getMessage(Context context) throws CodeError {
		return DiscordMessageWrapper.createNewMessageWrapper(this.getMessage(), context);
	}

	@ArucasFunction
	public WrapperClassValue getUser(Context context) throws CodeError {
		return DiscordUserWrapper.createNewDefinition(this.getUser(), context);
	}

	@ArucasFunction
	public WrapperClassValue getChannel(Context context) throws CodeError {
		return DiscordChannelWrapper.createNewChannelWrapper(this.getChannel(), context);
	}

	@ArucasFunction
	public WrapperClassValue getServer(Context context) throws CodeError {
		return DiscordServerWrapper.createNewChannelWrapper(this.getServer(), context);
	}

	@ArucasFunction
	public void reply(Context context, StringValue message) {
		this.getReplyCallback().reply(message.value).queue();
	}

	@ArucasFunction
	public void replyWithEmbed(Context context, MapValue mapValue) throws CodeError {
		this.getReplyCallback().replyEmbeds(DiscordUtils.parseMapAsEmbed(context, mapValue)).queue();
	}

	@ArucasFunction
	public void replyWithFile(Context context, FileValue fileValue) {
		this.getReplyCallback().replyFile(fileValue.value).queue();
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

	public static WrapperClassValue createNewEventWrapper(GenericEvent event, Context context) throws CodeError {
		DiscordEventWrapper eventWrapper = new DiscordEventWrapper();
		eventWrapper.event = event;
		return DEFINITION.createNewDefinition(eventWrapper, context, List.of());
	}
}
