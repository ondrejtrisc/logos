package cz.ondrejtrisc.logos.core;

import cz.ondrejtrisc.logos.core.Output.Output;

public class Parameter {

    private final String name;
    private Input substituent;
    private Function argument;

    public Parameter(String name) {
        this.name = name;
        this.substituent = null;
        this.argument = null;
    }

    public String getName() {
        return name;
    }

    public Input getSubstituent() {
        return substituent;
    }

    public void setSubstituent(Input substituent) {
        this.substituent = substituent;
    }

    public Function getArgument() {
        return argument;
    }

    @Override
    public String toString() {
        if (substituent != null) {
            return substituent.toString();
        }
        return name;
    }

    public Function evaluate() {
        return evaluateAndOutputValue(null);
    }

    public Function evaluateAndOutputValue(Output output) {
        argument = substituent.evaluateAndOutputValue(output);
        return argument;
    }
}
