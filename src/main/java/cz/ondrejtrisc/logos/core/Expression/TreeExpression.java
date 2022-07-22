package cz.ondrejtrisc.logos.core.Expression;

import cz.ondrejtrisc.logos.core.Node;

import java.util.ArrayList;

public class TreeExpression implements Expression {

    private final Expression root;
    private final ArrayList<Expression> children;

    public TreeExpression(Expression root, ArrayList<Expression> children) {
        this.root = root;
        this.children = children;
    }

    static TreeExpression parseFromProcessedString(String s) throws Exception {
        if (s.length() < 3 || s.charAt(s.length() - 1) != ')') {
            throw new Exception();
        }

        int i;
        if (s.charAt(0) == '(') { //root is a parenthesesExpression
            i = 1;
            int parenthesesDepth = 1;
            while ((i < s.length()) && parenthesesDepth > 0) {
                if (s.charAt(i) == '(') {
                    parenthesesDepth++;
                }
                else if (s.charAt(i) == ')') {
                    parenthesesDepth--;
                }
                i++;
            }
            if (i == s.length() || s.charAt(i) != '(') {
                throw new Exception();
            }
        }
        else {
            //find the first '(' outside of brackets and braces
            i = 0;
            int bracketsDepth = 0;
            int bracesDepth = 0;
            while ((i < s.length()) && (s.charAt(i) != '(' || bracketsDepth > 0 || bracesDepth > 0)) {
                if (s.charAt(i) == '[') {
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

            //if no '(' outside of braces was found
            if (i == s.length()) {
                throw new Exception();
            }
        }

        //parse the root
        String rootString = s.substring(0, i);
        Expression root = Expression.parseFromProcessedString(rootString);

        //identify sections of the string that can be children
        ArrayList<Integer> childEnds = new ArrayList<>();
        childEnds.add(i);
        while (i < s.length() - 1) {
            i++;
            int parenthesesDepth = 0;
            int bracketsDepth = 0;
            int bracesDepth = 0;
            while ((i < s.length() - 1) && (parenthesesDepth > 0 || bracketsDepth > 0 || bracesDepth > 0 || (s.charAt(i) != ',' && s.charAt(i) != ')'))) {
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
            childEnds.add(i);
        }

        //parse the children
        ArrayList<Expression> children = new ArrayList<>();
        for (int j = 0; j < childEnds.size() - 1; j++) {
            String childString = s.substring(childEnds.get(j) + 1, childEnds.get(j + 1));
            Expression child = Expression.parseFromProcessedString(childString);
            children.add(child);
        }

        return new TreeExpression(root, children);
    }

    @Override
    public String toString() {

        //expression look like: +()
        if (children.size() == 0) {
            return root.toString() + "()";
        }

        //expression look like: +(2)
        if (children.size() == 1) {
            return root.toString() + "(" + children.get(0).toString() + ")";
        }

        //expression look like: +(2, 3)
        StringBuilder childrenString = new StringBuilder((children.get(0) == null)?"":children.get(0).toString());
        for (int i = 1; i < this.children.size(); i++) {
            childrenString.append(", ").append((children.get(i) == null)?"":children.get(i).toString());
        }
        return root.toString() + "(" + childrenString + ")";
    }

    @Override
    public Node toNode() {
        Node ret = root.toNode();

        ArrayList<Node> retChildren = new ArrayList<>();
        for (Expression child : children) {
            retChildren.add(child.toNode());
        }

        ret.setChildren(retChildren);
        return ret;
    }
}
