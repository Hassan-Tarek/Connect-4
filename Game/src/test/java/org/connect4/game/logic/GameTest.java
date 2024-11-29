package org.connect4.game.logic;

import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.enums.PlayerType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTest {
    private Player redPlayer;
    private Player yellowPlayer;
    private Game game;

    @BeforeEach
    public void setup() {
        redPlayer = new Player(Color.RED, PlayerType.HUMAN);
        yellowPlayer = new Player(Color.YELLOW, PlayerType.HUMAN);
        game = new Game(new Board(), redPlayer, yellowPlayer, GameType.HUMAN_VS_HUMAN);
    }

    @Test
    public void testGameInitialization() {
        Assertions.assertNotNull(game);
        Assertions.assertEquals(redPlayer, game.getRedPlayer());
        Assertions.assertEquals(yellowPlayer, game.getYellowPlayer());
        Assertions.assertEquals(GameType.HUMAN_VS_HUMAN, game.getGameType());
    }

    @Test
    public void testGetWinner() {
        game.performCurrentPlayerMove(new Move(0));
        game.performCurrentPlayerMove(new Move(1));
        game.performCurrentPlayerMove(new Move(0));
        game.performCurrentPlayerMove(new Move(1));
        game.performCurrentPlayerMove(new Move(0));
        game.performCurrentPlayerMove(new Move(1));
        game.performCurrentPlayerMove(new Move(0));

        Assertions.assertNull(game.getWinner().orElse(null));
        Assertions.assertEquals(redPlayer, game.getWinner().orElse(null));
    }

    @Test
    public void testPerformCurrentPlayerMove() {
        Assertions.assertEquals(redPlayer, game.getCurrentPlayer());
        game.performCurrentPlayerMove(new Move(0));
        Assertions.assertEquals(game.getBoard().getPieceAt(0, 0).getColor(), Color.RED);
        Assertions.assertEquals(yellowPlayer, game.getCurrentPlayer());
    }
}
