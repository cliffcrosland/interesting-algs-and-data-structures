package com.cliffcrosland.sudoku;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by cliftoncrosland on 5/27/15.
 */
public class SudokuBoard {
    private int[][] board;

    public SudokuBoard(int[][] board) {
        assertCorrectDimensions(board);
        assertValidValues(board);
        this.board = board;
    }

    public SudokuBoard(String boardString) {
        this(getBoardFromString(boardString));
    }

    public int get(int r, int c) {
        return board[r][c];
    }

    public void set(int r, int c, int val) {
        assertValidValue(val);
        board[r][c] = val;
    }

    public void printBoard() {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                System.out.print(board[r][c] + " ");
            }
            System.out.println();
        }
    }

    public boolean isValidSudokuBoard() {
        return areColumnsValid() && areRowsValid() && areSquaresValid();
    }

    private boolean areColumnsValid() {
        for (int c = 0; c < board[0].length; c++) {
            Set<Integer> columnValues = new HashSet<Integer>();
            for (int r = 0; r < board.length; r++) {
                if (board[r][c] == 0) continue;
                if (columnValues.contains(board[r][c])) return false;
                columnValues.add(board[r][c]);
            }
        }
        return true;
    }

    private boolean areRowsValid() {
        for (int r = 0; r < board.length; r++) {
            Set<Integer> rowValues = new HashSet<Integer>();
            for (int c = 0; c < board.length; c++) {
                if (board[r][c] == 0) continue;
                if (rowValues.contains(board[r][c])) return false;
                rowValues.add(board[r][c]);
            }
        }
        return true;
    }

    private boolean areSquaresValid() {
        for (int squareR = 0; squareR < 3; squareR++) {
            for (int squareC = 0; squareC < 3; squareC++) {
                Set<Integer> squareValues = new HashSet<Integer>();
                for (int r = squareR * 3; r < squareR * 3 + 3; r++) {
                    for (int c = squareC * 3; c < squareC * 3 + 3; c++) {
                        if (board[r][c] == 0) continue;
                        if (squareValues.contains(board[r][c])) return false;
                        squareValues.add(board[r][c]);
                    }
                }
            }
        }
        return true;
    }

    public Set<Integer> getColumnValues(int c) {
        Set<Integer> columnValues = new HashSet<Integer>();
        for (int r = 0; r < board.length; r++) {
            if (board[r][c] == 0) continue;
            columnValues.add(board[r][c]);
        }
        return columnValues;
    }

    public Set<Integer> getRowValues(int r) {
        Set<Integer> rowValues = new HashSet<Integer>();
        for (int c = 0; c < board[0].length; c++) {
            if (board[r][c] == 0) continue;
            rowValues.add(board[r][c]);
        }
        return rowValues;
    }

    public Set<Integer> getSquareValues(int squareR, int squareC) {
        Set<Integer> squareValues = new HashSet<Integer>();
        for (int r = squareR * 3; r < squareR * 3 + 3; r++) {
            for (int c = squareC * 3; c < squareC * 3 + 3; c++) {
                if (board[r][c] == 0) continue;
                squareValues.add(board[r][c]);
            }
        }
        return squareValues;
    }

    public SudokuBoard copy() {
        return new SudokuBoard(copy(board));
    }

    private static int[][] copy(int[][] board) {
        int[][] copy = new int[board.length][board[0].length];
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                copy[r][c] = board[r][c];
            }
        }
        return copy;
    }

    private static void assertCorrectDimensions(int[][] board) {
        int numRows = board.length;
        int numCols = board[0].length;
        if (numRows != 9 || numCols != 9) {
            throw new IllegalArgumentException("board dimensions are not 9 by 9. They are " +
                    numRows + " by " + numCols);
        }
    }

    private static void assertValidValues(int[][] board) {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board.length; c++) {
                if (board[r][c] < 0 || board[r][c] > 9) {
                    throw new IllegalArgumentException("board values must be between 0 and 9, inclusive");
                }
            }
        }
    }

    private static void assertValidValue(int val) {
        if (val < 0 || val > 9) {
            throw new IllegalArgumentException("Value must be between 0 and 9, inclusive");
        }
    }

    private static int[][] getBoardFromString(String boardString) {
        if (boardString.length() != 9*9) {
            throw new IllegalArgumentException("Board string must be 9*9 characters in length");
        }
        int[][] board = new int[9][9];
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                char valChar = boardString.charAt(r * 9 + c);
                board[r][c] = valChar - '0';
            }
        }
        return board;
    }
}
