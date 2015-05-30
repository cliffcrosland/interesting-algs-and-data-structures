package com.cliffcrosland.descentparser;

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
