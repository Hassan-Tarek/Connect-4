package org.connect4.game.logic;

import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.exceptions.FullColumnException;
import org.connect4.game.logic.exceptions.InvalidColumnIndexException;
import org.connect4.game.logic.exceptions.InvalidMoveException;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.enums.PlayerType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {
    private Board board;
    private Player redPlayer;
    private Player yellowPlayer;
    private Game game;

    @BeforeEach
    public void setup() {
        board = new Board();
        redPlayer = new Player("john", Color.RED, PlayerType.HUMAN);
        yellowPlayer = new Player("jodie", Color.YELLOW, PlayerType.HUMAN);
        game = new Game(board, redPlayer, yellowPlayer, GameType.HUMAN_VS_HUMAN);
    }

    @Test
    public void testPlayerInitialization() {
        Assertions.assertEquals("john", redPlayer.getUsername());
        Assertions.assertEquals(PlayerType.HUMAN, redPlayer.getPlayerType());
        Assertions.assertEquals(Color.RED, redPlayer.getColor());
        Assertions.assertEquals(0, redPlayer.getScore());
    }

    @Test
    public void testMakeMove() {
        try {
            redPlayer.makeMove(new Move(board, 0));

            // Make a valid move
            Assertions.assertEquals(Color.RED, board.getPieceAt(0, 0).getColor());

            // Make an invalid move
            Assertions.assertThrows(InvalidMoveException.class, () -> yellowPlayer.makeMove(new Move(board, -1)));
        }
        catch (InvalidMoveException ex) {
            Assertions.fail("Unexpected InvalidMoveException: " + ex.getMessage());
        }
    }

    @Test
    public void testIsWin() {
        try {
            board.addPiece(0, Color.RED);
            board.addPiece(1, Color.RED);
            board.addPiece(2, Color.RED);
            board.addPiece(3, Color.RED);

            Assertions.assertTrue(redPlayer.isWin(game));
        } catch (InvalidColumnIndexException | FullColumnException e) {
            throw new RuntimeException(e);
        }
    }
}
