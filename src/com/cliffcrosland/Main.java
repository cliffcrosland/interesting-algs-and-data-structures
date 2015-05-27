package com.cliffcrosland;

import com.cliffcrosland.sudoku.test.SudokuSolverTest;

public class Main {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            SudokuSolverTest.runTests();
        }
    }
}
