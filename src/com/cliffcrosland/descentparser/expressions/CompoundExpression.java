package com.cliffcrosland.descentparser.expressions;

import com.cliffcrosland.descentparser.InterpreterState;
import com.cliffcrosland.descentparser.Tokens;

/**
 * Created by Cliff on 5/29/2015.
 */
public abstract class CompoundExpression implements Expression {

    protected Expression lhs;
    protected Expression rhs;

    protected CompoundExpression() {}

    protected CompoundExpression(Expression lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public abstract double eval(InterpreterState state);

    public static CompoundExpressionBuilder builder() {
        return new CompoundExpressionBuilder();
    }

    public static class CompoundExpressionBuilder {
        String token;
        Expression lhs;
        Expression rhs;

        public CompoundExpressionBuilder operator(String token) {
            if (!Tokens.isOperator(token)) {
                throw new RuntimeException(token + " is not an operator");
            }
            this.token = token;
            return this;
        }

        public CompoundExpressionBuilder terms(Expression lhs, Expression rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
            return this;
        }

        public CompoundExpression build() {
            if ("+".equals(token)) {
                return new AddCompoundExpression(lhs, rhs);
            } else if ("-".equals(token)) {
                return new SubtractCompoundExpression(lhs, rhs);
            } else if ("*".equals(token)) {
                return new MultiplyCompoundExpression(lhs, rhs);
            } else if ("/".equals(token)) {
                return new DivideCompoundExpression(lhs, rhs);
            } else if ("=".equals(token)) {
                return new IdentifierAssignmentCompoundExpression(lhs, rhs);
            }
            throw new RuntimeException("token is not an operator");
        }
    }
}
