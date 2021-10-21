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

	private String generateTraceback() {
		StringBuilder result = new StringBuilder();
		Context context = this.context;
		
		// Iterate trough all branches before this point
		Iterator<SymbolTable> iter = context.getSymbolTable().iterator();
		while (iter.hasNext()) {
			SymbolTable table = iter.next();
			Position pos = table.getPosition();
			result.insert(0, "File: %s, Line: %d, Column: %d, In: %s\n".formatted(
				pos.fileName, pos.line + 1, pos.column + 1, context.getDisplayName()
			));
		}
		
		// Add the error call.
		result.append("File: %s, Line: %d, Column: %d, In: %s\n".formatted(
			this.startPos.fileName, this.startPos.line + 1, this.startPos.column + 1, context.getDisplayName()
		));
		
		return "Traceback (most recent call last): \n%s".formatted(result);
	}
	
	@Override
	public String toString() {
		return "%s%s - '%s'".formatted(this.generateTraceback(), this.errorType.stringName, this.getMessage());
	}
}
