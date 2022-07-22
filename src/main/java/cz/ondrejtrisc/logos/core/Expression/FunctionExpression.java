package cz.ondrejtrisc.logos.core.Expression;

import cz.ondrejtrisc.logos.core.*;

import java.util.ArrayList;
import java.util.Collections;

public class FunctionExpression implements Expression {

    private final ArrayList<String> parameters;
    private final Expression body;

    public FunctionExpression(ArrayList<String> parameters, Expression body) {
        this.parameters = parameters;
        this.body = body;
    }

    static FunctionExpression parseFromProcessedString(String s) throws Exception {
        if (s.length() < 3 || s.charAt(0) != '{' || s.charAt(s.length() - 1) != '}') {
            throw new Exception();
        }

        try { //no parameters
            Expression body = Expression.parseFromProcessedString(s.substring(1, s.length() - 1));
            return new FunctionExpression(new ArrayList<>(), body);
        } catch (Exception e) {
            if (!s.contains(".")) {
                throw new Exception();
            }

            //parse the head
            String headString = s.substring(1, s.indexOf('.'));
            ArrayList<String> parameters = new ArrayList<>();
            Collections.addAll(parameters, headString.split(",", -1));
            for (String parameter : parameters) {
                try {
                    Double.parseDouble(parameter);
                    throw new Exception();
                } catch (Exception e2) {
                    for (String reservedString : reservedStrings) {
                        if (parameter.isEmpty() || parameter.contains(reservedString)) {
                            throw new Exception();
                        }
                    }
                }
            }

            //parse the body
            String bodyString = s.substring(s.indexOf('.') + 1, s.length() - 1);
            Expression body = Expression.parseFromProcessedString(bodyString);
            return new FunctionExpression(parameters, body);
        }
    }

    @Override
    public String toString() {

        //expression look like: {+(2, 3)}
        if (parameters.size() == 0) {
            return "{" + body.toString() + "}";
        }

        //expression look like: {x. +(2, x)}
        if (parameters.size() == 1) {
            return "{" + parameters.get(0) + ". " + body.toString() + "}";
        }

        //expression look like: {x, y. +(x, y)}
        StringBuilder parametersString = new StringBuilder(parameters.get(0));
        for (int i = 1; i < parameters.size(); i++) {
            parametersString.append(", ").append(parameters.get(i));
        }
        return "{" + parametersString + ". " + body.toString() + "}";
    }

    @Override
    public Node toNode() {
        ComposedFunction definition = this.toFunction();
        Node ret = new Node(new ArrayList<>(), definition);
        ret.referenceParameters(definition);
        return ret;
    }

    public ComposedFunction toFunction() {
        ArrayList<Parameter> retParameters = new ArrayList<>();
        for (String parameter : parameters) {
            retParameters.add(new Parameter(parameter));
        }
        Node root = body.toNode();
        return new ComposedFunction(retParameters, root);
    }
}
