package me.senseiwells.arucas.throwables;

import me.senseiwells.arucas.api.IArucasOutput;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.utils.StackTable;

import java.util.Iterator;

public class RuntimeError extends CodeError {
	private final Context context;

	public RuntimeError(String details, ISyntax syntaxHolder, Context context) {
		super(ErrorType.RUNTIME_ERROR, details, syntaxHolder);
		this.context = context;
	}

	public RuntimeError(String details, Context context) {
		this(details, ISyntax.empty(), context);
	}

	public RuntimeError(Throwable throwable, ISyntax syntaxHolder, Context context) {
		this(throwableToString(throwable), syntaxHolder, context);
	}

	public RuntimeError(Throwable throwable, Context context) {
		this(throwable, ISyntax.empty(), context);
	}

	private String generateTraceback(Context context, IArucasOutput output) {
		StringBuilder result = new StringBuilder();

		Position startPos = this.syntaxPosition.getStartPos();

		// Add the error call.
		result.append("File: %s, Line: %d, Column: %d, In: %s\n%s".formatted(
			startPos.fileName, startPos.line + 1, startPos.column + 1,
			context.getDisplayName(), output.getErrorFormatting()
		));

		// Iterate through all branches before this point
		Iterator<StackTable> iterator = context.getStackTable().iterator();
		while (iterator.hasNext()) {
			StackTable table = iterator.next();
			Position pos = table.getPosition().getStartPos();
			result.append("> File: %s, Line: %d, Column: %d, In: %s\n".formatted(
				pos.fileName, pos.line + 1, pos.column + 1, context.getDisplayName()
			));
		}

		return "%sTraceback (most recent call first): '%s'\n%s".formatted(
			output.getErrorFormattingBold(),
			this.getMessage(), result
		);
	}

	@Override
	public String toString(Context context) {
		return this.toString(context, false);
	}

	@Override
	public String toString(Context context, boolean raw) {
		IArucasOutput output = raw ? IArucasOutput.DUMMY : this.context.getOutput();
		return this.context != null ? "%s%s%s - '%s'%s".formatted(
			this.generateTraceback(this.context, output),
			output.getErrorFormattingBold(),
			this.errorType.stringName, this.getMessage(),
			output.getResetFormatting()
		) : super.toString();
	}
}
