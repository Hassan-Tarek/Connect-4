package org.connect4.game.ai.strategies;

import org.connect4.game.ai.enums.AIType;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.PlayerType;

/**
 * Abstract class representing an AI player in the Connect-4 game.
 * @author hassan
 */
public abstract class AI extends Player {
    private final AIType aiType;

    /**
     * Constructs an AI player with the specified AI type.
     * @param aiType The type of AI.
     */
    public AI(AIType aiType) {
        super("ai", Color.YELLOW, PlayerType.COMPUTER);
        this.aiType = aiType;
    }

    /**
     * Gets the type of AI.
     * @return The AI type.
     */
    public AIType getAiType() {
        return aiType;
    }

    /**
     * Abstract method to be implemented by subclasses for determining the next move.
     * @return The next move.
     */
    public abstract Move getNextMove();
}
