package org.homs.lechugatemplates;


import jucumber.JucumberJUnit5Runner;
import jucumber.anno.*;
import org.homs.lechugascript.Interpreter;
import org.homs.lechugascript.util.ReflectUtils;
import org.homs.lechugascript.util.TextFileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(JucumberJUnit5Runner.class)
@Feature("Lechuga templates")
@FeatureDescription("As a user, I want to use a Lechuga Templates.")
public class LechugaTemplatesAcceptanceTest {

    LechugaTemplate lechugaTemplate;
    Map<String, Object> model = new LinkedHashMap<>();
    String result;

    @Test
    @Scenario("")
    @Given({
            "the template 'index.html'",
            "the model provided by the function 'modelProvider'"
    })
    @When("the render method is executed")
    @Then("the result should be equals to file 'index.html.expected'")
    public void test01() throws Exception {
    }

    @Given("the template '(.+?)'")
    public void the_template(String templateFile) {
        var code = TextFileUtils.loadFileFromClasspath(templateFile);
        this.lechugaTemplate = new LechugaTemplate(new Interpreter(), templateFile, code);
    }

    @Given("the model provided by the function '(.+?)'")
    public void the_model(String modelProviderMethodName) {
        this.model = (Map<String, Object>) ReflectUtils.callMethod(this, modelProviderMethodName, new Object[]{});
    }

    @When("the render method is executed")
    public void execute_render() {
        this.result = this.lechugaTemplate.render(model);
    }

    @Then("the result should be equals to file '(.+?)'")
    public void the_result_should_be(String expectedOutputFile) {
        var expectedOutput = TextFileUtils.loadFileFromClasspath(expectedOutputFile);
        assertThat(result.replaceAll("\\s+", "")).isEqualTo(expectedOutput.replaceAll("\\s+", ""));
    }

    public Map<String, Object> modelProvider() {
        var model = new LinkedHashMap<String, Object>();
        model.put("title", "JOU");
        model.put("dogs", Arrays.asList(
                new Dog(123L, "chucho"),
                new Dog(456L, "faria")
        ));
        return model;
    }
}
