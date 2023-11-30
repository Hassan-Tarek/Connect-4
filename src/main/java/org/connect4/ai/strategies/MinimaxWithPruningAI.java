package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.ai.utils.Node;

public class MinimaxWithPruningAI extends MinimaxAI {
    public MinimaxWithPruningAI(Node node, int depth) {
        super(AIType.MINIMAX_WITH_PRUNING_AI, node, depth);
    }

    @Override
    public Node getBestMove() {
        return null;
    }
}
