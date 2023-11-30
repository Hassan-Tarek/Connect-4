package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.game.core.Board;

public class RandomChoiceAI extends AI {
    private final Board board;

    public RandomChoiceAI(Board board) {
        super(AIType.RANDOM_CHOICE_AI);
        this.board = board;
    }

    @Override
    public int getNextMove() {
        return 0;
    }
}
