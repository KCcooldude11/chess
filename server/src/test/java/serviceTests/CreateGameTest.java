package serviceTests;

import java.util.UUID;
import dataAccess.*;
import model.end.CreateGameEnd;
import model.request.CreateGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameTest {

    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService gameService;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = new GameDAO(DatabaseManager.getConnection());
        authDAO = new AuthDAO(DatabaseManager.getConnection());
        gameService = new GameService(gameDAO, authDAO);
    }

    @Test
    void createGameServiceSuccess() throws DataAccessException {
        String authToken = authDAO.createAuthToken("ExampleUsername");
        CreateGame req = new CreateGame("TempGameName");
        CreateGameEnd res = gameService.createGame(req, authToken);

        assertNotNull(gameDAO.getGame(res.gameID()));
    }

    @Test
    void createGameServiceErrors() {
        String validAuthToken = authDAO.createAuthToken("ExampleUsername");
        String invalidAuthToken = UUID.randomUUID().toString();
        CreateGame validReq = new CreateGame("TempGameName");
        CreateGame invalidReq = new CreateGame(null);

        assertThrows(DataAccessException.class, () -> gameService.createGame(validReq, invalidAuthToken));
        assertThrows(DataAccessException.class, () -> gameService.createGame(invalidReq, validAuthToken));
    }
}
