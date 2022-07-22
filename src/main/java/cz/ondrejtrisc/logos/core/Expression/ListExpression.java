package cz.ondrejtrisc.logos.core.Expression;

import cz.ondrejtrisc.logos.core.*;

import java.util.ArrayList;

public class ListExpression implements Expression {

    private final ArrayList<Expression> items;

    public ListExpression(ArrayList<Expression> items) {
        this.items = items;
    }

    static ListExpression parseFromProcessedString(String s) throws Exception {
        if (s.length() < 2 || s.charAt(0) != '[' || s.charAt(s.length() - 1) != ']') {
            throw new Exception();
        }
        if (s.equals("[]")) {
            return new ListExpression(new ArrayList<>());
        }

        //identify sections of the string that can be items
        ArrayList<Integer> itemEnds = new ArrayList<>();
        itemEnds.add(0);
        int i = 1;
        while (i < s.length()) {
            int parenthesesDepth = 0;
            int bracketsDepth = 0;
            int bracesDepth = 0;
            while ((i < s.length()) && (parenthesesDepth > 0 || bracketsDepth > 0 || bracesDepth > 0 || (s.charAt(i) != ',' && s.charAt(i) != ']'))) {
                if (s.charAt(i) == '(') {
                    parenthesesDepth++;
                }
                else if (s.charAt(i) == ')') {
                    parenthesesDepth--;
                }
                else if (s.charAt(i) == '[') {
                    bracketsDepth++;
                }
                else if (s.charAt(i) == ']') {
                    bracketsDepth--;
                }
                else if (s.charAt(i) == '{') {
                    bracesDepth++;
                }
                else if (s.charAt(i) == '}') {
                    bracesDepth--;
                }
                i++;
            }
            itemEnds.add(i);
            i++;
        }

        //parse the items
        ArrayList<Expression> items = new ArrayList<>();
        for (int j = 0; j < itemEnds.size() - 1; j++) {
            String itemString = s.substring(itemEnds.get(j) + 1, itemEnds.get(j + 1));
            Expression item = Expression.parseFromProcessedString(itemString);
            items.add(item);
        }

        return new ListExpression(items);
    }

    @Override
    public String toString() {

        //expression look like: []
        if (items.size() == 0) {
            return "[]";
        }

        //expression look like: [2]
        if (items.size() == 1) {
            return "[" + items.get(0).toString() + "]";
        }

        //expression look like: [2, 3]
        StringBuilder itemsString = new StringBuilder(items.get(0).toString());
        for (int i = 1; i < items.size(); i++) {
            itemsString.append(", ").append(items.get(i).toString());
        }
        return "[" + itemsString + "]";
    }

    @Override
    public Node toNode() {
        //empty list
        if (items.isEmpty()) {
            return new Node(new ArrayList<>(), new List(new ArrayList<>()));
        }

        ArrayList<Node> children = new ArrayList<>();
        for (Expression item : items) {
            children.add(item.toNode());
        }
        Function definition = new ElementaryFunction("[]");
        Node node = new Node(children, definition);
        Parameter parameter = new Parameter("");
        parameter.setSubstituent(node);
        return new ParametricNode(new ArrayList<>(), parameter);
    }
}
