package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.Value;

import java.util.*;

public class SymbolTable {
    public Map<String, Value<?>> symbolMap;
    public SymbolTable parent;

    public SymbolTable(SymbolTable parent) {
        this.symbolMap = new HashMap<>();
        this.parent = parent;
    }

    public SymbolTable() {
        this(null);
    }

    public SymbolTable setDefaultSymbols(Context context) {
        if (!this.symbolMap.isEmpty())
            return this;
        
        for (BuiltInFunction function : BuiltInFunction.getBuiltInFunctions()) {
            this.set(function.value, function.setContext(context));
        }
        
        return this;
    }

    public Value<?> get(String name) {
        Value<?> value = this.symbolMap.get(name);
        if (value == null && this.parent != null)
            return this.parent.get(name);
        return value;
    }
    
    public boolean has(String name) {
        return this.symbolMap.containsKey(name) || (this.parent != null && this.parent.has(name));
    }

    public void set(String name, Value<?> value) {
        if(!this.symbolMap.containsKey(name) && has(name)) {
            this.parent.set(name, value);
            return;
        }
        
        this.symbolMap.put(name, value);
    }
}
