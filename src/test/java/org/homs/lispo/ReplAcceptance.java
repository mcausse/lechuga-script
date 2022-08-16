package org.homs.lispo;

import jucumber.JucumberJUnit5Runner;
import jucumber.anno.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(JucumberJUnit5Runner.class)
@Feature("Lechuga REPL")
@FeatureDescription("As a user, I want to use a LechugaScript REPL.")
public class ReplAcceptance {

    RepLoop repl;
    InputStream is;
    ByteArrayOutputStream os;

    @Test
    @Scenario("REPL can execute a single expression")
    @When("the user types the expression '(+ 1 2 3)\n(q)\n'")
    @Then("the result should be '6'")
    public void simpleExpression() throws Exception {
    }

    @Test
    @Scenario("REPL can execute a single expression in multiple lines")
    @When("the user types the expression '(+ 1 \n2 \n3)\n(q)\n'")
    @Then("the result should be '6'")
    public void simpleExpressionMultiline() throws Exception {
    }

    @When("the user types the expression '(.*?)'")
    public void typeExpression(String expression) throws Throwable {
        this.is = new ByteArrayInputStream(expression.getBytes(StandardCharsets.UTF_8));
        this.os = new ByteArrayOutputStream();
        this.repl = new RepLoop(is, os, false);
        this.repl.repLoop();
    }

    @Then("the result should be '(.*?)'")
    public void assertResult(String expectedResult) {
        String result = os.toString(StandardCharsets.UTF_8);
        assertThat(result.trim()).isEqualTo(expectedResult);
    }


}
