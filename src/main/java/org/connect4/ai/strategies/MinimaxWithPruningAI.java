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
        if (node.isTerminal() || depth == 0) {
            return Optional.of(node);
        }

        Node bestNode = null;
        if (node.isMaxNode()) {
            for (Node child : node.getChildren()) {
                Optional<Node> resultNode = minimax(child, depth - 1, alpha, beta);
                int resultScore = resultNode.map(Node::getScore).orElse(Integer.MIN_VALUE);

                if (resultScore > alpha) {
                    alpha = resultScore;
                    bestNode = resultNode.orElse(null);
                }

                if (alpha >= beta) {
                    break;
                }
            }
        }
        else {
            for (Node child : node.getChildren()) {
                Optional<Node> resultNode = minimax(child, depth - 1, alpha, beta);
                int resultScore = resultNode.map(Node::getScore).orElse(Integer.MAX_VALUE);

                if (resultScore < beta) {
                    beta = resultScore;
                    bestNode = resultNode.orElse(null);
                }

                if (alpha >= beta) {
                    break;
                }
            }
        }

        return Optional.ofNullable(bestNode);
    }
}
