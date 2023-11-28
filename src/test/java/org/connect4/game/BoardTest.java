package org.connect4.game;

import org.connect4.game.exceptions.InvalidMoveException;
import org.connect4.game.utils.Color;
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
            boolean added = board.addPiece(0, Color.RED);
            Assertions.assertTrue(added);
            Assertions.assertNotNull(board.getPieces()[0][0]);

            added = board.addPiece(1, Color.YELLOW);
            Assertions.assertTrue(added);
            Assertions.assertNotNull(board.getPieces()[1][0]);

            added = board.addPiece(-1, Color.RED);
            Assertions.assertFalse(added);

            for (int i = 0; i < Board.ROWS; i++) {
                board.addPiece(2, Color.YELLOW);
            }
            added = board.addPiece(2, Color.RED);
            Assertions.assertFalse(added);
        } catch (InvalidMoveException ex) {
            ex.printStackTrace();
        }
    }
}
