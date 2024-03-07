package serviceTests;

import dataAccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;
import org.junit.jupiter.api.Assertions;
import chess.ChessGame;

public class ClearAllServiceTests {

    private IUserDAO userDAO;
    private IAuthDAO authDAO;
    private IGameDAO gameDAO;
    private ClearService clearService;

    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new UserDAO(DatabaseManager.getConnection());
        authDAO = new AuthDAO(DatabaseManager.getConnection());
        gameDAO = new GameDAO(DatabaseManager.getConnection());
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    @Test
    void ensureCompleteDataCleanup() throws DataAccessException {

        userDAO.createUser("UserOne", "PasswordOne", "userone@example.com");
        String authTokenOne = authDAO.createAuthToken("UserOne");
        ChessGame defaultGame = new ChessGame();
        int gameIDOne = gameDAO.createGame("FirstGame");

        clearService.clearAll();

        Assertions.assertNull(userDAO.getUser("UserOne"), "User data should be cleared.");
        Assertions.assertNull(authDAO.getAuthToken(authTokenOne), "Auth token data should be cleared.");
        Assertions.assertNull(gameDAO.getGame(gameIDOne), "Game data should be cleared.");
    }
}
