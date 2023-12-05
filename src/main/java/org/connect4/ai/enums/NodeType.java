package org.connect4.ai.enums;

/**
 * Enum representing the types of nodes in a Minimax tree.
 * @author hassan
 */
public enum NodeType {
    MIN,
    MAX;

    /**
     * Returns the opposite NodeType.
     * @return The opposite NodeType.
     */
    public NodeType opposite() {
        return this == MIN ? MAX : MIN;
    }
}
