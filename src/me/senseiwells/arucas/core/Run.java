package me.senseiwells.arucas.core;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.utils.SymbolTable;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.Value;

import java.util.List;
import java.util.Scanner;

@SuppressWarnings("all")
public class Run {

    public static SymbolTable symbolTable = new SymbolTable();
    public static boolean debug = false;

    public static void baseRun() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (line.trim().equals(""))
                continue;
            Value<?> values;
            try {
                values = run("System.in", line);
                if (debug)
                    System.out.println(values);
            }
            catch (Error e) {
                String error = e.toString();
                System.out.println(error);
                //break;
            }
        }
    }

    public static Value<?> run(String fileName, String line) throws Error {

        //Create context
        boolean isTerminal = fileName.equals("System.in");
        Context context = new Context(isTerminal ? "terminal" : fileName, null, null);
        SymbolTable table = isTerminal ? table = symbolTable.setDefaultSymbols(context) : new SymbolTable().setDefaultSymbols(context);
        context.symbolTable = table;

        //Create Tokens
        Lexer lexer = new Lexer(line, fileName);
        List<Token> values = lexer.createTokens();

        //Parse
        Parser parser = new Parser(values, context);
        Node nodeResult = parser.parse();

        //Run
        Interpreter interpreter = new Interpreter();
        try {
            Value<?> value = interpreter.visit(nodeResult, context);
            return value;
        }
        catch (ThrowValue tv) {
            throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot use keywords 'break' or 'continue' outside loop, and cannot use 'return' outside function", nodeResult.startPos, nodeResult.endPos);
        }
    }
}
