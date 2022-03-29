package me.senseiwells.arucas.extensions.discordapi;

import me.senseiwells.arucas.api.wrappers.ArucasDefinition;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
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

/**
 * DiscordAttachment class wrapper for Arucas. <br>
 * Fully Documented.
 * @author senseiwells
 */
@ArucasClass(name = "DiscordAttachment")
public class DiscordAttachmentWrapper implements IArucasWrappedClass {
	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION;

	private Message.Attachment attachment;

	/**
	 * Name: <code>&lt;DiscordAttachment>.saveToFile(file)</code> <br>
	 * Description: This allows you to save an attachment to a file <br>
	 * Parameter - File: the file you want to save the attachment to <br>
	 * Example: <code>attachment.saveToFile(new File("/home/user/Attachment.jpeg"));</code>
	 */
	@ArucasFunction
	public void saveToFile(Context context, FileValue fileValue) {
		this.attachment.downloadToFile(fileValue.value);
	}

	/**
	 * Name: <code>&lt;DiscordAttachment>.getFileName()</code> <br>
	 * Description: This allows you to get the file name of the attachment <br>
	 * Example: <code>attachment.getFileName();</code>
	 */
	@ArucasFunction
	public StringValue getFileName(Context context) {
		return StringValue.of(this.attachment.getFileName());
	}

	/**
	 * Name: <code>&lt;DiscordAttachment>.getFileExtension()</code> <br>
	 * Description: This allows you to get the file extension of the attachment <br>
	 * Example: <code>attachment.getFileExtension();</code>
	 */
	@ArucasFunction
	public StringValue getFileExtension(Context context) {
		return StringValue.of(this.attachment.getFileExtension());
	}

	/**
	 * Name: <code>&lt;DiscordAttachment>.isImage()</code> <br>
	 * Description: This allows you to check if the attachment is an image <br>
	 * Example: <code>attachment.isImage();</code>
	 */
	@ArucasFunction
	public BooleanValue isImage(Context context) {
		return BooleanValue.of(this.attachment.isImage());
	}

	/**
	 * Name: <code>&lt;DiscordAttachment>.isVideo()</code> <br>
	 * Description: This allows you to check if the attachment is a video <br>
	 * Example: <code>attachment.isVideo();</code>
	 */
	@ArucasFunction
	public BooleanValue isVideo(Context context) {
		return BooleanValue.of(this.attachment.isVideo());
	}

	/**
	 * Name: <code>&lt;DiscordAttachment>.getUrl()</code> <br>
	 * Description: This allows you to get the url of the attachment <br>
	 * Example: <code>attachment.getUrl();</code>
	 */
	@ArucasFunction
	public StringValue getUrl(Context context) {
		return StringValue.of(this.attachment.getUrl());
	}

	/**
	 * Name: <code>&lt;DiscordAttachment>.getSize()</code> <br>
	 * Description: This allows you to get the size of the attachment <br>
	 * Example: <code>attachment.getSize();</code>
	 */
	@ArucasFunction
	public NumberValue getSize(Context context) {
		return NumberValue.of(this.attachment.getSize());
	}

	public static WrapperClassValue newDiscordAttachment(Message.Attachment attachment, Context context) throws CodeError {
		DiscordAttachmentWrapper attachmentWrapper = new DiscordAttachmentWrapper();
		attachmentWrapper.attachment = attachment;
		return DEFINITION.createNewDefinition(attachmentWrapper, context, List.of());
	}
}
