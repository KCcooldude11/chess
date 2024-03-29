package dataAccess;

import model.UserData;

public interface IUserDAO {

    UserData getUser(String username) throws DataAccessException;
    void createUser(String username, String password, String email) throws DataAccessException;
    void clearAllUsers() throws DataAccessException;
    boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException;

}
