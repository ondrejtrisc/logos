package cz.ondrejtrisc.logos.core;

import cz.ondrejtrisc.logos.core.Output.Output;

import java.util.ArrayList;

public class ComposedFunction implements Function {

    private ArrayList<Parameter> parameters;
    private Node root;

    public ComposedFunction(ArrayList<Parameter> parameters, Node root) {
        this.parameters = parameters;
        this.root = root;
    }

    public ArrayList<Parameter> getParameters() {
        return parameters;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    @Override
    public String toString() {
        if (parameters.isEmpty()) {
            return "{" + root.toString() + "}";
        }

        StringBuilder parametersString = new StringBuilder(parameters.get(0).getName());
        for (int i = 1; i < parameters.size(); i++) {
            parametersString.append(", ").append(parameters.get(i).getName());
        }
        return "{" + parametersString + ". " + root.toString() + "}";
    }

    @Override
    public Function copy() {
        try {
            return Function.parseFromString(this.toString());
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public Function evaluateAndOutputValue(ArrayList<? extends Input> inputs, Output output) {
        ArrayList<Integer> indicesOfSubstitution = new ArrayList<>();
        for (int i = 0; i < Math.min(parameters.size(), inputs.size()); i++) {
            if (!inputs.get(i).isEmpty()) {
                indicesOfSubstitution.add(i);
            }
        }

        //all parameters are given substituents
        if (parameters.size() == indicesOfSubstitution.size()) {
            for (int i = 0; i < parameters.size(); i++) {
                parameters.get(i).setSubstituent(inputs.get(i));
            }
            return root.evaluateAndOutputValue(output);
        }

        //not all parameters are given substituents
        ComposedFunction ret = (ComposedFunction) this.copy();
        for (int i = 0; i < ret.parameters.size(); i++) {
            if (indicesOfSubstitution.contains(i)) {
                ret.getParameters().get(i).setSubstituent(inputs.get(i));
            }
        }
        ret.getRoot().substitute(ret, indicesOfSubstitution);
        for (Parameter parameter : ret.parameters) {
            parameter.setSubstituent(null);
        }
        ArrayList<Parameter> newParameters = new ArrayList<>();
        for (int i = 0; i < ret.parameters.size(); i++) {
            if (!indicesOfSubstitution.contains(i)) {
                newParameters.add(ret.parameters.get(i));
            }
        }
        ret.parameters = newParameters;
        if (output != null) {
            output.out(ret);
        }
        return ret;
    }
}
