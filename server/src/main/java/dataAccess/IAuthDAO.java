package dataAccess;

public interface IAuthDAO {
    // Creates a new authentication token for a user
    void createAuthToken(String username, String authToken) throws DataAccessException;

    // Validates an authentication token and returns the username it belongs to
    String validateAuthToken(String authToken) throws DataAccessException;

    // Deletes an authentication token
    void deleteAuthToken(String authToken) throws DataAccessException;

    void clearAuthTokens() throws DataAccessException;

    public void deleteAuthTokenForUser(String username) throws DataAccessException;
}
