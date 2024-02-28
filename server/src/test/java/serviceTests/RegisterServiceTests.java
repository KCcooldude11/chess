package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.request.Register; // Updated to use the new Register model
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

public class RegisterServiceTests {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService thisUserDAO;

    @BeforeEach
    void setup() {
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        thisUserDAO = new UserService(userDAO, authDAO);
    }

    @Test
    void successfulRegistration() throws DataAccessException {
        Register registrationRequest = new Register("NewUser", "UserPassword", "user@example.com");

        var registrationResult = thisUserDAO.register(registrationRequest);

        Assertions.assertEquals("NewUser", registrationResult.username(), "The username should match the registration request.");
        Assertions.assertNotNull(registrationResult.authToken(), "The authToken should not be null after successful registration.");
    }

    @Test
    void registrationWithDuplicateUsername() throws DataAccessException {
        Register firstRegistration = new Register("UserOne", "PasswordOne", "emailOne@example.com");
        thisUserDAO.register(firstRegistration);

        Register duplicateUsernameRegistration = new Register("UserOne", "PasswordTwo", "emailTwo@example.com");
        Assertions.assertThrows(DataAccessException.class, () -> thisUserDAO.register(duplicateUsernameRegistration), "Registration with a duplicate username should fail.");
    }

    @Test
    void registrationWithInvalidData() {
        Register invalidRegistration = new Register("", "", "");
        Assertions.assertThrows(DataAccessException.class, () -> thisUserDAO.register(invalidRegistration), "Registration with invalid data should fail.");
    }
}
