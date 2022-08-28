package org.homs.lechugascript;

import jucumber.JucumberJUnit5Runner;
import jucumber.anno.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(JucumberJUnit5Runner.class)
@Feature("Lechuga scripting")
@FeatureDescription("As a user, I want to use a LechugaScript (the brain-f*cking scripting language)).")
public class LechugaScriptAcceptance {

    Interpreter interpreter;
    Environment interpreterEnv;
    Object result;
    Exception exception;

    @Test
    @Scenario("a vanilla environment cannot execute (reduce ...)")
    @Given("a prepared vanilla environment")
    @When("the code is executed: '(reduce 0 (fn[acc x] (+ acc x)) [1 2 3 4])'")
    @Then("an exception should be thrown: 'java.lang.RuntimeException: variable not defined: 'reduce''")
    public void test01_Reduce_Without_Std_Should_Fail() throws Exception {
    }

    @Test
    @Scenario("an Std environment can execute (reduce ...)")
    @Given("a prepared Std environment")
    @When("the code is executed: '(reduce 0 (fn[acc x] (+ acc x)) [1 2 3 4])'")
    @Then("the result should be '10'")
    public void test02_Reduce_With_Std() throws Exception {
    }

    @Given("a prepared vanilla environment")
    public void prepare_Vanilla_Environment() throws Throwable {
        this.interpreter = new Interpreter();
        this.interpreterEnv = interpreter.getEnvironment();
    }

    @Given("a prepared Std environment")
    public void prepare_Std_Environment() throws Throwable {
        this.interpreter = new Interpreter();
        this.interpreterEnv = interpreter.getStdEnvironment();
    }

    @When("the code is executed: '(.+?)'")
    public void execute(String code) throws Throwable {
        this.exception = null;
        this.result = null;
        try {
            var parsedAsts = this.interpreter.parse(code, getClass().getName());
            this.result = interpreter.evaluate(parsedAsts, interpreterEnv);
        } catch (Exception e) {
            this.exception = e;
        }
    }

    @Then("the result should be '(.+?)'")
    public void assertResult(String expectedResult) throws Throwable {
        assertThat(String.valueOf(result)).isEqualTo(expectedResult);
        assertThat(exception).isNull();
    }

    @Then("an exception should be thrown: '(.+?)'")
    public void assertThatAnExceptionSouldBeThrown(String exception) {
        Throwable e = this.exception;
        while (e.getCause() != null) {
            e = e.getCause();
        }
        assertThat(String.valueOf(e)).isEqualTo(exception);
        assertThat(this.result).isNull();
    }
}
