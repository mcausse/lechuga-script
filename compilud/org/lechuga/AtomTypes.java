package org.lechuga; import org.homs.lechugascript.compiled.Closure;
public class AtomTypes {
    public Object test() {
       // [1 2.0 true false null "jou" []]
var v__0 = new java.util.ArrayList<>();
{
    final var v__1 = 1;
    v__0.add(v__1);
    final var v__2 = 2.0;
    v__0.add(v__2);
    final var v__3 = true;
    v__0.add(v__3);
    final var v__4 = false;
    v__0.add(v__4);
    final Object v__5 = null;
    v__0.add(v__5);
    final var v__6 = "jou";
    v__0.add(v__6);
    // []
    var v__7 = new java.util.ArrayList<>();
    {
    }
    v__0.add(v__7);
}

       return v__0;
    }
}