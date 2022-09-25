package org.homs.lechugatemplates;

import org.homs.lechugascript.Interpreter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class LechugaTemplateTranspilerTest {

    @Test
    void testTemplateToJs() {
        assertThat(new LechugaTemplateTranspiler().transpile("")).isEqualTo("(def __strb__ (new :java.lang.StringBuilder))(__strb__ :toString)");
        assertThat(new LechugaTemplateTranspiler().transpile("a")).isEqualTo("(def __strb__ (new :java.lang.StringBuilder))(__strb__ :append \"a\")\n" +
                "(__strb__ :toString)");
        assertThat(new LechugaTemplateTranspiler().transpile("<%a%>")).isEqualTo("(def __strb__ (new :java.lang.StringBuilder))a(__strb__ :toString)");
        assertThat(new LechugaTemplateTranspiler().transpile("<%=a%>")).isEqualTo("(def __strb__ (new :java.lang.StringBuilder))(__strb__ :append a)(__strb__ :toString)");
        assertThat(new LechugaTemplateTranspiler().transpile("<%!a%>")).isEqualTo("(def __strb__ (new :java.lang.StringBuilder))(__strb__ :toString)");

        try {
            new LechugaTemplateTranspiler().transpile("<%!a");
            fail("an exception should be thrown");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
            assertThat(e.getMessage()).isEqualTo("expected %>, but eof");
        }
    }

    @Test
    void testTemplateToJs_evaluating_empty() throws Throwable {
        String template = "";
        var viewModel = 123L;

        String jsCode = new LechugaTemplateTranspiler().transpile(template);

        var r = executeJs(jsCode, viewModel);
        assertThat(r).isEqualTo("");
    }

    @Test
    void testTemplateToJs_evaluating_simpleTag() throws Throwable {
        var viewModel = "world";
        String template = "alo <%=model%>!";

        String jsCode = new LechugaTemplateTranspiler().transpile(template);
        System.out.println(jsCode);

        var r = executeJs(jsCode, viewModel);
        assertThat(r).isEqualTo("alo world!");
    }

    @Test
    void testTemplateToJs_evaluating_simpleComment() throws Throwable {
        var viewModel = "world";
        String template = "alo <%!model%>!";

        String jsCode = new LechugaTemplateTranspiler().transpile(template);
        System.out.println(jsCode);

        var r = executeJs(jsCode, viewModel);
        assertThat(r).isEqualTo("alo !");
    }

    @Test
    void testTemplateToJs_evaluating_fullExample() throws Throwable {
        var viewModel = Arrays.asList(
                new Dog(123L, "chucho"),
                new Dog(456L, "faria")
        );
        String template = "<%! this is an example%>\n" +
                "<ul>\n" +
                "<% (for dog model %>\n" +
                "   <li><%=(dog :getId)%>-<%=(dog :getName)%></li>\n" +
                "<% ) %>\n" +
                "</ul>";

        String jsCode = new LechugaTemplateTranspiler().transpile(template);
        System.out.println(jsCode);

        var r = executeJs(jsCode, viewModel);
        assertThat(r.toString().replaceAll("\\s+", "")).isEqualTo("<ul><li>123-chucho</li><li>456-faria</li></ul>");
    }

    Object executeJs(String code, Object viewModel) throws Throwable {
        var i = new Interpreter();

        var asts = i.parse(code, "test");

        var env = i.getStdEnvironment();
        env.def("model", viewModel);
        var r = i.evaluate(asts, env);
        return r;
    }

}
