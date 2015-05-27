package com.cliffcrosland.sudoku.test;

import com.cliffcrosland.sudoku.SudokuBoard;
import com.cliffcrosland.sudoku.SudokuSolver;

public class SudokuSolverTest {

    public static void runTests() {
        SudokuBoard board = getWorldsHardestSudokuBoard();
        System.out.println("Solving hard sudoku board...");
        long start = System.currentTimeMillis();
        SudokuBoard solved = SudokuSolver.getSolvedSudokuBoard(board);
        long end = System.currentTimeMillis();
        solved.printBoard();
        System.out.println("Took " + (end - start) + " ms.");
        if (!solved.isValidSudokuBoard()) {
            throw new RuntimeException("The solution is not a valid sudoku board!");
        }
        System.out.println("DONE. All tests passed successfully!");
    }

    // http://www.telegraph.co.uk/news/science/science-news/9359579/Worlds-hardest-sudoku-can-you-crack-it.html
    private static SudokuBoard getWorldsHardestSudokuBoard() {
        String boardString =
                "800000000" +
                "003600000" +
                "070090200" +
                "050007000" +
                "000045700" +
                "000100030" +
                "001000068" +
                "008500010" +
                "090000400";

        return new SudokuBoard(boardString);
    }
}
