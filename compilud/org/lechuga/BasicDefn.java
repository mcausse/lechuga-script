package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class BasicDefn {
    public Object test() {
       // (def sum (fn [a b] (+ a b)))
// (def sum (fn [a b] (+ a b)))
// (fn [a b] (+ a b))
// (fn [a b] (+ a b))
final var v__0 = (org.homs.lechugascript.compiled.Closure) args_v__0 -> {
    final var a = args_v__0[0];
    final var b = args_v__0[1];
    // (+ a b)
    final var v__1 = a;
    final var v__2 = b;
    final var v__3 = org.homs.lechugascript.compiled.JRuntime.add(v__1, v__2);
    return v__3;
};
Object sum = v__0;
// (sum 2 3)
final var v__5 = 2;
final var v__6 = 3;
final var v__4 = ((org.homs.lechugascript.compiled.Closure) sum).apply(v__5, v__6);

       return v__4;
    }
}