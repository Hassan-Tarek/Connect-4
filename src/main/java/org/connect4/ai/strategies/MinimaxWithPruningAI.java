package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.ai.utils.Node;
import org.connect4.logging.AILogger;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * A class represents a Minimax AI player with alpha-beta pruning in the Connect-4 game.
 * @author hassan
 */
public class MinimaxWithPruningAI extends MinimaxAI {
    private static final Logger logger = AILogger.getLogger();

    /**
     * Constructs a MinimaxWithPruningAI player with the specified starting game node and depth.
     * @param node The current game node.
     * @param depth The depth limit for the Minimax search.
     */
    public MinimaxWithPruningAI(Node node, int depth) {
        super(AIType.MINIMAX_WITH_PRUNING_AI, node, depth);
        logger.info("MinimaxWithPruningAI player has been instantiated!");
    }

    /**
     * Implements the minimax algorithm with alpha-beta pruning.
     * @param node The current game node.
     * @param depth The remaining depth for the search.
     * @return An optional containing the best move node found, or empty if no move is possible.
     */
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

    /**
     * Minimizes the score for the current player while applying alpha-beta pruning.
     * @param node The current game node.
     * @param depth The remaining depth for the search.
     * @param alpha The alpha value for pruning.
     * @param beta The beta value for pruning.
     * @return An optional containing the best move node found, or empty if no move is possible.
     */
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

    /**
     * Maximizes the score for the current player while applying alpha-beta pruning.
     * @param node The current game node.
     * @param depth The remaining depth for the search.
     * @param alpha The alpha value for pruning.
     * @param beta The beta value for pruning.
     * @return An optional containing the best move node found, or empty if no move is possible.
     */
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
