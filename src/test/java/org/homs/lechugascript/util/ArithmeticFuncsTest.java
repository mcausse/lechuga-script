package org.homs.lechugascript.util;

import org.homs.lechugascript.tokenizer.TokenAt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ArithmeticFuncsTest {

    static Stream<Arguments> operandsForAdd() {
        return Stream.of(
                Arguments.of((byte) 1, (byte) 1, 2),
                Arguments.of((byte) 1, (short) 1, 2),
                Arguments.of((short) 1, (short) 1, 2),
                Arguments.of((short) 1, 1, 2),
                Arguments.of(1, 1, 2),
                Arguments.of(1, 1L, 2L),
                Arguments.of(1L, 1L, 2L),
                Arguments.of(1L, 1.2f, 2.2f),
                Arguments.of(1.1f, 1.2f, 2.3f),
                Arguments.of(1.1f, 1.2, 2.3),
                Arguments.of(1.1, 1.2, 2.3)
        );
    }

    @ParameterizedTest
    @MethodSource("operandsForAdd")
    void testAdd(Number a, Number b, Number expectedResult) throws Throwable {

        Number result = (Number) ArithmeticFuncs.funcAdd.eval(TokenAt.SYSTEM, null, Arrays.asList(a, b));

        assertEquals(result.doubleValue(), expectedResult.doubleValue(), 0.1);
    }

    static Stream<Arguments> operandsForSub() {
        return Stream.of(
                Arguments.of((byte) 1, (byte) 1, 0),
                Arguments.of((byte) 1, (short) 1, 0),
                Arguments.of((short) 1, (short) 1, 0),
                Arguments.of((short) 1, 1, 0),
                Arguments.of(1, 1, 0),
                Arguments.of(1, 1L, 0L),
                Arguments.of(1L, 1L, 0L),
                Arguments.of(1L, 1.2f, -0.2f),
                Arguments.of(1.1f, 1.2f, -0.1f),
                Arguments.of(1.1f, 1.2, -0.1),
                Arguments.of(1.1, 1.2, -0.1)
        );
    }

    @ParameterizedTest
    @MethodSource("operandsForSub")
    void testSub(Number a, Number b, Number expectedResult) throws Throwable {

        Number result = (Number) ArithmeticFuncs.funcSub.eval(TokenAt.SYSTEM, null, Arrays.asList(a, b));

        assertEquals(result.doubleValue(), expectedResult.doubleValue(), 0.1);
    }

    static Stream<Arguments> operandsForMul() {
        return Stream.of(
                Arguments.of((byte) 1, (byte) 1, 1),
                Arguments.of((byte) 1, (short) 1, 1),
                Arguments.of((short) 1, (short) 1, 1),
                Arguments.of((short) 1, 1, 1),
                Arguments.of(1, 1, 1),
                Arguments.of(1, 1L, 1L),
                Arguments.of(1L, 1L, 1L),
                Arguments.of(1L, 1.2f, 1.2f),
                Arguments.of(1.1f, 1.2f, 1.32f),
                Arguments.of(1.1f, 1.2, 1.32),
                Arguments.of(1.1, 1.2, 1.32)
        );
    }

    @ParameterizedTest
    @MethodSource("operandsForMul")
    void testMul(Number a, Number b, Number expectedResult) throws Throwable {

        Number result = (Number) ArithmeticFuncs.funcMul.eval(TokenAt.SYSTEM, null, Arrays.asList(a, b));

        assertEquals(result.doubleValue(), expectedResult.doubleValue(), 0.1);
    }

    static Stream<Arguments> operandsForDiv() {
        return Stream.of(
                Arguments.of((byte) 1, (byte) 1, 1),
                Arguments.of((byte) 1, (short) 1, 1),
                Arguments.of((short) 1, (short) 1, 1),
                Arguments.of((short) 1, 1, 1),
                Arguments.of(1, 1, 1),
                Arguments.of(1, 1L, 1L),
                Arguments.of(1L, 1L, 1L),
                Arguments.of(1L, 1.2f, 0.83333f),
                Arguments.of(1.1f, 1.2f, 0.916f),
                Arguments.of(1.1f, 1.2, 0.916),
                Arguments.of(1.1, 1.2, 0.916)
        );
    }

    @ParameterizedTest
    @MethodSource("operandsForDiv")
    void testDiv(Number a, Number b, Number expectedResult) throws Throwable {

        Number result = (Number) ArithmeticFuncs.funcDiv.eval(TokenAt.SYSTEM, null, Arrays.asList(a, b));

        assertEquals(result.doubleValue(), expectedResult.doubleValue(), 0.1);
    }

    static Stream<Arguments> operandsForMod() {
        return Stream.of(
                Arguments.of((byte) 1, (byte) 1, 0),
                Arguments.of((byte) 1, (short) 1, 0),
                Arguments.of((short) 1, (short) 1, 0),
                Arguments.of((short) 1, 1, 0),
                Arguments.of(1, 1, 0),
                Arguments.of(1, 1L, 0L),
                Arguments.of(1L, 1L, 0L),
                Arguments.of(1L, 1.2f, 1f),
                Arguments.of(1.1f, 1.2f, 1.1f),
                Arguments.of(1.1f, 1.2, 1.1),
                Arguments.of(1.1, 1.2, 1.1)
        );
    }

    @ParameterizedTest
    @MethodSource("operandsForMod")
    void testMod(Number a, Number b, Number expectedResult) throws Throwable {

        Number result = (Number) ArithmeticFuncs.funcMod.eval(TokenAt.SYSTEM, null, Arrays.asList(a, b));

        assertEquals(result.doubleValue(), expectedResult.doubleValue(), 0.1);
    }


    @Test
    void testFuncToByte() throws Throwable {

        Number num = 2;
        String str = "3";
        Object invalid = new byte[]{};

        assertThat(
                ArithmeticFuncs.funcToByte.eval(TokenAt.SYSTEM, null, Arrays.asList(num))
        ).isEqualTo(num.byteValue());

        assertThat(
                ArithmeticFuncs.funcToByte.eval(TokenAt.SYSTEM, null, Arrays.asList(str))
        ).isEqualTo(Byte.parseByte(str));

        try {
            ArithmeticFuncs.funcToByte.eval(TokenAt.SYSTEM, null, Arrays.asList(invalid));
            fail("");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    void testFuncToShort() throws Throwable {

        Number num = 2;
        String str = "3";
        Object invalid = new byte[]{};

        assertThat(
                ArithmeticFuncs.funcToShort.eval(TokenAt.SYSTEM, null, Arrays.asList(num))
        ).isEqualTo(num.shortValue());

        assertThat(
                ArithmeticFuncs.funcToShort.eval(TokenAt.SYSTEM, null, Arrays.asList(str))
        ).isEqualTo(Short.parseShort(str));

        try {
            ArithmeticFuncs.funcToShort.eval(TokenAt.SYSTEM, null, Arrays.asList(invalid));
            fail("");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    void testFuncToInt() throws Throwable {

        Number num = 2;
        String str = "3";
        Object invalid = new byte[]{};

        assertThat(
                ArithmeticFuncs.funcToInt.eval(TokenAt.SYSTEM, null, Arrays.asList(num))
        ).isEqualTo(num.intValue());

        assertThat(
                ArithmeticFuncs.funcToInt.eval(TokenAt.SYSTEM, null, Arrays.asList(str))
        ).isEqualTo(Integer.parseInt(str));

        try {
            ArithmeticFuncs.funcToInt.eval(TokenAt.SYSTEM, null, Arrays.asList(invalid));
            fail("");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    void testFuncToLong() throws Throwable {

        Number num = 2;
        String str = "3";
        Object invalid = new byte[]{};

        assertThat(
                ArithmeticFuncs.funcToLong.eval(TokenAt.SYSTEM, null, Arrays.asList(num))
        ).isEqualTo(num.longValue());

        assertThat(
                ArithmeticFuncs.funcToLong.eval(TokenAt.SYSTEM, null, Arrays.asList(str))
        ).isEqualTo(Long.parseLong(str));

        try {
            ArithmeticFuncs.funcToLong.eval(TokenAt.SYSTEM, null, Arrays.asList(invalid));
            fail("");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    void testFuncToFloat() throws Throwable {

        Number num = 2;
        String str = "3";
        Object invalid = new byte[]{};

        assertThat(
                ArithmeticFuncs.funcToFloat.eval(TokenAt.SYSTEM, null, Arrays.asList(num))
        ).isEqualTo(num.floatValue());

        assertThat(
                ArithmeticFuncs.funcToFloat.eval(TokenAt.SYSTEM, null, Arrays.asList(str))
        ).isEqualTo(Float.parseFloat(str));

        try {
            ArithmeticFuncs.funcToFloat.eval(TokenAt.SYSTEM, null, Arrays.asList(invalid));
            fail("");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    void testFuncToDouble() throws Throwable {

        Number num = 2;
        String str = "3";
        Object invalid = new byte[]{};

        assertThat(
                ArithmeticFuncs.funcToDouble.eval(TokenAt.SYSTEM, null, Arrays.asList(num))
        ).isEqualTo(num.doubleValue());

        assertThat(
                ArithmeticFuncs.funcToDouble.eval(TokenAt.SYSTEM, null, Arrays.asList(str))
        ).isEqualTo(Double.parseDouble(str));

        try {
            ArithmeticFuncs.funcToDouble.eval(TokenAt.SYSTEM, null, Arrays.asList(invalid));
            fail("");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }
}
