package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.end.CreateGameEnd;
import model.request.CreateGameReq;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.util.UUID;

public class CreateGameTest {

    @Test
    void createGameServiceSuccess() throws DataAccessException {
        GameDAO createGameDAO = new GameDAO();
        AuthDAO createAuthDAO = new AuthDAO();
        GameService gameService = new GameService(createGameDAO, createAuthDAO);
        String authToken = createAuthDAO.createAuthToken("ExampleUsername");
        CreateGameReq req = new CreateGameReq("TempGameName");
        CreateGameEnd res = gameService.createGame(req, authToken);
        Assertions.assertNotNull(createGameDAO.getGame(res.gameID()));
        createGameDAO.clearAllGames();
    }

    @Test
    void createGameServiceErrors() {
        GameDAO createGameErrorDAO = new GameDAO();
        AuthDAO createAuthErrorDAO = new AuthDAO();
        GameService gameService = new GameService(createGameErrorDAO, createAuthErrorDAO);
        String authToken = createAuthErrorDAO.createAuthToken("ExampleUsername");
        String badAuthToken = UUID.randomUUID().toString();
        CreateGameReq req = new CreateGameReq("TempGameName");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(req, badAuthToken));
        CreateGameReq newReq = new CreateGameReq(null);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(newReq, authToken));
        createGameErrorDAO.clearAllGames();
    }
}
