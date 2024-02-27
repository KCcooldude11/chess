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
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        gameDAO = new GameDAO();

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        //post
        Spark.post("/user", this::registerHandler);
        Spark.post("/session", this::loginHandler);
        Spark.post("/game", this::createGameHandler);
        //put
        Spark.put("/game", this::joinGameHandler);
        //get
        Spark.get("/game", this::listGamesHandler);
        //delete
        Spark.delete("/session", this::logoutHandler);
        Spark.delete("/db", this::deleteHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }
    private Object registerHandler(Request request, Response response) {
        UserService userService = new UserService(userDAO, authDAO);
        try {
            var thisReq = new Gson().fromJson(request.body(), RegisterReq.class);
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
            var thisReq = new Gson().fromJson(request.body(), LoginReq.class);
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
            var thisReq = new Gson().fromJson(request.body(), CreateGameReq.class);
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
            var thisReq = new Gson().fromJson(request.body(), JoinGameReq.class);
            gameService.joinGame(thisReq, authToken);
            return "";
        }
        catch(DataAccessException e) {
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
            response.status(500);
            return new Gson().toJson(new ErrorMessage(e.getMessage()));
        }
    }
    private Object listGamesHandler(Request request, Response response) {
        GameService gameService = new GameService(gameDAO, authDAO);
        try {
            ListGamesReq req = new ListGamesReq(request.headers("authorization"));
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
            LogoutReq thisReq = new LogoutReq(request.headers("authorization"));
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
