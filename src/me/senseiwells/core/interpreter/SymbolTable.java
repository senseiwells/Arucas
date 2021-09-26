package me.senseiwells.core.interpreter;

import me.senseiwells.core.values.Value;

import java.util.HashMap;

public class SymbolTable {

    public HashMap<String, Value<?>> symbolMap;
    public SymbolTable parent;

    public SymbolTable() {
        this.symbolMap = new HashMap<>();
        this.parent = null;
    }

    public Value<?> get(String name) {
        Value<?> value = this.symbolMap.get(name);
        if (value == null && this.parent != null)
            return this.parent.get(name);
        return value;
    }

    public SymbolTable set(String name, Value<?> value) {
        this.symbolMap.put(name, value);
        return this;
    }

    public SymbolTable remove(String name) {
        this.symbolMap.remove(name);
        return this;
    }
}
