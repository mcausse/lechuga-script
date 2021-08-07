package org.homs.lispo.eval;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PropertyAccessor {

    final String propertyName;

    final Field f;
    final Method m;

    public PropertyAccessor(Field f) {
        this.propertyName = f.getName();
        this.f = f;
        this.m = null;
    }

    public PropertyAccessor(Method m) {
        this.propertyName = m.getName();
        this.f = null;
        this.m = m;
    }

    public Object get(Object targetBean) {
        try {
            if (f != null) {
                return f.get(targetBean);
            } else {
                return m.invoke(targetBean);
            }
        } catch (Exception e) {
            throw new RuntimeException("accessing: " + targetBean + "#" + propertyName);
        }
    }
}