package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class SumExpression2 {
    public Object test() {
       // (+ (+ 1 2) (+ 0 3))
// (+ 1 2)
final var v__0 = 1;
final var v__1 = 2;
final var v__2 = org.homs.lechugascript.compiled.JRuntime.add(v__0, v__1);
// (+ 0 3)
final var v__3 = 0;
final var v__4 = 3;
final var v__5 = org.homs.lechugascript.compiled.JRuntime.add(v__3, v__4);
final var v__6 = org.homs.lechugascript.compiled.JRuntime.add(v__2, v__5);

       return v__6;
    }
}