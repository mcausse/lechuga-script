package org.homs.llp;

import org.homs.lispo.Interpreter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LispyTest {

    static Stream<Arguments> scriptsProvider() {
        return Stream.of(
                Arguments.of("llp.lsp", true)
        );
    }

    @Disabled
    @ParameterizedTest
    @MethodSource("scriptsProvider")
    void testRunScript(String scriptName, Object expectedResult) throws Throwable {
        Interpreter i = new Interpreter();

        Object result = i.evalClassPathFile(scriptName);

        assertThat(result).isEqualTo(expectedResult);
    }

}
