package org.connect4.game.core;

import org.connect4.game.exceptions.InvalidMoveException;
import org.connect4.game.enums.Color;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * A class represents the game board for Connect-4.
 * @author Hassan
 */
public class Board implements Cloneable {
    private static final Logger logger = Game.logger;
    public static final int ROWS = 6;
    public static final int COLS = 7;

    private Piece[][] pieces;
    private int[] currentRowIndex;

    /**
     * Constructs the game board and initialize its state.
     */
    public Board() {
        pieces = new Piece[ROWS][COLS];
        currentRowIndex = new int[COLS];
        Arrays.fill(currentRowIndex, 0);

        logger.fine("Board initialized successfully.");
    }

    /**
     * Gets the pieces of the game board.
     * @return The 2D array representing the pieces on the board.
     */
    public Piece[][] getPieces() {
        return pieces;
    }

    /**
     * Add the piece with a specific color to the board at a specific column.
     * @param col The column to add the piece.
     * @param color The color of the piece.
     * @throws InvalidMoveException if the move is invalid.
     */
    public void addPiece(int col, Color color) throws InvalidMoveException {
        if (!isValidMove(col)) {
            throw new InvalidMoveException("Invalid move for the column: " + col);
        }

        int row = currentRowIndex[col];
        Position position = new Position(row, col);
        pieces[row][col] = new Piece(position, color);
        currentRowIndex[col]++;

        logger.info("Piece added to column " + col + " at row " + row + ".");
    }

    /**
     * Gets the piece at the specified position on the board.
     * @param row The row index.
     * @param col The column index.
     * @return The piece at the specified position, or null if there is no piece.
     */
    public Piece getPieceAt(int row, int col) {
        if (isValidPosition(row, col)) {
            return pieces[row][col];
        }

        return null;
    }

    /**
     * Check whether a move is valid or not.
     * @param col The column for the move.
     * @return true if the move is valid, false otherwise.
     */
    public boolean isValidMove(int col) {
        boolean isValid  = isValidColumn(col) && !isColumnFull(col);

        if (isValid) {
            logger.info("Move is valid. Column: " + col);
        } else {
            logger.severe("Move is not valid. Column: " + col);
        }

        return isValid;
    }

    /**
     * Checks whether a column index is valid.
     * @param col The column index to check.
     * @return true if the column index is valid, false otherwise.
     */
    private boolean isValidColumn(int col) {
        return col >= 0 && col < Board.COLS;
    }

    /**
     * Checks whether a column is full.
     * @param col The column index to check.
     * @return true if the column is full, false otherwise.
     */
    private boolean isColumnFull(int col) {
        return currentRowIndex[col] >= Board.ROWS;
    }

    /**
     * Checks whether a position on the board is valid.
     * @param row The row index.
     * @param col The column index.
     * @return true if the position is valid, false otherwise.
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS;
    }

    /**
     * Checks whether the board is full.
     * @return true if the board is full, false otherwise.
     */
    public boolean isFull() {
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                if (pieces[i][j] == null) {
                    return false;
                }
            }
        }

        logger.info("The board is full.");
        return true;
    }

    /**
     * Creates and returns a copy of this Board object.
     *
     * @return A new Board object that is a copy of this instance.
     */
    @Override
    public Board clone() {
        try {
            Board cloned = (Board) super.clone();
            cloned.pieces = clonePieces();
            cloned.currentRowIndex = currentRowIndex.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Gets a deep copy of the pieces array.
     * @return A deep copy of the pieces array.
     * @throws CloneNotSupportedException Throw exception if the Piece class doesn't implement the Cloneable interface.
     */
    private Piece[][] clonePieces() throws CloneNotSupportedException {
        Piece[][] clonedPieces = new Piece[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (pieces[i][j] != null) {
                    clonedPieces[i][j] = pieces[i][j].clone();
                }
            }
        }

        return clonedPieces;
    }
}
