package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.game.core.Board;

import java.util.Random;

public class RandomChoiceAI extends AI {
    private final Board board;
    private final Random random;

    public RandomChoiceAI(Board board) {
        super(AIType.RANDOM_CHOICE_AI);
        this.board = board;
        this.random = new Random();
    }

    @Override
    public int getNextMove() {
        int col;

        do {
            col = random.nextInt(Board.COLS) + 1;
        } while (!board.isValidMove(col));

        return col;
    }
}
