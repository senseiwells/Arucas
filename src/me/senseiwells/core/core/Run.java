package me.senseiwells.core.core;

import me.senseiwells.core.utils.Context;
import me.senseiwells.core.throwables.Error;
import me.senseiwells.core.throwables.ThrowValue;
import me.senseiwells.core.utils.Interpreter;
import me.senseiwells.core.utils.SymbolTable;
import me.senseiwells.core.nodes.Node;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.core.values.Value;

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

    private static Value<?> run(String fileName, String line) throws Error {

        //Create Tokens
        Lexer lexer = new Lexer(line, fileName);
        List<Token> values = lexer.createTokens();

        //Create context
        Context context = new Context("terminal", null, null);
        symbolTable.setDefaultSymbols(context);
        context.symbolTable = symbolTable;

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
