package me.senseiwells.core.error;

import me.senseiwells.core.interpreter.SymbolTable;
import me.senseiwells.core.lexer.Position;

public class Context {

    public String displayName;
    public Context parent;
    public Position parentEntryPosition;
    public SymbolTable symbolTable;

    public Context(String displayName, Context parent, Position parentEntryPos) {
        this.displayName = displayName;
        this.parent = parent;
        this.parentEntryPosition = parentEntryPos;
        this.symbolTable = null;
    }
}
