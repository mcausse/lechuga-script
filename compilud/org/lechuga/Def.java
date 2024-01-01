package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class Def {
    public Object test() {
       // (+ (def con 2) con)
// (def con 2)
// (def con 2)
final var v__0 = 2;
Object con = v__0;
final var v__1 = con;
final var v__2 = org.homs.lechugascript.compiled.JRuntime.add(con, v__1);

       return v__2;
    }
}