package dataAccess;

import model.UserData;
import java.util.HashMap;

public class UserDAO implements IUserDAO {

    static private final HashMap<String, UserData> usersMap = new HashMap<>();

    @Override
    public UserData getUser(String username) {
        return usersMap.get(username);
    }

    @Override
    public void createUser(String username, String password, String email) {
        UserData user = new UserData(username, password, email);
        usersMap.put(username, user);
    }

    @Override
    public void clearAllUsers() {
        usersMap.clear();
    }
}
