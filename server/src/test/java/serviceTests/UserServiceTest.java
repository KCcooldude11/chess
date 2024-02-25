package serviceTests;

import dataAccess.IUserDAO;
import dataAccess.UserDAO;
import model.UserData;
import service.UserService;
import service.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private IUserDAO userDAO;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        // Use the actual UserDAO
        userDAO = new UserDAO();
        userService = new UserService(userDAO);
    }

    @Test
    public void registerUser_Success() throws Exception {
        // Arrange
        UserData newUser = new UserData("user", "pass", "email@example.com");
        // No need to mock getUser, assuming UserDAO can handle it directly

        // Act
        var result = userService.register("user", "pass", "email@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("user", result.getUsername());
        assertNotNull(result.getAuthToken());
    }

    @Test
    public void registerUser_Failure_UserExists() {
        // Arrange
        // Assuming UserDAO can handle adding and checking for existing users directly

        // Act
        // First, register a user
        assertDoesNotThrow(() -> userService.register("user", "pass", "email@example.com"));
        // Try to register the same user again and expect a ServiceException
        assertThrows(ServiceException.class, () -> {
            userService.register("user", "pass", "email@example.com");
        });
    }

}
