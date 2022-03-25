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
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

@ArucasClass(name = "DiscordMessage")
public class DiscordMessageWrapper implements IArucasWrappedClass {
	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION;

	private Message message;

	@ArucasFunction
	public StringValue getId(Context context) {
		return DiscordUtils.getId(this.message);
	}

	@ArucasFunction
	public StringValue getRaw(Context context) {
		return this.toString(context);
	}

	@ArucasFunction
	public StringValue toString(Context context) {
		return StringValue.of(this.message.getContentRaw());
	}

	@ArucasFunction
	public WrapperClassValue getChannel(Context context) throws CodeError {
		return DiscordChannelWrapper.createNewChannelWrapper(this.message.getChannel(), context);
	}

	@ArucasFunction
	public WrapperClassValue getServer(Context context) throws CodeError {
		return DiscordServerWrapper.createNewChannelWrapper(this.message.getGuild(), context);
	}

	@ArucasFunction
	public WrapperClassValue getAuthor(Context context) throws CodeError {
		return DiscordUserWrapper.createNewDefinition(this.message.getAuthor(), context);
	}

	@ArucasFunction
	public ListValue getAttachments(Context context) throws CodeError {
		ArucasList arucasList = new ArucasList();
		for (Message.Attachment attachment: this.message.getAttachments()) {
			arucasList.add(DiscordAttachmentWrapper.createNewDefinition(attachment, context));
		}
		return new ListValue(arucasList);
	}

	@ArucasFunction
	public void addReaction(Context context, StringValue emoteId) {
		Emote emote = this.message.getGuild().getEmoteById(emoteId.value);
		if (emote == null) {
			throw new RuntimeException("'%s' is not a valid emote id".formatted(emoteId.value));
		}
		this.message.addReaction(emote).complete();
	}

	@ArucasFunction
	public void addReactionUnicode(Context context, StringValue unicode) {
		this.message.addReaction(unicode.value).complete();
	}

	@ArucasFunction
	public void removeAllReactions(Context context) {
		this.message.clearReactions().complete();
	}

	@ArucasFunction
	public void delete(Context context) {
		this.message.delete().complete();
	}

	@ArucasFunction
	public void pin(Context context, BooleanValue booleanValue) {
		if (booleanValue.value) {
			this.message.pin().complete();
			return;
		}
		this.message.unpin().complete();
	}

	@ArucasFunction
	public BooleanValue isPinned(Context context) {
		return BooleanValue.of(this.message.isPinned());
	}

	@ArucasFunction
	public BooleanValue isEdited(Context context) {
		return BooleanValue.of(this.message.isEdited());
	}


	@ArucasFunction
	public WrapperClassValue reply(Context context, StringValue message) throws CodeError {
		return createNewMessageWrapper(this.message.reply(message.value).complete(), context);
	}


	@ArucasFunction
	public WrapperClassValue replyWithEmbed(Context context, MapValue mapValue) throws CodeError {
		Message message = this.message.replyEmbeds(DiscordUtils.parseMapAsEmbed(context, mapValue)).complete();
		return createNewMessageWrapper(message, context);
	}

	@ArucasFunction
	public WrapperClassValue replyWithFile(Context context, FileValue fileValue) throws CodeError {
		return createNewMessageWrapper(this.message.reply(fileValue.value).complete(), context);
	}

	public static WrapperClassValue createNewMessageWrapper(Message message, Context context) throws CodeError {
		DiscordMessageWrapper channelWrapper = new DiscordMessageWrapper();
		channelWrapper.message = message;
		return DEFINITION.createNewDefinition(channelWrapper, context, List.of());
	}
}
