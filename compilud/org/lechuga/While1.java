package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class While1 {
    public Object test() {
       // (def i 0)
// (def i 0)
final var v__0 = 0;
Object i = v__0;
// (while (< i 5) (set i (+ i 1)))
// (while (< i 5) (set i (+ i 1)))
Object v__1 = null;
while (true) {
    // (< i 5)
    final var v__2 = i;
    final var v__3 = 5;
    final var v__4 = org.homs.lechugascript.compiled.JRuntime.lt(v__2, v__3);
    if (!v__4) {break;}
    // (set i (+ i 1))
    // (set i (+ i 1))
    // (+ i 1)
    final var v__5 = i;
    final var v__6 = 1;
    final var v__7 = org.homs.lechugascript.compiled.JRuntime.add(v__5, v__6);
    i = v__7;
    v__1 = i;
}

       return v__1;
    }
}