package com.cliffcrosland.sudoku;

import java.util.HashSet;
import java.util.Set;

public class SudokuSolver {

    public static SudokuBoard getSolvedSudokuBoard(SudokuBoard board) {
        assertIsValidSudokuBoard(board);
        SudokuBoard copy = board.copy();
        boolean solved = recursiveSolveSudokuBoard(copy);
        if (!solved) {
            throw new RuntimeException("Encountered an unsolvable sudoku board");
        }
        return copy;
    }

    private static boolean recursiveSolveSudokuBoard(SudokuBoard board) {
        LocationPossibilities location = getMostConstrainedEmptyLocation(board);
        if (location == null) {
            return true;
        }
        if (location.possibilities.isEmpty()) {
            return false;
        }
        for (int value : location.possibilities) {
            board.set(location.row, location.col, value);
            if (recursiveSolveSudokuBoard(board)) {
                return true;
            }
        }
        board.set(location.row, location.col, 0);
        return false;
    }

    private static LocationPossibilities getMostConstrainedEmptyLocation(SudokuBoard board) {
        LocationPossibilities mostConstrained = null;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board.get(r, c) == 0) {
                    Set<Integer> constraints = new HashSet<Integer>();
                    Set<Integer> possibilities = new HashSet<Integer>();
                    constraints.addAll(board.getRowValues(r));
                    constraints.addAll(board.getColumnValues(c));
                    constraints.addAll(board.getSquareValues(r / 3, c / 3));
                    for (int val = 1; val <= 9; val++) {
                        if (constraints.contains(val)) continue;
                        possibilities.add(val);
                    }
                    if (mostConstrained == null ||
                            possibilities.size() < mostConstrained.possibilities.size()) {
                        mostConstrained = new LocationPossibilities(r, c, possibilities);
                    }
                }
            }
        }
        return mostConstrained;
    }

    private static void assertIsValidSudokuBoard(SudokuBoard board) {
        if (!board.isValidSudokuBoard()) {
            throw new IllegalArgumentException("Sudoku board must be a valid board in order to solve it.");
        }
    }

    private static class LocationPossibilities {
        public Set<Integer> possibilities;
        public int row;
        public int col;

        public LocationPossibilities(int r, int c, Set<Integer> possibilities) {
            this.row = r;
            this.col = c;
            this.possibilities = possibilities;
        }
    }
}
