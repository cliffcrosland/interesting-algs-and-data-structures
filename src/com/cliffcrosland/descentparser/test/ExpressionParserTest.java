package com.cliffcrosland.descentparser.test;

import com.cliffcrosland.descentparser.Expression;
import com.cliffcrosland.descentparser.ExpressionParser;
import com.cliffcrosland.descentparser.InterpreterState;

/**
 * Created by Cliff on 5/29/2015.
 */
public class ExpressionParserTest {
    public static void runTests() {
        System.out.println("Running tests...");
        InterpreterState state = new InterpreterState();
        String[] testCases = new String[] {
                "3 * 4 + 2 => 14",
                "3 * (4 + 2) => 18",
                "(3) => 3",
                "(1 + 2) * (3 + 4) => 21",
                "1 + 2 - 3 + 4 - 5 => -1",
                "2 - 1 => 1",
                "4 / 2 => 2",
                "((1 + 2) * ((8 - 4) * 9) / 3 - 36) => 0",
                "foo = 13 => 13",
                "foo => 13",
                "bar = (1 + 2) * (3 + 5) => 24",
                "bar - foo => 11",
                "bar - foo => 11",
                "baz = bar - foo + 7 * 3 => 32",
                "baz => 32"
        };
        for (String test : testCases) {
            runTestCase(test, state);
        }
        System.out.println("DONE. All tests passed successfully!");
    }

    private static void runTestCase(String test, InterpreterState state) {
        String[] testValues = test.split("=>");
        String expression = testValues[0].trim();
        double value = Double.parseDouble(testValues[1].trim());
        Expression e = ExpressionParser.parseExpression(expression);
        if (!areEqual(value, e.eval(state))) {
            throw new RuntimeException("test failed! Expression was: '" + expression + "'. Expected: " + value
                    + ". Got: " + e.eval(state));
        }
    }

    private static boolean areEqual(double a, double b) {
        return Math.abs(a - b) < 1e-6;
    }
}
