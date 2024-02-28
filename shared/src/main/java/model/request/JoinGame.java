package model.request;

public class JoinGame {
    private String playerColor;
    private int gameID;

    public JoinGame() {
    }

    public JoinGame(String playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }
    public int getGameID(){
        return gameID;
    }
}