package service;

import dataAccess.IUserDAO;
import model.UserData;
import model.AuthData;

public class UserService {
    private IUserDAO userDAO; // Assume this is initialized elsewhere, e.g., constructor

    public AuthData register(String username, String password, String email) throws ServiceException {
        // Check if user exists
        if (userDAO.getUser(username) != null) {
            throw new ServiceException("User already exists.");
        }
        // Create user
        userDAO.createUser(new UserData(username, password, email));
        // Create auth token
        String authToken = generateAuthToken(username);
        userDAO.createAuth(new AuthData(authToken, username));
        return new AuthData(authToken, username);
    }

    public AuthData login(String username, String password) throws ServiceException {
        UserData user = userDAO.getUser(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new ServiceException("Invalid username or password.");
        }
        String authToken = generateAuthToken(username);
        userDAO.createAuth(new AuthData(authToken, username));
        return new AuthData(authToken, username);
    }

    private String generateAuthToken(String username) {
        // Implementation to generate a unique authToken
        return "authTokenFor-" + username; // Simplified for illustration
    }
}
