package dataAccess;

import model.UserData;


public interface IUserDAO {
    void insertUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;
    // Add more methods as needed for your application
}
