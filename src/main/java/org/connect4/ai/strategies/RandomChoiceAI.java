package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.game.core.Board;
import org.connect4.logging.AILogger;

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
        logger.info("RandomChoiceAI player has been instantiated!");
    }

    /**
     * Generates a random move for the current game board.
     * @return The generated move.
     */
    @Override
    public int getNextMove() {
        if (board.isFull()) {
            logger.warning("Board is full!");
            return -1;
        }

        int col;
        do {
            col = random.nextInt(Board.COLS) + 1;
        } while (!board.isValidMove(col));

        logger.info("Next move: " + col);
        return col;
    }
}
