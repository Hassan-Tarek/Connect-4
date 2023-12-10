package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.ai.utils.Node;
import org.connect4.logging.AILogger;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * A class represents a Minimax AI player without pruning in the Connect-4 game.
 * @author hassan
 */
public class MinimaxWithoutPruningAI extends MinimaxAI {
    private static final Logger logger = AILogger.getLogger();

    /**
     * Constructs a MinimaxWithoutPruningAI player with the specified starting game node and depth.
     * @param node The current game node.
     * @param depth The depth limit for the Minimax search.
     */
    public MinimaxWithoutPruningAI(Node node, int depth) {
        super(AIType.MINIMAX_WITHOUT_PRUNING_AI, node, depth);
        logger.finest("MinimaxWithoutPruningAI player has been instantiated!");
    }

    /**
     * Implements the minimax algorithm without pruning.
     * @param node The current game node.
     * @param depth The remaining depth for the search.
     * @return An optional containing the best move node found, or empty if no move is possible.
     */
    @Override
    protected Optional<Node> minimax(Node node, int depth) {
        Optional<Node> bestNode;

        if (node.isMaxNode()) {
            bestNode = maximize(node, depth);
        } else {
            bestNode = minimize(node, depth);
        }

        return bestNode;
    }

    /**
     * Minimizes the score for the current player.
     * @param node The current game node.
     * @param depth The remaining depth for the search.
     * @return An optional containing the best move node found, or empty if no move is possible.
     */
    private Optional<Node> minimize(Node node, int depth) {
        logger.finest("Entered MinimaxWithoutPruningAI minimize method.");

        if (node.isTerminal() || depth == 0) {
            logger.fine("Reached terminal node or maximum depth in minimize method.");
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

        logger.finest("Exiting MinimaxWithoutPruningAI minimize method.");
        return Optional.ofNullable(bestNode);
    }

    /**
     * Maximizes the score for the current player.
     * @param node The current game node.
     * @param depth The remaining depth for the search.
     * @return An optional containing the best move node found, or empty if no move is possible.
     */
    private Optional<Node> maximize(Node node, int depth) {
        logger.finest("Entered MinimaxWithoutPruningAI maximize method.");

        if (node.isTerminal() || depth == 0) {
            logger.fine("Reached terminal node or maximum depth in maximize method.");
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

        logger.finest("Exiting MinimaxWithoutPruningAI maximize method.");
        return Optional.ofNullable(bestNode);
    }
}
