package org.connect4.game.logic;

import org.connect4.game.ai.AIFactory;
import org.connect4.game.ai.enums.AIType;
import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.enums.PlayerType;
import org.connect4.game.logic.utils.WinnerChecker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WinnerCheckerTest {
    private Game game;
    private WinnerChecker winnerChecker;

    @BeforeEach
    public void setup() {
        Board board = new Board();
        Player humanPlayer = new Player(Color.RED, PlayerType.HUMAN);
        Player aiPlayer = AIFactory.getAIPlayer(board, AIType.RANDOM_CHOICE_AI);
        winnerChecker = new WinnerChecker(board);
        game = new Game(board, humanPlayer, aiPlayer, GameType.HUMAN_VS_COMPUTER);
    }

    @Test
    public void testHasWinner() {
        Assertions.assertFalse(winnerChecker.hasWinner());

        game.performCurrentPlayerMove(new Move(0));
        game.performCurrentPlayerMove(new Move(1));
        game.performCurrentPlayerMove(new Move(0));
        game.performCurrentPlayerMove(new Move(1));
        game.performCurrentPlayerMove(new Move(0));
        game.performCurrentPlayerMove(new Move(1));
        game.performCurrentPlayerMove(new Move(0));

        Assertions.assertTrue(winnerChecker.hasWinner());
    }

    @Test
    public void testDetermineWinner() {
        Assertions.assertEquals(winnerChecker.determineWinner(), Color.NONE);

        game.performCurrentPlayerMove(new Move(0));
        game.performCurrentPlayerMove(new Move(1));
        game.performCurrentPlayerMove(new Move(0));
        game.performCurrentPlayerMove(new Move(1));
        game.performCurrentPlayerMove(new Move(0));
        game.performCurrentPlayerMove(new Move(1));
        game.performCurrentPlayerMove(new Move(0));

        Assertions.assertEquals(winnerChecker.determineWinner(), Color.RED);
    }
}
