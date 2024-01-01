package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class SimpleLogic {
    public Object test() {
       // (and true true (or false false (= 23 23)))
final var v__0 = true;
final var v__1 = true;
// (or false false (= 23 23))
final var v__2 = false;
final var v__3 = false;
// (= 23 23)
final var v__4 = 23;
final var v__5 = 23;
final var v__6 = org.homs.lechugascript.compiled.JRuntime.eq(v__4, v__5);
final var v__7 = org.homs.lechugascript.compiled.JRuntime.or(v__2, v__3, v__6);
final var v__8 = org.homs.lechugascript.compiled.JRuntime.and(v__0, v__1, v__7);

       return v__8;
    }
}