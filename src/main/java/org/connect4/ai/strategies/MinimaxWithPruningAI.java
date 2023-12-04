package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.ai.utils.Node;
import org.connect4.logging.AILogger;

import java.util.Optional;
import java.util.logging.Logger;

public class MinimaxWithPruningAI extends MinimaxAI {
    private static final Logger logger = AILogger.getLogger();

    public MinimaxWithPruningAI(Node node, int depth) {
        super(AIType.MINIMAX_WITH_PRUNING_AI, node, depth);
        logger.info("MinimaxWithPruningAI player has been instantiated!");
    }

    @Override
    protected Optional<Node> minimax(Node node, int depth) {
        int alphaValue = Integer.MIN_VALUE;
        int betaValue = Integer.MAX_VALUE;
        Optional<Node> bestNode;

        if (node.isMaxNode()) {
            bestNode = maximize(node, depth, alphaValue, betaValue);
        } else {
            bestNode = minimize(node, depth, alphaValue, betaValue);
        }

        return bestNode;
    }

    private Optional<Node> minimize(Node node, int depth, int alpha, int beta) {
        logger.finest("Entered MinimaxWithPruningAI minimize method.");

        if (node.isTerminal() || depth == 0) {
            logger.info("Reached terminal node or maximum depth in minimize method.");
            return Optional.of(node);
        }

        Node bestNode = null;
        for (Node child : node.getChildren()) {
            Optional<Node> resultNode = maximize(child, depth - 1, alpha, beta);
            int resultScore = resultNode.map(Node::getScore).orElse(Integer.MAX_VALUE);

            if (resultScore < beta) {
                beta = resultScore;
                bestNode = resultNode.orElse(null);
            }

            if (alpha >= beta) {
                break;
            }
        }

        logger.finest("Exiting MinimaxWithPruningAI minimize method.");
        return Optional.ofNullable(bestNode);
    }

    private Optional<Node> maximize(Node node, int depth, int alpha, int beta) {
        logger.finest("Entered MinimaxWithPruningAI maximize method.");

        if (node.isTerminal() || depth == 0) {
            logger.info("Reached terminal node or maximum depth in maximize method.");
            return Optional.of(node);
        }

        Node bestNode = null;
        for (Node child : node.getChildren()) {
            Optional<Node> resultNode = minimize(child, depth - 1, alpha, beta);
            int resultScore = resultNode.map(Node::getScore).orElse(Integer.MIN_VALUE);

            if (resultScore > alpha) {
                alpha = resultScore;
                bestNode = resultNode.orElse(null);
            }

            if (alpha >= beta) {
                break;
            }
        }

        logger.finest("Exiting MinimaxWithPruningAI maximize method.");
        return Optional.ofNullable(bestNode);
    }
}
