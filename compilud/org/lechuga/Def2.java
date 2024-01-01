package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class Def2 {
    public Object test() {
       // (def a (def b 5))
// (def b 5)
final var v__0 = 5;
var b = v__0;
var a = b;
final var v__1 = a;
final var v__2 = b;
final var v__3 = org.homs.lechugascript.compiled.JRuntime.add(v__1, v__2);

       return v__3;
    }
}