package me.senseiwells.arucas.core;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.SymbolTable;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.Value;

import java.util.List;
import java.util.Scanner;


public class Run {

    public static SymbolTable symbolTable = new SymbolTable();
    public static boolean debug = false;

    @SuppressWarnings("InfiniteLoopStatement")
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
            catch (CodeError e) {
                String error = e.toString();
                System.out.println(error);
            }
        }
    }

    public static Value<?> run(String fileName, String line) throws CodeError {
        String[] fileNameRoot = fileName.split("/");
        Context context = new Context(fileNameRoot[fileNameRoot.length - 1], null, null);
        context.symbolTable = fileName.equals("System.in") ? symbolTable.setDefaultSymbols(context) : new SymbolTable().setDefaultSymbols(context);

        List<Token> values = new Lexer(line, fileName).createTokens();

        Node nodeResult = new Parser(values, context).parse();
        try {
            return nodeResult.visit();
        }
        catch (ThrowValue tv) {
            throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot use keywords 'break' or 'continue' outside loop, and cannot use 'return' outside function", nodeResult.startPos, nodeResult.endPos);
        }
    }
}
