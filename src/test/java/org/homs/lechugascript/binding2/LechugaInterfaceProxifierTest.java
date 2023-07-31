package org.homs.lechugascript.binding2;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LechugaInterfaceProxifierTest {

    @Test
    void juas() throws Throwable {

        Jou jou = LechugaInterfaceProxifier.build(Jou.class);

        String r = jou.juas("a", 6);

        assertThat(r).isEqualTo("a6");
    }

    @Test
    void add() throws Throwable {

        Jou jou = LechugaInterfaceProxifier.build(Jou.class);

        int r = jou.add(2, 3);

        assertThat(r).isEqualTo(5);
    }
}