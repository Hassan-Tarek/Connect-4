package org.connect4.game.ai.enums;

import java.io.Serializable;

/**
 * Enum representing different types of AI strategies available in the Connect-4 game.
 * @author hassan
 */
public enum AIType implements Serializable {
    MINIMAX_WITHOUT_PRUNING_AI,
    MINIMAX_WITH_PRUNING_AI,
    RANDOM_CHOICE_AI
}
