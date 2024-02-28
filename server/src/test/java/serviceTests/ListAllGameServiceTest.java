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
    void ensureGamesAreListedCorrectly() throws DataAccessException {
        String userToken = authDAO.createAuthToken("GamerUser");
        for (int i = 1; i <= 4; i++) {
            gameDAO.createGame("Game" + i);
        }

        ListGames listRequest = new ListGames(userToken);
        var listResult = gameServ.listGames(listRequest);
        Assertions.assertEquals(4, listResult.games().size(), "Should list exactly 4 games.");

        gameDAO.clearAllGames();
    }

    @Test
    void handleUnauthorizedGameListing() {
        String invalidToken = UUID.randomUUID().toString();
        ListGames listRequest = new ListGames(invalidToken);

        Assertions.assertThrows(DataAccessException.class, () -> gameServ.listGames(listRequest), "Unauthorized access should throw an exception.");

        authDAO.clearAuthTokens();
    }
}
