package com.cliffcrosland.descentparser;

/**
 * Created by Cliff on 5/29/2015.
 */
public class ExpressionParser {

    public static Expression parseExpression(String expressionString) {
        Tokens tokens = Tokens.tokenize(expressionString);
        Expression exp = parseExpression(tokens);
        if (tokens.hasMoreTokens()) {
            throw new InvalidExpressionException("We have tokens left over after parsing the expression");
        }
        return exp;
    }

    private static Expression parseExpression(Tokens tokens) {
        return parseExpression(tokens, 0);
    }

    // Expression grammar:
    // E -> T
    // E -> T op E
    // T -> (E)
    // T -> number
    private static Expression parseExpression(Tokens tokens, int prec) {
        Expression exp = parseTerm(tokens);
        String token;
        while (tokens.hasMoreTokens()) {
            token = tokens.nextToken();
            int newPrec = Tokens.precedence(token);
            if (newPrec < prec || ")".equals(token)) {
                tokens.saveToken(token);
                break;
            }
            if (!Tokens.isOperator(token)) {
                throw new InvalidExpressionException("Expected an operator but got: '" + token + "'");
            }
            Expression rhs = parseExpression(tokens, newPrec);
            exp = CompoundExpression.builder()
                    .operator(token).terms(exp, rhs).build();
        }
        return exp;
    }

    private static Expression parseTerm(Tokens tokens) {
        String token = tokens.nextToken();
        if ("(".equals(token)) {
            Expression exp = parseExpression(tokens);
            if (!")".equals(tokens.nextToken())) throw new InvalidExpressionException("Unbalanced parens");
            return exp;
        }
        if (Tokens.isValue(token)) {
            return new ValueExpression(token);
        }
        throw new InvalidExpressionException("Term first token is not a paren nor a value: " + token);
    }

    public static class InvalidExpressionException extends RuntimeException {
        public InvalidExpressionException(String e) {
            super(e);
        }
    }
}
