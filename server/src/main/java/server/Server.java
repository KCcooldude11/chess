package server;

import spark.Spark;
import service.UserService; // Import your UserService
import service.GameService; // Import your GameService

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Initialize services
        UserService userService = new UserService();
        GameService gameService = new GameService();

        // Initialize handlers with their services
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);

        // Register your endpoints
        userHandler.initializeRoutes();
        gameHandler.initializeRoutes();

        // Handle exceptions here if you have global error handling

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
