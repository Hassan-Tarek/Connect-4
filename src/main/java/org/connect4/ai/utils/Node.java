package org.connect4.ai.utils;

import org.connect4.ai.enums.NodeType;
import org.connect4.ai.heuristics.Heuristic;
import org.connect4.game.core.Board;
import org.connect4.game.exceptions.InvalidMoveException;
import org.connect4.game.utils.WinnerChecker;
import org.connect4.logging.AILogger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Node {
    private static final Logger logger = AILogger.getLogger();

    private final State state;
    private final NodeType nodeType;
    private final int col;
    private final int score;
    private final boolean isTerminal;
    private final List<Node> children;

    public Node(State state, NodeType nodeType, int col) {
        this.state = state;
        this.nodeType = nodeType;
        this.col = col;
        this.score = Heuristic.evaluate(state);
        this.isTerminal = determineTerminal();
        this.children = isTerminal ? new ArrayList<>() : expand();
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

    public boolean isTerminal() {
        return isTerminal;
    }

    public List<Node> getChildren() {
        return new ArrayList<>(children);
    }

    public boolean isMaxNode() {
        return this.nodeType == NodeType.MAX;
    }

    private boolean determineTerminal() {
        boolean isTerminal = state.getBoard().isFull() || WinnerChecker.hasWinner(state.getBoard());
        logger.info("Node terminal status: " + isTerminal);
        return isTerminal;
    }

    private List<Node> expand() {
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

        logger.info("This node has: " + childrenList.size() + " child nodes.");
        return childrenList;
    }
}
