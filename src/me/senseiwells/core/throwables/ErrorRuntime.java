package me.senseiwells.core.throwables;

import me.senseiwells.core.utils.Context;
import me.senseiwells.core.utils.Position;

public class ErrorRuntime extends Error {

    public Context context;

    public ErrorRuntime(String details, Position startPos, Position endPos, Context context) {
        super(ErrorType.RUNTIME_ERROR, details, startPos, endPos);
        this.context = context;
    }

    private String generateTraceback() {
        StringBuilder result = new StringBuilder();
        Position pos = this.startPos;
        Context context = this.context;
        while (context != null) {
            result.insert(0, "File: " + pos.fileName + ", Line: " + pos.line + 1 + ", In: " + context.displayName + "\n");
            pos = context.parentEntryPosition;
            context = context.parent;
        }
        return "Traceback (most recent call last): \n" + result;
    }

    @Override
    public String toString() {
        String error = this.generateTraceback();
        error += this.errorType.stringName + " - " +  "'" + this.getMessage() +  "'";
        error += "\n>> " + this.startPos.fileText;
        return error;
    }
}
