package org.homs.lechugascript;

import org.homs.lechugascript.parser.ast.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class InterpreterTest {

    final static Map<Integer, String> ordinalsMap = new LinkedHashMap<>();

    static {
        ordinalsMap.put(1, "one");
        ordinalsMap.put(2, "two");
        ordinalsMap.put(3, "three");
    }

    public static class Dog {

        public int id;
        public String name;

        public Dog() {
        }

        public Dog(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Dog dog = (Dog) o;
            return id == dog.id && Objects.equals(name, dog.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        @Override
        public String toString() {
            return id + "-" + name;
        }
    }

    static Stream<Arguments> expressionProvider() {
        return Stream.of(
                Arguments.of("", null),
                Arguments.of(";this is a comment", null),
                Arguments.of("1 2 3", 3),
                Arguments.of("42", 42),
                Arguments.of("3.14159", 3.14159),
                Arguments.of("null", null),
                Arguments.of("true", true),
                Arguments.of("false", false),
                Arguments.of("\"jou\"", "jou"),
                Arguments.of(":jou", "jou"),
                Arguments.of("[42 3.14159 \"jou\" null false]", Arrays.asList(42, 3.14159, "jou", null, false)),
                Arguments.of("{[1 :one][2 :two][3 :three]}", ordinalsMap),

                Arguments.of("\"jo\\(\\)\\nu\"", "jo()\nu"),
                Arguments.of("\"\\t\\\"\\n\\r\"", "\t\"\n\r"),

                Arguments.of("(multi)", null),
                Arguments.of("(multi 1)", 1),
                Arguments.of("(multi 1 2 3)", 3),

                Arguments.of("(let {})", null),
                Arguments.of("(let {}1)", 1),
                Arguments.of("(let {}1 2 3)", 3),
                Arguments.of("(let {[a 5]}a)", 5),
                Arguments.of("(let {[a 2][b 3][c (+ a b)]}c)", 5),

                Arguments.of("(inc 2)", 3),
                Arguments.of("(inc (+ 1 1))", 3),
                Arguments.of("(def a 2)(inc a)", 3),
                Arguments.of("(def a 2)(inc a)a", 3),

                Arguments.of("(dec 2)", 1),
                Arguments.of("(dec (+ 1 1))", 1),
                Arguments.of("(def a 2)(dec a)", 1),
                Arguments.of("(def a 2)(dec a)a", 1),

                Arguments.of("((quote null) :getClass)", NullAst.class),
                Arguments.of("((quote true) :getClass)", BooleanAst.class),
                Arguments.of("((quote 1) :getClass)", NumberAst.class),
                Arguments.of("((quote :1) :getClass)", StringAst.class),
                Arguments.of("((quote []) :getClass)", ListAst.class),
                Arguments.of("((quote {}) :getClass)", MapAst.class),
                Arguments.of("((quote pi) :getClass)", SymbolAst.class),
                Arguments.of("((quote (multi 1 2 3)) :getClass)", ParenthesisAst.class),

                Arguments.of("(def pi 3.14159)", 3.14159),
                Arguments.of("(def pi 3.14159) pi", 3.14159),
                Arguments.of("(def pi 1 2 3.14159)", 3.14159),
                Arguments.of("(def pi 1 2 3.14159) pi", 3.14159),

                Arguments.of("(if true :ok)", "ok"),
                Arguments.of("(if false :ok)", null),
                Arguments.of("(if true :ok :ko)", "ok"),
                Arguments.of("(if false :ok :ko)", "ko"),

                Arguments.of("(def r 1)(if true (def r 2) (def r 3)) r", 1),
                Arguments.of("(def r 1)(if true (set r 2) (set r 3)) r", 2),
                Arguments.of("(def r 1)(if false (set r 2) (set r 3)) r", 3),

                Arguments.of("((fn [] 3))", 3),
                Arguments.of("((fn [x] x) 3)", 3),
                Arguments.of("((fn [x] (if x 1 2)) true)", 1),
                Arguments.of("((fn [x] (if x 1 2)) false)", 2),
                Arguments.of("(def identity (fn[x]x)) (identity 42)", 42),
                Arguments.of("((fn [x] x) 1 2 3)", 1),
                Arguments.of("((fn [...x] x))", Arrays.asList()),
                Arguments.of("((fn [...x] x) 1 2 3)", Arrays.asList(1, 2, 3)),

                Arguments.of("((fn [x?] x))", null),
                Arguments.of("((fn [x?] x)2)", 2),
                Arguments.of("((fn [x? y?] x) 2 3)", 2),
                Arguments.of("((fn [x? y?] y) 2 3)", 3),
                Arguments.of("((fn [x? y?] x) 2)", 2),
                Arguments.of("((fn [x? y?] y) 2)", null),
                Arguments.of("((fn [x? y?] x))", null),
                Arguments.of("((fn [x? y?] y))", null),

                Arguments.of("(defn identity [x] x) (identity 1)", 1),
                Arguments.of("((defn identity [x] x) 1)", 1),
                Arguments.of("(defn to-array [x] x) (to-array 1 2 3)", 1),
                Arguments.of("(defn to-array [...x] x) (to-array 1 2 3)", Arrays.asList(1, 2, 3)),

                Arguments.of("(def chucho (new :org.homs.lechugascript.InterpreterTest$Dog))", new Dog()),
                Arguments.of("(def chucho (new :org.homs.lechugascript.InterpreterTest$Dog 1 :chuchales))", new Dog(1, "chuchales")),
                Arguments.of("(def chucho (new :org.homs.lechugascript.InterpreterTest$Dog 1 :chuchales)) (chucho :toString)", new Dog(1, "chuchales").toString()),

                Arguments.of("(def chucho (new :org.homs.lechugascript.InterpreterTest$Dog)) (chucho :setId 42) (chucho :getId)", 42),
                Arguments.of("(def chucho (new :org.homs.lechugascript.InterpreterTest$Dog)) (chucho :setName :chuchales) (chucho :getName)", "chuchales"),

                Arguments.of("(and false false false)", false),
                Arguments.of("(and true false false)", false),
                Arguments.of("(and true true false)", false),
                Arguments.of("(and true true true)", true),

                Arguments.of("(or false false false)", false),
                Arguments.of("(or true false false)", true),
                Arguments.of("(or false true false)", true),
                Arguments.of("(or false false true)", true),
                Arguments.of("(or true true true)", true),


                Arguments.of("(defn to-long [x] (x :longValue)) (to-long 3.14159)", 3L),

                Arguments.of("(defn concat [s1 s2] (s1 :concat s2)) (concat :jou :juas)", "joujuas"),

                // TODO
                //        WARNING: An illegal reflective access operation has occurred
                //        WARNING: Illegal reflective access by org.homs.lispo.util.ReflectUtils (file:/C:/java/workospace/lispo2021/target/classes/) to method java.util.ArrayList$Itr.hasNext()
                //        WARNING: Please consider reporting this to the maintainers of org.homs.lispo.util.ReflectUtils
                //        WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
                //        WARNING: All illegal access operations will be denied in a future release
//                Arguments.of("(def it ([1 2 3] :iterator)) (while (it :hasNext) (it :next))", 3),


                Arguments.of("(:jou :concat :juas)", "joujuas"),
                Arguments.of("(((:jou :getClass) :getSimpleName) :toUpperCase)", "STRING"),
                Arguments.of("(:jou :substring 1)", "ou"),
                Arguments.of("(:jou :substring 1 2)", "o"),
                Arguments.of("([1 2 3] :get 0)", 1),
                Arguments.of("([1 2 3] :get 1)", 2),
                Arguments.of("([1 2 3] :get 2)", 3),

                Arguments.of("({[1 :one][2 :two][3 :three]} :get 1)", "one"),
                Arguments.of("({[1 :one][2 :two][3 :three]} :get 2)", "two"),
                Arguments.of("({[1 :one][2 :two][3 :three]} :get 3)", "three"),

                Arguments.of("(defn + [a b] (call-static :java.lang.Integer :sum a b)) (+ 2 3)", 5),

                Arguments.of("(for-each [] (fn [x i] x))", null),
                Arguments.of("(for-each [] (fn [x i] i))", null),
                Arguments.of("(for-each [:a] (fn [x i] x))", "a"),
                Arguments.of("(for-each [:a](fn [x i] i))", 0),
                Arguments.of("(for-each [:a :b] (fn [x i] x))", "b"),
                Arguments.of("(for-each [:a :b] (fn [x i] i))", 1),
                Arguments.of("(for-each [:a :b :c] (fn [x i] x))", "c"),
                Arguments.of("(for-each [:a :b :c] (fn [x i] i))", 2),

                Arguments.of("(def r 0) (for e [1 2 3] (set r (+ r e))(set r (+ r e)))", 6 * 2),
                Arguments.of("(def r 0) (for e [1 2 3] (set r (+ r e)))", 6),
                Arguments.of("(def r 0) (for e [] (set r (+ r e)))", null),


                Arguments.of(
                        "(defn + [a b] (call-static :java.lang.Integer :sum a b))" +
                                "(def ++ (fn [x] (fn [y] (+ x y))))" +
                                "((++ 2) 3)"
                        , 5),
                Arguments.of(
                        "(defn + [a b] (call-static :java.lang.Integer :sum a b))" +
                                "(((curry +) 2) 3)"
                        , 5),

                Arguments.of("\"\"\"arrr\nrrrgh\"\"\"", "arrr\nrrrgh"),

                Arguments.of(
                        "(defn juas [a] null)   \n" +
                                "(defn jou [a]          \n" +
                                "   (juas :b)           \n" +
                                "   a                   \n" +
                                ")                      \n" +
                                "(jou :a)               \n",
                        "a")
        );
    }

    @ParameterizedTest
    @MethodSource("expressionProvider")
    void testInterpret(String expression, Object expectedResult) throws Throwable {
        Interpreter i = new Interpreter();

        var env = i.getEnvironment();
        var asts = i.parse(expression, "test");
        Object result = i.evaluate(asts, env);

        assertThat(result).isEqualTo(expectedResult);
    }

    static Stream<Arguments> lambdaExpansionProvider() {
        return Stream.of(
                Arguments.of("(=> true)", "(fn [] true)"),
                Arguments.of("(a b => (* a b))", "(fn [a b] (* a b))"),
                Arguments.of("(a => (b => (* a b)))", "(fn [a] (fn [b] (* a b)))"),
                Arguments.of("(a => (b => (c => (* a b c))))", "(fn [a] (fn [b] (fn [c] (* a b c))))"),

                Arguments.of("((=> true))", "true"),
                Arguments.of("((=> true false))", "false"),
                Arguments.of("((a b => (* a b)) 2 3)", "6"),
                Arguments.of("(((a => (b => (* a b))) 2) 3)", "6"),

                Arguments.of("(a b => {[a b]})", "(fn [a b] {[a b]})"),
                Arguments.of("((a b => {[a b]}) :age 12)", "{age=12}")
        );
    }

    @ParameterizedTest
    @MethodSource("lambdaExpansionProvider")
    void test_lambdaExpansion(String expression, String expectedResult) throws Throwable {
        Interpreter i = new Interpreter();

        var env = i.getEnvironment();
        var asts = i.parse(expression, "test");
        Object result = i.evaluate(asts, env);

        assertThat(String.valueOf(result)).isEqualTo(expectedResult);
    }

    static Stream<Arguments> scriptsProvider() {
        return Stream.of(
                Arguments.of("std-test.lsp", true),
                Arguments.of("examples.lsp", true),
                Arguments.of("hores.lsp", true),
                Arguments.of("n-queens.lechuga", true)
        );
    }

    @ParameterizedTest
    @MethodSource("scriptsProvider")
    void testRunScript(String scriptName, Object expectedResult) throws Throwable {

//        // TODO coverage
//        final Map<String, Set<TokenAt>> coverajes = new TreeMap<>();
//        BiConsumer<Environment, Ast> coverajesListener = (env, ast) -> {
//            var tokenAt = ast.getTokenAt();
//            coverajes.putIfAbsent(tokenAt.sourceDesc, new TreeSet<>());
//            coverajes.get(tokenAt.sourceDesc).add(tokenAt);
//        };

        Interpreter i = new Interpreter();

        var env = i.getStdEnvironment();
        var asts = i.parseFileFromClasspath(scriptName, StandardCharsets.UTF_8);
        Object result = i.evaluate(asts, env/*, coverajesListener*/);

        assertThat(result).isEqualTo(expectedResult);

//        // TODO coverage
//        for (var source : coverajes.entrySet()) {
//            for (TokenAt line : source.getValue()) {
//                System.out.println(line);
//            }
//        }
    }

    @Test
    void error_functionDefinitionWithAnInvalidArgumentAst() {

        Interpreter i = new Interpreter();

        try {
            var env = i.getEnvironment();
            var asts = i.parse("(fn [1] 1)", "test");
            Object result = i.evaluate(asts, env);

            fail("an exception should be thrown");
        } catch (Throwable t) {

            assertThat(t.getMessage()).isEqualTo("error evaluating: (fn [1] 1); at test:1,1");
            assertThat(t.getCause()).isNotNull();
            assertThat(t.getCause().getMessage()).isEqualTo("required a SymbolAst as an argument of a function definition at test:1,6");
        }
    }

    @Test
    void getLechugaDoc() throws Throwable {
        Interpreter i = new Interpreter();
        var envVars = new TreeSet<>(i.getEnvironment().parent.getVariables().keySet());
        var stdEnvVars = new TreeSet<>(i.getStdEnvironment().parent.getVariables().keySet());

        System.out.println("===================================");
        System.out.println("BUILTIN DEFINITIONS");
        System.out.println("===================================");
        printVars(envVars);
        System.out.println();
        System.out.println();

        System.out.println("===================================");
        System.out.println("STD DEFINITIONS");
        System.out.println("===================================");
        printVars(stdEnvVars);
        System.out.println();
    }

    void printVars(Set<String> envVars) {
        int i = 0;
        for (var var : envVars) {
            System.out.print(var);
            if (i == 5) {
                System.out.println();
                i = 0;
            } else {
                for (int j = 0; j < 17 - var.length(); j++) {
                    System.out.print(" ");
                }
                i++;
            }
        }
    }


//    @Test
//    void error_lt_int_double() throws Throwable {
//
//        Interpreter i = new Interpreter();
//
//        var env = i.getEnvironment();
//        var asts = i.parse("(< 1 1.2)", "test");
//        Object result = i.evaluate(asts, env);
//
//    }
}