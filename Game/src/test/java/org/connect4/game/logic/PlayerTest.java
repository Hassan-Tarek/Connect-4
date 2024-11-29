package org.connect4.game.logic;

import org.connect4.game.ai.AIFactory;
import org.connect4.game.ai.enums.AIType;
import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.enums.PlayerType;
import org.connect4.game.logic.exceptions.FullColumnException;
import org.connect4.game.logic.exceptions.InvalidColumnIndexException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {
    private Board board;
    private Player player;
    private Game game;

    @BeforeEach
    public void setup() {
        board = new Board();
        player = new Player(Color.RED, PlayerType.HUMAN);
        Player aiPlayer = AIFactory.getAIPlayer(board, AIType.RANDOM_CHOICE_AI);
        game = new Game(board, player, aiPlayer, GameType.HUMAN_VS_COMPUTER);
    }

    @Test
    public void testPlayerInitialization() {
        Assertions.assertEquals(PlayerType.HUMAN, player.getPlayerType());
        Assertions.assertEquals(Color.RED, player.getColor());
        Assertions.assertEquals(0, player.getScore());
    }

    @Test
    public void testIsWin() {
        try {
            board.addPiece(0, Color.RED);
            board.addPiece(1, Color.RED);
            board.addPiece(2, Color.RED);
            board.addPiece(3, Color.RED);

            Assertions.assertTrue(player.isWinner(game));
        } catch (InvalidColumnIndexException | FullColumnException e) {
            throw new RuntimeException(e);
        }
    }
}
