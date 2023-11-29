package org.connect4.ai;

import org.connect4.game.Board;

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
