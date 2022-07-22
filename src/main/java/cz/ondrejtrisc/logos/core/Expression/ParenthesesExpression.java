package cz.ondrejtrisc.logos.core.Expression;

import cz.ondrejtrisc.logos.core.Node;
import cz.ondrejtrisc.logos.core.Parameter;
import cz.ondrejtrisc.logos.core.ParametricNode;

import java.util.ArrayList;

public class ParenthesesExpression implements Expression {

    private final Expression body;

    public ParenthesesExpression(Expression body) {
        this.body = body;
    }

    static ParenthesesExpression parseFromProcessedString(String s) throws Exception {
        if (s.length() < 3 || s.charAt(0) != '(' || s.charAt(s.length() - 1) != ')') {
            throw new Exception();
        }

        Expression body = Expression.parseFromProcessedString(s.substring(1, s.length() - 1));
        return new ParenthesesExpression(body);
    }

    @Override
    public String toString() {
        return "(" + body.toString() + ')';
    }

    @Override
    public Node toNode() {
        Parameter parameter = new Parameter("");
        parameter.setSubstituent(body.toNode());
        return new ParametricNode(new ArrayList<>(), parameter);
    }
}
