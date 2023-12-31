package org.connect4.game.logic;

import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.PlayerType;
import org.connect4.game.logic.exceptions.FullColumnException;
import org.connect4.game.logic.exceptions.InvalidColumnIndexException;
import org.connect4.game.logic.exceptions.InvalidMoveException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MoveTest {
    private Board board;
    private Player player;
    private Move move;

    @BeforeEach
    public void setup() {
        board = new Board();
        player = new Player(Color.RED, PlayerType.HUMAN);
        move = new Move(board, 0);
    }

    @Test
    public void testApplyMove() {
        try {
            move.applyMove(player);
        } catch (InvalidMoveException e) {
            Assertions.fail("Invalid Move");
        }

        Assertions.assertEquals(player.getColor(), board.getPieceAt(0, 0).getColor());
    }

    @Test
    public void testIsValid() {
        for (int column = 0; column < Board.COLS; column++) {
            move = new Move(board, column);
            Assertions.assertTrue(move.isValid());
        }

        // Fill a column to test
        try {
            for (int row = 0; row < Board.ROWS; row++) {
                board.addPiece(0, Color.RED);
            }
        } catch (InvalidColumnIndexException | FullColumnException e) {
            throw new RuntimeException(e);
        }
        move = new Move(board, 0);
        Assertions.assertFalse(move.isValid());

        // Test invalid move
        move = new Move(board, -1);
        Assertions.assertFalse(move.isValid());
        move = new Move(board, Board.COLS);
        Assertions.assertFalse(move.isValid());
    }
}
