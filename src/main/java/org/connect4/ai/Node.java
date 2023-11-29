package org.connect4.ai;

import org.connect4.game.Board;
import org.connect4.game.exceptions.InvalidMoveException;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final State state;
    private final NodeType nodeType;
    private final int col;
    private final int score;
    private final List<Node> children;

    public Node(State state, NodeType nodeType,  int col) throws CloneNotSupportedException {
        this.state = state;
        this.nodeType = nodeType;
        this.col = col;
        this.score = Heuristic.evaluate(state);
        this.children = expand();
    }

    public State getState() {
        return state;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public int getCol() {
        return col;
    }

    public int getScore() {
        return score;
    }

    public List<Node> getChildren() {
        return children;
    }

    public boolean isMaxNode() {
        return this.nodeType == NodeType.MAX;
    }

    private List<Node> expand() throws CloneNotSupportedException {
        List<Node> childrenList = new ArrayList<>();

        for (int i = 0; i < Board.COLS; i++) {
            if (state.getBoard().isValidMove(i)) {
                State childState = state.clone();
                NodeType childNodeType = nodeType.opposite();
                try {
                    childState.getBoard().addPiece(i, childState.getPlayerColor().opposite());
                } catch (InvalidMoveException e) {
                    throw new RuntimeException(e);
                }
                Node child = new Node(childState, childNodeType, i);
                childrenList.add(child);
            }
        }

        return childrenList;
    }
}
