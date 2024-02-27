package server;

import service.ClearService;
import spark.Spark;
import service.UserService;
import service.GameService;
import dataAccess.UserDAO; // Import the UserDAO
import dataAccess.GameDAO; // Import the GameDAO
import dataAccess.IUserDAO; // Import the IUserDAO interface
import dataAccess.IGameDAO; // Import the IGameDAO interface
import dataAccess.IAuthDAO;
import dataAccess.AuthDAO;
import server.AdminHandler;
import server.UserHandler;
import server.GameHandler;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Instantiate DAO implementations
        IUserDAO userDAO = new UserDAO();
        IGameDAO gameDAO = new GameDAO();
        IAuthDAO authDAO = new AuthDAO(); // Instantiate AuthDAO

        // Initialize services with DAOs
        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO); // Pass both DAOs to GameService
        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);

        // Initialize handlers with their services
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);
        AdminHandler adminHandler = new AdminHandler(clearService); // Initialize AdminHandler with ClearService


        // Register your endpoints
        userHandler.initializeRoutes();
        gameHandler.initializeRoutes();
        adminHandler.setupRoutes();

        // Handle exceptions here if you have global error handling
        //not sure if this is needed
        Spark.exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace(); // Print the stack trace for debugging
            response.status(500); // Set HTTP status to 500 (Internal Server Error)
            response.body("Internal server error"); // Set the response body
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    public static void main(String[] args) {
        Server server = new Server();
        int port = 8080; // Or any other port you wish to use
        server.run(port);
        System.out.println("Server running on port: " + port);
    }
}
