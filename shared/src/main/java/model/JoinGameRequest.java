package model;

public class JoinGameRequest {
    private int gameID;
    private String playerColor; // This can be null if the player joins as an observer

    // Constructor
    public JoinGameRequest() {
    }

    // Getters and Setters
    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }
}
