package org.homs.lechugascript.util;

import org.homs.lechugascript.eval.Func;

import java.util.Arrays;
import java.util.List;

public class ArithmeticFuncs {

    private static final List<Class<? extends Number>> numericTypes = Arrays.asList(Byte.class, Short.class, Integer.class,
            Long.class, Float.class, Double.class);

    protected static int findMaxType(List<?> args) {
        int max = numericTypes.indexOf(Integer.class);
        for (Object arg : args) {
            int type = numericTypes.indexOf(arg.getClass());
            max = Math.max(max, type);
        }
        return max;
    }

    public static Func funcAdd = (tokenAt, ev, args) -> {
        int type = findMaxType(args);

        if (numericTypes.get(type) == Integer.class) {
            int r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.intValue();
                } else {
                    r = r + v.intValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Long.class) {
            long r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.longValue();
                } else {
                    r = r + v.longValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Float.class) {
            float r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.floatValue();
                } else {
                    r = r + v.floatValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Double.class) {
            double r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.doubleValue();
                } else {
                    r = r + v.doubleValue();
                }
            }
            return r;
        }
        throw new RuntimeException();
    };
    public static Func funcSub = (tokenAt, ev, args) -> {
        int type = findMaxType(args);

        if (numericTypes.get(type) == Integer.class) {
            int r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.intValue();
                } else {
                    r = r - v.intValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Long.class) {
            long r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.longValue();
                } else {
                    r = r - v.longValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Float.class) {
            float r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.floatValue();
                } else {
                    r = r - v.floatValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Double.class) {
            double r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.doubleValue();
                } else {
                    r = r - v.doubleValue();
                }
            }
            return r;
        }
        throw new RuntimeException();
    };
    public static Func funcMul = (tokenAt, ev, args) -> {
        int type = findMaxType(args);

        if (numericTypes.get(type) == Integer.class) {
            int r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.intValue();
                } else {
                    r = r * v.intValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Long.class) {
            long r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.longValue();
                } else {
                    r = r * v.longValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Float.class) {
            float r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.floatValue();
                } else {
                    r = r * v.floatValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Double.class) {
            double r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.doubleValue();
                } else {
                    r = r * v.doubleValue();
                }
            }
            return r;
        }
        throw new RuntimeException();
    };

    public static Func funcDiv = (tokenAt, ev, args) -> {
        int type = findMaxType(args);

        if (numericTypes.get(type) == Integer.class) {
            int r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.intValue();
                } else {
                    r = r / v.intValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Long.class) {
            long r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.longValue();
                } else {
                    r = r / v.longValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Float.class) {
            float r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.floatValue();
                } else {
                    r = r / v.floatValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Double.class) {
            double r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.doubleValue();
                } else {
                    r = r / v.doubleValue();
                }
            }
            return r;
        }
        throw new RuntimeException();
    };

    public static Func funcMod = (tokenAt, ev, args) -> {
        int type = findMaxType(args);

        if (numericTypes.get(type) == Integer.class) {
            int r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.intValue();
                } else {
                    r = r % v.intValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Long.class) {
            long r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.longValue();
                } else {
                    r = r % v.longValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Float.class) {
            float r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.floatValue();
                } else {
                    r = r % v.floatValue();
                }
            }
            return r;
        } else if (numericTypes.get(type) == Double.class) {
            double r = 0;
            for (int i = 0; i < args.size(); i++) {
                Number v = (Number) args.get(i);
                if (i == 0) {
                    r = v.doubleValue();
                } else {
                    r = r % v.doubleValue();
                }
            }
            return r;
        }
        throw new RuntimeException();
    };

    public static Func funcToByte = (tokenAt, ev, args) -> {
        Object arg = args.get(0);
        if (arg instanceof Number) {
            return ((Number) arg).byteValue();
        } else if (arg instanceof String) {
            return Byte.valueOf((String) arg);
        } else {
            throw new RuntimeException(
                    "expected a Number/String value, but received: " + arg + " " + tokenAt.toString());
        }
    };
    public static Func funcToShort = (tokenAt, ev, args) -> {
        Object arg = args.get(0);
        if (arg instanceof Number) {
            return ((Number) arg).shortValue();
        } else if (arg instanceof String) {
            return Short.valueOf((String) arg);
        } else {
            throw new RuntimeException(
                    "expected a Number/String value, but received: " + arg + " " + tokenAt.toString());
        }
    };
    public static Func funcToInt = (tokenAt, ev, args) -> {
        Object arg = args.get(0);
        if (arg instanceof Number) {
            return ((Number) arg).intValue();
        } else if (arg instanceof String) {
            return Integer.valueOf((String) arg);
        } else {
            throw new RuntimeException(
                    "expected a Number/String value, but received: " + arg + " " + tokenAt.toString());
        }
    };
    public static Func funcToLong = (tokenAt, ev, args) -> {
        Object arg = args.get(0);
        if (arg instanceof Number) {
            return ((Number) arg).longValue();
        } else if (arg instanceof String) {
            return Long.valueOf((String) arg);
        } else {
            throw new RuntimeException(
                    "expected a Number/String value, but received: " + arg + " " + tokenAt.toString());
        }
    };
    public static Func funcToFloat = (tokenAt, ev, args) -> {
        Object arg = args.get(0);
        if (arg instanceof Number) {
            return ((Number) arg).floatValue();
        } else if (arg instanceof String) {
            return Float.valueOf((String) arg);
        } else {
            throw new RuntimeException(
                    "expected a Number/String value, but received: " + arg + " " + tokenAt.toString());
        }
    };
    public static Func funcToDouble = (tokenAt, ev, args) -> {
        Object arg = args.get(0);
        if (arg instanceof Number) {
            return ((Number) arg).doubleValue();
        } else if (arg instanceof String) {
            return Double.valueOf((String) arg);
        } else {
            throw new RuntimeException(
                    "expected a Number/String value, but received: " + arg + " " + tokenAt.toString());
        }
    };
}
