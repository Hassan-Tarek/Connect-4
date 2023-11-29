package org.connect4.ai;

import org.connect4.game.Player;
import org.connect4.game.utils.Color;
import org.connect4.game.utils.PlayerType;

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
