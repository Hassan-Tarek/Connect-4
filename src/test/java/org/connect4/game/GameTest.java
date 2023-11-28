package org.connect4.game;

import org.connect4.game.exceptions.InvalidMoveException;
import org.connect4.game.utils.Color;
import org.connect4.game.utils.GameType;
import org.connect4.game.utils.PlayerType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTest {
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
    public void testGameInitialization() {
        Assertions.assertNotNull(games[0]);
        Assertions.assertEquals(boards[0], games[0].getBoard());
        Assertions.assertEquals(redPlayer, games[0].getRedPlayer());
        Assertions.assertEquals(yellowPlayer, games[0].getYellowPlayer());
        Assertions.assertEquals(GameType.HUMAN_VS_HUMAN, games[0].getGameType());
    }

    @Test
    public void testHasWinner() {
        try {
            redPlayer.makeMove(games[1].getBoard(), 0);
            redPlayer.makeMove(games[1].getBoard(), 0);
            redPlayer.makeMove(games[1].getBoard(), 0);
            redPlayer.makeMove(games[1].getBoard(), 0);

            yellowPlayer.makeMove(games[2].getBoard(), 0);
            yellowPlayer.makeMove(games[2].getBoard(), 1);
            yellowPlayer.makeMove(games[2].getBoard(), 2);
            yellowPlayer.makeMove(games[2].getBoard(), 3);

            redPlayer.makeMove(games[3].getBoard(), 0);
            yellowPlayer.makeMove(games[3].getBoard(), 1);
            redPlayer.makeMove(games[3].getBoard(), 1);
            yellowPlayer.makeMove(games[3].getBoard(), 2);
            yellowPlayer.makeMove(games[3].getBoard(), 2);
            redPlayer.makeMove(games[3].getBoard(), 2);
            yellowPlayer.makeMove(games[3].getBoard(), 3);
            yellowPlayer.makeMove(games[3].getBoard(), 3);
            yellowPlayer.makeMove(games[3].getBoard(), 3);
            redPlayer.makeMove(games[3].getBoard(), 3);

            Assertions.assertFalse(games[0].hasWinner());
            Assertions.assertTrue(games[1].hasWinner());
            Assertions.assertTrue(games[2].hasWinner());
            Assertions.assertTrue(games[3].hasWinner());
        }
        catch (InvalidMoveException ex) {
            Assertions.fail("Unexpected InvalidMoveException: " + ex.getMessage());
        }
    }

    @Test
    public void testGetWinner() {
        try {
            redPlayer.makeMove(games[1].getBoard(), 0);
            redPlayer.makeMove(games[1].getBoard(), 0);
            redPlayer.makeMove(games[1].getBoard(), 0);
            redPlayer.makeMove(games[1].getBoard(), 0);

            yellowPlayer.makeMove(games[2].getBoard(), 0);
            yellowPlayer.makeMove(games[2].getBoard(), 1);
            yellowPlayer.makeMove(games[2].getBoard(), 2);
            yellowPlayer.makeMove(games[2].getBoard(), 3);

            redPlayer.makeMove(games[3].getBoard(), 0);
            yellowPlayer.makeMove(games[3].getBoard(), 1);
            redPlayer.makeMove(games[3].getBoard(), 1);
            yellowPlayer.makeMove(games[3].getBoard(), 2);
            yellowPlayer.makeMove(games[3].getBoard(), 2);
            redPlayer.makeMove(games[3].getBoard(), 2);
            yellowPlayer.makeMove(games[3].getBoard(), 3);
            yellowPlayer.makeMove(games[3].getBoard(), 3);
            yellowPlayer.makeMove(games[3].getBoard(), 3);
            redPlayer.makeMove(games[3].getBoard(), 3);

            Assertions.assertNull(games[0].getWinner());
            Assertions.assertEquals(redPlayer, games[1].getWinner());
            Assertions.assertEquals(yellowPlayer, games[2].getWinner());
            Assertions.assertEquals(redPlayer, games[3].getWinner());
        } catch (InvalidMoveException ex) {
            Assertions.fail("Unexpected InvalidMoveException: " + ex.getMessage());
        }
    }

    @Test
    public void testPerformCurrentPlayerMove() {
        Assertions.assertEquals(redPlayer, games[0].getCurrentPlayer());

        try {
            games[0].performCurrentPlayerMove(0);
        } catch (InvalidMoveException ex) {
            Assertions.fail("Unexpected InvalidMoveException: " + ex.getMessage());
        }

        Assertions.assertEquals(yellowPlayer, games[0].getCurrentPlayer());
    }
}
