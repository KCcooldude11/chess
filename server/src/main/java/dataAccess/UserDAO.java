package dataAccess;

import model.UserData;
import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class UserDAO implements IUserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.getUsername())) {
            throw new DataAccessException("User already exists.");
        }
        users.put(user.getUsername(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (!users.containsKey(username)) {
            throw new DataAccessException("User does not exist.");
        }
        return users.get(username);
    }
}
