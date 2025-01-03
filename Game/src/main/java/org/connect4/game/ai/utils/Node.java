package org.connect4.game.ai.utils;

import org.connect4.game.ai.enums.NodeType;
import org.connect4.game.ai.heuristics.Heuristic;
import org.connect4.game.logging.AILogger;
import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.exceptions.InvalidMoveException;
import org.connect4.game.logic.utils.WinnerChecker;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A class represents a node in the game tree for the Minimax algorithm.
 * @author hassan
 */
public class Node {
    private static final Logger LOGGER = AILogger.getLogger();

    private final State state;
    private final NodeType nodeType;
    private final Move move;
    private final boolean isTerminal;
    private final WinnerChecker winnerChecker;
    private int score;

    /**
     * Constructs a new node with the given state, node type, and column index.
     * @param state The state of the game board.
     * @param nodeType The type of node (MIN or MAX).
     * @param move The move was made.
     */
    public Node(State state, NodeType nodeType, Move move) {
        this.state = state;
        this.nodeType = nodeType;
        this.move = move;
        this.isTerminal = determineTerminal();
        this.winnerChecker = new WinnerChecker(state.getBoard());
        this.score = Heuristic.evaluate(state.getBoard());
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
     * Gets the move was made in this node.
     * @return The move.
     */
    public Move getMove() {
        return move;
    }

    /**
     * Checks if this node is a terminal node.
     * @return true if the node is terminal, false otherwise.
     */
    public boolean isTerminal() {
        return isTerminal;
    }

    /**
     * Gets the score of this node.
     * @return The score of this node.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score of this node.
     * @param score The score to be set.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Gets the list of child nodes of this node.
     * @return The list of child nodes.
     */
    public List<Node> getChildren() {
        return expand();
    }

    /**
     * Checks if this node is a MAX node.
     * @return true if this is a MAX node, false otherwise.
     */
    public boolean isMaxNode() {
        return this.nodeType == NodeType.MAX;
    }

    /**
     * Prints the tree structure starting from the current node.
     * @param depth The depth of the tree to print.
     * @param out The output stream where the tree structure will be printed.
     */
    public void printChildrenTree(int depth, OutputStream out) {
        NodePrinter printer = new NodePrinter(this, depth, out);
        printer.print();
    }

    /**
     * Determines if the current node is terminal.
     * @return true if the node is terminal, false otherwise.
     */
    private boolean determineTerminal() {
        boolean isTerminal = state.getBoard().isFull() || winnerChecker.hasWinner();
        if (isTerminal)
            LOGGER.info("Reach a terminal node");
        return isTerminal;
    }

    /**
     * Expands the current node by generating child nodes.
     * @return The list of child nodes.
     */
    private List<Node> expand() {
        List<Node> childrenList = new ArrayList<>();

        for (int i = 0; i < Board.COLS; i++) {
            State childState = state.clone();
            Move newMove = new Move(i);
            childState.setPlayerColor(state.getPlayerColor().opposite());
            NodeType childNodeType = nodeType.opposite();
            if (newMove.isValid(childState.getBoard())) {
                try {
                    childState.getBoard().addPiece(i, childState.getPlayerColor());
                } catch (InvalidMoveException e) {
                    LOGGER.severe("Invalid move: " + e.getMessage());
                }
                Node child = new Node(childState, childNodeType, newMove);
                childrenList.add(child);
            }
        }

        LOGGER.info("This node has: " + childrenList.size() + " child nodes.");
        return childrenList;
    }

    /**
     * Calculates the best score for the current node.
     * @param depth The remaining depth.
     * @return The best score for the current node.
     */
    private int getBestScore(int depth) {
        if (this.isTerminal() || depth < 0) {
            return this.getScore();
        }

        int bestScore;
        if (this.isMaxNode()) {
            bestScore = Integer.MIN_VALUE;
            for (Node child : this.getChildren()) {
                int childScore = child.getBestScore(depth - 1);
                child.setScore(childScore); // Update child score
                bestScore = Math.max(bestScore, childScore);
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            for (Node child : this.getChildren()) {
                int childScore = child.getBestScore(depth - 1);
                child.setScore(childScore); // Update child score
                bestScore = Math.min(bestScore, childScore);
            }
        }

        return bestScore;
    }

    /**
     * Calculates the best scores for the children nodes of the current node.
     * @param depth The remaining depth.
     * @return A list of the best scores for each child node.
     */
    private List<Integer> getChildrenBestScores(int depth) {
        List<Integer> childrenBestScores = new ArrayList<>();
        for (Node child : this.getChildren()) {
            int childBestScore = child.getBestScore(depth - 1);
            childrenBestScores.add(childBestScore);
        }
        return childrenBestScores;
    }

    /**
     * A class responsible for visualizing the game tree.
     * @param root The root node of the tree to be printed.
     * @param depth The maximum depth of the tree to be printed.
     * @param out The output stream where the tree structure will be printed.
     */
    private record NodePrinter(Node root, int depth, OutputStream out) {
        /**
         * Prints the game tree.
         */
        public void print() {
            printNode(root, "", depth, false);
        }

        /**
         * Prints the node along with its children in a tree structure.
         * @param node The current node to be printed.
         * @param prefix The prefix to be added to the node's representation.
         * @param depth The depth of the tree to be printed.
         * @param isLeaf Indicates whether the node is a leaf node.
         */
        private void printNode(Node node, String prefix, int depth, boolean isLeaf) {
            if (node == null || depth < 0) {
                return;
            }

            List<Node> children = node.getChildren();
            int nodeBestScore = node.getBestScore(depth);
            List<Integer> nodeChildrenBestScores = node.getChildrenBestScores(depth);
            String type = node.nodeType.name().toLowerCase();
            StringBuilder message = new StringBuilder();
            message.append("<").append(Character.toUpperCase(type.charAt(0)))
                    .append(type.substring(1)).append(">")
                    .append(" => ").append("[");
            for (int i = 0; i < nodeChildrenBestScores.size(); i++) {
                message.append(nodeChildrenBestScores.get(i));
                if (i < nodeChildrenBestScores.size() - 1) {
                    message.append(", ");
                }
            }
            message.append("] = ").append(nodeBestScore);
            try {
                out.write((prefix + (isLeaf ? "└─ " : "├─ ") + message + '\n').getBytes());
            } catch (IOException e) {
                LOGGER.severe("Failed to write to output stream: " + e.getMessage());
            }

            for (int i = 0; i < children.size(); i++) {
                printNode(children.get(i), prefix + (isLeaf ? "   " : "│  "),
                        depth - 1, (i == children.size() - 1));
            }
        }
    }
}
