package me.senseiwells.arucas.extensions.discordapi;

import me.senseiwells.arucas.api.wrappers.ArucasDefinition;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassValue;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;

@ArucasClass(name = "DiscordChannel")
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
		this.channel.sendTyping().complete();
	}

	@ArucasFunction
	public WrapperClassValue sendMessage(Context context, StringValue message) throws CodeError {
		return DiscordMessageWrapper.createNewMessageWrapper(this.channel.sendMessage(message.value).complete(), context);
	}


	@ArucasFunction
	public WrapperClassValue sendEmbed(Context context, MapValue embed) throws CodeError {
		Message message = this.channel.sendMessageEmbeds(DiscordUtils.parseMapAsEmbed(context, embed)).complete();
		return DiscordMessageWrapper.createNewMessageWrapper(message, context);
	}

	@ArucasFunction
	public WrapperClassValue sendFile(Context context, FileValue fileValue) throws CodeError {
		return DiscordMessageWrapper.createNewMessageWrapper(this.channel.sendFile(fileValue.value).complete(), context);
	}

	public static WrapperClassValue createNewChannelWrapper(MessageChannel channel, Context context) throws CodeError {
		DiscordChannelWrapper channelWrapper = new DiscordChannelWrapper();
		channelWrapper.channel = channel;
		return DEFINITION.createNewDefinition(channelWrapper, context, List.of());
	}
}
