package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class DirectFnCurry {
    public Object test() {
       // (fn [a] (fn [b] (+ a b)))
final var v__0 = (org.homs.lechugascript.compiled.Closure) args_v__0 -> {
    final var a = args_v__0[0];
    // (fn [b] (+ a b))
    final var v__1 = (org.homs.lechugascript.compiled.Closure) args_v__1 -> {
        final var b = args_v__1[0];
        final var v__2 = a;
        final var v__3 = b;
        final var v__4 = org.homs.lechugascript.compiled.JRuntime.add(v__2, v__3);
        return v__4;
    };
    return v__1;
};
// ((fn [a] (fn [b] (+ a b))) 2)
final var v__6 = 2;
final var v__5 = ((org.homs.lechugascript.compiled.Closure) v__0).apply(v__6);
// (((fn [a] (fn [b] (+ a b))) 2) 3)
final var v__8 = 3;
final var v__7 = ((org.homs.lechugascript.compiled.Closure) v__5).apply(v__8);

       return v__7;
    }
}