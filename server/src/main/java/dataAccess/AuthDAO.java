package dataAccess;

import java.util.HashMap;
import java.util.Map;

public class AuthDAO implements IAuthDAO {
    // Map to store authentication tokens associated with usernames
    private final Map<String, String> authTokensToUsernames = new HashMap<>();
    private final Map<String, String> usernamesToAuthTokens = new HashMap<>();

    @Override
    public void createAuthToken(String username, String authToken) throws DataAccessException {
        if (authTokensToUsernames.containsKey(authToken) || usernamesToAuthTokens.containsKey(username)) {
            throw new DataAccessException("AuthToken already exists or user already has a token.");
        }
        authTokensToUsernames.put(authToken, username);
        usernamesToAuthTokens.put(username, authToken);
    }

    @Override
    public String validateAuthToken(String authToken) throws DataAccessException {
        if (!authTokensToUsernames.containsKey(authToken)) {
            throw new DataAccessException("Invalid or expired authToken.");
        }
        return authTokensToUsernames.get(authToken);
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        if (!authTokensToUsernames.containsKey(authToken)) {
            throw new DataAccessException("AuthToken not found.");
        }
        String username = authTokensToUsernames.remove(authToken);
        usernamesToAuthTokens.remove(username);
    }
    @Override
    public void clearAuthTokens() throws DataAccessException {
        authTokensToUsernames.clear();
    }
}
