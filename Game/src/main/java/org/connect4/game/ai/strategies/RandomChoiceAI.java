package org.connect4.game.ai.strategies;

import org.connect4.game.ai.enums.AIType;
import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logging.AILogger;

import java.util.Random;
import java.util.logging.Logger;

/**
 * A class represents a RandomChoice AI player in the Connect-4 game.
 * @author hassan
 */
public class RandomChoiceAI extends AI {
    private static final Logger logger = AILogger.getLogger();

    private final Board board;
    private final Random random;

    /**
     * Constructs a RandomChoiceAI player with the specified game board.
     * @param board The current game board.
     */
    public RandomChoiceAI(Board board) {
        super(AIType.RANDOM_CHOICE_AI);
        this.board = board;
        this.random = new Random();
        logger.finest("RandomChoiceAI player has been instantiated!");
    }

    /**
     * Generates a random move for the current game board.
     * @return The next move.
     */
    @Override
    public Move getNextMove() {
        Move move = new Move(-1);
        if (board.isFull()) {
            logger.warning("Board is full!");
            return move;
        }

        int column;
        do {
            column = random.nextInt(Board.COLS);
            move = new Move(column);
        } while (!move.isValid(board));

        logger.info("Next move at column: " + column);
        return move;
    }
}
