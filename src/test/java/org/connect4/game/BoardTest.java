package org.connect4.game;

import org.connect4.game.core.Board;
import org.connect4.game.exceptions.FullColumnException;
import org.connect4.game.exceptions.InvalidColumnIndexException;
import org.connect4.game.enums.Color;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardTest {
    private Board board;

    @BeforeEach
    public void setup() {
        board = new Board();
    }

    @Test
    public void testBoardInitialization() {
        Assertions.assertNotNull(board);
        Assertions.assertNotNull(board.getPieces());
    }

    @Test
    public void testAddPiece() {
        try {
            board.addPiece(0, Color.RED);
            Assertions.assertNotNull(board.getPieceAt(0, 0));

            board.addPiece(1, Color.YELLOW);
            Assertions.assertNotNull(board.getPieceAt(0, 1));

            Assertions.assertThrows(InvalidColumnIndexException.class, () -> board.addPiece(-1, Color.RED));

            for (int i = 0; i < Board.ROWS; i++) {
                board.addPiece(2, Color.YELLOW);
            }
            Assertions.assertThrows(FullColumnException.class, () -> board.addPiece(2, Color.RED));
        } catch (InvalidColumnIndexException | FullColumnException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testIsFull() {
        Assertions.assertFalse(board.isFull());

        // Fill the board.
        for (int column = 0; column < Board.COLS; column++) {
            while (!board.isColumnFull(column)) {
                try {
                    board.addPiece(column, Color.RED);
                } catch (InvalidColumnIndexException | FullColumnException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Assertions.assertTrue(board.isFull());
    }

    @Test
    public void testIsValidColumn() {
        Board board = new Board();

        // Test valid column indexes
        for (int col = 0; col < Board.COLS; col++) {
            Assertions.assertTrue(board.isValidColumn(col));
        }

        // Test invalid column indexes
        Assertions.assertFalse(board.isValidColumn(-1));
        Assertions.assertFalse(board.isValidColumn(Board.COLS));
    }

    @Test
    public void testIsColumnFull() {
        Board board = new Board();

        // Fill a column
        for (int row = 0; row < Board.ROWS; row++) {
            try {
                board.addPiece(0, Color.RED);
            } catch (InvalidColumnIndexException | FullColumnException e) {
                throw new RuntimeException(e);
            }
        }

        Assertions.assertTrue(board.isColumnFull(0));
        Assertions.assertFalse(board.isColumnFull(1));
    }
}
