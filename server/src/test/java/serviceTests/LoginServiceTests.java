package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.request.Login;
import model.end.LoginEnd;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

public class LoginServiceTests {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService thisAuthService;

    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        thisAuthService = new UserService(userDAO, authDAO);
    }

    @Test
    void ensureSuccessfulLogin() throws DataAccessException {
        String username = "UniqueUser";
        String password = "SecurePassword";
        String email = "user@example.com";
        userDAO.createUser(username, password, email);

        Login loginAttempt = new Login(username, password);
        LoginEnd loginOutcome = thisAuthService.login(loginAttempt);

        Assertions.assertEquals(username, loginOutcome.username(), "Username should match the one provided.");
        Assertions.assertNotNull(loginOutcome.authToken(), "Auth token should not be null or empty.");
        Assertions.assertFalse(loginOutcome.authToken().isEmpty(), "Auth token should not be empty.");
    }

    @Test
    void handleLoginErrors() {
        String existingUsername = "ExistingUser";
        String correctPassword = "CorrectPassword";
        userDAO.createUser(existingUsername, correctPassword, "existinguser@example.com");

        // Incorrect password attempt
        Login wrongPasswordAttempt = new Login(existingUsername, "WrongPassword");
        Assertions.assertThrows(DataAccessException.class, () -> thisAuthService.login(wrongPasswordAttempt), "Login with incorrect password should fail.");

        // Non-existing user attempt
        Login nonExistingUserAttempt = new Login("NonExistingUser", correctPassword);
        Assertions.assertThrows(DataAccessException.class, () -> thisAuthService.login(nonExistingUserAttempt), "Login with non-existing username should fail.");
    }
}
