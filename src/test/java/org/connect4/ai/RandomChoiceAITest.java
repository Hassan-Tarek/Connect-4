package org.connect4.ai;

import org.connect4.ai.strategies.AI;
import org.connect4.ai.strategies.RandomChoiceAI;
import org.connect4.game.core.Board;
import org.connect4.game.enums.Color;
import org.connect4.game.exceptions.InvalidMoveException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RandomChoiceAITest {
    private Board board;
    private Color color;
    private AI ai;
    private int nextMove;

    @BeforeEach
    public void setup() {
        board = new Board();
        color = Color.RED;
        ai = new RandomChoiceAI(board);
        nextMove = -1;
    }

    @Test
    public void testGetNextMove() throws InvalidMoveException {
        testEmptyBoard();
        testFullBoard();
    }

    private void testEmptyBoard() {
        nextMove = ai.getNextMove();
        Assertions.assertTrue(nextMove >= 0 && nextMove < Board.COLS, "The column index should be within the bounds of the board");
    }

    private void testFullBoard() throws InvalidMoveException {
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                board.addPiece(col, color);
                color = color.opposite();
            }
        }
        nextMove = ai.getNextMove();
        Assertions.assertEquals(-1, nextMove, "The RandomChoiceAI should return -1 indicating that the Board is full");
    }
}
