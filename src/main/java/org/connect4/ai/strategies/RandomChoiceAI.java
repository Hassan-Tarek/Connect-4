package org.connect4.ai.strategies;

import org.connect4.ai.enums.AIType;
import org.connect4.game.core.Board;
import org.connect4.logging.AILogger;

import java.util.Random;
import java.util.logging.Logger;

public class RandomChoiceAI extends AI {
    private static final Logger logger = AILogger.getLogger();

    private final Board board;
    private final Random random;

    public RandomChoiceAI(Board board) {
        super(AIType.RANDOM_CHOICE_AI);
        this.board = board;
        this.random = new Random();
        logger.info("RandomChoiceAI player has been instantiated!");
    }

    @Override
    public int getNextMove() {
        int col;

        do {
            col = random.nextInt(Board.COLS) + 1;
        } while (!board.isValidMove(col));

        logger.info("Next move: " + col);
        return col;
    }
}
