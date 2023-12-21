package org.connect4.game.logic;

import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.exceptions.InvalidMoveException;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.enums.PlayerType;

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
        redPlayer = new Player("john", Color.RED, PlayerType.HUMAN);
        yellowPlayer = new Player("jodie", Color.YELLOW, PlayerType.HUMAN);
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
    public void testGetWinner() {
        try {
            redPlayer.makeMove(new Move(boards[1], 0));
            redPlayer.makeMove(new Move(boards[1], 0));
            redPlayer.makeMove(new Move(boards[1], 0));
            redPlayer.makeMove(new Move(boards[1], 0));

            yellowPlayer.makeMove(new Move(boards[2], 0));
            yellowPlayer.makeMove(new Move(boards[2], 1));
            yellowPlayer.makeMove(new Move(boards[2], 2));
            yellowPlayer.makeMove(new Move(boards[2], 3));

            redPlayer.makeMove(new Move(boards[3], 0));
            yellowPlayer.makeMove(new Move(boards[3], 1));
            redPlayer.makeMove(new Move(boards[3], 1));
            yellowPlayer.makeMove(new Move(boards[3], 2));
            yellowPlayer.makeMove(new Move(boards[3], 2));
            redPlayer.makeMove(new Move(boards[3], 2));
            yellowPlayer.makeMove(new Move(boards[3], 3));
            yellowPlayer.makeMove(new Move(boards[3], 3));
            yellowPlayer.makeMove(new Move(boards[3], 3));
            redPlayer.makeMove(new Move(boards[3], 3));

            Assertions.assertNull(games[0].getWinner());
            Assertions.assertEquals(redPlayer, games[1].getWinner());
            Assertions.assertEquals(yellowPlayer, games[2].getWinner());
            Assertions.assertEquals(redPlayer, games[3].getWinner());
        }
        catch (InvalidMoveException ex) {
            Assertions.fail("Unexpected InvalidMoveException: " + ex.getMessage());
        }
    }

    @Test
    public void testPerformCurrentPlayerMove() {
        Assertions.assertEquals(redPlayer, games[0].getCurrentPlayer());

        try {
            games[0].performCurrentPlayerMove(new Move(games[0].getBoard(), 0));
        } catch (InvalidMoveException ex) {
            Assertions.fail("Unexpected InvalidMoveException: " + ex.getMessage());
        }

        Assertions.assertEquals(yellowPlayer, games[0].getCurrentPlayer());
    }
}
