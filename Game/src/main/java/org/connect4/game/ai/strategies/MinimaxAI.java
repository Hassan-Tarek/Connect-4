package org.connect4.game.ai.strategies;

import org.connect4.game.ai.enums.AIType;
import org.connect4.game.ai.utils.Node;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logging.AILogger;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Abstract class representing a Minimax AI player in the Connect-4 game.
 * @author hassan
 */
public abstract class MinimaxAI extends AI {
    private static final Logger LOGGER = AILogger.getLogger();

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
     * @return The next move.
     */
    @Override
    public Move getNextMove() {
        Optional<Node> result = minimax(node, depth);

        if (result.isPresent()) {
            Move bestMove = result.get().getMove();
            LOGGER.fine("Best move found at column: " + bestMove.getColumn());
            return bestMove;
        } else {
            LOGGER.warning("No valid move found!");
            return null;
        }
    }

    /**
     * Abstract method to be implemented by subclasses for Minimax search.
     * @param node The current game node.
     * @param depth The remaining depth for the search.
     * @return An optional containing the best move node found, or empty if no move is possible.
     */
    protected abstract Optional<Node> minimax(Node node, int depth);
}
