package com.cliffcrosland.descentparser.expressions;

import com.cliffcrosland.descentparser.InterpreterState;

/**
 * Created by Cliff on 5/29/2015.
 */
public class AddCompoundExpression extends CompoundExpression {

    public AddCompoundExpression(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    public double eval(InterpreterState state) {
        return lhs.eval(state) + rhs.eval(state);
    }

}
