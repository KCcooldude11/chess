package model.request;

public class CreateGame {
    private String gameName;

    // Gson requires a no-arg constructor for deserialization

    public CreateGame(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }
}
