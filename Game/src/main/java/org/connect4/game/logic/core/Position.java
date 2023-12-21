package org.connect4.game.logic.core;

/**
 * A class represents the position of game piece.
 * @author Hassan
 */
public class Position implements Cloneable {
    private final int row;
    private final int column;

    /**
     * Constructs a new position with the specified row and column.
     * @param row The row coordinate.
     * @param column The column coordinate.
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Gets the row coordinate of the position.
     * @return The row coordinate.
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column coordinate of the position.
     * @return The column coordinate.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Creates and returns a copy of this Position object.
     * @return A new Position object that is a copy of this instance.
     */
    @Override
    public Position clone() {
        try {
            return (Position) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
