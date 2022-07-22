package cz.ondrejtrisc.logos.core.Expression;

import cz.ondrejtrisc.logos.core.ElementaryFunction;
import cz.ondrejtrisc.logos.core.Function;
import cz.ondrejtrisc.logos.core.Node;

import java.util.ArrayList;

public class SimpleExpression implements Expression {

    private final String content;

    public SimpleExpression(String content) {
        this.content = content;
    }

    static SimpleExpression parseFromProcessedString(String s) throws Exception {
        try {
            Double.parseDouble(s);
            return new SimpleExpression(s);
        } catch (Exception e) {
            String testString;
            if (s.length() > 2 && s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"') {
                testString = s.substring(1, s.length() - 1);
            }
            else {
                testString = s;
            }
            for (String reservedString : reservedStrings) {
                if (testString.contains(reservedString)) {
                    throw new Exception();
                }
            }
        }
        return new SimpleExpression(s);
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    public Node toNode() {
        Function definition = new ElementaryFunction(content);
        return new Node(new ArrayList<>(), definition);
    }
}
