package me.senseiwells.arucas.throwables;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.utils.StackTable;

import java.util.Iterator;

public class RuntimeError extends CodeError {
	private Context context;
	
	public RuntimeError(String details, Position startPos, Position endPos, Context context) {
		super(ErrorType.RUNTIME_ERROR, details, startPos, endPos);
		this.context = context;
	}
	
	public RuntimeError(String details, Position startPos, Position endPos) {
		this(details, startPos, endPos, null);
	}

	public RuntimeError setContext(Context context) {
		this.context = context;
		return this;
	}

	private String generateTraceback(Context context) {
		StringBuilder result = new StringBuilder();
		
		// Add the error call.
		result.append("File: %s, Line: %d, Column: %d, In: %s\n".formatted(
			this.startPos.fileName, this.startPos.line + 1, this.startPos.column + 1, context.getDisplayName()
		));
		
		// Iterate through all branches before this point
		Iterator<StackTable> iterator = context.getStackTable().iterator();
		while (iterator.hasNext()) {
			StackTable table = iterator.next();
			Position pos = table.getPosition();
			result.append("File: %s, Line: %d, Column: %d, In: %s\n".formatted(
				pos.fileName, pos.line + 1, pos.column + 1, context.getDisplayName()
			));
		}
		
		return "Traceback (most recent call first): '%s'\n%s".formatted(this.getMessage(), result);
	}
	
	@Override
	public String toString(Context context) {
		// If this context is not null use that instead
		context = this.context != null ? this.context : context;
		return context != null
			? "%s%s - '%s'".formatted(this.generateTraceback(context), this.errorType.stringName, this.getMessage())
			: super.toString(null);
	}
}
