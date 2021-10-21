package me.senseiwells.arucas.throwables;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.utils.SymbolTable;

import java.util.ArrayList;
import java.util.Iterator;

public class ErrorRuntime extends CodeError {
	private final Context context;
	
	public ErrorRuntime(String details, Position startPos, Position endPos, Context context) {
		super(ErrorType.RUNTIME_ERROR, details, startPos, endPos);
		this.context = context;
	}
	
	public ErrorRuntime(String details, Position startPos, Position endPos) {
		this(details, startPos, endPos, null);
	}

	private String generateTraceback(Context context) {
		StringBuilder result = new StringBuilder();
		
		// Add the error call.
		result.append("File: %s, Line: %d, Column: %d, In: %s\n".formatted(
			this.startPos.fileName, this.startPos.line + 1, this.startPos.column + 1, context.getDisplayName()
		));
		
		// Iterate trough all branches before this point
		Iterator<SymbolTable> iter = context.getSymbolTable().iterator();
		while (iter.hasNext()) {
			SymbolTable table = iter.next();
			Position pos = table.getPosition();
			result.append("File: %s, Line: %d, Column: %d, In: %s\n".formatted(
				pos.fileName, pos.line + 1, pos.column + 1, context.getDisplayName()
			));
		}
		
		return "Traceback (most recent call first): '%s'\n%s".formatted(this.getMessage(), result);
	}
	
	@Override
	public String toString(Context context) {
		if (this.context != null)
			return "%s%s - '%s'".formatted(this.generateTraceback(this.context), this.errorType.stringName, this.getMessage());
		else if (context != null)
			return "%s%s - '%s'".formatted(this.generateTraceback(context), this.errorType.stringName, this.getMessage());
		else
			return super.toString(null);
	}
}
