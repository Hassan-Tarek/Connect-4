package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.ai.utils.Node;

import java.util.Optional;

public class MinimaxWithoutPruningAI extends MinimaxAI {
    public MinimaxWithoutPruningAI(Node node, int depth) {
        super(AIType.MINIMAX_WITHOUT_PRUNING_AI, node, depth);
    }

    @Override
    public Optional<Node> getBestMove() {
        return minimax(getNode(), getDepth());
    }

    private Optional<Node> minimax(Node node, int depth) {
        return Optional.empty();
    }
}
