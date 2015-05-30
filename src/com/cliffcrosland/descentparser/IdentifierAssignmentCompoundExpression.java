package com.cliffcrosland.descentparser;

/**
 * Created by Cliff on 5/29/2015.
 */
public class IdentifierAssignmentCompoundExpression extends CompoundExpression {
    private String identifier;
    private Expression rhs;

    public IdentifierAssignmentCompoundExpression(Expression lhs, Expression rhs) {
        if (!IdentifierExpression.class.isInstance(lhs)) {
            throw new RuntimeException("Assignment expressions must have an identifier expression on the left-hand " +
                    "side. The kind of expression was: " + identifier.getClass().getSimpleName());
        }
        IdentifierExpression identifierExp = (IdentifierExpression) lhs;
        this.identifier = identifierExp.identifier();
        this.rhs = rhs;
    }

    public double eval(InterpreterState state) {
        state.setIdentifierExpression(identifier, rhs);
        return rhs.eval(state);
    }
}
