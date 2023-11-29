package org.connect4.ai;

import org.connect4.game.Board;

import java.util.List;

public class Node {
    private final Board board;
    private final NodeType nodeType;
    private final int col;
    private int score;
    private List<Node> children;

    public Node(Board board, NodeType nodeType, int col) {
        this.board = board;
        this.nodeType = nodeType;
        this.col = col;
    }

    public Board getBoard() {
        return board;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public boolean isMaxNode() {
        return this.nodeType == NodeType.MAX;
    }

    public int getCol() {
        return col;
    }
}
