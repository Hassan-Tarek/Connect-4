package org.connect4.game.logic;

import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.exceptions.FullColumnException;
import org.connect4.game.logic.exceptions.InvalidColumnIndexException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MoveTest {
    private Board board;
    private Move move;

    @BeforeEach
    public void setup() {
        board = new Board();
        move = new Move(0);
    }

    @Test
    public void testIsValid() {
        for (int column = 0; column < Board.COLS; column++) {
            move = new Move(column);
            Assertions.assertTrue(move.isValid(board));
        }

        // Fill a column to test
        try {
            for (int row = 0; row < Board.ROWS; row++) {
                board.addPiece(0, Color.RED);
            }
        } catch (InvalidColumnIndexException | FullColumnException e) {
            throw new RuntimeException(e);
        }
        move = new Move(0);
        Assertions.assertFalse(move.isValid(board));

        // Test invalid move
        move = new Move(-1);
        Assertions.assertFalse(move.isValid(board));
        move = new Move(Board.COLS);
        Assertions.assertFalse(move.isValid(board));
    }
}
