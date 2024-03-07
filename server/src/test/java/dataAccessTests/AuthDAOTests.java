package dataAccessTests;

import dataAccess.*;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {

    private Connection conn;
    private AuthDAO authDAO;
    private

    UserDAO userDAO;

    @BeforeEach
    public void setUp() throws Exception {

        conn = DatabaseManager.getConnection();

        DatabaseManager.clearDatabase();
        authDAO = new AuthDAO(conn);
        userDAO = new UserDAO(conn);
    }

    @AfterEach
    public void tearDown() throws Exception {
        DatabaseManager.clearDatabase();

        if (conn != null) conn.close();
    }

    private void createUser(String username, String password, String email) throws SQLException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
        }
    }

    @Test
    public void createAuthTokenSuccess() throws DataAccessException, SQLException {

        String username = "testUser";
        createUser(username, "testPassword", "testUser@example.com");

        String authToken = authDAO.createAuthToken(username);
        assertNotNull(authToken, "Auth token should be generated");
    }

    @Test
    public void getAuthTokenSuccess() throws DataAccessException, SQLException {
        String username = "testUser";
        createUser(username, "testPassword", "testUser@example.com"); // Ensure user exists
        String authToken = authDAO.createAuthToken(username);
        AuthData result = authDAO.getAuthToken(authToken);
        assertEquals(username, result.getUsername(), "Should return correct username associated with the auth token");
    }

    @Test
    public void getAuthTokenFailure() throws DataAccessException {
        String authToken = "nonExistentToken";
        AuthData result = authDAO.getAuthToken(authToken);
        assertNull(result, "Should return null for a non-existent auth token");
    }

    @Test
    public void deleteAuthTokenSuccess() throws DataAccessException {
        String username = "testUser" + UUID.randomUUID();
        // Ensure the user exists
        userDAO.createUser(username, "password", username + "@example.com");
        String authToken = authDAO.createAuthToken(username);
        authDAO.deleteAuthToken(authToken);
        AuthData result = authDAO.getAuthToken(authToken);
        assertNull(result, "Auth token should be deleted successfully");
    }

    @Test
    public void clearAuthTokensSuccess() throws DataAccessException {
        String username1 = "testUser1" + UUID.randomUUID();
        String username2 = "testUser2" + UUID.randomUUID();
        // Ensure the users exist
        userDAO.createUser(username1, "password1", username1 + "@example.com");
        userDAO.createUser(username2, "password2", username2 + "@example.com");
        authDAO.createAuthToken(username1);
        authDAO.createAuthToken(username2);

        authDAO.clearAuthTokens();

        String someToken = "anyToken";
        AuthData result = authDAO.getAuthToken(someToken);
        assertNull(result, "All auth tokens should be cleared successfully");
    }
    @Test
    public void createAuthTokenFailure() {
        String nonExistentUsername = "nonExistentUser" + UUID.randomUUID();

        // Attempt to create an auth token for a non-existent user
        // This should fail because the user does not exist in the database,
        // and a foreign key constraint violation should occur.
        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuthToken(nonExistentUsername);
        }, "Attempting to create an auth token for a non-existent user should throw DataAccessException due to foreign key constraint violation.");
    }

}
