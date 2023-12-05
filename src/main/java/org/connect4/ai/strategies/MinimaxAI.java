package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.ai.utils.Node;
import org.connect4.logging.AILogger;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Abstract class representing a Minimax AI player in the Connect-4 game.
 * @author hassan
 */
public abstract class MinimaxAI extends AI {
    private static final Logger logger = AILogger.getLogger();

    private final Node node;
    private final int depth;

    /**
     * Constructs a MinimaxAI player with the specified AI type, starting game node, and depth limit for the search algorithm.
     * @param aiType The type of AI.
     * @param node The current game node.
     * @param depth The depth limit for the Minimax search.
     */
    public MinimaxAI(AIType aiType, Node node, int depth) {
        super(aiType);
        this.node = node;
        this.depth = depth;
    }

    /**
     * Gets the starting game node for the Minimax search.
     * @return The starting game node.
     */
    public Node getNode() {
        return node;
    }

    /**
     * Gets the depth limit for the Minimax search.
     * @return The depth limit.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Computes and returns the next move based on the Minimax algorithm.
     * @return The column index of the next move.
     */
    @Override
    public int getNextMove() {
        Optional<Node> result = minimax(node, depth);
        int nextMove;

        if (result.isPresent()) {
            Node bestMove = result.get();
            logger.fine("Best move found at column: " + bestMove.getCol());
            nextMove = bestMove.getCol();
        } else {
            logger.info("No valid move found!");
            nextMove = -1;
        }

        return nextMove;
    }

    /**
     * Abstract method to be implemented by subclasses for Minimax search.
     * @param node The current game node.
     * @param depth The remaining depth for the search.
     * @return An optional containing the best move node found, or empty if no move is possible.
     */
    protected abstract Optional<Node> minimax(Node node, int depth);
}
