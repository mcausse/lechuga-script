package org.homs.lechugatemplates;

import org.homs.lechugascript.parser.ast.Ast;

import java.util.List;

public class LechugaTemplate {

    final List<Ast> asts;

    public LechugaTemplate(List<Ast> asts) {
        this.asts = asts;
    }
}
