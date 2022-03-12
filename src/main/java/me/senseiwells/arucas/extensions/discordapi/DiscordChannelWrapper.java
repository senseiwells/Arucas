package me.senseiwells.arucas.extensions.discordapi;

import me.senseiwells.arucas.api.wrappers.ArucasDefinition;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.ArucasWrapper;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassValue;
import me.senseiwells.arucas.values.functions.FunctionValue;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;

@ArucasWrapper(name = "DiscordChannel")
public class DiscordChannelWrapper implements IArucasWrappedClass {
	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION;

	private MessageChannel channel;

	@ArucasFunction
	public WrapperClassValue getMessageFromId(Context context, StringValue messageId) throws CodeError {
		return DiscordMessageWrapper.createNewMessageWrapper(this.channel.getHistory().getMessageById(messageId.value), context);
	}

	@ArucasFunction
	public ListValue getHistory(Context context, NumberValue amount) throws CodeError {
		List<Message> messages = this.channel.getHistory().retrievePast(amount.value.intValue()).complete();
		ArucasList arucasList = new ArucasList();
		for (Message message : messages) {
			arucasList.add(DiscordMessageWrapper.createNewMessageWrapper(message, context));
		}
		return new ListValue(arucasList);
	}

	@ArucasFunction
	public void markTyping(Context context) {
		this.channel.sendTyping().queue();
	}

	@ArucasFunction
	public void sendMessage(Context context, StringValue message) {
		this.channel.sendMessage(message.value).queue();
	}

	@ArucasFunction
	public void sendMessage(Context context, StringValue message, FunctionValue then) {
		this.channel.sendMessage(message.value).queue(DiscordMessageWrapper.getMessageCallback(context, then));
	}

	@ArucasFunction
	public void sendEmbed(Context context, MapValue embed) throws CodeError {
		this.channel.sendMessageEmbeds(DiscordUtils.parseMapAsEmbed(context, embed)).queue();
	}

	@ArucasFunction
	public void sendEmbed(Context context, MapValue embed, FunctionValue then) throws CodeError {
		this.channel.sendMessageEmbeds(DiscordUtils.parseMapAsEmbed(context, embed)).queue(DiscordMessageWrapper.getMessageCallback(context, then));
	}

	@ArucasFunction
	public void sendFile(Context context, FileValue fileValue) {
		this.channel.sendFile(fileValue.value).queue();
	}

	@ArucasFunction
	public void sendFile(Context context, FileValue fileValue, FunctionValue then) {
		this.channel.sendFile(fileValue.value).queue(DiscordMessageWrapper.getMessageCallback(context, then));
	}

	public static WrapperClassValue createNewChannelWrapper(MessageChannel channel, Context context) throws CodeError {
		DiscordChannelWrapper channelWrapper = new DiscordChannelWrapper();
		channelWrapper.channel = channel;
		return DEFINITION.createNewDefinition(channelWrapper, context, List.of());
	}
}
