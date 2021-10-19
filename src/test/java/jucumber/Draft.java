package jucumber;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

// @RunWith()
//@ExtendWith()
@Feature("Calculator")
@FeatureDescription("As a user, I want to use a calculator to add numbers, and it should work properly.")
public class Draft {

    private static void jacumber() throws Exception {
        StackTraceElement calleeStackFrame = getCalleeStackFrame();
        Class<?> featureClassName = Class.forName(calleeStackFrame.getClassName());
        Method scenarioMethod = featureClassName.getMethod(calleeStackFrame.getMethodName());

        Object feature = featureClassName.getConstructor().newInstance();

        if (scenarioMethod.isAnnotationPresent(Given.class)) {
            Given given = scenarioMethod.getAnnotation(Given.class);
            for (String predicate : given.value()) {
                executeStepMethod(feature, Given.class, predicate);
            }
        }
        if (scenarioMethod.isAnnotationPresent(When.class)) {
            When when = scenarioMethod.getAnnotation(When.class);
            for (String predicate : when.value()) {
                executeStepMethod(feature, When.class, predicate);
            }
        }
        if (scenarioMethod.isAnnotationPresent(Then.class)) {
            Then then = scenarioMethod.getAnnotation(Then.class);
            for (String predicate : then.value()) {
                executeStepMethod(feature, Then.class, predicate);
            }
        }
    }


    private static void executeStepMethod(Object feature, Class<? extends Annotation> annotationClass, String predicate) throws Exception {
        for (Method method : feature.getClass().getDeclaredMethods()) {
            if (//Modifier.isPublic(method.getModifiers()) &&
                    !method.isAnnotationPresent(Scenario.class) &&
                            method.isAnnotationPresent(annotationClass)) {

                Annotation stepAnnotation = method.getAnnotation(annotationClass);
                final String[] stepRegexps;
                if (Given.class.equals(stepAnnotation.annotationType())) {
                    stepRegexps = ((Given) stepAnnotation).value();
                } else if (When.class.equals(stepAnnotation.annotationType())) {
                    stepRegexps = ((When) stepAnnotation).value();
                } else if (Then.class.equals(stepAnnotation.annotationType())) {
                    stepRegexps = ((Then) stepAnnotation).value();
                } else {
                    throw new RuntimeException();//TODO
                }

                for (String stepRegexp : stepRegexps) {
                    Pattern p = Pattern.compile(stepRegexp);
                    Matcher m = p.matcher(predicate);
                    if (m.matches()) {
                        List<String> args = new ArrayList<>();
                        for (int i = 1; i <= m.groupCount(); i++) {
                            args.add(m.group(i));
                        }
                        method.invoke(feature, args.toArray());
                        return;
                    }
                }
            }
        }
        throw new RuntimeException();//TODO
    }

    private static StackTraceElement getCalleeStackFrame() {
        return new RuntimeException().getStackTrace()[2];
    }

    final Calculator calculator = new Calculator();

    @Test
    @Scenario("Verify the initial state")
    @When("the calculator is powered on")
    @Then("the displayed result should be '0'")
    public void test01InitialState() throws Exception {
        jacumber();
    }


    @Test
    @Scenario("Press a single digit")
    @Given("a calculator in the initial state")
    @When("I enter the digit 2")
    @Then("the displayed result should be '2'")
    public void test02EnterSingleDigit() throws Exception {
        jacumber();
    }

    @Test
    @Scenario("Enter a number")
    @Given("a calculator in the initial state")
    @When({"I enter the digit 1",
            "I enter the digit 2",
            "I enter the digit 3"})
    @Then("the displayed result should be '123'")
    public void test03PressANumber() throws Exception {
        jacumber();
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
        jacumber();
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
        jacumber();
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
