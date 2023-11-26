package org.connect4.game;

public class Board {
    public static final int ROWS = 6;
    public static final int COLS = 7;

    private Piece[][] pieces;
    private int[] currentRowIndex;

    public Board() {
        initializeBoard();
    }

    public Piece[][] getPieces() {
        return pieces;
    }

    public void setPieces(Piece[][] pieces) {
        this.pieces = pieces;
    }

    private void initializeBoard() {
        pieces = new Piece[ROWS][COLS];
        currentRowIndex = new int[COLS];

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                pieces[i][j] = null;
            }
        }

        for (int i = 0; i < COLS; i++) {
            currentRowIndex[i] = 0;
        }
    }

    private boolean isValidMove(int col) {
        return col >= 0 && col < COLS
                && currentRowIndex[col] >= 0
                && currentRowIndex[col] < ROWS;
    }

    public void addPiece(int col, Color color) {
        if (isValidMove(col)) {
            Position position = new Position(currentRowIndex[col], col);
            pieces[col][currentRowIndex[col]] = new Piece(position, color);
            currentRowIndex[col]++;
        }
    }
}
