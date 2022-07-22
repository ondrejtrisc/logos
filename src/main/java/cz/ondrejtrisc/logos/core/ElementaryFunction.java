package cz.ondrejtrisc.logos.core;

import cz.ondrejtrisc.logos.core.Output.Output;
import cz.ondrejtrisc.logos.service.PriceDataService;

import java.util.ArrayList;
import java.util.HashMap;

public class ElementaryFunction implements Function {

    //for tradebot simulation
    private static PriceDataService priceDataService;
    private static HashMap<String, Double> portfolio;

    private final String name;

    public ElementaryFunction(String name) {
        this.name = name;
    }

    public static void setPriceDataService(PriceDataService priceDataService) {
        ElementaryFunction.priceDataService = priceDataService;
    }

    public static void setPortfolio(HashMap<String, Double> portfolio) {
        ElementaryFunction.portfolio = portfolio;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    @Override
    public Function copy() {
        return this;
    }

    @Override
    public Function evaluateAndOutputValue(ArrayList<? extends Input> inputs, Output output) {
        try {
            String retString;
            switch (name) {
                case "[]":
                    ArrayList<Function> items = new ArrayList<>();
                    for (Input input : inputs) {
                        items.add(input.evaluateAndOutputValue(output));
                    }
                    return new List(items);
                case "!":
                    if (inputs.get(0).evaluate().toString().equals("true")) {
                        retString = "false";
                    }
                    else if (inputs.get(0).evaluate().toString().equals("false")) {
                        retString = "true";
                    }
                    else {
                        throw new Exception();
                    }
                    break;
                case "&":
                    retString = "true";
                    for (Input input : inputs) {
                        if (input.evaluate().toString().equals("false")) {
                            retString = "false";
                            break;
                        }
                        else if (input.evaluate().toString().equals("true")) {
                            continue;
                        }
                        throw new Exception();
                    }
                    break;
                case "||":
                    retString = "false";
                    for (Input input : inputs) {
                        if (input.evaluate().toString().equals("true")) {
                            retString = "true";
                            break;
                        }
                        else if (input.evaluate().toString().equals("false")) {
                            continue;
                        }
                        throw new Exception();
                    }
                    break;
                case "=":
                    try {
                        String s = String.valueOf(Double.parseDouble(inputs.get(0).evaluate().toString()));
                        retString = "true";
                        for (int i = 1; i < inputs.size(); i++) {
                            if (!s.equals(String.valueOf(Double.parseDouble(inputs.get(i).evaluate().toString())))) {
                                retString = "false";
                                break;
                            }
                        }
                    }
                    catch (NumberFormatException e) {
                        String s = inputs.get(0).evaluate().toString();
                        retString = "true";
                        for (int i = 1; i < inputs.size(); i++) {
                            if (!s.equals(inputs.get(i).evaluate().toString())) {
                                retString = "false";
                                break;
                            }
                        }
                    }
                    break;
                case "<=":
                    double d1 = Double.parseDouble(inputs.get(0).evaluate().toString());
                    double d2 = Double.parseDouble(inputs.get(1).evaluate().toString());
                    if (d1 <= d2) {
                        retString = "true";
                    }
                    else {
                        retString = "false";
                    }
                    break;
                case ">=":
                    d1 = Double.parseDouble(inputs.get(0).evaluate().toString());
                    d2 = Double.parseDouble(inputs.get(1).evaluate().toString());
                    if (d1 >= d2) {
                        retString = "true";
                    }
                    else {
                        retString = "false";
                    }
                    break;
                case "<":
                    d1 = Double.parseDouble(inputs.get(0).evaluate().toString());
                    d2 = Double.parseDouble(inputs.get(1).evaluate().toString());
                    if (d1 < d2) {
                        retString = "true";
                    }
                    else {
                        retString = "false";
                    }
                    break;
                case ">":
                    d1 = Double.parseDouble(inputs.get(0).evaluate().toString());
                    d2 = Double.parseDouble(inputs.get(1).evaluate().toString());
                    if (d1 > d2) {
                        retString = "true";
                    }
                    else {
                        retString = "false";
                    }
                    break;
                case "+":
                    Double retNum = 0.0;
                    for (Input input : inputs) {
                        retNum += Double.parseDouble(input.evaluate().toString());
                    }
                    retString = String.valueOf(retNum);
                    break;
                case "-":
                    retString = String.valueOf(Double.parseDouble(inputs.get(0).evaluate().toString()) - Double.parseDouble(inputs.get(1).evaluate().toString()));
                    break;
                case "*":
                    retNum = 1.0;
                    for (Input input : inputs) {
                        retNum *= Double.parseDouble(input.evaluate().toString());
                    }
                    retString = String.valueOf(retNum);
                    break;
                case "/":
                    double denominator = Double.parseDouble(inputs.get(1).evaluate().toString());
                    if (denominator == 0) {
                        throw new Exception();
                    }
                    else {
                        retString = String.valueOf(Double.parseDouble(inputs.get(0).evaluate().toString()) / denominator);
                    }
                    break;
                case "pow":
                    double base = Double.parseDouble(inputs.get(0).evaluate().toString());
                    double exponent = Double.parseDouble(inputs.get(1).evaluate().toString());
                    retNum = Math.pow(base, exponent);
                    if (retNum.isNaN()) {
                        throw new Exception();
                    }
                    retString = String.valueOf(retNum);
                    break;
                case "root":
                    double degree = Double.parseDouble(inputs.get(0).evaluate().toString());
                    double radicand = Double.parseDouble(inputs.get(1).evaluate().toString());
                    retNum = Math.pow(radicand, 1 / degree);
                    if (retNum.isNaN()) {
                        throw new Exception();
                    }
                    retString = String.valueOf(retNum);
                    break;
                case "log":
                    base = Double.parseDouble(inputs.get(0).evaluate().toString());
                    double antiLogarithm = Double.parseDouble(inputs.get(1).evaluate().toString());
                    retNum = Math.log(antiLogarithm) / Math.log(base);
                    if (retNum.isNaN()) {
                        throw new Exception();
                    }
                    retString = String.valueOf(retNum);
                    break;
                case "sin":
                    double angle = Double.parseDouble(inputs.get(0).evaluate().toString());
                    retNum = Math.sin(angle);
                    if (retNum.isNaN()) {
                        throw new Exception();
                    }
                    retString = String.valueOf(retNum);
                    break;
                case "cos":
                    angle = Double.parseDouble(inputs.get(0).evaluate().toString());
                    retNum = Math.cos(angle);
                    if (retNum.isNaN()) {
                        throw new Exception();
                    }
                    retString = String.valueOf(retNum);
                    break;
                case "tan":
                    angle = Double.parseDouble(inputs.get(0).evaluate().toString());
                    retNum = Math.tan(angle);
                    if (retNum.isNaN()) {
                        throw new Exception();
                    }
                    retString = String.valueOf(retNum);
                    break;
                case "++":
                    StringBuilder retStringBuilder = new StringBuilder();
                    for (Input input : inputs) {
                        String childString = input.evaluate().toString();
                        if (childString.length() < 2 || childString.charAt(0) != '\"' || childString.charAt(childString.length() - 1) != '\"') {
                            throw new Exception();
                        }
                        childString = childString.substring(1, childString.length() - 1);
                        retStringBuilder.append(childString);
                    }
                    retString = "\"" + retStringBuilder + "\"";
                    break;
                case "if":
                    if (inputs.get(0).evaluate().toString().equals("true")) {
                        return inputs.get(1).evaluateAndOutputValue(output);
                    }
                    else if (inputs.get(0).evaluate().toString().equals("false")) {
                        return inputs.get(2).evaluateAndOutputValue(output);
                    }
                    throw new Exception();
                case "map":
                    Function function = inputs.get(0).evaluate();
                    List list = (List) inputs.get(1).evaluate();
                    ArrayList<Function> results = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        Function result = function.evaluate(list.get(i).toNode());
                        if (output != null) {
                            output.out(result);
                        }
                        results.add(result);
                    }
                    return new List(results);
                case "filter":
                    function = inputs.get(0).evaluate();
                    list = (List) inputs.get(1).evaluate();
                    ArrayList<Function> filtered = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        Function item = list.get(i);
                        Function result = function.evaluate(item.toNode());
                        if (result.toString().equals("true")) {
                            filtered.add(item);
                        }
                        else if (!result.toString().equals("false")) {
                            throw new Exception();
                        }
                    }
                    if (output != null) {
                        for (Function result : filtered) {
                            output.out(result);
                        }
                    }
                    return new List(filtered);
                case "reduce":
                    function = inputs.get(0).evaluate();
                    list = (List) inputs.get(1).evaluate();
                    Function ret = inputs.get(2).evaluate();
                    for (int i = 0; i < list.size(); i++) {
                        ArrayList<Node> ins = new ArrayList<>();
                        ins.add(ret.toNode());
                        ins.add(list.get(i).toNode());
                        ret = function.evaluate(ins);
                    }
                    if (output != null) {
                        output.out(ret);
                    }
                    return ret;
                case "OWN":
                    Double own = portfolio.get(inputs.get(0).evaluate().toString());
                    if (own == null) {
                        own = 0.0;
                    }
                    retString = String.valueOf(own);
                    break;
                default:
                    retString = String.valueOf(priceDataService.findOpenByStockAndDate(name, inputs.get(0).evaluate().toString()));
            }
            try {
                double retNum = Double.parseDouble(retString);
                if (retNum == (int) retNum) {
                    retString = String.valueOf((int) retNum);
                }
            } catch (Exception ignored) {}
            Function ret = new ElementaryFunction(retString);
            if (output != null) {
                output.out(ret);
            }
            return ret;
        } catch (Exception e) {
            Function ret =  new ElementaryFunction("error");
            if (output != null) {
                output.out(ret);
            }
            return ret;
        }
    }
}
