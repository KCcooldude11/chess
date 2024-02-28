package serviceTests;

import dataAccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;
import org.junit.jupiter.api.Assertions;

public class ClearAllServiceTests {

    private IUserDAO userDAO;
    private IAuthDAO authDAO;
    private IGameDAO gameDAO;
    private ClearService clearService;

    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        gameDAO = new GameDAO();
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    @Test
    void ensureCompleteDataCleanup() throws DataAccessException {
        // Preliminary data setup
        userDAO.createUser("UserOne", "PasswordOne", "userone@example.com");
        String authTokenOne = authDAO.createAuthToken("UserOne");
        int gameIDOne = gameDAO.createGame("FirstGame");

        // Execute the clear service
        clearService.clearAll();

        // Validate that all data has been removed
        Assertions.assertNull(userDAO.getUser("UserOne"), "User data should be cleared.");
        Assertions.assertNull(authDAO.getAuthToken(authTokenOne), "Auth token data should be cleared.");
        Assertions.assertNull(gameDAO.getGame(gameIDOne), "Game data should be cleared.");
    }
}
