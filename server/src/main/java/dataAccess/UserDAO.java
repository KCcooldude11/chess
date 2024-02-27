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

    @Override
    public void clearUsers() throws DataAccessException {
        users.clear(); // Assuming 'users' is the Map storing user data
    }
}
