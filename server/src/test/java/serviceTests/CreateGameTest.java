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
    private GameService gameService;

    @BeforeEach
    void setUp() throws DataAccessException {
                DatabaseManager.clearDatabase();
        gameDAO = new GameDAO(DatabaseManager.getConnection());
        authDAO = new AuthDAO(DatabaseManager.getConnection());
        gameService = new GameService(gameDAO, authDAO);
    }
    void createUser(String username, String password, String email) throws DataAccessException {
        // Use your UserDAO or equivalent method to insert a user into the database
        UserDAO userDAO = new UserDAO(DatabaseManager.getConnection());
        userDAO.createUser(username, password, email);

        // Confirm that the user now exists in the database
        UserData user = userDAO.getUser(username);
        if (user == null) {
            throw new IllegalStateException("User creation failed. User does not exist in the database.");
        }
    }


    @Test
    void createGameServiceSuccess() throws DataAccessException {
        // First, create a user in the database
        String username = "ExampleUsername";
        String password = "password"; // Example password, adjust as needed
        String email = "example@example.com"; // Example email, adjust as needed
        createUser(username, password, email); // Make sure this user is now in the database

        // Now that the user exists, create an auth token for them
        String authToken = authDAO.createAuthToken(username);

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
