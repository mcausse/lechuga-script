package org.homs.lechugatemplates;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript.Interpreter;
import org.homs.lechugascript.parser.ast.Ast;

import java.util.List;
import java.util.Map;

public class LechugaTemplate {

    final String templateNameDesc;
    final Interpreter interpreter;
    final Environment env;
    final List<Ast> asts;

    public LechugaTemplate(Interpreter interpreter, String templateNameDesc, String templateSources) {
        String lechugaScriptCode = new LechugaTemplateTranspiler().transpile(templateSources);
        this.templateNameDesc = templateNameDesc;
        this.interpreter = interpreter;
        try {
            this.env = interpreter.getStdEnvironment();
            this.asts = interpreter.parse(lechugaScriptCode, templateNameDesc);
        } catch (Throwable t) {
            throw new RuntimeException("parsing: " + templateNameDesc, t);
        }
    }

    public String render(Map<String, Object> model) {
        try {
            var env = new Environment(this.env);
            for (var entry : model.entrySet()) {
                env.def(entry.getKey(), entry.getValue());
            }
            return String.valueOf(interpreter.evaluate(asts, env));
        } catch (Throwable t) {
            throw new RuntimeException("evaluating: " + templateNameDesc, t);
        }
    }
}
