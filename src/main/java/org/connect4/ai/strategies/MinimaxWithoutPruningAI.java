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
        if (node.isTerminal() || depth == 0) {
            return Optional.of(node);
        }

        int bestScore = node.isMaxNode() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Node bestNode = null;
        if (node.isMaxNode()) {
            for (Node child : node.getChildren()) {
                Optional<Node> resultNode = minimax(child, depth - 1);
                int resultScore = resultNode.map(Node::getScore).orElse(Integer.MIN_VALUE);

                if (resultScore > bestScore) {
                    bestScore = resultScore;
                    bestNode = resultNode.orElse(null);
                }
            }
        }
        else {
            for (Node child : node.getChildren()) {
                Optional<Node> resultNode = minimax(child, depth - 1);
                int resultScore = resultNode.map(Node::getScore).orElse(Integer.MAX_VALUE);

                if (resultScore < bestScore) {
                    bestScore = resultScore;
                    bestNode = resultNode.orElse(null);
                }
            }
        }

        return Optional.ofNullable(bestNode);
    }
}
