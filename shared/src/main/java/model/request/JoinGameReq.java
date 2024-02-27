package model.request;

public class JoinGameReq {
    private String playerColor;
    private int gameID;

    public JoinGameReq() {
    }

    public JoinGameReq(String playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }
    public int getGameID(){
        return gameID;
    }
}