package cz.ondrejtrisc.logos.core;

import cz.ondrejtrisc.logos.core.Expression.Expression;
import cz.ondrejtrisc.logos.core.Expression.TreeExpression;
import cz.ondrejtrisc.logos.core.Output.Output;

import java.util.ArrayList;

public class ParametricNode extends Node {

    private Parameter parameter;

    public ParametricNode(ArrayList<Node> children, Parameter parameter) {
        super(children, null);
        this.parameter = parameter;
    }

    @Override
    public void referenceParameters(ComposedFunction context) {
        //reference parameters in substituent
        if (parameter.getSubstituent() instanceof Node) {
            Node substituent = (Node) parameter.getSubstituent();
            if (substituent.definition instanceof ElementaryFunction) {
                String name = ((ElementaryFunction) substituent.definition).getName();
                for (Parameter contextParameter : context.getParameters()) {
                    if (name.equals(contextParameter.getName())) {
                        parameter.setSubstituent(substituent.toParametricNode(contextParameter));
                        break;
                    }
                }
            }
            ((Node) parameter.getSubstituent()).referenceParameters(context);
        }

        super.referenceParameters(context);
    }

    @Override
    public String toString() {
        if (children.isEmpty()) {
            return parameter.toString();
        }

        StringBuilder childrenString = new StringBuilder(children.get(0).toString());
        for (int i = 1; i < children.size(); i++) {
            childrenString.append(", ").append(children.get(i).toString());
        }

        //substituent is a node other than just list
        if (parameter.getName().equals("")
            && !(
                parameter.getSubstituent() instanceof Node
                && ((Node) parameter.getSubstituent()).getDefinition() instanceof ElementaryFunction
                && ((ElementaryFunction) ((Node) parameter.getSubstituent()).getDefinition()).getName().equals("[]")
            ) && !(parameter.getArgument() instanceof List)
        ) {
            return "(" + parameter + ")" + "(" + childrenString + ")";
        }

        return parameter + "(" + childrenString + ")";
    }

    @Override
    public Function evaluateAndOutputValue(Output output) {
        if (children.isEmpty()) {
            definition = parameter.evaluateAndOutputValue(output);
            value = definition;
            return value;
        }

        definition = parameter.evaluate().copy();
        value = definition.evaluateAndOutputValue(children, output);
        return value;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void substitute(ComposedFunction context, ArrayList<Integer> indicesOfSubstitution) {
        if (context.getParameters().contains(parameter) && indicesOfSubstitution.contains(context.getParameters().indexOf(parameter))) {
            Input substituent = parameter.getSubstituent();
            parameter = new Parameter("");
            parameter.setSubstituent(substituent);
        }
        else if (parameter.getSubstituent() instanceof Node) {
            ((Node) parameter.getSubstituent()).substitute(context, indicesOfSubstitution);
        }

        super.substitute(context, indicesOfSubstitution);
    }

    @Override
    public ArrayList<String> writeEvaluation(ArrayList<String> previousPart) {
        if (definition instanceof ComposedFunction) {
            ArrayList<Integer> indicesOfSubstitution = new ArrayList<>();
            for (int i = 0; i < Math.min(((ComposedFunction) definition).getParameters().size(), children.size()); i++) {
                if (!children.get(i).isEmpty()) {
                    indicesOfSubstitution.add(i);
                }
            }

            if (((ComposedFunction) definition).getParameters().size() == indicesOfSubstitution.size()) {
                previousPart.addAll(this.writeSubstituentEvaluation());

                if (!children.isEmpty()) {
                    return ((ComposedFunction) definition).getRoot().writeEvaluation(previousPart);
                }

                return previousPart;
            }

            if (children.isEmpty()) {
                previousPart.add(this.toString());
                return previousPart;
            }
        }
        else if (definition instanceof ElementaryFunction) {
            previousPart.addAll(this.writeSubstituentEvaluation());
            return previousPart;
        }

        previousPart.add(this.toString());

        if (value != null && !value.toString().equals(this.toString())) {
            previousPart.add(value.toString());
        }
        return previousPart;
    }

    public ArrayList<String> writeSubstituentEvaluation() {
        if (!(parameter.getSubstituent() instanceof Node)) {
            return new ArrayList<>();
        }

        ArrayList<String> substituentEvaluation = ((Node) parameter.getSubstituent()).writeEvaluation();

        if (children.isEmpty()) {
            return substituentEvaluation;
        }

        StringBuilder childrenString = new StringBuilder();
        for (int i = 0; i < children.size() - 1; i++) {
            childrenString.append(children.get(i).toString()).append(", ");
        }
        childrenString.append(children.get(children.size() - 1).toString());

        ArrayList<String> ret = new ArrayList<>();
        String rootString;
        for (String line : substituentEvaluation) {
            try {
                Expression expression = Expression.parseFromString(line);
                if (expression instanceof TreeExpression) {
                    rootString = "(" + line + ")";
                }
                else {
                    rootString = line;
                }
            } catch (Exception e) {
                rootString = "error";
            }
            ret.add(rootString + "(" + childrenString + ")");
        }

        return ret;
    }

    public boolean nontrivialEvaluation() {
        if (!children.isEmpty()) {
            return true;
        }
        if (parameter.getSubstituent() instanceof Node) {
            return ((Node) parameter.getSubstituent()).nontrivialEvaluation();
        }
        return false;
    }
}
