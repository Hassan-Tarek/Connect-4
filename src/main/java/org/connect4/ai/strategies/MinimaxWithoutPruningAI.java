package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.ai.utils.Node;

import java.util.Optional;

public class MinimaxWithoutPruningAI extends MinimaxAI {
    public MinimaxWithoutPruningAI(Node node, int depth) {
        super(AIType.MINIMAX_WITHOUT_PRUNING_AI, node, depth);
    }

    @Override
    protected Optional<Node> minimax(Node node, int depth) {
        if (node.isTerminal() || depth == 0) {
            return Optional.of(node);
        }

        Optional<Node> bestNode;
        if (node.isMaxNode()) {
            bestNode = maximize(node, depth);
        } else {
            bestNode = minimize(node, depth);
        }

        return bestNode;
    }

    private Optional<Node> minimize(Node node, int depth) {
        if (node.isTerminal() || depth == 0) {
            return Optional.of(node);
        }

        int bestScore = Integer.MAX_VALUE;
        Node bestNode = null;
        for (Node child : node.getChildren()) {
            Optional<Node> resultNode = maximize(child, depth - 1);
            int resultScore = resultNode.map(Node::getScore).orElse(Integer.MAX_VALUE);

            if (resultScore < bestScore) {
                bestScore = resultScore;
                bestNode = resultNode.orElse(null);
            }
        }

        return Optional.ofNullable(bestNode);
    }

    private Optional<Node> maximize(Node node, int depth) {
        if (node.isTerminal() || depth == 0) {
            return Optional.of(node);
        }

        int bestScore = Integer.MIN_VALUE;
        Node bestNode = null;
        for (Node child : node.getChildren()) {
            Optional<Node> resultNode = minimize(child, depth - 1);
            int resultScore = resultNode.map(Node::getScore).orElse(Integer.MIN_VALUE);

            if (resultScore > bestScore) {
                bestScore = resultScore;
                bestNode = resultNode.orElse(null);
            }
        }

        return Optional.ofNullable(bestNode);
    }
}
