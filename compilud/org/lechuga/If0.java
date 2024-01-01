package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class If0 {
    public Object test() {
       // (if (= 2 2) 3)
// (if (= 2 2) 3)
Object v__0 = null;
// (= 2 2)
final var v__1 = 2;
final var v__2 = 2;
final var v__3 = org.homs.lechugascript.compiled.JRuntime.eq(v__1, v__2);
if (v__3) {
    final var v__4 = 3;
    v__0 = v__4;
}

       return v__0;
    }
}