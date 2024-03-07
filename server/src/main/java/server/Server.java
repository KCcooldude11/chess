package server;


import dataAccess.*;
import service.*;
import spark.*;
import model.request.*;
import model.end.ErrorMessage;
import java.util.Objects;
import com.google.gson.Gson;

public class Server {
    private IUserDAO userDAO;
    private IAuthDAO authDAO;
    private IGameDAO gameDAO;

    public int run(int desiredPort) {
        initializeDAOs(); // Now catches DataAccessException internally
        configureSpark(desiredPort);
        setupEndpoints();
        Spark.awaitInitialization();
        return Spark.port();
    }
    private void initializeDAOs() {
        try {
            DatabaseManager.initializeDatabase();
            this.userDAO = new UserDAO(DatabaseManager.getConnection());
            this.authDAO = new AuthDAO(DatabaseManager.getConnection());
            this.gameDAO = new GameDAO(DatabaseManager.getConnection());
        } catch (DataAccessException e) {
            System.err.println("Failed to initialize DAOs due to database access issue: " + e.getMessage());
            // Depending on your application's requirements, you might want to stop the server or re-throw as a runtime exception.
            throw new RuntimeException("Server initialization failed.", e);
        }
    }
    private void configureSpark(int port) {
        Spark.port(port);
        Spark.staticFiles.location("web");
    }
    private void setupEndpoints() {
        // POST requests
        Spark.post("/user", this::registerHandler);
        Spark.post("/session", this::loginHandler);
        Spark.post("/game", this::createGameHandler);
        // PUT requests
        Spark.put("/game", this::joinGameHandler);
        // GET requests
        Spark.get("/game", this::listGamesHandler);
        // DELETE requests
        Spark.delete("/session", this::logoutHandler);
        Spark.delete("/db", this::deleteHandler);
    }
    private Object registerHandler(Request request, Response response) {
        UserService userService = new UserService(userDAO, authDAO);
        try {
            var thisReq = new Gson().fromJson(request.body(), Register.class);
            var thisRes = userService.register(thisReq);
            return new Gson().toJson(thisRes);
        }
        catch(DataAccessException e) {
            if(Objects.equals(e.getMessage(), "User already exists")) {
                response.status(403);
                return new Gson().toJson(new ErrorMessage("Error: already taken"));
            }
            if(Objects.equals(e.getMessage(), "Bad request")) {
                response.status(400);
                return new Gson().toJson(new ErrorMessage("Error: bad request"));
            }
            response.status(500);
            return new Gson().toJson(new ErrorMessage("Error: DataAccessException thrown but not caught correctly"));
        }
        catch(Exception e) {
            response.status(500);
            return new Gson().toJson(new ErrorMessage(e.getMessage()));
        }
    }
    private Object loginHandler(Request request, Response response) {
        UserService userService = new UserService(userDAO, authDAO);
        try {
            var thisReq = new Gson().fromJson(request.body(), Login.class);
            var thisRes = userService.login(thisReq);
            return new Gson().toJson(thisRes);
        }
        catch(DataAccessException e) {
            response.status(401);
            return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
        }
        catch(Exception e) {
            response.status(500);
            return new Gson().toJson(new ErrorMessage(e.getMessage()));
        }
    }
    private Object createGameHandler(Request request, Response response) {
        GameService gameService = new GameService(gameDAO, authDAO);
        try {
            String authToken = request.headers("authorization");
            var thisReq = new Gson().fromJson(request.body(), CreateGame.class);
            var thisRes = gameService.createGame(thisReq, authToken);
            return new Gson().toJson(thisRes);
        }
        catch(DataAccessException e) {
            if(Objects.equals(e.getMessage(), "Bad request")) {
                response.status(400);
                return new Gson().toJson(new ErrorMessage("Error: bad request"));
            }
            if(Objects.equals(e.getMessage(), "Unauthorized")) {
                response.status(401);
                return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
            }
            response.status(500);
            return new Gson().toJson(new ErrorMessage("Error: DataAccessException thrown but not caught correctly"));
        }
        catch(Exception e) {
            response.status(500);
            return new Gson().toJson(new ErrorMessage(e.getMessage()));
        }
    }
    private Object joinGameHandler(Request request, Response response) {
        GameService gameService = new GameService(gameDAO, authDAO);
        try {
            String authToken = request.headers("authorization");
            var thisReq = new Gson().fromJson(request.body(), JoinGame.class);
            gameService.joinGame(thisReq, authToken);
            return "";
        }
        catch(DataAccessException e) {
            System.out.println("DataAccessException in joinGameHandler: " + e.getMessage());
            if(Objects.equals(e.getMessage(), "Unauthorized")) {
                response.status(401);
                return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
            }
            if(Objects.equals(e.getMessage(), "Bad request")) {
                response.status(400);
                return new Gson().toJson(new ErrorMessage("Error: bad request"));
            }
            if(Objects.equals(e.getMessage(), "Already taken")) {
                response.status(403);
                return new Gson().toJson(new ErrorMessage("Error: already taken"));
            }
            response.status(500);
            return new Gson().toJson(new ErrorMessage("Error: DataAccessException thrown but not caught correctly"));
        }
        catch(Exception e) {
            System.out.println("DataAccessException in joinGameHandler: " + e.getMessage());
            response.status(500);
            return new Gson().toJson(new ErrorMessage(e.getMessage()));
        }
    }
    private Object listGamesHandler(Request request, Response response) {
        GameService gameService = new GameService(gameDAO, authDAO);
        try {
            ListGames req = new ListGames(request.headers("authorization"));
            var thisRes = gameService.listGames(req);
            return new Gson().toJson(thisRes);
        }
        catch(DataAccessException e) {
            response.status(401);
            return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
        }
        catch(Exception e) {
            response.status(500);
            return new Gson().toJson(new ErrorMessage(e.getMessage()));
        }
    }
    private Object logoutHandler(Request request, Response response) {
        UserService userService = new UserService(userDAO, authDAO);
        try {
            Logout thisReq = new Logout(request.headers("authorization"));
            userService.logout(thisReq);
            return "";
        }
        catch(DataAccessException e) {
            response.status(401);
            return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
        }
        catch(Exception e) {
            response.status(500);
            return new Gson().toJson(new ErrorMessage(e.getMessage()));
        }
    }
    private Object deleteHandler(Request request, Response response) {
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        try {
            clearService.clearAll();
        }
        catch (Exception e) {
            response.status(500);
            return new Gson().toJson(new ErrorMessage(e.toString()));
        }
        return "";
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
