package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import model.request.JoinGameReq;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.util.Random;

public class JoinGameServiceTests {
    @Test
    void joinGameServiceSuccess() throws DataAccessException {
        GameDAO joinGameDAO = new GameDAO();
        AuthDAO joinAuthDAO = new AuthDAO();
        GameService gameService = new GameService(joinGameDAO, joinAuthDAO);
        String authToken = joinAuthDAO.createAuthToken("ExampleUsername");
        int gameID = joinGameDAO.createGame("Game");
        JoinGameReq req = new JoinGameReq("WHITE", gameID);
        gameService.joinGame(req, authToken);
        Assertions.assertEquals(new GameData(gameID, "ExampleUsername", null,
                "Game", joinGameDAO.getGame(gameID).game()), joinGameDAO.getGame(gameID));
        joinGameDAO.ClearAllGames();
    }

    @Test
    void joinGameServiceErrors() throws DataAccessException {
        GameDAO joinGameErrorDAO = new GameDAO();
        AuthDAO joinAuthErrorDAO = new AuthDAO();
        GameService gameService = new GameService(joinGameErrorDAO, joinAuthErrorDAO);
        String authToken = joinAuthErrorDAO.createAuthToken("ExampleUsername");
        int gameID = joinGameErrorDAO.createGame("Game");
        JoinGameReq req = new JoinGameReq("WHITE", gameID);
        gameService.joinGame(req, authToken);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(req, authToken));
        Random random = new Random();
        JoinGameReq newReq = new JoinGameReq("WHITE", random.nextInt(10000));
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(newReq, authToken));
        joinGameErrorDAO.ClearAllGames();
    }
}
