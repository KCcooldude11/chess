package dataAccess;

import model.UserData;

public interface IUserDAO {
    void insertUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;

    void clearUsers() throws DataAccessException;

}
