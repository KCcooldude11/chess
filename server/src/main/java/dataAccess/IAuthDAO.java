package dataAccess;

import model.AuthData;

public interface IAuthDAO {
    AuthData getAuthToken(String authToken);
    String createAuthToken(String username) throws DataAccessException;
    void deleteAuthToken(String authToken) throws DataAccessException;
    void clearAuthTokens() throws DataAccessException;

}
