package serviceTests;

import java.util.UUID;
import dataAccess.*;
import model.end.CreateGameEnd;
import model.request.CreateGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import model.UserData;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameTest {

    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserDAO userDAO;
    private GameService gameService;

    @BeforeEach
    void setUp() throws DataAccessException {
                DatabaseManager.clearDatabase();
        gameDAO = new GameDAO(DatabaseManager.getConnection());
        authDAO = new AuthDAO(DatabaseManager.getConnection());
        userDAO = new UserDAO(DatabaseManager.getConnection());
        gameService = new GameService(gameDAO, authDAO);
    }
    void createUser(String username, String password, String email) throws DataAccessException {

        userDAO.createUser(username, password, email);

        UserData user = userDAO.getUser(username);
        if (user == null) {
            throw new IllegalStateException("User creation failed. User does not exist in the database.");
        }
    }


    @Test
    void createGameServiceSuccess() throws DataAccessException {

        String username = "ExampleUsername";
        String password = "password";
        String email = "example@example.com";
        createUser(username, password, email);

        String authToken = authDAO.createAuthToken(username);

        CreateGame req = new CreateGame("TempGameName");
        CreateGameEnd res = gameService.createGame(req, authToken);

        assertNotNull(gameDAO.getGame(res.gameID()));
    }


    @Test
    void createGameServiceErrors() throws DataAccessException {
        // Create a user first
        userDAO.createUser("ExampleUsername", "password", "email@example.com");

        String validAuthToken = authDAO.createAuthToken("ExampleUsername");
        String invalidAuthToken = UUID.randomUUID().toString();
        CreateGame validReq = new CreateGame("TempGameName");
        CreateGame invalidReq = new CreateGame(null);

        // Now the creation of an auth token should not throw a foreign key constraint violation
        assertThrows(DataAccessException.class, () -> gameService.createGame(validReq, invalidAuthToken));
        assertThrows(DataAccessException.class, () -> gameService.createGame(invalidReq, validAuthToken));
    }

}
