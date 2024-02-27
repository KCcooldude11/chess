package service;


import java.util.UUID;
import dataAccess.IUserDAO;
import dataAccess.IAuthDAO;
import dataAccess.DataAccessException;
import model.UserData;
import model.AuthData;

public class UserService {
    private IUserDAO userDAO;
    private IAuthDAO authDAO; // Assume this is initialized elsewhere

    public UserService(IUserDAO userDAO, IAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(String username, String password, String email) throws ServiceException {
        try {
            // Validate input parameters
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty() || email == null || email.trim().isEmpty()) {
                throw new ServiceException("Missing or invalid registration information.");
            }

            // Check if user already exists
            UserData existingUser = userDAO.getUser(username);
            if (existingUser != null) {
                throw new ServiceException("User already exists.");
            }

            // Create new user
            UserData newUser = new UserData(username, password, email); // Assuming constructor matches
            userDAO.insertUser(newUser);

            // Create auth token for new user
            String authToken = generateAuthToken(username);
            authDAO.createAuthToken(username, authToken);

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
            authDAO.createAuthToken(username, authToken);
            System.out.println("Login successful for username: " + username + ", Auth Token: " + authToken);
            return new AuthData(authToken, username);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to log in.", e);
        }
    }
    public boolean userExists(String username) throws ServiceException {
        try {
            UserData user = userDAO.getUser(username);
            return user != null;
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to check if user exists.", e);
        }
    }
    public void logout(String authToken) throws ServiceException {
        try {
            // Invalidate the authToken
            authDAO.deleteAuthToken(authToken); // Assuming a method to invalidate auth token exists in IUserDAO
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to log out.", e);
        }
    }

    // Placeholder for the generateAuthToken method
    private String generateAuthToken(String username) {
        // Implement token generation logic
        return UUID.randomUUID().toString(); // Example using UUID
    }
}
