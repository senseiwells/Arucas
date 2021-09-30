package me.senseiwells.core.error;

import me.senseiwells.core.lexer.Position;

public class Error extends Exception {

    public ErrorType errorType;
    public final Position startPos;
    public final Position endPos;


    public Error(ErrorType errorType, String details, Position startPos, Position endPos) {
        super(details);
        this.errorType = errorType;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    @Override
    public String toString() {
        String error = this.errorType.stringName + " - " +  "'" + this.getMessage() +  "'";
        error += "\nFile: " + this.startPos.fileName + ", Line: " + this.startPos.line + 1;
        error += "\n>> " + this.startPos.fileText;
        return error;
    }

    public enum ErrorType {

        ILLEGAL_CHAR_ERROR      ("Illegal Character Error"),
        ILLEGAL_SYNTAX_ERROR    ("Illegal Syntax Error"),
        ILLEGAL_OPERATION_ERROR ("Illegal Operation Error"),
        EXPECTED_CHAR_ERROR     ("Expected Character Error"),
        RUNTIME_ERROR           ("Runtime Error"),
        CAUGHT_ERROR            ("Caught Error");

        public String stringName;

        ErrorType(String stringName) {
            this.stringName = stringName;
        }
    }
}
