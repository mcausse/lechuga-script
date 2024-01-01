package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class BasicDef2 {
    public Object test() {
       // (def pi 3.14159)
// (def pi 3.14159)
final var v__0 = 3.14159;
Object pi = v__0;
// (def pi2 (+ pi pi))
// (def pi2 (+ pi pi))
// (+ pi pi)
final var v__1 = pi;
final var v__2 = pi;
final var v__3 = org.homs.lechugascript.compiled.JRuntime.add(v__1, v__2);
Object pi2 = v__3;
final var v__4 = pi2;

       return v__4;
    }
}