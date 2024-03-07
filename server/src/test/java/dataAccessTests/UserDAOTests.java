package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {
    private Connection conn;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws Exception {
        conn = DatabaseManager.getConnection();
        DatabaseManager.clearDatabase();
        userDAO = new UserDAO(conn);
    }

    @AfterEach
    public void tearDown() throws Exception {
        DatabaseManager.clearDatabase();
        if (conn != null) conn.close();
    }

    @Test
    public void createUserSuccess() throws DataAccessException {
        userDAO.createUser("testUser", "password", "testUser@example.com");
        UserData result = userDAO.getUser("testUser");
        assertNotNull(result, "User should be created successfully");
    }

    @Test
    public void createUserFailure() {
        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser("testUser", "password", "testUser@example.com");

            userDAO.createUser("testUser", "newPassword", "testUser2@example.com");
        }, "Expected DataAccessException to be thrown due to duplicate username");
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        userDAO.createUser("testUser", "password", "testUser@example.com");
        UserData result = userDAO.getUser("testUser");
        assertEquals("testUser", result.getUsername(), "Should return correct user");
    }

    @Test
    public void getUserFailure() throws DataAccessException {
        UserData result = userDAO.getUser("nonExistentUser");
        assertNull(result, "Should return null for a non-existent user");
    }

    @Test
    public void verifyUserSuccess() throws DataAccessException {
        userDAO.createUser("testUser", "password", "testUser@example.com");
        assertTrue(userDAO.verifyUser("testUser", "password"), "Password should be verified successfully");
    }

    @Test
    public void verifyUserFailure() throws DataAccessException {
        userDAO.createUser("testUser", "password", "testUser@example.com");
        assertFalse(userDAO.verifyUser("testUser", "wrongPassword"), "Wrong password should not pass verification");
    }

    @Test
    public void clearAllUsersSuccess() throws DataAccessException {
        userDAO.createUser("testUser1", "password1", "testUser1@example.com");
        userDAO.createUser("testUser2", "password2", "testUser2@example.com");
        userDAO.clearAllUsers();
        assertNull(userDAO.getUser("testUser1"), "All users should be cleared successfully");
        assertNull(userDAO.getUser("testUser2"), "All users should be cleared successfully");
    }
}
