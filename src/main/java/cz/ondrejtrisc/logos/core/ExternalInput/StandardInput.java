package cz.ondrejtrisc.logos.core.ExternalInput;

import cz.ondrejtrisc.logos.core.Function;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class StandardInput extends ExternalInput {

    public StandardInput(String name) {
        this.name = name;
    }

    @Override
    Function in() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        try {
            line = reader.readLine();
        }
        catch (Exception e) {
            line = "inputError";
        }
        try {
            Integer.parseInt(line);
            return Function.parseFromString(line);
        }
        catch (Exception e) {
            try {
                Double.parseDouble(line);
                return Function.parseFromString(line);
            } catch (Exception e2) {
                try {
                    if (line.equals("true") || line.equals("false")) {
                        return Function.parseFromString(line);
                    }
                    return Function.parseFromString("\"" + line + "\"");
                } catch (Exception e3) {
                    return null;
                }
            }
        }
    }
}
