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
        logger.finest("MinimaxWithPruningAI player has been instantiated!");
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
        return minimax(node, depth, alphaValue, betaValue);
    }

    private Optional<Node> minimax(Node node, int depth, int alpha, int beta) {
        logger.finest("Entered MinimaxWithPruningAI minimax method.");

        if (node.isTerminal() || depth == 0) {
            logger.fine("Reached terminal node or maximum depth in minimax method.");
            return Optional.of(node);
        }

        Optional<Node> bestNode;
        if (node.isMaxNode()) {
            bestNode = maximize(node, depth, alpha, beta);
        } else {
            bestNode = minimize(node, depth, alpha, beta);
        }

        logger.finest("Exiting MinimaxWithPruningAI minimax method.");
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

        int bestScore = Integer.MAX_VALUE;
        Node bestNode = null;
        for (Node child : node.getChildren()) {
            Optional<Node> resultNode = minimax(child, depth - 1, alpha, beta);
            int resultScore = resultNode.map(Node::getScore).orElse(Integer.MAX_VALUE);
            child.setScore(resultScore);

            if (resultScore < bestScore) {
                bestScore = resultScore;
                bestNode = child;
            }

            beta = Math.min(beta, bestScore);
            if (beta <= alpha) {
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

        int bestScore = Integer.MIN_VALUE;
        Node bestNode = null;
        for (Node child : node.getChildren()) {
            Optional<Node> resultNode = minimax(child, depth - 1, alpha, beta);
            int resultScore = resultNode.map(Node::getScore).orElse(Integer.MIN_VALUE);
            child.setScore(resultScore);

            if (resultScore > bestScore) {
                bestScore = resultScore;
                bestNode = child;
            }

            alpha = Math.max(alpha, bestScore);
            if (alpha >= beta) {
                break;
            }
        }

        logger.finest("Exiting MinimaxWithPruningAI maximize method.");
        return Optional.ofNullable(bestNode);
    }
}
