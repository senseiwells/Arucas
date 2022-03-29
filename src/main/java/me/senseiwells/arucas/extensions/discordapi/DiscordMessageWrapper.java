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

/**
 * DiscordMessage class wrapper for Arucas. <br>
 * Fully Documented.
 * @author senseiwells
 */
@ArucasClass(name = "DiscordMessage")
public class DiscordMessageWrapper implements IArucasWrappedClass {
	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION;

	private Message message;

	/**
	 * Name: <code>&lt;DiscordMessage>.getId()</code> <br>
	 * Description: This gets the id of the message <br>
	 * Returns - String: the id of the message <br>
	 * Example: <code>message.getId();</code>
	 */
	@ArucasFunction
	public StringValue getId(Context context) {
		return DiscordUtils.getId(this.message);
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.getRaw()</code> <br>
	 * Description: This gets the raw message content <br>
	 * Returns - String: the raw message content <br>
	 * Example: <code>message.getRaw();</code>
	 */
	@ArucasFunction
	public StringValue getRaw(Context context) {
		return this.toString(context);
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.toString()</code> <br>
	 * Description: This gets the raw message content <br>
	 * Returns - String: the raw message content <br>
	 * Example: <code>message.toString();</code>
	 */
	@ArucasFunction
	public StringValue toString(Context context) {
		return StringValue.of(this.message.getContentRaw());
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.getChannel()</code> <br>
	 * Description: This gets the channel the message was sent in <br>
	 * Returns - DiscordChannel: the channel the message was sent in <br>
	 * Example: <code>message.getChannel();</code>
	 */
	@ArucasFunction
	public WrapperClassValue getChannel(Context context) throws CodeError {
		return DiscordChannelWrapper.newDiscordChannel(this.message.getChannel(), context);
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.getServer()</code> <br>
	 * Description: This gets the server the message was sent in <br>
	 * Returns - DiscordServer: the server the message was sent in <br>
	 * Example: <code>message.getServer();</code>
	 */
	@ArucasFunction
	public WrapperClassValue getServer(Context context) throws CodeError {
		return DiscordServerWrapper.newDiscordServer(this.message.getGuild(), context);
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.getAuthor()</code> <br>
	 * Description: This gets the author of the message <br>
	 * Returns - DiscordUser: the author of the message <br>
	 * Example: <code>message.getAuthor();</code>
	 */
	@ArucasFunction
	public WrapperClassValue getAuthor(Context context) throws CodeError {
		return DiscordUserWrapper.newDiscordUser(this.message.getAuthor(), context);
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.getAttachments()</code> <br>
	 * Description: This gets the attachments of the message <br>
	 * Returns - List: list with the attachments of the message <br>
	 * Example: <code>message.getAttachments();</code>
	 */
	@ArucasFunction
	public ListValue getAttachments(Context context) throws CodeError {
		ArucasList arucasList = new ArucasList();
		for (Message.Attachment attachment: this.message.getAttachments()) {
			arucasList.add(DiscordAttachmentWrapper.newDiscordAttachment(attachment, context));
		}
		return new ListValue(arucasList);
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.addReaction(emojiId)</code> <br>
	 * Description: This adds a reaction to the message with a specific emoji id <br>
	 * Parameter - String: the emoji id <br>
	 * Throws - Error: "... is not a valid emote id" if the emoji is not found <br>
	 * Example: <code>message.addReaction("012789012930198");</code>
	 */
	@ArucasFunction
	public void addReaction(Context context, StringValue emoteId) {
		Emote emote = this.message.getGuild().getEmoteById(emoteId.value);
		if (emote == null) {
			throw new RuntimeException("'%s' is not a valid emote id".formatted(emoteId.value));
		}
		this.message.addReaction(emote).complete();
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.addReactionUnicode(unicode)</code> <br>
	 * Description: This adds a reaction to the message with a specific unicode <br>
	 * Parameter - String: the unicode character <br>
	 * Example: <code>message.addReactionUnicode("\uD83D\uDE00");</code>
	 */
	@ArucasFunction
	public void addReactionUnicode(Context context, StringValue unicode) {
		this.message.addReaction(unicode.value).complete();
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.removeAllReactions()</code> <br>
	 * Description: This removes all reactions from the message <br>
	 * Example: <code>message.removeAllReactions();</code>
	 */
	@ArucasFunction
	public void removeAllReactions(Context context) {
		this.message.clearReactions().complete();
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.delete()</code> <br>
	 * Description: This deletes the message <br>
	 * Example: <code>message.delete();</code>
	 */
	@ArucasFunction
	public void delete(Context context) {
		this.message.delete().complete();
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.pin(bool)</code> <br>
	 * Description: This pins the message if true, and removes if false <br>
	 * Parameter - Boolean: true to pin, false to unpin <br>
	 * Example: <code>message.pin(true);</code>
	 */
	@ArucasFunction
	public void pin(Context context, BooleanValue booleanValue) {
		if (booleanValue.value) {
			this.message.pin().complete();
			return;
		}
		this.message.unpin().complete();
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.isPinned()</code> <br>
	 * Description: This checks if the message is pinned <br>
	 * Returns - Boolean: true if the message is pinned, false if not <br>
	 * Example: <code>message.isPinned();</code>
	 */
	@ArucasFunction
	public BooleanValue isPinned(Context context) {
		return BooleanValue.of(this.message.isPinned());
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.isEdited()</code> <br>
	 * Description: This checks if the message is edited <br>
	 * Returns - Boolean: true if the message is edited, false if not <br>
	 * Example: <code>message.isEdited();</code>
	 */
	@ArucasFunction
	public BooleanValue isEdited(Context context) {
		return BooleanValue.of(this.message.isEdited());
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.reply(message)</code> <br>
	 * Description: This replies to the message with the given message <br>
	 * Parameter - String: the message <br>
	 * Returns - DiscordMessage: the message that was sent <br>
	 * Example: <code>message.reply("Replied!");</code>
	 */
	@ArucasFunction
	public WrapperClassValue reply(Context context, StringValue message) throws CodeError {
		return newDiscordMessage(this.message.reply(message.value).complete(), context);
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.replyWithEmbed(embedMap)</code> <br>
	 * Description: This replies to the message with the given embed map <br>
	 * Parameter - Map: the embed map <br>
	 * Returns - DiscordMessage: the message that was sent <br>
	 * Example: <code>message.replyWithEmbed({"title": "Embed Title", "description": "Embed Description"});</code>
	 */
	@ArucasFunction
	public WrapperClassValue replyWithEmbed(Context context, MapValue mapValue) throws CodeError {
		Message message = this.message.replyEmbeds(DiscordUtils.parseMapAsEmbed(context, mapValue)).complete();
		return newDiscordMessage(message, context);
	}

	/**
	 * Name: <code>&lt;DiscordMessage>.replyWithFile(file)</code> <br>
	 * Description: This replies to the message with the given file <br>
	 * Parameter - File: the file <br>
	 * Returns - DiscordMessage: the message that was sent <br>
	 * Example: <code>message.replyWithFile(new File("path/to/file"));</code>
	 */
	@ArucasFunction
	public WrapperClassValue replyWithFile(Context context, FileValue fileValue) throws CodeError {
		return newDiscordMessage(this.message.reply(fileValue.value).complete(), context);
	}

	public static WrapperClassValue newDiscordMessage(Message message, Context context) throws CodeError {
		DiscordMessageWrapper channelWrapper = new DiscordMessageWrapper();
		channelWrapper.message = message;
		return DEFINITION.createNewDefinition(channelWrapper, context, List.of());
	}
}
