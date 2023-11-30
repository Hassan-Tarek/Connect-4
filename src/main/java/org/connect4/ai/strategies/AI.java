package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.game.core.Player;
import org.connect4.game.enums.Color;
import org.connect4.game.enums.PlayerType;

public abstract class AI extends Player {
    private final AIType aiType;

    public AI(AIType aiType) {
        super("AI", null, Color.YELLOW, PlayerType.COMPUTER);
        this.aiType = aiType;
    }

    public AIType getAiType() {
        return aiType;
    }

    public abstract int getNextMove();
}
