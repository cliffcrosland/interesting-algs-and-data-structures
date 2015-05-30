package com.cliffcrosland.descentparser;

/**
 * Created by Cliff on 5/29/2015.
 */
public class AddCompoundExpression extends CompoundExpression {

    public AddCompoundExpression(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    public double value() {
        return lhs.value() + rhs.value();
    }

}
