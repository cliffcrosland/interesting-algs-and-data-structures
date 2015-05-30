package com.cliffcrosland.descentparser;

/**
 * Created by Cliff on 5/29/2015.
 */
public class ValueExpression implements Expression {

    private double value;

    public ValueExpression(String value) {
        if (!Tokens.isValue(value)) {
            throw new RuntimeException("The token " + value + " is not a value.");
        }
        this.value = Double.parseDouble(value);
    }

    public double eval(InterpreterState state) {
        return value;
    }
}
