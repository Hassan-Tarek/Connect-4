package org.connect4.game.ai;

import org.connect4.game.ai.enums.AIType;
import org.connect4.game.ai.enums.NodeType;
import org.connect4.game.ai.strategies.AI;
import org.connect4.game.ai.strategies.MinimaxWithPruningAI;
import org.connect4.game.ai.strategies.MinimaxWithoutPruningAI;
import org.connect4.game.ai.strategies.RandomChoiceAI;
import org.connect4.game.ai.utils.Node;
import org.connect4.game.ai.utils.State;
import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.enums.Color;

/**
 * A factory class that provides different AI players based on the specified AI type.
 * @author Hassan
 */
public class AIFactory {
    /**
     * Creates an AI player based on the specified AI type.
     * @param board  The current state of the board to be used by the AI.
     * @param aiType The type of AI to create.
     * @return An AI player that corresponds to the specified AI type.
     */
    public static AI getAIPlayer(Board board, AIType aiType) {
        State state = new State(board, Color.RED);
        Node node = new Node(state, NodeType.MAX, null);

        return switch (aiType) {
            case RANDOM_CHOICE_AI -> new RandomChoiceAI(board);
            case MINIMAX_WITHOUT_PRUNING_AI -> new MinimaxWithoutPruningAI(node, 4);
            case MINIMAX_WITH_PRUNING_AI -> new MinimaxWithPruningAI(node, 6);
        };
    }
}
