package org.homs.lechugatemplates;

public class LechugaTemplateTranspiler {

    final String beginExpression = "<%";
    final String endExpression = "%>";

    public String transpile(String template) {
        StringBuilder s = new StringBuilder();
        s.append("(def __strb__ (new :java.lang.StringBuilder))");

        int pos = 0;
        while (true) {

            int posBeginTag = template.indexOf(beginExpression, pos);
            if (posBeginTag < 0) {
                break;
            }

            s.append(transpileText(template.substring(pos, posBeginTag)));

            int posBeginExp = posBeginTag + beginExpression.length();
            int posEndExp = template.indexOf(endExpression, posBeginExp);
            if (posEndExp < 0) {
                throw new RuntimeException("expected " + endExpression + ", but eof");
            }

            String expression = template.substring(posBeginExp, posEndExp);
            s.append(transpileExpression(expression));

            pos = posEndExp + endExpression.length();
        }
        s.append(transpileText(template.substring(pos)));

        s.append("(__strb__ :toString)");
        return s.toString();
    }

    String transpileText(String text) {
        StringBuilder s = new StringBuilder();
        int i = 0;
        for (var line : text.split("\\n")) {
            if (line.isEmpty()) {
                i++;
                continue;
            }
            s.append("(__strb__ :append \"");
            if (i > 0) {
                s.append("\\n");
            }
            s.append(line
                    .replace("\"", "\\\"")
            );
            s.append("\")\n");
            i++;
        }
        return s.toString();
    }

    String transpileExpression(String expression) {
        if (expression.charAt(0) == '!') {
            return "";
        } else if (expression.charAt(0) == '=') {
            return "(__strb__ :append " + expression.substring(1) + ")";
        } else {
            return expression;
        }
    }
}
