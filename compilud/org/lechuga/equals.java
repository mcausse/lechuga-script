package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class equals {
    public Object test() {
       // (= 23 23 23)
final var v__0 = 23;
final var v__1 = 23;
final var v__2 = 23;
final var v__3 = org.homs.lechugascript.compiled.JRuntime.eq(v__0, v__1, v__2);

       return v__3;
    }
}