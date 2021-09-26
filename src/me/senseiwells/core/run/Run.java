package me.senseiwells.core.run;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.interpreter.SymbolTable;
import me.senseiwells.core.lexer.Lexer;
import me.senseiwells.core.nodes.Node;
import me.senseiwells.core.parser.Parser;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.core.values.Value;

import java.util.List;
import java.util.Scanner;

public class Run {

    public static SymbolTable symbolTable = new SymbolTable();

    public static void baseRun() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            Value<?> values;
            try {
                values = run("System.in", line);
                System.out.println(values);
            }
            catch (Error e) {
                String error = e.toString();
                System.out.println(error);
                //break;
            }
        }
    }

    @SuppressWarnings({"SameParameterValue", "UnnecessaryLocalVariable"})
    private static Value<?> run(String fileName, String line) throws Error {
        //Create context
        Context context = new Context("program", null, null);
        context.symbolTable = symbolTable;

        //Create Tokens
        Lexer lexer = new Lexer(line, fileName);
        List<Token> values = lexer.createTokens();

        //Try To Parse
        Parser parser = new Parser(values, context);
        Node nodeResult = parser.parse();

        //Run
        Interpreter interpreter = new Interpreter();
        Value<?> value = interpreter.visit(nodeResult, context);

        return value;
    }
}
