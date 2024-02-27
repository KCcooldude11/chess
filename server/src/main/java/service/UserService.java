package service;


import java.util.UUID;
import dataAccess.IUserDAO;
import dataAccess.DataAccessException;
import model.UserData;
import model.AuthData;

public class UserService {
    private IUserDAO userDAO; // Assume this is initialized elsewhere

    public UserService(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthData register(String username, String password, String email) throws ServiceException {
        try {
            // Check if user already exists
            UserData existingUser = userDAO.getUser(username);
            if (existingUser != null) {
                throw new ServiceException("User already exists.");
            }

            // Create new user
            UserData newUser = new UserData(username, password, email); // Assuming constructor matches
            userDAO.insertUser(newUser);

            // Create auth token for new user
            String authToken = generateAuthToken(username); // Assume this method exists to generate a token
            userDAO.createAuth(username, authToken); // Corrected to pass username and authToken separately

            return new AuthData(authToken, username);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to register user.", e);
        }
    }

    // Example login method
    public AuthData login(String username, String password) throws ServiceException {
        try {
            UserData user = userDAO.getUser(username);
            if (user == null || !user.getPassword().equals(password)) {
                throw new ServiceException("Invalid username or password.");
            }
            String authToken = generateAuthToken(username);
            userDAO.createAuth(username, authToken);
            System.out.println("Login successful for username: " + username + ", Auth Token: " + authToken);
            return new AuthData(authToken, username);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to log in.", e);
        }
    }
    public void logout(String authToken) throws ServiceException {
        try {
            // Invalidate the authToken
            userDAO.deleteAuth(authToken); // Assuming a method to invalidate auth token exists in IUserDAO
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to log out.", e);
        }
    }

    // Placeholder for the generateAuthToken method
    private String generateAuthToken(String username) {
        // Implement token generation logic
        return UUID.randomUUID().toString(); // Example using UUID
    }
    public boolean validateAuthToken(String authToken) throws ServiceException {
        try {
            // Validate the authToken
            return userDAO.validateAuth(authToken); // Assuming a method to validate auth tokens exists in IUserDAO
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to validate auth token.", e);
        }
    }

    // Add other UserService methods as necessary...
}
