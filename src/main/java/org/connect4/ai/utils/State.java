package org.connect4.ai.utils;

import org.connect4.game.core.Board;
import org.connect4.game.enums.Color;

public class State implements Cloneable {
    private final Board board;
    private final Color playerColor;

    public State(Board board, Color playercolor) {
        this.board = board;
        this.playerColor = playercolor;
    }

    public Board getBoard() {
        return board;
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    @Override
    public State clone() throws CloneNotSupportedException {
        Board clonedBoard = board.clone();
        return new State(clonedBoard, playerColor);
    }
}
