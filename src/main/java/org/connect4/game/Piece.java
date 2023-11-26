package org.connect4.game;

public class Piece {
    private final Position position;
    private final Color color;

    public Piece(Position position, Color color) {
        this.position = position;
        this.color = color;
    }

    public Position getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }
}
