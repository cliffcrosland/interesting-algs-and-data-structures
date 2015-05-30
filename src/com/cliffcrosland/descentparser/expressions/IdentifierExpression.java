package com.cliffcrosland.descentparser.expressions;

import com.cliffcrosland.descentparser.InterpreterState;

public class IdentifierExpression implements Expression {

    private String identifier;

    public IdentifierExpression(String identifier) {
        this.identifier = identifier;
    }

    public double eval(InterpreterState state) {
        return state.lookupExpression(identifier).eval(state);
    }

    public String identifier() {
        return identifier;
    }
}
