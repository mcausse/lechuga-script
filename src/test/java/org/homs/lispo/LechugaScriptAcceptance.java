package org.homs.lispo;

import jucumber.JucumberJUnit5Runner;
import jucumber.anno.*;
import org.homs.lispo.parser.ast.Ast;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(JucumberJUnit5Runner.class)
@Feature("Lechuga scripting")
@FeatureDescription("As a user, I want to use a LechugaScript (the brain-f*cking scripting language)).")
public class LechugaScriptAcceptance {

    Interpreter interpreter;
    Environment interpreterEnv;
    List<Ast> parsedAsts;

    @Test
    @Scenario("Verify that the STD library is ready to use, by using (reduce ...)")
    @Given("a prepared Std environment")
    @When("the code is executed: '(reduce 0 (fn[acc x] (+ acc x)) [1 2 3 4])'")
    @Then("the result should be '10'")
    public void test01_Std_Is_Ready() throws Exception {
    }

    @Given("a prepared Std environment")
    public void prepare_Std_Environment() throws Throwable {
        this.interpreter = new Interpreter();
        this.interpreterEnv = interpreter.getStdEnvironment();
    }

    @When("the code is executed: '(.+?)'")
    public void execute(String code) {
        this.parsedAsts = this.interpreter.parse(code, getClass().getName());
    }

    @Then("the result should be '(.+?)'")
    public void assertResult(String result) throws Throwable {
        Object r = interpreter.evaluate(parsedAsts, interpreterEnv);
        assertThat(String.valueOf(r)).isEqualTo(result);
    }
}
