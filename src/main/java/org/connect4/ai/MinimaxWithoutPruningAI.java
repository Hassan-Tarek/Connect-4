package org.connect4.ai;

import org.connect4.game.Board;

public class MinimaxWithoutPruningAI extends AI {
    public MinimaxWithoutPruningAI(AIType aiType) {
        super(aiType);
    }

    @Override
    public int getNextMove(Board board, int depth) {
        return 0;
    }
}
