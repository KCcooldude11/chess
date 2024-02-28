package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.request.ListGames;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.util.UUID;

public class ListAllGameServiceTest {

    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService gameServ;

    @BeforeEach
    void setup() throws DataAccessException {
        gameDAO = new GameDAO();
        authDAO = new AuthDAO();
        gameServ = new GameService(gameDAO, authDAO);
    }

    @Test
    void listGamesServiceSuccess() throws DataAccessException {
        GameDAO listGame = new GameDAO();
        AuthDAO listAuthDAO = new AuthDAO();
        GameService listGameService = new GameService(listGame, listAuthDAO);
        String authToken = listAuthDAO.createAuthToken("ExampleUsername");
        listGame.createGame("Game1");
        listGame.createGame("Game2");
        listGame.createGame("Game3");
        listGame.createGame("Game4");
        ListGames req = new ListGames(authToken);
        var res = listGameService.listGames(req);
        Assertions.assertEquals(4, res.games().size());
        listGame.clearAllGames();
    }

    @Test
    void handleUnauthorizedGameListing() {
        String invalidToken = UUID.randomUUID().toString();
        ListGames listRequest = new ListGames(invalidToken);

        Assertions.assertThrows(DataAccessException.class, () -> gameServ.listGames(listRequest), "Unauthorized access should throw an exception.");

        authDAO.clearAuthTokens();
    }
}
