package org.homs.lechugatemplates;

import org.homs.lechugascript.Interpreter;
import org.homs.lechugascript.parser.ast.Ast;

import java.util.List;
import java.util.Map;

public class LechugaTemplate {

    final Interpreter interpreter;
    final List<Ast> asts;

    public LechugaTemplate(Interpreter interpreter, String templateNameDesc, String templateSources) {
        String lechugaScriptCode = new LechugaTemplateTranspiler().transpile(templateSources);
        this.interpreter = interpreter;
        this.asts = interpreter.parse(lechugaScriptCode, templateNameDesc);
    }

    public String render(Map<String, Object> model) {
        try {
            var env = interpreter.getStdEnvironment();
            for (var entry : model.entrySet()) {
                env.def(entry.getKey(), entry.getValue());
            }
            return String.valueOf(interpreter.evaluate(asts, env));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
