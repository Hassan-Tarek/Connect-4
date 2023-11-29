package org.connect4.ai;

public class MinimaxWithoutPruningAI extends MinimaxAI {
    public MinimaxWithoutPruningAI(Node node, int depth) {
        super(AIType.MINIMAX_WITHOUT_PRUNING_AI, node, depth);
    }

    @Override
    public Node getBestMove() {
        return null;
    }
}
