package org.connect4.game.ai;

import org.connect4.game.ai.strategies.AI;
import org.connect4.game.ai.strategies.RandomChoiceAI;
import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.exceptions.FullColumnException;
import org.connect4.game.logic.exceptions.InvalidColumnIndexException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RandomChoiceAITest {
    private Board board;
    private Color color;
    private AI ai;
    private Move nextMove;

    @BeforeEach
    public void setup() {
        board = new Board();
        color = Color.RED;
        ai = new RandomChoiceAI(board);
        nextMove = null;
    }

    @Test
    public void testGetNextMove() throws InvalidColumnIndexException, FullColumnException {
        testEmptyBoard();
        testFullBoard();
    }

    private void testEmptyBoard() {
        nextMove = ai.getNextMove();
        Assertions.assertTrue(nextMove.isValid(), "The column index should be within the bounds of the board");
    }

    private void testFullBoard() throws InvalidColumnIndexException, FullColumnException {
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                board.addPiece(col, color);
                color = color.opposite();
            }
        }
        nextMove = ai.getNextMove();
        Assertions.assertEquals(-1, nextMove.getColumn(), "The RandomChoiceAI should return -1 indicating that the Board is full");
    }
}
