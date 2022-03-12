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
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ArucasWrapper(name = "DiscordMessage")
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
		this.message.addReaction(emote).queue();
	}

	@ArucasFunction
	public void addReactionUnicode(Context context, StringValue unicode) {
		this.message.addReaction(unicode.value).queue();
	}

	@ArucasFunction
	public void removeAllReactions(Context context) {
		this.message.clearReactions().queue();
	}

	@ArucasFunction
	public void delete(Context context) {
		this.message.delete().queue();
	}

	@ArucasFunction
	public void pin(Context context, BooleanValue booleanValue) {
		if (booleanValue.value) {
			this.message.pin().queue();
			return;
		}
		this.message.unpin().queue();
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
	public void reply(Context context, StringValue message, FunctionValue then) {
		this.message.reply(message.value).queue(getMessageCallback(context, then));
	}

	@ArucasFunction
	public void reply(Context context, StringValue message) {
		this.message.reply(message.value).queue();
	}

	@ArucasFunction
	public void replyWithEmbed(Context context, MapValue mapValue, FunctionValue then) throws CodeError {
		this.message.replyEmbeds(DiscordUtils.parseMapAsEmbed(context, mapValue)).queue(getMessageCallback(context, then));
	}

	@ArucasFunction
	public void replyWithEmbed(Context context, MapValue mapValue) throws CodeError {
		this.message.replyEmbeds(DiscordUtils.parseMapAsEmbed(context, mapValue)).queue();
	}

	@ArucasFunction
	public void replyWithFile(Context context, FileValue fileValue, FunctionValue then) {
		this.message.reply(fileValue.value).queue(getMessageCallback(context, then));
	}

	@ArucasFunction
	public void replyWithFile(Context context, FileValue fileValue) {
		this.message.reply(fileValue.value).queue();
	}

	public static WrapperClassValue createNewMessageWrapper(Message message, Context context) throws CodeError {
		DiscordMessageWrapper channelWrapper = new DiscordMessageWrapper();
		channelWrapper.message = message;
		return DEFINITION.createNewDefinition(channelWrapper, context, List.of());
	}

	public static Consumer<Message> getMessageCallback(Context context, FunctionValue then) {
		return msg -> {
			context.getThreadHandler().runAsyncFunctionInContext(context.createBranch(), branchContext -> {
				List<Value<?>> parameters = new ArrayList<>();
				parameters.add(createNewMessageWrapper(msg, context));
				then.call(context, parameters);
			});
		};
	}
}
