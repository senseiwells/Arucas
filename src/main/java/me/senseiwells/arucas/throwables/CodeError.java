package me.senseiwells.arucas.throwables;

import me.senseiwells.arucas.api.IArucasOutput;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Position;

import java.util.Objects;

public class CodeError extends Exception {

	public final ErrorType errorType;
	public final ISyntax syntaxPosition;
	
	public CodeError(ErrorType errorType, String details, ISyntax syntaxPosition) {
		super(details);
		this.errorType = errorType;
		this.syntaxPosition = Objects.requireNonNull(syntaxPosition);
	}
	
	@Override
	public String toString() {
		return this.toString(null);
	}
	
	public String toString(Context context) {
		Position startPos = this.syntaxPosition.getStartPos();
		IArucasOutput output = context == null ? null : context.getOutput();
		return "%s%s - '%s'\n%s> File: %s, Line: %d, Column: %d".formatted(
			output == null ? "" : output.getErrorFormattingBold(),
			this.errorType.stringName, this.getMessage(),
			output == null ? "" : output.getErrorFormatting(),
			startPos.fileName, startPos.line + 1, startPos.column + 1
		);
	}

	public enum ErrorType {
		ILLEGAL_CHAR_ERROR      ("Illegal Character Error"),
		ILLEGAL_SYNTAX_ERROR    ("Illegal Syntax Error"),
		UNKNOWN_IDENTIFIER      ("Variable or function was not found"),
		ILLEGAL_OPERATION_ERROR ("Illegal Operation Error"),
		EXPECTED_CHAR_ERROR     ("Expected Character Error"),
		INTERRUPTED_ERROR       ("Program interrupted"),
		RUNTIME_ERROR           ("Runtime Error"),
		STOP                    ("Program stopped");

		public final String stringName;

		ErrorType(String stringName) {
			this.stringName = stringName;
		}
	}
}
