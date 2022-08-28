package org.homs.lechugascript.parser;

import org.homs.lechugascript.parser.ast.Ast;
import org.homs.lechugascript.parser.ast.ListAst;
import org.homs.lechugascript.tokenizer.EToken;
import org.homs.lechugascript.tokenizer.Token;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {

    static Token tokenBuild(EToken type, String value) {
        return new Token(type, value, "", -1, -1);
    }

    @Test
    void parse() {
        Iterator<Token> tokenizer = Arrays.asList(
                tokenBuild(EToken.NUMERIC, "3.14159")
        ).iterator();
        Parser parser = new Parser(tokenizer);

        List<Ast> resultAst = parser.parse();

        assertThat(resultAst).hasSize(1);
        assertThat(resultAst.get(0).toString()).isEqualTo("3.14159");
    }

    @ParameterizedTest
    @CsvSource({
            "NULL,null,NullAst",
            "BOOL,true,BooleanAst",
            "NUMERIC,123,NumberAst",
            "NUMERIC,3.14159,NumberAst",
            "STRING,joujou,StringAst",
            "SYMBOL,*,SymbolAst",
            "INTERPOLATION_STRING,joujuas,InterpolationStringAst"
    })
    void parse_the_atomic_tokens_properly(String tokenTypeName, String tokenValue, String simpleAstClassName) {
        Parser parser = new Parser(null);
        EToken eToken = EToken.valueOf(tokenTypeName);
        Token token = new Token(eToken, tokenValue, "", -1, -1);

        Ast ast = parser.parseToken(token);

        assertThat(ast.getClass().getSimpleName()).isEqualTo(simpleAstClassName);
    }

    static Stream<Arguments> provideListAstTokens() {
        return Stream.of(
                Arguments.of(0, tokenBuild(EToken.OPEN_LIST, "["), Arrays.asList(tokenBuild(EToken.CLOSE_LIST, "]"))),
                Arguments.of(1, tokenBuild(EToken.OPEN_LIST, "["), Arrays.asList(
                        tokenBuild(EToken.NUMERIC, "3.14159"),
                        tokenBuild(EToken.CLOSE_LIST, "]"))),
                Arguments.of(2, tokenBuild(EToken.OPEN_LIST, "["), Arrays.asList(
                        tokenBuild(EToken.NUMERIC, "3.14159"),
                        tokenBuild(EToken.NUMERIC, "123"),
                        tokenBuild(EToken.CLOSE_LIST, "]")))
        );
    }

    @ParameterizedTest
    @MethodSource("provideListAstTokens")
    void parse_the_ListAst(int listSize, Token tokensHead, List<Token> tokensTail) {
        Parser parser = new Parser(tokensTail.iterator());

        ListAst ast = parser.parseListAst(tokensHead);

        assertThat(ast.getValues().size()).isEqualTo(listSize);
    }

    static Stream<Arguments> provideUnclosedListAstTokens() {
        return Stream.of(
                Arguments.of(tokenBuild(EToken.OPEN_LIST, "["), Arrays.asList()),
                Arguments.of(tokenBuild(EToken.OPEN_LIST, "["), Arrays.asList(
                        tokenBuild(EToken.NUMERIC, "3.14159"))),
                Arguments.of(tokenBuild(EToken.OPEN_LIST, "["), Arrays.asList(
                        tokenBuild(EToken.NUMERIC, "3.14159"),
                        tokenBuild(EToken.NUMERIC, "123")))
        );
    }

    @ParameterizedTest()
    @MethodSource("provideUnclosedListAstTokens")
    void fail_parsing_an_unclosed_list(Token tokensHead, List<Token> tokensTail) {
        Parser parser = new Parser(tokensTail.iterator());

        RuntimeException e = assertThrows(
                RuntimeException.class,
                () -> parser.parseListAst(tokensHead),
                "expected an exception thrown"
        );

        assertThat(e.getMessage()).isEqualTo("expected ] but EOF; at :-1,-1");
    }

}