package me.senseiwells.core.interpreter;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.values.BooleanValue;
import me.senseiwells.core.values.BuiltInFunctionValue;
import me.senseiwells.core.values.Value;

import java.util.HashMap;

public class SymbolTable {

    public HashMap<String, Value<?>> symbolMap;
    public SymbolTable parent;

    public SymbolTable(SymbolTable parent) {
        this.symbolMap = new HashMap<>();
        this.parent = parent;
    }

    public SymbolTable() {
        this(null);
    }

    public void setDefaultSymbols(Context context) {
        this.set("true", new BooleanValue(true));
        this.set("false", new BooleanValue(false));
        for (BuiltInFunctionValue.BuiltInFunction function : BuiltInFunctionValue.BuiltInFunction.values()) {
            this.set(function.name, new BuiltInFunctionValue(function.name).setContext(context));
        }
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

    public enum Literal {

        TRUE("true"),
        FALSE("false");

        String name;

        Literal(String name) {
            this.name = name;
        }

        public static Literal stringToLiteral(String word) {
            for (Literal value : Literal.values()) {
                if (word.equals(value.name))
                    return value;
            }
            return null;
        }
    }
}
