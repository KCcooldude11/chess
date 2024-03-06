package serviceTests;

import java.util.UUID;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.UserDAO;
import model.AuthData;
import model.request.Logout;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

public class LogoutServiceTests {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService thisUserDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new UserDAO(DatabaseManager.getConnection());
        authDAO = new AuthDAO(DatabaseManager.getConnection());
        thisUserDAO = new UserService(userDAO, authDAO);
    }

    @Test
    void successfulLogout() throws DataAccessException {
        String username = "UserForLogout";
        userDAO.createUser(username, "password123", "user@logout.test");
        String authToken = authDAO.createAuthToken(username);

        Logout logoutRequest = new Logout(authToken);
        thisUserDAO.logout(logoutRequest);

        AuthData authDataPostLogout = authDAO.getAuthToken(authToken);
        Assertions.assertNull(authDataPostLogout, "AuthToken should be null after logout.");

    }

    @Test
    void logoutWithInvalidToken() {
        String fakeAuthToken = UUID.randomUUID().toString();

        Logout logoutRequest = new Logout(fakeAuthToken);
        Assertions.assertThrows(DataAccessException.class, () -> thisUserDAO.logout(logoutRequest), "Logging out with an invalid authToken should throw an exception.");
    }
}
