package org.connect4.ai;

import org.connect4.ai.heuristics.Heuristic;
import org.connect4.game.core.Board;
import org.connect4.game.enums.Color;

import org.connect4.game.exceptions.InvalidMoveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HeuristicTest {
    private Board[] boards;
    private int score;

    @BeforeEach
    public void setup() {
        boards = new Board[2];
        boards[0] = new Board();
        boards[1] = new Board();
        score = -1;
    }

    @Test
    public void testEvaluate() throws InvalidMoveException {
        testEmptyBoard();
        testRowOfRedPieces();
        testColumnOfYellowPieces();
        testLeftDiagonalOfRedPieces();
        testRightDiagonalOfYellowPieces();
    }

    private void testEmptyBoard() {
        score = Heuristic.evaluate(boards[0]);
        Assertions.assertEquals(0, score);
    }

    private void testRowOfRedPieces() throws InvalidMoveException {
        boards[0].addPiece(6, Color.RED);
        boards[0].addPiece(4, Color.RED);
        score = Heuristic.evaluate(boards[0]);
        Assertions.assertEquals(16, score);
    }

    private void testColumnOfYellowPieces() throws InvalidMoveException {
        boards[1].addPiece(6, Color.YELLOW);
        boards[1].addPiece(6, Color.YELLOW);
        boards[1].addPiece(6, Color.YELLOW);
        score = Heuristic.evaluate(boards[1]);
        Assertions.assertEquals(-117, score);
    }

    private void testLeftDiagonalOfRedPieces() throws InvalidMoveException {
        boards[0].addPiece(0, Color.RED);
        boards[0].addPiece(1, Color.YELLOW);
        boards[0].addPiece(1, Color.RED);
        boards[0].addPiece(2, Color.YELLOW);
        boards[0].addPiece(2, Color.YELLOW);
        boards[0].addPiece(2, Color.RED);
        boards[0].addPiece(3, Color.YELLOW);
        boards[0].addPiece(3, Color.YELLOW);
        boards[0].addPiece(3, Color.YELLOW);
        boards[0].addPiece(3, Color.RED);
        score = Heuristic.evaluate(boards[0]);
        Assertions.assertEquals(1034, score);
    }

    private void testRightDiagonalOfYellowPieces() throws InvalidMoveException {
        boards[1].addPiece(0, Color.RED);
        boards[1].addPiece(0, Color.RED);
        boards[1].addPiece(0, Color.RED);
        boards[1].addPiece(0, Color.YELLOW);
        boards[1].addPiece(1, Color.RED);
        boards[1].addPiece(1, Color.RED);
        boards[1].addPiece(1, Color.YELLOW);
        boards[1].addPiece(2, Color.RED);
        boards[1].addPiece(2, Color.YELLOW);
        boards[1].addPiece(3, Color.YELLOW);
        score = Heuristic.evaluate(boards[1]);
        Assertions.assertEquals(-1123, score);
    }
}
