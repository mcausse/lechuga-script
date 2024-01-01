package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class BasicDef3 {
    public Object test() {
       // (def a 2)
final var v__0 = 2;
var a = v__0;
// (def b 3)
final var v__1 = 3;
var b = v__1;
final var v__2 = a;
final var v__3 = b;
final var v__4 = org.homs.lechugascript.compiled.JRuntime.add(v__2, v__3);

       return v__4;
    }
}