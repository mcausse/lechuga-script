package org.homs.lechugascript.binding2;

public interface Jou {

    @Lechuga("(concat a b)")
    String juas(
            @LechugaArg("a") String a,
            @LechugaArg("b") int b
    );

    @Lechuga("(+ a b)")
    int add(
            @LechugaArg("a") int a,
            @LechugaArg("b") int b
    );
}
