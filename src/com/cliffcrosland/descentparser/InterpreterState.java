package com.cliffcrosland.descentparser;

import com.cliffcrosland.descentparser.expressions.Expression;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cliff on 5/29/2015.
 */
public class InterpreterState {

    Map<String, Expression> identifierExpressions = new HashMap<String, Expression>();

    public void setIdentifierExpression(String identifier, Expression exp) {
        identifierExpressions.put(identifier, exp);
    }

    public Expression lookupExpression(String identifier) {
        if (!identifierExpressions.containsKey(identifier)) {
            throw new RuntimeException("Identifier '" + identifier + "' is undefined.");
        }
        return identifierExpressions.get(identifier);
    }

}
