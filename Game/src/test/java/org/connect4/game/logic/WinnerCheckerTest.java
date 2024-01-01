package org.connect4.game.logic;

import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.exceptions.InvalidMoveException;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.enums.PlayerType;
import org.connect4.game.logic.utils.WinnerChecker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WinnerCheckerTest {
    private Board[] boards;
    private Player redPlayer;
    private Player yellowPlayer;
    private Game[] games;

    @BeforeEach
    public void setup() {
        boards = new Board[4];
        for (int i = 0; i < boards.length; i++) {
            boards[i] = new Board();
        }
        redPlayer = new Player(Color.RED, PlayerType.HUMAN);
        yellowPlayer = new Player(Color.YELLOW, PlayerType.HUMAN);
        games = new Game[4];
        for (int i = 0; i < games.length; i++) {
            games[i] = new Game(boards[i], redPlayer, yellowPlayer, GameType.HUMAN_VS_HUMAN);
        }
    }

    @Test
    public void testHasWinner() {
        try {
            redPlayer.makeMove(new Move(1), games[1].getBoard());
            redPlayer.makeMove(new Move(1), games[1].getBoard());
            redPlayer.makeMove(new Move(1), games[1].getBoard());
            redPlayer.makeMove(new Move(1), games[1].getBoard());

            yellowPlayer.makeMove(new Move(0), games[2].getBoard());
            yellowPlayer.makeMove(new Move(1), games[2].getBoard());
            yellowPlayer.makeMove(new Move(2), games[2].getBoard());
            yellowPlayer.makeMove(new Move(3), games[2].getBoard());

            redPlayer.makeMove(new Move(1), games[3].getBoard());
            yellowPlayer.makeMove(new Move(2), games[3].getBoard());
            redPlayer.makeMove(new Move(2), games[3].getBoard());
            yellowPlayer.makeMove(new Move(3), games[3].getBoard());
            yellowPlayer.makeMove(new Move(3), games[3].getBoard());
            redPlayer.makeMove(new Move(3), games[3].getBoard());
            yellowPlayer.makeMove(new Move(4), games[3].getBoard());
            yellowPlayer.makeMove(new Move(4), games[3].getBoard());
            yellowPlayer.makeMove(new Move(4), games[3].getBoard());
            redPlayer.makeMove(new Move(4), games[3].getBoard());

            Assertions.assertFalse(WinnerChecker.hasWinner(boards[0]));
            Assertions.assertTrue(WinnerChecker.hasWinner(boards[1]));
            Assertions.assertTrue(WinnerChecker.hasWinner(boards[2]));
            Assertions.assertTrue(WinnerChecker.hasWinner(boards[3]));
        }
        catch (InvalidMoveException ex) {
            Assertions.fail("Unexpected InvalidMoveException: " + ex.getMessage());
        }
    }
}
