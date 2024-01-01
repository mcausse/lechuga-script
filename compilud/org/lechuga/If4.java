package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class If4 {
    public Object test() {
       // (def jou (if (= 2 2) 3 4))
// (def jou (if (= 2 2) 3 4))
// (if (= 2 2) 3 4)
// (if (= 2 2) 3 4)
Object v__0 = null;
// (= 2 2)
final var v__1 = 2;
final var v__2 = 2;
final var v__3 = org.homs.lechugascript.compiled.JRuntime.eq(v__1, v__2);
if (v__3) {
    final var v__4 = 3;
    v__0 = v__4;
}
else {
    final var v__5 = 4;
    v__0 = v__5;
}
Object jou = v__0;
final var v__6 = jou;

       return v__6;
    }
}