package org.connect4.ai;

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
        return getBestMove().getCol();
    }

    public abstract Node getBestMove();
}
