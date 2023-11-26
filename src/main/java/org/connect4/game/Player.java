package org.connect4.game;

public class Player {
    private String firstName;
    private String lastName;
    private final PlayerType playerType;
    private final Color color;
    private int score;

    public Player(String firstName, String lastName, Color color, PlayerType playerType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.playerType = playerType;
        this.color = color;
        this.score = 0;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public Color getColor() {
        return color;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void resetScore() {
        this.score = 0;
    }

    public void incrementScore() {
        this.score++;
    }

    public void makeMove(Board board, int colIndex) {
        board.addPiece(colIndex, color);
    }

    public boolean isWin(Game game) {
        if (game.hasWinner()) {
            return game.getWinner() == this;
        }

        return false;
    }
}
