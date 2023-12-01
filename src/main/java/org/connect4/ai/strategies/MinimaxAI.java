package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.ai.utils.Node;

import java.util.Optional;

public abstract class MinimaxAI extends AI {
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
        return getBestMove().map(Node::getCol).orElse(-1);
    }

    public abstract Optional<Node> getBestMove();
}
