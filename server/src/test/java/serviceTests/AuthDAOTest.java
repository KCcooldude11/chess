package serviceTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
public class AuthDAOTest {

    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() {
        authDAO = new AuthDAO();
        // Consider clearing or initializing your AuthDAO here if needed
    }

    @Test
    public void testValidTokenReturnsUsername() throws DataAccessException {
        String username = "testUser";
        String authToken = "validToken";
        authDAO.createAuthToken(username, authToken);

        String retrievedUsername = authDAO.validateAuthToken(authToken);
        Assertions.assertEquals(username, retrievedUsername, "Valid token should return the correct username.");
    }

    @Test
    public void testInvalidTokenThrowsException() {
        String invalidToken = "invalidToken";
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.validateAuthToken(invalidToken);
        }, "Invalid token should result in DataAccessException.");
    }

    @Test
    public void testDeletingOneTokenDoesNotAffectOthers() throws DataAccessException {
        String username = "testUserWithMultipleTokens";
        String authToken1 = "validToken1";
        String authToken2 = "validToken2";
        authDAO.createAuthToken(username, authToken1);
        authDAO.createAuthToken(username, authToken2);

        // Delete the first token
        authDAO.deleteAuthToken(authToken1);

        // Attempt to validate the second token, which should still be valid
        Assertions.assertDoesNotThrow(() -> authDAO.validateAuthToken(authToken2), "Deleting one token should not affect other tokens for the same user.");

        // Ensure the deleted token is no longer valid
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.validateAuthToken(authToken1), "Deleted token should be invalid.");
    }

    @AfterEach
    public void tearDown() {
        // Clean up resources, if necessary
    }
}
