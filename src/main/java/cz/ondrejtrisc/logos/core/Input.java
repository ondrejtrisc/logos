package cz.ondrejtrisc.logos.core;

import cz.ondrejtrisc.logos.core.Output.Output;

public interface Input {

    String toString();

    default Function evaluate() {
        return evaluateAndOutputValue(null);
    }

    Function evaluateAndOutputValue(Output output);

    boolean isEmpty();
}
