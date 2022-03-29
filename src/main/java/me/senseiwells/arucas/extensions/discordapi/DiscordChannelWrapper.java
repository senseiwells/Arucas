package me.senseiwells.arucas.extensions.discordapi;

import me.senseiwells.arucas.api.wrappers.ArucasDefinition;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassValue;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;

/**
 * DiscordChannel class wrapper for Arucas. <br>
 * Fully Documented.
 * @author senseiwells
 */
@ArucasClass(name = "DiscordChannel")
public class DiscordChannelWrapper implements IArucasWrappedClass {
	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION;

	private MessageChannel channel;

	/**
	 * Name: <code>&lt;DiscordChannel>.getMessageFromId(messageId)</code> <br>
	 * Description: This gets a message by its id <br>
	 * Parameter - String: the id of the message <br>
	 * Returns - DiscordMessage: the message <br>
	 * Throws - Error: "Message with id ... couldn't be found" if the message is not found <br>
	 * Example: <code>channel.getMessageFromId("12345678901234567890123456789012");</code>
	 */
	@ArucasFunction
	public WrapperClassValue getMessageFromId(Context context, StringValue messageId) throws CodeError {
		Message message = this.channel.getHistory().getMessageById(messageId.value);
		if (message == null) {
			throw new RuntimeException("Message with id " + messageId.value + " couldn't be found");
		}
		return DiscordMessageWrapper.newDiscordMessage(message, context);
	}

	/**
	 * Name: <code>&lt;DiscordChannel>.getHistory(amount)</code> <br>
	 * Description: This gets the last X messages <br>
	 * Parameter - Number: the amount of messages to get <br>
	 * Returns - List: the messages <br>
	 * Example: <code>channel.getMessages(10);</code>
	 */
	@ArucasFunction
	public ListValue getHistory(Context context, NumberValue amount) throws CodeError {
		List<Message> messages = this.channel.getHistory().retrievePast(amount.value.intValue()).complete();
		ArucasList arucasList = new ArucasList();
		for (Message message : messages) {
			arucasList.add(DiscordMessageWrapper.newDiscordMessage(message, context));
		}
		return new ListValue(arucasList);
	}

	/**
	 * Name: <code>&lt;DiscordChannel>.markTyping()</code> <br>
	 * Description: This marks the bot as typing in this channel, it lasts 10 seconds or until the message is sent <br>
	 * Example: <code>channel.markTyping();</code>
	 */
	@ArucasFunction
	public void markTyping(Context context) {
		this.channel.sendTyping().complete();
	}

	/**
	 * Name: <code>&lt;DiscordChannel>.sendMessage(message)</code> <br>
	 * Description: This sends a message to this channel <br>
	 * Parameter - String: the message <br>
	 * Returns - DiscordMessage: the message that was sent <br>
	 * Example: <code>channel.sendMessage("Hello World!");</code>
	 */
	@ArucasFunction
	public WrapperClassValue sendMessage(Context context, StringValue message) throws CodeError {
		return DiscordMessageWrapper.newDiscordMessage(this.channel.sendMessage(message.value).complete(), context);
	}

	/**
	 * Name: <code>&lt;DiscordChannel>.sendEmbed(embedMap)</code> <br>
	 * Description: This sends an embed to this channel <br>
	 * Parameter - Map: the embed map <br>
	 * Returns - DiscordMessage: the message that was sent <br>
	 * Example: <code>channel.sendEmbed({"title": "EMBED!", "description": ["Wow", "Nice"], "colour": 0xFFFFFF});</code>
	 */
	@ArucasFunction
	public WrapperClassValue sendEmbed(Context context, MapValue embed) throws CodeError {
		Message message = this.channel.sendMessageEmbeds(DiscordUtils.parseMapAsEmbed(context, embed)).complete();
		return DiscordMessageWrapper.newDiscordMessage(message, context);
	}

	/**
	 * Name: <code>&lt;DiscordChannel>.sendFile(file)</code> <br>
	 * Description: This sends a file to this channel <br>
	 * Parameter - File: the file <br>
	 * Returns - DiscordMessage: the message that was sent <br>
	 * Example: <code>channel.sendFile(file);</code>
	 */
	@ArucasFunction
	public WrapperClassValue sendFile(Context context, FileValue fileValue) throws CodeError {
		return DiscordMessageWrapper.newDiscordMessage(this.channel.sendFile(fileValue.value).complete(), context);
	}

	public static WrapperClassValue newDiscordChannel(MessageChannel channel, Context context) throws CodeError {
		DiscordChannelWrapper channelWrapper = new DiscordChannelWrapper();
		channelWrapper.channel = channel;
		return DEFINITION.createNewDefinition(channelWrapper, context, List.of());
	}
}
