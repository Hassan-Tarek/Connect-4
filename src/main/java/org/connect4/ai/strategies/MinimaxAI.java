package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.ai.utils.Node;
import org.connect4.logging.AILogger;

import java.util.Optional;
import java.util.logging.Logger;

public abstract class MinimaxAI extends AI {
    private static final Logger logger = AILogger.getLogger();

    private final Node node;
    private final int depth;

    public MinimaxAI(AIType aiType, Node node, int depth) {
        super(aiType);
        this.node = node;
        this.depth = depth;
    }

    public Node getNode() {
        return node;
    }

    public int getDepth() {
        return depth;
    }

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

    protected abstract Optional<Node> minimax(Node node, int depth);
}
