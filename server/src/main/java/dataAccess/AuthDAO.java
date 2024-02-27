package dataAccess;

import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AuthDAO implements IAuthDAO {
    // Map to store authentication tokens associated with usernames
    private final Map<String, Set<String>> usernamesToAuthTokens = new ConcurrentHashMap<>();

    @Override
    public synchronized void createAuthToken(String username, String authToken) throws DataAccessException {
        System.out.println("AuthDAO: Attempting to create authToken for username: " + username);
        usernamesToAuthTokens.computeIfAbsent(username, k -> new HashSet<>()).add(authToken);
        System.out.println("Auth token created and associated with user: " + username);
    }

    @Override
    public synchronized String validateAuthToken(String authToken) throws DataAccessException {
        System.out.println("AuthDAO: Validating authToken: " + authToken);
        for (Map.Entry<String, Set<String>> entry : usernamesToAuthTokens.entrySet()) {
            if (entry.getValue().contains(authToken)) {
                System.out.println("AuthDAO: AuthToken validated for username: " + entry.getKey());
                return entry.getKey();
            }
        }
        System.out.println("AuthDAO: Invalid or expired authToken.");
        throw new DataAccessException("Invalid or expired authToken.");
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        System.out.println("AuthDAO: Attempting to delete authToken: " + authToken);
        boolean found = false;
        for (Map.Entry<String, Set<String>> entry : usernamesToAuthTokens.entrySet()) {
            if (entry.getValue().remove(authToken)) {
                System.out.println("AuthDAO: AuthToken deleted for username: " + entry.getKey());
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("AuthDAO: AuthToken not found.");
            throw new DataAccessException("AuthToken not found.");
        }
    }

    @Override
    public void clearAuthTokens() throws DataAccessException {
        System.out.println("AuthDAO: Clearing all authTokens.");
        usernamesToAuthTokens.clear();
        System.out.println("AuthDAO: All authTokens cleared.");
    }

    @Override
    public void deleteAuthTokenForUser(String username) throws DataAccessException {
        if (usernamesToAuthTokens.containsKey(username)) {
            usernamesToAuthTokens.remove(username);
            System.out.println("AuthDAO: All auth tokens removed for user: " + username);
        } else {
            System.out.println("AuthDAO: No auth tokens found for user: " + username);
        }
    }
}
