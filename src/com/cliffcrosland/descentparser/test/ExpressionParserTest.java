package com.cliffcrosland.descentparser.test;

import com.cliffcrosland.descentparser.Expression;
import com.cliffcrosland.descentparser.ExpressionParser;

/**
 * Created by Cliff on 5/29/2015.
 */
public class ExpressionParserTest {
    public static void runTests() {
        System.out.println("Running tests...");
        String[] testCases = new String[] {
                "3 * 4 + 2 = 14",
                "3 * (4 + 2) = 18",
                "(3) = 3",
                "(1 + 2) * (3 + 4) = 21",
                "2 - 1 = 1",
                "4 / 2 = 2",
                "((1 + 2) * ((8 - 4) * 9) / 3 - 36) = 0"
        };
        for (String test : testCases) {
            runTestCase(test);
        }
        System.out.println("DONE. All tests passed successfully!");
    }

    private static void runTestCase(String test) {
        String[] testValues = test.split("=");
        String expression = testValues[0].trim();
        double value = Double.parseDouble(testValues[1].trim());
        Expression e = ExpressionParser.parseExpression(expression);
        if (!areEqual(value, e.value())) {
            throw new RuntimeException("test failed! Expression was: '" + expression + "'. Expected: " + value
                    + ". Got: " + e.value());
        }
    }

    private static boolean areEqual(double a, double b) {
        return Math.abs(a - b) < 1e-6;
    }
}
