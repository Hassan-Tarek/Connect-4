package org.connect4.game;

import org.connect4.game.core.Board;
import org.connect4.game.exceptions.InvalidMoveException;
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

            Assertions.assertThrows(InvalidMoveException.class, () -> board.addPiece(-1, Color.RED));

            for (int i = 0; i < Board.ROWS; i++) {
                board.addPiece(2, Color.YELLOW);
            }
            Assertions.assertThrows(InvalidMoveException.class, () -> board.addPiece(2, Color.RED));
        } catch (InvalidMoveException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void testIsValidMove() {
        Board board = new Board();

        for (int col = 0; col < Board.COLS; col++) {
            Assertions.assertTrue(board.isValidMove(col));
        }

        // Fill a column to test
        try {
            for (int row = 0; row < Board.ROWS; row++) {
                board.addPiece(0, Color.RED);
            }
        } catch (InvalidMoveException ex) {
            Assertions.fail("Unexpected InvalidMoveException: " + ex.getMessage());
        }

        // Test invalid move
        Assertions.assertFalse(board.isValidMove(0));
        Assertions.assertFalse(board.isValidMove(-1));
        Assertions.assertFalse(board.isValidMove(Board.COLS));
    }
}
