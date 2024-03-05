package dataAccess;

import model.UserData;

public interface IUserDAO {

    UserData getUser(String username) throws DataAccessException;
    void createUser(String username, String password, String email);
    void clearAllUsers();
    boolean verifyUser(String username, String providedClearTextPassword);

}
