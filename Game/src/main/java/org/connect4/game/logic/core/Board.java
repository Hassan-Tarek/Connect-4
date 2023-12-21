package org.connect4.game.logic.core;

import org.connect4.game.logging.GameLogger;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.exceptions.FullColumnException;
import org.connect4.game.logic.exceptions.InvalidColumnIndexException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * A class represents the game board for Connect-4.
 * @author Hassan
 */
public class Board implements Cloneable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger logger = GameLogger.getLogger();
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

        logger.finest("Board initialized successfully.");
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
     * @param column The column to add the piece.
     * @param color The color of the piece.
     * @throws InvalidColumnIndexException if the column index is not valid.
     * @throws FullColumnException if the column is full of pieces.
     */
    public void addPiece(int column, Color color) throws InvalidColumnIndexException, FullColumnException {
        if (!isValidColumn(column)) {
            throw new InvalidColumnIndexException("Invalid column index: " + column);
        }

        if (isColumnFull(column)) {
            throw new FullColumnException("Column: " + column + " is full.");
        }

        int row = currentRowIndex[column];
        Position position = new Position(row, column);
        pieces[row][column] = new Piece(position, color);
        currentRowIndex[column]++;

        logger.fine("Piece added to column " + column + " at row " + row + ".");
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
     * Checks whether a column index is valid.
     * @param column The column index to check.
     * @return true if the column index is valid, false otherwise.
     */
    public boolean isValidColumn(int column) {
        return column >= 0 && column < Board.COLS;
    }

    /**
     * Checks whether a column is full.
     * @param column The column index to check.
     * @return true if the column is full, false otherwise.
     */
    public boolean isColumnFull(int column) {
        return currentRowIndex[column] >= Board.ROWS;
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
