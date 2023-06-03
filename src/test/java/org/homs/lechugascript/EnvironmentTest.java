package org.homs.lechugascript;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class EnvironmentTest {

    @Test
    void fail_when_getting_an_undefined_value() {
        final Environment e = new Environment(null);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> e.get("pi"),
                "expected an exception thrown"
        );

        assertThat(ex.getMessage()).isEqualTo("variable not defined: 'pi'");
    }

    @Test
    void obtain_a_defined_value() {
        Environment e = new Environment(null);
        e.def("pi", 3.14159);

        double obtained = (double) e.get("pi");

        assertThat(obtained).isEqualTo(3.14159);
    }

    @Test
    void modify_a_defined_value() {
        Environment e = new Environment(null);
        e.def("pi", 3);

        e.set("pi", 3.14159);

        double obtained = (double) e.get("pi");
        assertThat(obtained).isEqualTo(3.14159);
    }

    @Test
    void cannot_modify_an_undefined_value() {
        final Environment e = new Environment(null);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> e.set("pi", 3.14159),
                "expected an exception thrown"
        );

        assertThat(ex.getMessage()).isEqualTo("undefined variable for 'set' operation: pi");
    }

    @Test
    void double_def() {
        Environment e = new Environment(null);
        e.def("pi", 3);

        try {
            e.def("pi", 3.14159);

            fail("");
        } catch (Exception ex) {
            assertThat(ex.getMessage()).isEqualTo("yet defined variable: 'pi'; use set");
        }
    }
}