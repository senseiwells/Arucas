package me.senseiwells.arucas.extensions.discordapi;

import me.senseiwells.arucas.api.wrappers.ArucasDefinition;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.ArucasWrapper;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.FileValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassValue;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

@ArucasWrapper(name = "DiscordAttachment")
public class DiscordAttachmentWrapper implements IArucasWrappedClass {
	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION;

	private Message.Attachment attachment;

	@ArucasFunction
	public void saveToFile(Context context, FileValue fileValue) {
		this.attachment.downloadToFile(fileValue.value);
	}

	@ArucasFunction
	public StringValue getFileName(Context context) {
		return StringValue.of(this.attachment.getFileName());
	}

	@ArucasFunction
	public StringValue getFileExtension(Context context) {
		return StringValue.of(this.attachment.getFileExtension());
	}

	@ArucasFunction
	public BooleanValue isImage(Context context) {
		return BooleanValue.of(this.attachment.isImage());
	}

	@ArucasFunction
	public BooleanValue isVideo(Context context) {
		return BooleanValue.of(this.attachment.isVideo());
	}

	@ArucasFunction
	public StringValue getUrl(Context context) {
		return StringValue.of(this.attachment.getUrl());
	}

	@ArucasFunction
	public NumberValue getSize(Context context) {
		return NumberValue.of(this.attachment.getSize());
	}

	public static WrapperClassValue createNewDefinition(Message.Attachment attachment, Context context) throws CodeError {
		DiscordAttachmentWrapper attachmentWrapper = new DiscordAttachmentWrapper();
		attachmentWrapper.attachment = attachment;
		return DEFINITION.createNewDefinition(attachmentWrapper, context, List.of());
	}
}
