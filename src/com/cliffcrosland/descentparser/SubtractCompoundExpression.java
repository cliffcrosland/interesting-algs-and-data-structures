package com.cliffcrosland.descentparser;

/**
 * Created by Cliff on 5/29/2015.
 */
public class SubtractCompoundExpression extends CompoundExpression {

    public SubtractCompoundExpression(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    public double eval(InterpreterState state) {
        return lhs.eval(state) - rhs.eval(state);
    }
}
