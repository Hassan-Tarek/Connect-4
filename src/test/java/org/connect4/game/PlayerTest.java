package org.connect4.game;

import org.connect4.game.exceptions.InvalidMoveException;
import org.connect4.game.utils.Color;
import org.connect4.game.utils.GameType;
import org.connect4.game.utils.PlayerType;
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
        redPlayer = new Player("John", "Hardy", Color.RED, PlayerType.HUMAN);
        yellowPlayer = new Player("Jodie", "Albert", Color.YELLOW, PlayerType.HUMAN);
        game = new Game(board, redPlayer, yellowPlayer, GameType.HUMAN_VS_HUMAN);
    }

    @Test
    public void testPlayerInitialization() {
        Assertions.assertEquals("John", redPlayer.getFirstName());
        Assertions.assertEquals("Hardy", redPlayer.getLastName());
        Assertions.assertEquals(PlayerType.HUMAN, redPlayer.getPlayerType());
        Assertions.assertEquals(Color.RED, redPlayer.getColor());
        Assertions.assertEquals(0, redPlayer.getScore());
    }

    @Test
    public void testMakeMove() {
        try {
            redPlayer.makeMove(board, 0);

            // Make a valid move
            Assertions.assertEquals(Color.RED, board.getPieces()[0][0].getColor());

            // Make an invalid move
            Assertions.assertThrows(InvalidMoveException.class, () -> redPlayer.makeMove(board, -1));
        }
        catch (InvalidMoveException ex) {
            ex.printStackTrace();
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
        }
        catch (InvalidMoveException ex) {
            ex.printStackTrace();
        }
    }
}
