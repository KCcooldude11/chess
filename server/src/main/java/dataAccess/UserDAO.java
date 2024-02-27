package dataAccess;

import model.UserData;
import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class UserDAO implements IUserDAO {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, String> authTokens = new HashMap<>(); // Maps username to authToken

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.getUsername())) {
            throw new DataAccessException("User already exists.");
        }
        users.put(user.getUsername(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        System.out.println("Retrieving user from database: " + username);
        if (!users.containsKey(username)) {
            return null;
        }
        return users.get(username);
    }

    // Assume this method signature exists in IUserDAO
    public void createAuth(String username, String authToken) throws DataAccessException {
        if (!users.containsKey(username)) {
            throw new DataAccessException("Cannot create auth for non-existing user.");
        }
        authTokens.put(username, authToken);
    }

    // New method to delete an auth token
    public void deleteAuth(String authToken) throws DataAccessException {
        // This is a simple linear search; consider a more efficient approach for a real application
        String userKey = authTokens.entrySet().stream()
                .filter(entry -> authToken.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new DataAccessException("Auth token not found."));

        authTokens.remove(userKey);
    }
    @Override
    public void clearUsers() throws DataAccessException {
        users.clear(); // Assuming 'users' is the Map storing user data
    }
    @Override
    public boolean validateAuth(String authToken) throws DataAccessException {
        return authTokens.containsValue(authToken);
    }
}
