package com.cliffcrosland.descentparser;

/**
 * Created by Cliff on 5/29/2015.
 */
public class DivideCompoundExpression extends CompoundExpression {
    public DivideCompoundExpression(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    public double eval(InterpreterState state) {
        return lhs.eval(state) / rhs.eval(state);
    }
}
