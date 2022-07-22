package cz.ondrejtrisc.logos.core.ExternalInput;

import cz.ondrejtrisc.logos.core.Function;
import cz.ondrejtrisc.logos.core.Input;
import cz.ondrejtrisc.logos.core.Output.Output;

public abstract class ExternalInput implements Input {

    String name;
    Function value;

    abstract Function in();

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Function evaluateAndOutputValue(Output output) {
        if (value == null) {
            value = in();
        }
        if (output != null) {
            output.out(value);
        }
        return value;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
