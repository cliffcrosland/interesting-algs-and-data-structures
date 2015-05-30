package com.cliffcrosland.descentparser.expressions;

import com.cliffcrosland.descentparser.InterpreterState;

/**
 * Created by Cliff on 5/29/2015.
 */
public interface Expression {
    public double eval(InterpreterState state);
}
