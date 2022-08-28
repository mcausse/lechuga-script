package org.homs.lechugascript.tokenizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class TokenizerTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            " ",
            "\n",
            "\t",
            "     ",
            "  \t \n  ",
            " \t ; jou \n\n   ",
            ";jou\n;juas\n",
            " ;jou\n;juas\n "
    })
    void consume_whitespaces_of_the_program_properly(String program) {

        // Arrange a Tokenizer instance with the given program
        Tokenizer t = new Tokenizer(program, "sourceDesc");

        // Act
        t.consumeWhitespaces();

        // Assert that all the program is consumed because is
        // all of whitespaces and/or line comments
        assertThat(t.p).isEqualTo(program.length());
    }

    @ParameterizedTest
    @CsvSource({
            "'',1,1",
            "'  \t \n  ',2,3",
            "' \t ; jou \n\n',3,1",

            "'jou',1,4",
            "'jou\n',2,1",
            "'jou\njuas',2,5",
            "'jou\njuas\n',3,1"
    })
    void update_rows_and_columns_when_consuming_the_program(String program, int row, int column) {

        // Arrange a Tokenizer instance with the given program
        Tokenizer t = new Tokenizer(program, "sourceDesc");

        // Act
        for (int i = 0; i < program.length(); i++) {
            t.updateRowCol(i);
        }

        // Assert that the row/column after consuming the program
        // are seeked as expected
        assertThat(t.row).isEqualTo(row);
        assertThat(t.col).isEqualTo(column);
    }

    @ParameterizedTest
    @CsvSource({
            "'',                '[]'",
            "' \n\t \n ',       '[]'",
            "'()',              '[OPEN_PAR:(, CLOSE_PAR:)]'",
            "'[]',              '[OPEN_LIST:[, CLOSE_LIST:]]'",
            "'{}',              '[OPEN_MAP:{, CLOSE_MAP:}]'",

            "'null',            '[NULL:null]'",
            "'true false',      '[BOOL:true, BOOL:false]'",
            "':jou :juas ',     '[STRING:jou, STRING:juas]'",
            "'1 2.3',           '[NUMERIC:1, NUMERIC:2.3]'",
            "'jou',             '[SYMBOL:jou]'",

            "'\"hoho\"',        '[STRING:hoho]'",
            "'\"ho\nho\"',      '[STRING:ho\nho]'",
            "'\"ho\\\"ho\"',    '[STRING:ho\"ho]'",
            "'\"\"\"jou\"juas\"\"\"',  '[INTERPOLATION_STRING:jou\"juas]'",
            ";comment',         '[]'",
    })
    void tokenize_a_program_properly(String program, String expectedStringifiedTokens) {

        // Arrange a Tokenizer instance with the given program
        Tokenizer t = new Tokenizer(program, "sourceDesc");

        // Act
        List<Token> tokens = new ArrayList<>();
        while (t.hasNext()) {
            tokens.add(t.next());
        }

        // Assert that the produced Tokens are the expected ones
        assertThat(tokens.toString()).isEqualTo(expectedStringifiedTokens);
    }

    @Test
    void throw_an_exception_when_tokenizing_a_program_with_an_unclosed_string() {

        // Arrange a Tokenizer instance with the given program
        Tokenizer t = new Tokenizer(":jou \"hohoho :juas", "sourceDesc");

        // Act
        try {
            while (t.hasNext()) {
                t.next();
            }
            fail("an exception should be thrown");
        } catch (Exception e) {

            // Assert that an exception is produced, and indicates that the
            // problem is an unclosed string
            assertThat(e).isInstanceOf(RuntimeException.class);
            assertThat(e.getMessage()).isEqualTo("expected closing \" but eof; opened at sourceDesc:1:6");
        }
    }

    @Test
    void throw_an_exception_when_tokenizing_a_program_with_an_unclosed_multiline_string() {

        // Arrange a Tokenizer instance with the given program
        Tokenizer t = new Tokenizer(":jou \"\"\"hohoho :juas", "sourceDesc");

        // Act
        try {
            while (t.hasNext()) {
                t.next();
            }
            fail("an exception should be thrown");
        } catch (Exception e) {

            // Assert that an exception is produced, and indicates that the
            // problem is an unclosed string
            assertThat(e).isInstanceOf(RuntimeException.class);
            assertThat(e.getMessage()).isEqualTo("expected closing \"\"\" but eof; opened at sourceDesc:1:6");
        }
    }
}
