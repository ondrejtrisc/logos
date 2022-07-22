package cz.ondrejtrisc.logos.core.Output;

import cz.ondrejtrisc.logos.core.Function;

public class StandardOutput implements Output {

    @Override
    public void out(Function ret) {
        String retString = ret.toString();
        if (retString.length() > 2 && retString.charAt(0) == '\"' && retString.charAt(retString.length() - 1) == '\"') {
            retString = retString.substring(1, retString.length() - 1);
        }
        System.out.println(retString);
    }
}
