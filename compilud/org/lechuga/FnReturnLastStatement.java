package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class FnReturnLastStatement {
    public Object test() {
       // (fn [] 1 2 3 4 5)
final var v__0 = (org.homs.lechugascript.compiled.Closure) args_v__0 -> {
    final var v__1 = 1;
    final var v__2 = 2;
    final var v__3 = 3;
    final var v__4 = 4;
    final var v__5 = 5;
    return v__5;
};
// ((fn [] 1 2 3 4 5))
final var v__6 = ((org.homs.lechugascript.compiled.Closure) v__0).apply();

       return v__6;
    }
}