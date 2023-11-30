package org.connect4.ai.enums;

public enum NodeType {
    MIN,
    MAX;

    public NodeType opposite() {
        return this == MIN ? MAX : MIN;
    }
}
