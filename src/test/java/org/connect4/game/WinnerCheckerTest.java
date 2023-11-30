package org.connect4.game;

import org.connect4.game.exceptions.InvalidMoveException;
import org.connect4.game.enums.Color;
import org.connect4.game.enums.GameType;
import org.connect4.game.enums.PlayerType;
import org.connect4.game.utils.WinnerChecker;

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
        redPlayer = new Player("John", "Hardy", Color.RED, PlayerType.HUMAN);
        yellowPlayer = new Player("Jodie", "Albert", Color.YELLOW, PlayerType.HUMAN);
        games = new Game[4];
        for (int i = 0; i < games.length; i++) {
            games[i] = new Game(boards[i], redPlayer, yellowPlayer, GameType.HUMAN_VS_HUMAN);
        }
    }

    @Test
    public void testHasWinner() {
        try {
            redPlayer.makeMove(games[1].getBoard(), 1);
            redPlayer.makeMove(games[1].getBoard(), 1);
            redPlayer.makeMove(games[1].getBoard(), 1);
            redPlayer.makeMove(games[1].getBoard(), 1);

            yellowPlayer.makeMove(games[2].getBoard(), 0);
            yellowPlayer.makeMove(games[2].getBoard(), 1);
            yellowPlayer.makeMove(games[2].getBoard(), 2);
            yellowPlayer.makeMove(games[2].getBoard(), 3);

            redPlayer.makeMove(games[3].getBoard(), 1);
            yellowPlayer.makeMove(games[3].getBoard(), 2);
            redPlayer.makeMove(games[3].getBoard(), 2);
            yellowPlayer.makeMove(games[3].getBoard(), 3);
            yellowPlayer.makeMove(games[3].getBoard(), 3);
            redPlayer.makeMove(games[3].getBoard(), 3);
            yellowPlayer.makeMove(games[3].getBoard(), 4);
            yellowPlayer.makeMove(games[3].getBoard(), 4);
            yellowPlayer.makeMove(games[3].getBoard(), 4);
            redPlayer.makeMove(games[3].getBoard(), 4);

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
