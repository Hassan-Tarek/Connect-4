package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.ai.utils.Node;

import java.util.Optional;

public class MinimaxWithPruningAI extends MinimaxAI {
    public MinimaxWithPruningAI(Node node, int depth) {
        super(AIType.MINIMAX_WITH_PRUNING_AI, node, depth);
    }

    @Override
    public Optional<Node> getBestMove() {
        return minimax(getNode(), getDepth(), Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private Optional<Node> minimax(Node node, int depth, int alpha, int beta) {
        return Optional.empty();
    }
}
