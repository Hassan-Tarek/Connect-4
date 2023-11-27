package org.connect4.game;

/**
 * A class represents the position of game piece.
 * @author Hassan
 */
public class Position {
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
}
