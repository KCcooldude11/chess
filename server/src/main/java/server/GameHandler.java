package server;

import com.google.gson.Gson;
import service.GameService;
import model.GameData;
import spark.ResponseTransformer;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
        this.gson = new Gson();
    }

    public void initializeRoutes() {
        // Create game route
        spark.Spark.post("/game/create", "application/json", (request, response) -> {
            // Assuming the body contains gameName and creatorUsername
            GameData gameData = gson.fromJson(request.body(), GameData.class);
            return gameService.createGame(gameData.getGameName(), gameData.getWhiteUsername());
        }, gson::toJson);

        // List games route (assuming a method exists in GameService)
        spark.Spark.get("/game/list", "application/json", (request, response) -> {
            return gameService.listGames();
        }, gson::toJson);
    }
}
