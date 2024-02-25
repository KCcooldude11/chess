package service;

import dataAccess.DataAccessException;
import dataAccess.IUserDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
    private IUserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO(); // In a real scenario, consider dependency injection.
    }

    public AuthData registerUser(UserData newUser) throws ServiceException {
        try {
            // Basic validation
            if (newUser.getUsername() == null || newUser.getPassword() == null || newUser.getEmail() == null) {
                throw new ServiceException("Registration data is incomplete.");
            }

            // Check if user already exists
            try {
                if (userDAO.getUser(newUser.getUsername()) != null) {
                    throw new ServiceException("Username already taken.");
                }
            } catch (DataAccessException e) {
                // User does not exist, proceed
            }

            // Insert the new user
            userDAO.insertUser(newUser);

            // Generate authentication token for the new user (simplified for this example)
            String authToken = generateAuthToken(newUser.getUsername());
            return new AuthData(authToken, newUser.getUsername());

        } catch (DataAccessException e) {
            throw new ServiceException("Failed to register the user.", e);
        }
    }

    private String generateAuthToken(String username) {
        // Placeholder: generate a real token in a real application
        return "authTokenFor-" + username;
    }
}