package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import model.request.*;
import model.end.*;
import java.util.Objects;

public class UserService {

    private final IUserDAO userDAO;
    private final IAuthDAO authDAO;

    public UserService(IUserDAO userDAO, IAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterEnd register(Register register) throws DataAccessException {
        if(register.getUsername() == null || register.getUsername().isEmpty() ||
                register.getPassword() == null || register.getPassword().isEmpty() ||
                register.getEmail() == null || register.getEmail().isEmpty()) {
            throw new DataAccessException("Bad request");
        }
        UserData user = userDAO.getUser(register.getUsername());
        if(user != null) {
            throw new DataAccessException("User already exists");
        }

        // createUser now hashes the password internally
        userDAO.createUser(register.getUsername(), register.getPassword(), register.getEmail());
        String authToken = authDAO.createAuthToken(register.getUsername());
        return new RegisterEnd(register.getUsername(), authToken);
    }

    public LoginEnd login(Login req) throws DataAccessException {
        // Verify if the provided plaintext password matches the hashed password stored in the database
        boolean isValidUser = userDAO.verifyUser(req.getUsername(), req.getPassword());

        if(!isValidUser) {
            throw new DataAccessException("Unauthorized Access");
        }

        String authToken = authDAO.createAuthToken(req.getUsername());
        return new LoginEnd(req.getUsername(), authToken);
    }

    public void logout(Logout req) throws DataAccessException {
        AuthData authData = authDAO.getAuthToken(req.getAuthToken());

        if(authData == null) {
            throw new DataAccessException("Unauthorized Access");
        }

        authDAO.deleteAuthToken(req.getAuthToken());
    }
}
