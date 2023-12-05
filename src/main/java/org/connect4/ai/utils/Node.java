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

/**
 * A class represents a node in the game tree for the Minimax algorithm.
 * @author hassan
 */
public class Node {
    private static final Logger logger = AILogger.getLogger();

    private final State state;
    private final NodeType nodeType;
    private final int col;
    private final int score;
    private final boolean isTerminal;
    private final List<Node> children;

    /**
     * Constructs a new node with the given state, node type, and column index.
     * @param state The state of the game board.
     * @param nodeType The type of node (MIN or MAX).
     * @param col The column index where the move was made.
     */
    public Node(State state, NodeType nodeType, int col) {
        this.state = state;
        this.nodeType = nodeType;
        this.col = col;
        this.score = Heuristic.evaluate(state);
        this.isTerminal = determineTerminal();
        this.children = isTerminal ? new ArrayList<>() : expand();
    }

    /**
     * Gets the state of the game.
     * @return The state of the game.
     */
    public State getState() {
        return state;
    }

    /**
     * Gets the type of this node (MIN or MAX).
     * @return The type of node.
     */
    public NodeType getNodeType() {
        return nodeType;
    }

    /**
     * Gets the column index where the move was made in this node.
     * @return The column index.
     */
    public int getCol() {
        return col;
    }

    /**
     * Gets the score of this node.
     * @return The score of this node.
     */
    public int getScore() {
        return score;
    }

    /**
     * Checks if this node is a terminal node.
     * @return true if the node is terminal, false otherwise.
     */
    public boolean isTerminal() {
        return isTerminal;
    }

    /**
     * Gets the list of child nodes of this node.
     * @return The list of child nodes.
     */
    public List<Node> getChildren() {
        return new ArrayList<>(children);
    }

    /**
     * Checks if this node is a MAX node.
     * @return true if this is a MAX node, false otherwise.
     */
    public boolean isMaxNode() {
        return this.nodeType == NodeType.MAX;
    }

    /**
     * Determines if the current node is terminal.
     * @return true if the node is terminal, false otherwise.
     */
    private boolean determineTerminal() {
        boolean isTerminal = state.getBoard().isFull() || WinnerChecker.hasWinner(state.getBoard());
        logger.info("Node terminal status: " + isTerminal);
        return isTerminal;
    }

    /**
     * Expands the current node by generating child nodes.
     * @return The list of child nodes.
     */
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
