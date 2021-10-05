package org.homs.lispo.util;

/**
 * <ul>
 * <li>negative integer - less than
 * <li>zero - equal to
 * <li>positive integer - greater than
 * </ul>
 *
 * @see Comparable#compareTo(Object)
 */
public class ComparableUtils {

    public static <T extends Comparable<T>> boolean eq(T a, T b) {
        return a.compareTo(b) == 0;
    }

    public static <T extends Comparable<T>> boolean ne(T a, T b) {
        return a.compareTo(b) != 0;
    }

    public static <T extends Comparable<T>> boolean gt(T a, T b) {
        return a.compareTo(b) > 0;
    }

    public static <T extends Comparable<T>> boolean ge(T a, T b) {
        return a.compareTo(b) >= 0;
    }

    public static <T extends Comparable<T>> boolean lt(T a, T b) {
        return a.compareTo(b) < 0;
    }

    public static <T extends Comparable<T>> boolean le(T a, T b) {
        return a.compareTo(b) <= 0;
    }

}
