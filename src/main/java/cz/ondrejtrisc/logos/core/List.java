package cz.ondrejtrisc.logos.core;

import cz.ondrejtrisc.logos.core.Output.Output;

import java.util.ArrayList;

public class List implements Function {

    private final ArrayList<Function> items;

    public List(ArrayList<Function> items) {
        this.items = items;
    }

    public String toString() {
        if (items.isEmpty()) {
            return "[]";
        }

        StringBuilder itemsString = new StringBuilder(items.get(0).toString());
        for (int i = 1; i < items.size(); i++) {
            itemsString.append(", ").append(items.get(i).toString());
        }
        return "[" + itemsString + "]";
    }

    @Override
    public Function copy() {
        ArrayList<Function> ret = new ArrayList<>();
        for (Function item : items) {
            ret.add(item.copy());
        }
        return new List(ret);
    }

    @Override
    public Function evaluateAndOutputValue(ArrayList<? extends Input> inputs, Output output) {
        ArrayList<Function> ret = new ArrayList<>();
        for (Function item : items) {
            Function retItem = item.evaluate(inputs);
            ret.add(retItem);
            if (output != null) {
                output.out(retItem);
            }
        }
        return new List(ret);
    }

    public int size() {
        return items.size();
    }

    public Function get(int i) {
        return items.get(i);
    }
}
