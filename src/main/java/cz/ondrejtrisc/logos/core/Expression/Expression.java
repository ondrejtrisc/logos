package cz.ondrejtrisc.logos.core.Expression;

import cz.ondrejtrisc.logos.core.Node;

public interface Expression {

    String[] reservedStrings = {"(", ")", "[", "]", "{", "}", ",", ".", "\""};

    static Expression parseFromString(String s) throws Exception {

        //remove whitespace not enclosed in quotation marks
        char[] charArray = s.toCharArray();
        boolean quoted = false;
        StringBuilder processed = new StringBuilder();
        for (char ch : charArray) {
            if (ch == '\"') {
                quoted = !quoted;
            }
            if (quoted || !Character.isWhitespace(ch)) {
                processed.append(ch);
            }
        }
        s = processed.toString();

        return parseFromProcessedString(s);
    }

    static Expression parseFromProcessedString(String s) throws Exception {
        //try out if any parseFromProcessedString method doesn't throw exception
        try {
            return SimpleExpression.parseFromProcessedString(s);
        } catch (Exception e) {
            try {
                return ParenthesesExpression.parseFromProcessedString(s);
            } catch (Exception e2) {
                try {
                    return FunctionExpression.parseFromProcessedString(s);
                } catch (Exception e3) {
                    try {
                        return TreeExpression.parseFromProcessedString(s);
                    } catch (Exception e4) {
                        return ListExpression.parseFromProcessedString(s);
                    }
                }
            }
        }
    }

    String toString();

    Node toNode();

    static void main(String[] args){
        String[] testStringsValid = {
            "",
            "5",
            "5.0",
            "-10",
            "test",
            "{test}",
            "{{test}}",
            "{a(b, c)}",
            "{a. test}",
            "{a, b. test}",
            "{a, b, c, d, e. test}",
            "[]",
            "[](test)",
            "[test]",
            "[{test}]",
            "[[test]]",
            "[a(b, c)]",
            "[a](b)",
            "[a, [b, [c, d]]]",
            "([a](b))(c)",
            "(a)(b)",
            "(a(b))(c)",
            "test()",
            "test(, )",
            "test(a, )",
            "test(, a)",
            "test(, , a)",
            "test(, a, , b)",
            "{test}(test)",
            "[test](test, test)",
            "[a, ]",
            "[, a]",
            "test(, , , test, , test, test)",
            "test({test}, [test], test, a(b, c), )",
            "[[a(b, c)], [d], e(f)]",
            "{n. if(=(n, 0), 1, *(n, ({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}({t. {f, n. if(=(n, 0), 1, *(n, f(-(n, 1))))}(t(t))}))(-(n, 1))))}",
            "{x. [\"What is your name?\", ++(\"Hello \", x)]}",
        };
        String[] testStringsInvalid = {
            "()",
            "te(st",
            "te)st",
            "te{st",
            "te}st",
            "te[st",
            "te]st",
            "te,st",
            "te.st",
            "te\"st",
            "{test",
            "test}",
            "{}",
            "{te(st}",
            "{. test}",
            "{, . test}",
            "{, a. test}",
            "{a,. test}",
            "{a, test}",
            "{a, {b} .test}",
            "a(b)(c)",
            "test(",
            "test)",
        };
        boolean passed = true;
        for (String s : testStringsValid) {
            try {
                Expression expression = Expression.parseFromString(s);
                if (!s.equals(expression.toString())) {
                    passed = false;
                    System.out.println(s + " != " + expression);
                }
                try {
                    Node node = expression.toNode();
                    try {
                        if (!s.equals(node.toString())) {
                            passed = false;
                            System.out.println(s + " -> " + node);
                        }
                    } catch (Exception toStringException) {
                        passed = false;
                        System.out.println(s + " -> " + "node writing failed");
                        System.out.println(toStringException.getMessage());
                    }
                } catch (Exception nodeException) {
                    passed = false;
                    System.out.println(s + " -> " + "node parsing failed");
                    System.out.println(nodeException.getMessage());
                }
            } catch (Exception expressionException) {
                passed = false;
                System.out.println(s + " -> " + "invalid");
                System.out.println(expressionException.getMessage());
            }
        }
        for (String s : testStringsInvalid) {
            try {
                Expression.parseFromString(s);
                passed = false;
                System.out.println(s + " -> " + "valid");
            } catch (Exception ignored) {
            }
        }
        if (passed) {
            System.out.println("Success! test passed");
        }
    }
}
