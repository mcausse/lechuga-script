package org.homs.lechugascript2025.runtime;

import org.homs.lechugascript2025.Callable;

public class ComparableFuncs {

    public static Callable eq = (env, args) -> {
        if (args.size() != 2) {
            throw new RuntimeException("required 2 args, but " + args.size());
        }

        Double a = ((Number) args.get(0)).doubleValue();
        Double b = ((Number) args.get(1)).doubleValue();

        return ComparableUtils.eq(a, b);
    };
    public static Callable ne = (env, args) -> {
        if (args.size() != 2) {
            throw new RuntimeException("required 2 args, but " + args.size());
        }

        Double a = ((Number) args.get(0)).doubleValue();
        Double b = ((Number) args.get(1)).doubleValue();

        return ComparableUtils.ne(a, b);
    };

    public static Callable lt = (env, args) -> {
        if (args.size() != 2) {
            throw new RuntimeException("required 2 args, but " + args.size());
        }

        Double a = ((Number) args.get(0)).doubleValue();
        Double b = ((Number) args.get(1)).doubleValue();

        return ComparableUtils.lt(a, b);
    };
    public static Callable le = (env, args) -> {
        if (args.size() != 2) {
            throw new RuntimeException("required 2 args, but " + args.size());
        }

        Double a = ((Number) args.get(0)).doubleValue();
        Double b = ((Number) args.get(1)).doubleValue();

        return ComparableUtils.le(a, b);
    };

    public static Callable gt = (env, args) -> {
        if (args.size() != 2) {
            throw new RuntimeException("required 2 args, but " + args.size());
        }

        Double a = ((Number) args.get(0)).doubleValue();
        Double b = ((Number) args.get(1)).doubleValue();

        return ComparableUtils.gt(a, b);
    };
    public static Callable ge = (env, args) -> {
        if (args.size() != 2) {
            throw new RuntimeException("required 2 args, but " + args.size());
        }

        Double a = ((Number) args.get(0)).doubleValue();
        Double b = ((Number) args.get(1)).doubleValue();

        return ComparableUtils.ge(a, b);
    };
}
