package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.UserDAO;
import model.request.Register; // Updated to use the new Register model
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;
import java.util.UUID;

public class RegisterServiceTests {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService thisUserDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        DatabaseManager.clearDatabase();
        userDAO = new UserDAO(DatabaseManager.getConnection());
        authDAO = new AuthDAO(DatabaseManager.getConnection());
        thisUserDAO = new UserService(userDAO, authDAO);
    }

    @Test
    void successfulRegistration() throws DataAccessException {
        String uniqueEmail = "user" + UUID.randomUUID().toString() + "@example.com";
        Register registrationRequest = new Register("NewUser", "UserPassword", uniqueEmail);

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
