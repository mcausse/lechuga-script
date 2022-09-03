package org.homs.lechugascript.binding;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class CmdArgumentsProcessorBindingsTest {

    @Test
    void getModifiers_isEmpty() throws Throwable {

        var a = InterfaceBindingFactory.build(CmdArgumentsProcessor.class);

        var r = a.getModifiers();

        assertThat(r).isEmpty();
    }

    @Test
    void getFiles_isEmpty() throws Throwable {

        var a = InterfaceBindingFactory.build(CmdArgumentsProcessor.class);

        var r = a.getFiles();

        assertThat(r).isEmpty();
    }


    @Test
    void name() throws Throwable {
        String[] args = {"--ip=127.0.0.1", "v8.txt", "--port=23"};

        var sut = InterfaceBindingFactory.build(CmdArgumentsProcessor.class);

        // Act
        sut.processArgs(Arrays.asList(args));

        assertThat(sut.getFiles().size()).isEqualTo(1);
        assertThat(sut.getFiles().toString()).isEqualTo("[v8.txt]");

        assertThat(sut.getModifiers().size()).isEqualTo(2);
        assertThat(sut.getModifiers().toString()).isEqualTo("{ip=127.0.0.1, port=23}");
    }

    @Test
    void error() throws Throwable {

        String[] args = {"--ip=127.0.0.1", "v8.txt", "--port22222223"};

        var sut = InterfaceBindingFactory.build(CmdArgumentsProcessor.class);

        try {
            // Act
            sut.processArgs(Arrays.asList(args));

            fail("an exception should be thrown");
        } catch (RuntimeException e) {
            assertThat(e).getRootCause().hasMessageContaining("unexpected argument: --port22222223");
        }
    }
}
