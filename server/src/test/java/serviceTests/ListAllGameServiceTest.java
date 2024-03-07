package serviceTests;

import chess.ChessGame;
import dataAccess.*;
import model.request.ListGames;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.util.UUID;

public class ListAllGameServiceTest {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserDAO userDAO;
    private GameService gameService;

    @BeforeEach
    void setUp() throws DataAccessException {
        // Initialize DAOs
        gameDAO = new GameDAO(DatabaseManager.getConnection());
        authDAO = new AuthDAO(DatabaseManager.getConnection());
        userDAO = new UserDAO(DatabaseManager.getConnection());
        gameService = new GameService(gameDAO, authDAO);

        // Clear database tables involved in tests
        DatabaseManager.clearDatabase();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        // Optional: Clear database tables again to ensure a clean state for other tests
        DatabaseManager.clearDatabase();
    }

    @Test
    void listGamesServiceSuccess() throws DataAccessException {
        // Ensure unique username by using a UUID
        String uniqueUsername = "ExampleUsername" + UUID.randomUUID();
        userDAO.createUser(uniqueUsername, "password", uniqueUsername + "@example.com");

        String authToken = authDAO.createAuthToken(uniqueUsername);
        gameDAO.createGame("Game1");
        gameDAO.createGame("Game2");
        gameDAO.createGame("Game3");
        gameDAO.createGame("Game4");

        ListGames req = new ListGames(authToken);
        var res = gameService.listGames(req);

        Assertions.assertEquals(4, res.games().size());
    }

    @Test
    void listGamesServiceErrors() throws DataAccessException {
        String uniqueUsername = "ExampleUsername" + UUID.randomUUID();
        userDAO.createUser(uniqueUsername, "password", uniqueUsername + "@example.com");

        String validAuthToken = authDAO.createAuthToken(uniqueUsername);

        // Use an invalid auth token
        ListGames req = new ListGames(UUID.randomUUID().toString());
        Assertions.assertThrows(DataAccessException.class, () -> gameService.listGames(req), "Attempting to list games with an invalid auth token should throw an error.");
    }
}
