package org.connect4.ai;

public class MinimaxWithPruningAI extends MinimaxAI {
    public MinimaxWithPruningAI(Node node, int depth) {
        super(AIType.MINIMAX_WITH_PRUNING_AI, node, depth);
    }

    @Override
    public Node getBestMove() {
        return null;
    }
}
