package org.homs.lechugatemplates;

import org.homs.lechugascript.Interpreter;
import org.homs.lechugascript.util.TextFileUtils;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class LechugaTemplateTest {

    @Test
    void render() {

        final String templateFileName = "index.html";

        final String code;
        {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream is = loader.getResourceAsStream(templateFileName);
            code = TextFileUtils.read(is, TextFileUtils.UTF8);
        }

        var template = new LechugaTemplate(new Interpreter(), templateFileName, code);

        var model = new LinkedHashMap<String, Object>();
        model.put("title", "JOU");
        model.put("dogs", Arrays.asList(
                new Dog(123L, "chucho"),
                new Dog(456L, "faria")
        ));

        var r = template.render(model);

        assertThat(r).contains(
                "<title>JOU</title>",
                "<h1>JOU</h1>",
                "<li>123 - chucho</li>",
                "<li>456 - faria</li>"
        );
    }
}