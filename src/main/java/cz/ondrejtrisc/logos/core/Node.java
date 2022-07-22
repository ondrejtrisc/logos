package cz.ondrejtrisc.logos.core;

import cz.ondrejtrisc.logos.core.Expression.Expression;
import cz.ondrejtrisc.logos.core.Output.Output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Node implements Input {

    ArrayList<Node> children;
    Function definition;
    Function value;

    public Node(ArrayList<Node> children, Function definition) {
        this.children = children;
        this.definition = definition;
        this.value = null;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public Function getDefinition() {
        return definition;
    }

    public static Node parseFromString(String s) throws Exception {
        return Expression.parseFromString(s).toNode();
    }

    public static ArrayList<String> evaluateAndGetEvaluation(String s) throws Exception {
        Node node = parseFromString(s);
        node.evaluate();
        return node.writeEvaluation();
    }

    public ParametricNode toParametricNode(Parameter parameter) {
        return new ParametricNode(this.children, parameter);
    }

    public void referenceParameters(ComposedFunction context) {
        //reference parameters in definition
        if (definition instanceof ComposedFunction) {
            Node root = ((ComposedFunction) definition).getRoot();
            if (root.definition instanceof ElementaryFunction) {
                String name = ((ElementaryFunction) root.definition).getName();
                for (Parameter parameter : context.getParameters()) {
                    if (name.equals(parameter.getName())) {
                        ((ComposedFunction) definition).setRoot(root.toParametricNode(parameter));
                        break;
                    }
                }
            }
            ((ComposedFunction) definition).getRoot().referenceParameters(context);
        }

        //reference parameters in children
        for (Node child : children) {
            if (child.definition instanceof ElementaryFunction) {
                String name = ((ElementaryFunction) child.definition).getName();
                for (Parameter parameter : context.getParameters()) {
                    if (name.equals(parameter.getName())) {
                        children.set(children.indexOf(child), child.toParametricNode(parameter));
                        break;
                    }
                }
            }
            child.referenceParameters(context);
        }
    }

    public String toString() {
        String rootString = definition.toString();
        if (definition instanceof ComposedFunction) {
            ArrayList<Input> inputs = new ArrayList<>();
            for (Parameter parameter : ((ComposedFunction) definition).getParameters()) {
                inputs.add(parameter.getSubstituent());
            }
            for (Parameter parameter : ((ComposedFunction) definition).getParameters()) {
                parameter.setSubstituent(null);
            }
            rootString = definition.toString();
            for (Parameter parameter : ((ComposedFunction) definition).getParameters()) {
                parameter.setSubstituent(inputs.get(((ComposedFunction) definition).getParameters().indexOf(parameter)));
            }
        }

        if (children.isEmpty()) {
            return rootString;
        }

        StringBuilder childrenString = new StringBuilder(children.get(0).toString());
        for (int i = 1; i < children.size(); i++) {
            childrenString.append(", ").append(children.get(i).toString());
        }

        if (definition instanceof ElementaryFunction && ((ElementaryFunction) definition).getName().equals("[]")) {
            return "[" + childrenString + "]";
        }

        return rootString + "(" + childrenString + ")";
    }

    public Function evaluateAndOutputValue(Output output) {
        if (children.isEmpty()) {
            value = definition;
            if (output != null) {
                output.out(value);
            }
            return value;
        }
        value = definition.evaluateAndOutputValue(children, output);
        return value;
    }

    public boolean isEmpty() {
        return (definition instanceof ElementaryFunction && ((ElementaryFunction) definition).getName().equals(""));
    }

    public void substitute(ComposedFunction context, ArrayList<Integer> indicesOfSubstitution) {
        if (definition instanceof ComposedFunction) {
            ((ComposedFunction) definition).getRoot().substitute(context, indicesOfSubstitution);
        }
        for (Node child : children) {
            child.substitute(context, indicesOfSubstitution);
        }
    }

    public ArrayList<String> writeEvaluation() {
        return writeEvaluation(new ArrayList<>());
    }

    public ArrayList<String> writeEvaluation(ArrayList<String> previousPart) {
        if (definition instanceof ComposedFunction) {
            ArrayList<Integer> indicesOfSubstitution = new ArrayList<>();
            for (int i = 0; i < Math.min(((ComposedFunction) definition).getParameters().size(), children.size()); i++) {
                if (!children.get(i).isEmpty()) {
                    indicesOfSubstitution.add(i);
                }
            }

            if (((ComposedFunction) definition).getParameters().size() == indicesOfSubstitution.size()) {
                previousPart.add(this.toString());
                return ((ComposedFunction) definition).getRoot().writeEvaluation(previousPart);
            }

            if (children.isEmpty()) {
                previousPart.add(this.toString());
                return previousPart;
            }
        }

        previousPart.add(this.toString());

        if (definition instanceof ElementaryFunction) {
            ArrayList<String> childrenValues = new ArrayList<>();
            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                if (child.value != null && child.nontrivialEvaluation()) {
                    ArrayList<String> childEvaluation = child.writeEvaluation();
                    for (int j = 1; j < childEvaluation.size(); j++) {
                        String line = childEvaluation.get(j);
                        StringBuilder retLine = new StringBuilder(definition.toString() + "(");
                        for (String childValue : childrenValues) {
                            retLine.append(childValue).append(", ");
                        }
                        retLine.append(line);
                        for (int k = i + 1; k < children.size(); k++) {
                            retLine.append(", ").append(children.get(k).toString());
                        }
                        retLine.append(")");

                        previousPart.add(retLine.toString());
                    }
                    childrenValues.add(child.value.toString());
                }
                else {
                    childrenValues.add(child.toString());
                }
            }
        }

        if (value != null && !value.toString().equals(this.toString())) {
            previousPart.add(value.toString());
        }
        else if (definition instanceof ElementaryFunction && !definition.toString().equals(this.toString())) {
            previousPart.add(definition.toString());
        }
        return previousPart;
    }

    public boolean nontrivialEvaluation() {
        return !children.isEmpty();
    }

    public static void main(String[] args) {
        boolean passed = true;

        HashMap<String, String> evaluationTests = new HashMap<>();
        evaluationTests.put("{a, x. {r, y. a({z. r(r, z)}, y)}({r, y. a({z. r(r, z)}, y)}, x)}({f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}, 5)", "120");
        evaluationTests.put("{x. [{y. +(x, y)}, {z. -(z, x)}]}(5, 6)", "[{y. +(5, y)}, {z. -(z, 5)}]");
        evaluationTests.put("(({x, y. x(y)}({z. z}))({a. a}))(2)", "2");
        evaluationTests.put("({a. {t. a(t(t))}({t. a(t(t))})}({f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}))(5)", "120");
        evaluationTests.put("{x. [x]}(5)", "[5]");
        evaluationTests.put("{x. x}(-(-(5, 1), 1))", "3");
        evaluationTests.put("{a. {b. +(a, b)}(2)}(3)", "5");
        evaluationTests.put("{a. a(2)}({b, c. +(b, c)}(3))", "5");
        evaluationTests.put("{a, b, c, d, e, f. a(b(c, d), e(f))}(, , 2, , , 3, , 5)", "{a, b, d, e. a(b(2, d), e(3))}");
        evaluationTests.put("+(2, 3)", "5");
        evaluationTests.put("test(+(2, 3))", "error");
        evaluationTests.put("[]", "[]");
        evaluationTests.put("[](test)", "[]");
        evaluationTests.put("[[[5, 6]]]", "[[[5, 6]]]");
        evaluationTests.put("[+(+(1, 1), -(4, 1)), -(2, 3)]", "[5, -1]");
        evaluationTests.put("{x. x}([5, 6])", "[5, 6]");
        evaluationTests.put("{x. x}([[[5, 6]]])", "[[[5, 6]]]");
        evaluationTests.put("{y, z. [y, z]}([2, 3], 4)", "[[2, 3], 4]");
        evaluationTests.put("{x. {y, z. [y, z]}(x, 4)}([2, 3])", "[[2, 3], 4]");
        evaluationTests.put("{x, y. [x, y]}(2, 3)", "[2, 3]");
        evaluationTests.put("{x, y. [x, y]}([2, 3])", "{y. [[2, 3], y]}");
        evaluationTests.put("map({x. +(2, x)}, [2, 3, 4, 5])", "[4, 5, 6, 7]");
        evaluationTests.put("filter({x. =(x, 2)}, [1, 2, 3])", "[2]");
        evaluationTests.put("filter({x. =(x, 2)}, [1, 2, 3, 2])", "[2, 2]");
        evaluationTests.put("reduce({x, y. +(x, y)}, [1, 2, 3, 4, 5], 0)", "15");
        evaluationTests.put("{x. (x(2))(3)}({y, z. +(y, z)})", "5");
        evaluationTests.put("[{x. x}, {x. +(x, 2)}](5)", "[5, 7]");
        evaluationTests.put("[{x. +(x, 2)}, {x. -(x, 2)}](3)", "[5, 1]");
        evaluationTests.put("[+, *](2, 3)", "[5, 6]");
        evaluationTests.put("{x. x}([+, *](2, 3))", "[5, 6]");
        evaluationTests.put("{x. x}([{y, z. +(y, z)}, {y, z. *(y, z)}](2, 3))", "[5, 6]");
        evaluationTests.put("&(!(false), ||(true, false), =(0, 0.0), <(5, 6), >(3.0, 2.0))", "true");
        evaluationTests.put("&(true, false)", "false");
        evaluationTests.put("if(true, 1, 2)", "1");
        evaluationTests.put("if(false, 1, 2)", "2");

        for (Map.Entry test : evaluationTests.entrySet()) {
            String expressionString = (String) test.getKey();
            try {
                Node node = parseFromString(expressionString);
                String valueString = node.evaluate().toString();
                if (!valueString.equals(evaluationTests.get(expressionString))) {
                    passed = false;
                    System.out.println(expressionString + " -> " + valueString);
                }
                try {
                    node.writeEvaluation();
                } catch (Exception e2) {
                    passed = false;
                    System.out.println(expressionString + " -> " + "evaluation writing failed");
                }
            }
            catch (Exception e) {
                passed = false;
                System.out.println(expressionString + " -> " + "evaluation failed");
            }
        }

        HashMap<String, String[]> evaluationWritingTests = new HashMap<>();
        evaluationWritingTests.put("({a. {t. a(t(t))}({t. a(t(t))})}({f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}))(5)", new String[] {
                "({a. {t. a(t(t))}({t. a(t(t))})}({f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}))(5)",
                "({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(5)",
                "({f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))})))(5)",
                "{n. if(=(n, 0), 1, *(n, ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(n, 1))))}(5)",
                "if(=(5, 0), 1, *(5, ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(5, 1))))",
                "if(false, 1, *(5, ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(5, 1))))",
                "if(false, 1, *(5, ({f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))})))(-(5, 1))))",
                "if(false, 1, *(5, {n. if(=(n, 0), 1, *(n, ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(n, 1))))}(-(5, 1))))",
                "if(false, 1, *(5, if(=(-(5, 1), 0), 1, *(-(5, 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(5, 1), 1))))))",
                "if(false, 1, *(5, if(=(4, 0), 1, *(-(5, 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(5, 1), 1))))))",
                "if(false, 1, *(5, if(false, 1, *(-(5, 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(5, 1), 1))))))",
                "if(false, 1, *(5, if(false, 1, *(4, ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(5, 1), 1))))))",
                "if(false, 1, *(5, if(false, 1, *(4, ({f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))})))(-(-(5, 1), 1))))))",
                "if(false, 1, *(5, if(false, 1, *(4, {n. if(=(n, 0), 1, *(n, ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(n, 1))))}(-(-(5, 1), 1))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(=(-(-(5, 1), 1), 0), 1, *(-(-(5, 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(5, 1), 1), 1))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(=(-(4, 1), 0), 1, *(-(-(5, 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(5, 1), 1), 1))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(=(3, 0), 1, *(-(-(5, 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(5, 1), 1), 1))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(-(-(5, 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(5, 1), 1), 1))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(-(4, 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(5, 1), 1), 1))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(5, 1), 1), 1))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, ({f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))})))(-(-(-(5, 1), 1), 1))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, {n. if(=(n, 0), 1, *(n, ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(n, 1))))}(-(-(-(5, 1), 1), 1))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(=(-(-(-(5, 1), 1), 1), 0), 1, *(-(-(-(5, 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(5, 1), 1), 1), 1))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(=(-(-(4, 1), 1), 0), 1, *(-(-(-(5, 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(5, 1), 1), 1), 1))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(=(-(3, 1), 0), 1, *(-(-(-(5, 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(5, 1), 1), 1), 1))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(=(2, 0), 1, *(-(-(-(5, 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(5, 1), 1), 1), 1))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(-(-(-(5, 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(5, 1), 1), 1), 1))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(-(-(4, 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(5, 1), 1), 1), 1))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(-(3, 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(5, 1), 1), 1), 1))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(5, 1), 1), 1), 1))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, ({f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))})))(-(-(-(-(5, 1), 1), 1), 1))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, {n. if(=(n, 0), 1, *(n, ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(n, 1))))}(-(-(-(-(5, 1), 1), 1), 1))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(=(-(-(-(-(5, 1), 1), 1), 1), 0), 1, *(-(-(-(-(5, 1), 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(5, 1), 1), 1), 1), 1))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(=(-(-(-(4, 1), 1), 1), 0), 1, *(-(-(-(-(5, 1), 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(5, 1), 1), 1), 1), 1))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(=(-(-(3, 1), 1), 0), 1, *(-(-(-(-(5, 1), 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(5, 1), 1), 1), 1), 1))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(=(-(2, 1), 0), 1, *(-(-(-(-(5, 1), 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(5, 1), 1), 1), 1), 1))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(=(1, 0), 1, *(-(-(-(-(5, 1), 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(5, 1), 1), 1), 1), 1))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(-(-(-(-(5, 1), 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(5, 1), 1), 1), 1), 1))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(-(-(-(4, 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(5, 1), 1), 1), 1), 1))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(-(-(3, 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(5, 1), 1), 1), 1), 1))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(-(2, 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(5, 1), 1), 1), 1), 1))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(1, ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(5, 1), 1), 1), 1), 1))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(1, ({f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))})))(-(-(-(-(-(5, 1), 1), 1), 1), 1))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(1, {n. if(=(n, 0), 1, *(n, ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(n, 1))))}(-(-(-(-(-(5, 1), 1), 1), 1), 1))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(1, if(=(-(-(-(-(-(5, 1), 1), 1), 1), 1), 0), 1, *(-(-(-(-(-(5, 1), 1), 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(-(5, 1), 1), 1), 1), 1), 1))))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(1, if(=(-(-(-(-(4, 1), 1), 1), 1), 0), 1, *(-(-(-(-(-(5, 1), 1), 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(-(5, 1), 1), 1), 1), 1), 1))))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(1, if(=(-(-(-(3, 1), 1), 1), 0), 1, *(-(-(-(-(-(5, 1), 1), 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(-(5, 1), 1), 1), 1), 1), 1))))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(1, if(=(-(-(2, 1), 1), 0), 1, *(-(-(-(-(-(5, 1), 1), 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(-(5, 1), 1), 1), 1), 1), 1))))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(1, if(=(-(1, 1), 0), 1, *(-(-(-(-(-(5, 1), 1), 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(-(5, 1), 1), 1), 1), 1), 1))))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(1, if(=(0, 0), 1, *(-(-(-(-(-(5, 1), 1), 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(-(5, 1), 1), 1), 1), 1), 1))))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(1, if(true, 1, *(-(-(-(-(-(5, 1), 1), 1), 1), 1), ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(-(-(-(-(-(5, 1), 1), 1), 1), 1), 1))))))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, *(1, 1))))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, if(false, 1, 1)))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, *(2, 1))))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, if(false, 1, 2)))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, *(3, 2))))))",
                "if(false, 1, *(5, if(false, 1, *(4, if(false, 1, 6)))))",
                "if(false, 1, *(5, if(false, 1, *(4, 6))))",
                "if(false, 1, *(5, if(false, 1, 24)))",
                "if(false, 1, *(5, 24))",
                "if(false, 1, 120)",
                "120"
        });

        for (Map.Entry test : evaluationWritingTests.entrySet()) {
            String expressionString = (String) test.getKey();
            try {
                Node node = parseFromString(expressionString);
                node.evaluate();
                ArrayList<String> evaluation = node.writeEvaluation();
                for (int i = 0; i < evaluation.size(); i++) {
                    if (!evaluation.get(i).equals(evaluationWritingTests.get(expressionString)[i])) {
                        passed = false;
                    }
                }
                if (!passed) {
                    for (String line : evaluation) {
                        System.out.println(line);
                    }
                }
            }
            catch (Exception e) {
                passed = false;
                System.out.println(expressionString + " -> " + "evaluation or evaluation writing failed");
            }
        }

        if (passed) {
            System.out.println("Success! test passed");
        }
    }
}
