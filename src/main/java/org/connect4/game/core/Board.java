package org.connect4.game.core;

import org.connect4.game.exceptions.InvalidMoveException;
import org.connect4.game.enums.Color;

import java.util.logging.Logger;

/**
 * The class represents the game board for Connect-4.
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
        initializeBoard();
        initializeCurrentRowIndex();
    }

    /**
     * Gets the pieces of the game board.
     * @return The 2D array representing the pieces on the board.
     */
    public Piece[][] getPieces() {
        return pieces;
    }

    /**
     * Set the pieces of the game board.
     * @param pieces The 2D array representing the pieces on the board.
     */
    public void setPieces(Piece[][] pieces) {
        this.pieces = pieces;
    }

    /**
     * Initialize the 2D array that represent the board pieces.
     */
    private void initializeBoard() {
        pieces = new Piece[ROWS][COLS];

        // Initialize pieces to null
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                pieces[i][j] = null;
            }
        }

        logger.fine("Board initialized successfully.");
    }

    /**
     * Initialize the array that track the current row index of each column.
     */
    private void initializeCurrentRowIndex() {
        currentRowIndex = new int[COLS];

        // Initialize currentRowIndex array to 0
        for (int i = 0; i < COLS; i++) {
            currentRowIndex[i] = 0;
        }

        logger.fine("Current row index initialized.");
    }

    /**
     * Check whether a move is valid or not.
     * @param col The column for the move.
     * @return true if the move is valid, false otherwise.
     */
    public boolean isValidMove(int col) {
        boolean isValid  = col >= 0 && col < COLS
                && currentRowIndex[col] >= 0
                && currentRowIndex[col] < ROWS;

        if (isValid) {
            logger.info("Move is valid. Column: " + col);
        }
        else {
            logger.severe("Move is not valid. Column: " + col);
        }

        return isValid;
    }

    /**
     * Add the piece with a specific color to the board at a specific column.
     * @param col The column to add the piece.
     * @param color The color of the piece.
     * @throws InvalidMoveException if the move is invalid.
     */
    public boolean addPiece(int col, Color color) throws InvalidMoveException {
        boolean isAdded = false;
        if (isValidMove(col)) {
            Position position = new Position(currentRowIndex[col], col);
            pieces[currentRowIndex[col]][col] = new Piece(position, color);
            currentRowIndex[col]++;
            isAdded = true;
            logger.info("Piece added to the column: " + col);
        } else {
            logger.severe("Piece cannot be added to the column: " + col);
            throw new InvalidMoveException("Invalid move for the column: " + col);
        }

        return isAdded;
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
            cloned.pieces = pieces.clone();
            cloned.currentRowIndex = currentRowIndex.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
