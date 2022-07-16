package jucumber;

import jucumber.anno.Feature;
import jucumber.anno.FeatureDescription;
import jucumber.anno.Scenario;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class JucumberJUnit5Runner implements BeforeAllCallback, BeforeTestExecutionCallback {

    Jucumber jucumber = new Jucumber();

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (context.getTestClass().isPresent()) {
            var featureClass = context.getTestClass().get();
            if (featureClass.isAnnotationPresent(Feature.class)) {
                var featureAnno = featureClass.getAnnotation(Feature.class);
                System.out.println(Feature.class.getSimpleName() + ": " + featureAnno.value());
                if (featureClass.isAnnotationPresent(FeatureDescription.class)) {
                    var featureDesc = featureClass.getAnnotation(FeatureDescription.class);
                    System.out.println("Description: " + featureDesc.value());
                }
            }
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        if (context.getTestInstance().isPresent() && context.getTestMethod().isPresent()) {
            var method = context.getTestMethod().get();
            var featureObject = context.getTestInstance().get();
            if (method.isAnnotationPresent(Scenario.class)) {
                var scenarioAnno = method.getAnnotation(Scenario.class);

                System.out.println("\n" + Scenario.class.getSimpleName() + ": " + scenarioAnno.value());

                jucumber.executeScenario(featureObject, method);
            }
        }
    }
}


