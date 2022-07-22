package cz.ondrejtrisc.logos.core;

import cz.ondrejtrisc.logos.core.ExternalInput.StandardInput;
import cz.ondrejtrisc.logos.core.Output.StandardOutput;
import cz.ondrejtrisc.logos.core.Expression.Expression;
import cz.ondrejtrisc.logos.core.Output.Output;

import java.util.ArrayList;

public interface Function {

    static Function parseFromString(String s) throws Exception {
        return Expression.parseFromString(s).toNode().definition;
    }

    String toString();

    Function copy();

    default Function evaluate(Input input) {
        return evaluateAndOutputValue(input, null);
    }

    default Function evaluate(ArrayList<? extends Input> inputs) {
        return evaluateAndOutputValue(inputs, null);
    }

    default Function evaluateAndOutputValue(Input input, Output output) {
        ArrayList<Input> inputs = new ArrayList<>();
        inputs.add(input);
        return this.evaluateAndOutputValue(inputs, output);
    }

    Function evaluateAndOutputValue(ArrayList<? extends Input> inputs, Output output);

    default Node toNode() {
        return new Node(new ArrayList<>(), this);
    }

    static void main(String[] args) throws Exception {
        String s = "{x. [\"What is your name?\", ++(\"Hello \", x)]}";
        Function function = parseFromString(s);
        StandardInput input = new StandardInput("standardInput");
        StandardOutput output = new StandardOutput();
        function.evaluateAndOutputValue(input, output);
    }
}
