package dataAccess;

import java.util.HashMap;
import java.util.Map;

public class AuthDAO implements IAuthDAO {
    // Map to store authentication tokens associated with usernames
    private final Map<String, String> authTokensToUsernames = new HashMap<>();
    private final Map<String, String> usernamesToAuthTokens = new HashMap<>();

    @Override
    public void createAuthToken(String username, String authToken) throws DataAccessException {
        System.out.println("AuthDAO: Attempting to create authToken for username: " + username);
        if (authTokensToUsernames.containsKey(authToken) || usernamesToAuthTokens.containsKey(username)) {
            System.out.println("Conflict detected. AuthToken already exists or user already has a token.");
            throw new DataAccessException("AuthToken already exists or user already has a token.");
        }
        authTokensToUsernames.put(authToken, username);
        usernamesToAuthTokens.put(username, authToken);
        System.out.println("Auth token created and associated with user: " + username);
    }

    @Override
    public String validateAuthToken(String authToken) throws DataAccessException {
        System.out.println("AuthDAO: Validating authToken: " + authToken);
        if (!authTokensToUsernames.containsKey(authToken)) {
            System.out.println("AuthDAO: Invalid or expired authToken.");
            throw new DataAccessException("Invalid or expired authToken.");
        }
        String username = authTokensToUsernames.get(authToken);
        System.out.println("AuthDAO: AuthToken validated for username: " + username);
        return username;
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        System.out.println("AuthDAO: Attempting to delete authToken: " + authToken);
        if (!authTokensToUsernames.containsKey(authToken)) {
            System.out.println("AuthDAO: AuthToken not found.");
            throw new DataAccessException("AuthToken not found.");
        }
        String username = authTokensToUsernames.remove(authToken);
        usernamesToAuthTokens.remove(username);
        System.out.println("AuthDAO: AuthToken deleted for username: " + username);
    }
    @Override
    public void clearAuthTokens() throws DataAccessException {
        System.out.println("AuthDAO: Clearing all authTokens.");
        authTokensToUsernames.clear();
        usernamesToAuthTokens.clear();
        System.out.println("AuthDAO: All authTokens cleared.");
    }
    @Override
    public void deleteAuthTokenForUser(String username) throws DataAccessException {
        if (usernamesToAuthTokens.containsKey(username)) {
            String existingToken = usernamesToAuthTokens.remove(username);
            authTokensToUsernames.remove(existingToken);
            System.out.println("Existing auth token removed for user: " + username);
        }
    }
}
