package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class FunctionPass {
    public Object test() {
       // (def apply (fn [f a b] (f a b)))
// (fn [f a b] (f a b))
final var v__0 = (org.homs.lechugascript.compiled.Closure) args_v__0 -> {
    final var f = args_v__0[0];
    final var a = args_v__0[1];
    final var b = args_v__0[2];
    // (f a b)
    final var v__2 = a;
    final var v__3 = b;
    final var v__1 = ((org.homs.lechugascript.compiled.Closure) f).apply(v__2, v__3);
    return v__1;
};
var apply = v__0;
// (apply (fn [a b] (+ a b)) 2 3)
// (fn [a b] (+ a b))
final var v__5 = (org.homs.lechugascript.compiled.Closure) args_v__5 -> {
    final var a = args_v__5[0];
    final var b = args_v__5[1];
    final var v__6 = a;
    final var v__7 = b;
    final var v__8 = org.homs.lechugascript.compiled.JRuntime.add(v__6, v__7);
    return v__8;
};
final var v__9 = 2;
final var v__10 = 3;
final var v__4 = ((org.homs.lechugascript.compiled.Closure) apply).apply(v__5, v__9, v__10);

       return v__4;
    }
}