package org.homs.lispo.eval;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtils {

    static String getGetterName(final String propName) {
        if (Character.isUpperCase(propName.charAt(0)) || propName.length() > 1
                && Character.isUpperCase(propName.charAt(1))) {
            return "get" + propName;
        }
        return "get" + Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
    }

    static String getIsGetterName(final String propName) {
        if (Character.isUpperCase(propName.charAt(0)) || propName.length() > 1
                && Character.isUpperCase(propName.charAt(1))) {
            return "is" + propName;
        }
        return "is" + Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
    }

    public static Object get(final Object targetBean, final String field) {
        PropertyAccessor propertyAccessor;
        try {
            Method m = targetBean.getClass().getMethod(getGetterName(field));
            propertyAccessor = new PropertyAccessor(m);
        } catch (final Exception e) {
            try {
                Method m = targetBean.getClass().getMethod(getIsGetterName(field));
                propertyAccessor = new PropertyAccessor(m);
            } catch (final Exception e1) {
                try {
                    Field f = targetBean.getClass().getField(field);
                    propertyAccessor = new PropertyAccessor(f);
                } catch (final Exception e2) {
                    throw new RuntimeException("not found accessor for: " + targetBean + "#" + field, e2);
                }
            }
        }
        return propertyAccessor.get(targetBean);
    }
}

