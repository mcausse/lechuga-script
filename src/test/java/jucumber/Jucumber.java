package jucumber;

import jucumber.anno.Given;
import jucumber.anno.Scenario;
import jucumber.anno.Then;
import jucumber.anno.When;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Jucumber {

    public void executeScenario(Object featureObject, Method scenarioMethod) throws Exception {
        if (scenarioMethod.isAnnotationPresent(Given.class)) {
            Given given = scenarioMethod.getAnnotation(Given.class);
            for (String predicate : given.value()) {
                executeStepMethod(featureObject, Given.class, predicate);
            }
        }
        if (scenarioMethod.isAnnotationPresent(When.class)) {
            When when = scenarioMethod.getAnnotation(When.class);
            for (String predicate : when.value()) {
                executeStepMethod(featureObject, When.class, predicate);
            }
        }
        if (scenarioMethod.isAnnotationPresent(Then.class)) {
            Then then = scenarioMethod.getAnnotation(Then.class);
            for (String predicate : then.value()) {
                executeStepMethod(featureObject, Then.class, predicate);
            }
        }
    }

    protected void executeStepMethod(Object feature, Class<? extends Annotation> annotationClass, String predicate) throws Exception {

        System.out.println(annotationClass.getSimpleName() + " " + predicate);

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
                    Pattern p = Pattern.compile(stepRegexp, Pattern.DOTALL);
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

}
