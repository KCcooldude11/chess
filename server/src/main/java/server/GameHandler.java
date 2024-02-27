package server;

import util.ErrorMessage;
import service.ServiceException;
import model.JoinGameRequest;
import service.GameService;
import model.GameData;
import util.JsonUtil; // Ensure you have the JsonUtil class created

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void initializeRoutes() {
        // Create game route
        // Create game route
        spark.Spark.post("/game", "application/json", (request, response) -> {
            System.out.println("Request Body: " + request.body()); // Print the request body to console for debugging
            String authToken = request.headers("Authorization");
            GameData gameData = JsonUtil.fromJson(request.body(), GameData.class);
            String gameName = gameData.getGameName();

            // Validate game name before proceeding
            if (gameName == null || gameName.trim().isEmpty()) {
                response.status(400); // Bad Request
                return JsonUtil.toJson(new ErrorMessage("Game name is required"));
            }

            // Assuming createGame method now returns a GameData object
            try {
                GameData newGame = gameService.createGame(authToken, gameName);
                response.status(200); // Set the status accordingly
                response.type("application/json");
                return JsonUtil.toJson(newGame);
            } catch (ServiceException e) {
                response.status(500); // Internal Server Error
                return JsonUtil.toJson(new ErrorMessage("Failed to create game: " + e.getMessage()));
            }
        });


        // List games route
        spark.Spark.get("/game", "application/json", (request, response) -> {
            response.status(200); // Set the status accordingly
            return JsonUtil.toJson(gameService.listGames());
        });

        // Join game route
        spark.Spark.put("/game", "application/json", (request, response) -> {
            String authToken = request.headers("Authorization");
            JoinGameRequest joinRequest = JsonUtil.fromJson(request.body(), JoinGameRequest.class);

            try {
                gameService.joinGame(authToken, joinRequest.getGameID(), joinRequest.getPlayerColor());
                response.status(200); // OK
                response.type("application/json");
                return JsonUtil.toJson("Joined game successfully");
            } catch (ServiceException e) {
                response.status(400); // Bad request or other appropriate status
                response.type("application/json");
                return JsonUtil.toJson(new ErrorMessage(e.getMessage()));
            }
        });
    }

    // Assuming an ErrorMessage class exists to format error messages
    private class ErrorMessage {
        String message;

        public ErrorMessage(String message) {
            this.message = message;
        }
    }
}
