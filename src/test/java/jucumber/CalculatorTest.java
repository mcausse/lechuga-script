package jucumber;

import jucumber.anno.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(JucumberJUnit5Runner.class)
@Feature("Calculator")
@FeatureDescription("As a user, I want to use a calculator to add numbers, and it should work properly.")
public class CalculatorTest {

    final Calculator calculator = new Calculator();

    @Test
    @Scenario("Verify the initial state")
    @When("the calculator is powered on")
    @Then("the displayed result should be '0'")
    public void test01InitialState() throws Exception {
    }


    @Test
    @Scenario("Press a single digit")
    @Given("a calculator in the initial state")
    @When("I enter the digit 2")
    @Then("the displayed result should be '2'")
    public void test02EnterSingleDigit() throws Exception {
    }

    @Test
    @Scenario("Enter a number")
    @Given("a calculator in the initial state")
    @When({"I enter the digit 1",
            "I enter the digit 2",
            "I enter the digit 3"})
    @Then("the displayed result should be '123'")
    public void test03PressANumber() throws Exception {
    }

    @Test
    @Scenario("Enter a number")
    @Given({"a calculator in the initial state",
            "the digit 2 is pressed",
            "the digit 3 is pressed"
    })
    @When("I press Enter")
    @Then("the displayed result should be '0'")
    public void test04EnterANumber() throws Exception {
    }

    @Test
    @Scenario("Add two numbers")
    @Given({"a calculator in the initial state",
            "the digit 2 is pressed",
            "the digit 3 is pressed",
            "the Enter key is pressed",
            "the digit 4 is pressed",
            "the digit 5 is pressed"
    })
    @When("I press Add")
    @Then("the displayed result should be '68'")
    public void test05AddTwoNumbers() throws Exception {
    }

    @Given("a calculator in the initial state")
    @When("the calculator is powered on")
    void resetCalc() {
        this.calculator.pressReset();
    }

    @Given("the digit (\\d) is pressed")
    @When("I enter the digit (\\d)")
    void pressDigit(String digit) {
        calculator.pressDigit(Integer.parseInt(digit));
    }

    @Given("the Enter key is pressed")
    @When("I press Enter")
    void pressEnter() {
        calculator.pressEnter();
    }

    @When("I press Add")
    void pressAdd() {
        calculator.pressAdd();
    }

    @Then("the displayed result should be '(.*?)'")
    void assertDisplayedResult(String displayedResult) {
        assertThat(calculator.getResult()).isEqualTo(displayedResult);
    }

}
