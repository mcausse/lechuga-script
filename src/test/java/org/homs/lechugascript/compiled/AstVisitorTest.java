package org.homs.lechugascript.compiled;

import org.homs.lechugascript.parser.Parser;
import org.homs.lechugascript.parser.ast.Ast;
import org.homs.lechugascript.tokenizer.Tokenizer;
import org.junit.jupiter.api.Test;

import java.util.List;

class AstVisitorTest {

    public List<Ast> parse(String program, String sourceDesc) {
        Tokenizer tokenizer = new Tokenizer(program, sourceDesc);
        Parser parser = new Parser(tokenizer);
        List<Ast> asts = parser.parse();
        return asts;
    }

    @Test
    void name0() {
        List<Ast> asts = parse("(def a 123)", "test");
        var visitor = new AstVisitor();

        asts.forEach(visitor::visit);

        System.out.println(visitor);
    }

    @Test
    void name() {
        List<Ast> asts = parse("(+ 3 4)", "test");
        var visitor = new AstVisitor();

        asts.forEach(visitor::visit);

        System.out.println(visitor);
    }

    @Test
    void name1() {
        List<Ast> asts = parse("(def five (+ 2 3)) five", "test");
        var visitor = new AstVisitor();

        asts.forEach(visitor::visit);

        System.out.println(visitor);

// (def five (+ 2 3))
        final var v__0 = 2;
        final var v__1 = 3;
        final var v__2 = org.homs.lechugascript.compiled.JRuntime.add(v__0, v__1);
        var five = v__2;
        final var v__3 = five;

        System.out.println(v__3);
    }

    @Test
    void name2() {
        List<Ast> asts = parse("(fn [a b] (+ a b))", "test");
        var visitor = new AstVisitor();

        asts.forEach(visitor::visit);

        System.out.println(visitor);

        // (fn [a b] (+ a b))
        final var v__0 = (Closure) args_v__0 -> {
            final var a = args_v__0[0];
            final var b = args_v__0[1];
            final var v__1 = a;
            final var v__2 = b;
            final var v__3 = org.homs.lechugascript.compiled.JRuntime.add(v__1, v__2);
            return v__3;
        };
    }

    @Test
    void name3() {
        List<Ast> asts = parse("((fn [a b] (+ a b)) 2 3)", "test");
        var visitor = new AstVisitor();

        asts.forEach(visitor::visit);

        System.out.println(visitor);


// (fn [a b] (+ a b))
        final var v__0 = (Closure) args_v__0 -> {
            final var a = args_v__0[0];
            final var b = args_v__0[1];
            final var v__1 = a;
            final var v__2 = b;
            final var v__3 = org.homs.lechugascript.compiled.JRuntime.add(v__1, v__2);
            return v__3;
        };
// ((fn [a b] (+ a b)) 2 3)
        final var v__5 = 2;
        final var v__6 = 3;
        final var v__4 = v__0.apply(v__5, v__6);

        System.out.println(v__4);
    }

    @Test
    void name31() {
        List<Ast> asts = parse("(def sum (fn [a b] (+ a b)))   (sum 2 3)", "test");
        var visitor = new AstVisitor();

        asts.forEach(visitor::visit);

        System.out.println(visitor);

// (def sum (fn [a b] (+ a b)))
// (fn [a b] (+ a b))
        final var v__0 = (Closure) args_v__0 -> {
            final var a = args_v__0[0];
            final var b = args_v__0[1];
            final var v__1 = a;
            final var v__2 = b;
            final var v__3 = org.homs.lechugascript.compiled.JRuntime.add(v__1, v__2);
            return v__3;
        };
        var sum = v__0;
// (sum 2 3)
        final var v__5 = 2;
        final var v__6 = 3;
        final var v__4 = sum.apply(v__5, v__6);

        System.out.println(v__4);
    }
    @Test
    void name31_macro_lambda() {
        List<Ast> asts = parse("(def sum (a b => (+ a b)))   (sum 2 3)", "test");
        var visitor = new AstVisitor();

        asts.forEach(visitor::visit);

        System.out.println(visitor);
    }
    @Test
    void name311() {
        List<Ast> asts = parse("(def sum (fn [a] (fn [b] (+ a b))))   ((sum 2) 3)", "test");
        var visitor = new AstVisitor();

        asts.forEach(visitor::visit);

        System.out.println(visitor);


// (def sum (fn [a] (fn [b] (+ a b))))
// (fn [a] (fn [b] (+ a b)))
        final var v__0 = (Closure) args_v__0 -> {
            final var a = args_v__0[0];
            // (fn [b] (+ a b))
            final var v__1 = (Closure) args_v__1 -> {
                final var b = args_v__1[0];
                final var v__2 = a;
                final var v__3 = b;
                final var v__4 = org.homs.lechugascript.compiled.JRuntime.add(v__2, v__3);
                return v__4;
            };
            return v__1;
        };
        var sum = v__0;
// (sum 2)
        final var v__6 = 2;
        final var v__5 = sum.apply(v__6);
// ((sum 2) 3)
        final var v__8 = 3;
        final var v__7 = ((Closure) v__5).apply(v__8);


        System.out.println(v__7);
    }

    @Test
    void name32() {
        List<Ast> asts = parse("(((fn [a] (fn [b] (+ a b)))  2) 3)", "test");
        var visitor = new AstVisitor();

        asts.forEach(visitor::visit);

        System.out.println(visitor);

// (fn [a] (fn [b] (+ a b)))
        final var v__0 = (Closure) args_v__0 -> {
            final var a = args_v__0[0];
            // (fn [b] (+ a b))
            final var v__1 = (Closure) args_v__1 -> {
                final var b = args_v__1[0];
                final var v__2 = a;
                final var v__3 = b;
                final var v__4 = org.homs.lechugascript.compiled.JRuntime.add(v__2, v__3);
                return v__4;
            };
            return v__1;
        };
// ((fn [a] (fn [b] (+ a b))) 2)
        final var v__6 = 2;
        final var v__5 = v__0.apply(v__6);
// (((fn [a] (fn [b] (+ a b))) 2) 3)
        final var v__8 = 3;
        final var v__7 = ((Closure) v__5).apply(v__8);


        System.out.println(v__7);
    }

    @Test
    void list() {
        List<Ast> asts = parse("(def ints [1 2 3])", "test");
        var visitor = new AstVisitor();

        asts.forEach(visitor::visit);

        System.out.println(visitor);

// (def ints [1 2 3])
// [1 2 3]
        var v__0 = new java.util.ArrayList<>();
        {
            final var v__1 = 1;
            v__0.add(v__1);
            final var v__2 = 2;
            v__0.add(v__2);
            final var v__3 = 3;
            v__0.add(v__3);
        }
        var ints = v__0;

    }
}