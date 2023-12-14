package org.connect4.ai;

import org.connect4.ai.enums.AIType;
import org.connect4.ai.enums.NodeType;
import org.connect4.ai.strategies.MinimaxAI;
import org.connect4.ai.strategies.MinimaxWithPruningAI;
import org.connect4.ai.strategies.MinimaxWithoutPruningAI;
import org.connect4.ai.utils.Node;
import org.connect4.ai.utils.State;
import org.connect4.game.core.Board;
import org.connect4.game.core.Move;
import org.connect4.game.enums.Color;
import org.connect4.game.exceptions.FullColumnException;
import org.connect4.game.exceptions.InvalidColumnIndexException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

public class MinimaxAITest {
    private int depth;
    private MinimaxAI ai;

    @BeforeEach
    public void setup() {
        depth = 4;
        ai = null;
    }

    @TestFactory
    public Stream<DynamicTest> testMinimaxAI() {
        return Stream.of(AIType.MINIMAX_WITHOUT_PRUNING_AI, AIType.MINIMAX_WITH_PRUNING_AI)
                .map(aiType -> DynamicTest.dynamicTest("Test getNextMove for: " + aiType,
                        () -> {
                            Class<? extends MinimaxAI> aiClass = createAI(aiType);
                            testGetNextMove(aiClass);
                        }
                ));
    }

    public Class<? extends MinimaxAI> createAI(AIType aiType) {
        return switch (aiType) {
            case MINIMAX_WITHOUT_PRUNING_AI -> MinimaxWithoutPruningAI.class;
            case MINIMAX_WITH_PRUNING_AI -> MinimaxWithPruningAI.class;
            default -> throw new IllegalArgumentException("Unsupported AI type: " + aiType);
        };
    }

    private void testGetNextMove(Class<? extends MinimaxAI> aiClass) throws InvalidColumnIndexException, FullColumnException {
        testEmptyBoard(aiClass);
        testWinningMove(aiClass);
        testBlockingOpponentMove(aiClass);
    }

    private void testEmptyBoard(Class<? extends MinimaxAI> aiClass) {
        State state = new State(new Board(), Color.RED);
        Node node = new Node(state, NodeType.MAX, null);
        try {
            ai = aiClass.getConstructor(Node.class, int.class).newInstance(node, depth);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        Move move = ai.getNextMove();
        Assertions.assertTrue(move.isValid(), "The column index should be within the bounds of the board");
    }

    private void testWinningMove(Class<? extends MinimaxAI> aiClass) throws InvalidColumnIndexException, FullColumnException {
        State state = new State(new Board(), Color.YELLOW);
        state.getBoard().addPiece(0, Color.RED);
        state.getBoard().addPiece(1, Color.RED);
        state.getBoard().addPiece(2, Color.RED);
        Node node = new Node(state, NodeType.MAX, null);
        try {
            ai = aiClass.getConstructor(Node.class, int.class).newInstance(node, depth);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        Move move = ai.getNextMove();
        Assertions.assertEquals(3, move.getColumn(), "The AI should choose the winning move");
    }

    private void testBlockingOpponentMove(Class<? extends MinimaxAI> aiClass) throws InvalidColumnIndexException, FullColumnException {
        State state = new State(new Board(), Color.YELLOW);
        state.getBoard().addPiece(0, Color.YELLOW);
        state.getBoard().addPiece(1, Color.YELLOW);
        state.getBoard().addPiece(2, Color.YELLOW);
        Node node = new Node(state, NodeType.MAX, null);
        try {
            ai = aiClass.getConstructor(Node.class, int.class).newInstance(node, depth);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        Move move = ai.getNextMove();
        Assertions.assertEquals(3, move.getColumn(), "The AI should block the opponent's winning move");
    }
}
