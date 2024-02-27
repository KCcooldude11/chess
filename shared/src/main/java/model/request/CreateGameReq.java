package model.request;

public class CreateGameReq {
    private String gameName;

    // Gson requires a no-arg constructor for deserialization
    public CreateGameReq() {
    }

    public CreateGameReq(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
