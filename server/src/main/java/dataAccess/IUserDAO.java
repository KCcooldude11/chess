package dataAccess;

import model.UserData;

public interface IUserDAO {
    void insertUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;

    // Method to create an authentication token for a user
    void createAuth(String username, String authToken) throws DataAccessException;

    // Method to delete an authentication token
    void deleteAuth(String authToken) throws DataAccessException;

    void clearUsers() throws DataAccessException;
}
