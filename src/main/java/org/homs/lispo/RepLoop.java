package org.homs.lispo;

import org.homs.lispo.tokenizer.EToken;
import org.homs.lispo.tokenizer.Tokenizer;

import java.io.*;

public class RepLoop {

    public static final String SOURCE_DESC = "REPL";
    public static final String QUIT_SENTENCE = "(q)";

    public static void main(String[] args) throws Throwable {
        new RepLoop().repLoop();
    }

    final Interpreter interpreter;
    final Environment env;
    final BufferedReader reader;
    final PrintStream out;
    final boolean printPromptSymbols;

    public RepLoop() throws Throwable {
        this(System.in, System.out, true);
    }

    public RepLoop(InputStream in, OutputStream out, boolean printPromptSymbols) throws Throwable {
        this.interpreter = new Interpreter();
        this.env = interpreter.getStdEnvironment();
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.out = new PrintStream(out);
        this.printPromptSymbols = printPromptSymbols;
    }

    void repLoop() {
        while (true) {
            try {
                String expression = read();
                if (QUIT_SENTENCE.equals(expression.trim())) {
                    break;
                }
                Object result = eval(expression);
                print(result);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    String read() throws IOException {
        if (printPromptSymbols) {
            out.print(">>> ");
        }

        String expression = reader.readLine() + "\n";
        while (!isExpressionComplete(expression)) {
            if (printPromptSymbols) {
                out.print("... ");
            }
            expression += reader.readLine() + "\n";
        }

        return expression;
    }

    Object eval(String expression) throws Throwable {
        var asts = interpreter.parse(expression, "REPL");
        return interpreter.evaluate(asts, env);
    }

    void print(Object result) {
        out.println(result);
    }

    boolean isExpressionComplete(String expression) {
        Tokenizer tokenizer = new Tokenizer(expression, SOURCE_DESC);

        int openedPar = 0;
        int openedLists = 0;
        int openedMaps = 0;
        while (tokenizer.hasNext()) {
            var token = tokenizer.next();
            if (token.type == EToken.OPEN_PAR) {
                openedPar++;
            }
            if (token.type == EToken.OPEN_LIST) {
                openedLists++;
            }
            if (token.type == EToken.OPEN_MAP) {
                openedMaps++;
            }

            if (token.type == EToken.CLOSE_PAR) {
                openedPar--;
            }
            if (token.type == EToken.CLOSE_LIST) {
                openedLists--;
            }
            if (token.type == EToken.CLOSE_MAP) {
                openedMaps--;
            }
        }

        return openedPar == 0 && openedLists == 0 && openedMaps == 0;
    }
}
