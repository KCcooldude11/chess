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
        if(register.getUsername() == null || register.getUsername().isEmpty() || register.getPassword() == null || register.getPassword().isEmpty() || register.getEmail() == null || register.getEmail().isEmpty()) {
            throw new DataAccessException("Bad request");
        }
        UserData user = userDAO.getUser(register.getUsername());
        if(user != null) {
            throw new DataAccessException("User already exists");
        }

        userDAO.createUser(register.getUsername(), register.getPassword(), register.getEmail());
        String auth = authDAO.createAuthToken(register.getUsername());
        return new RegisterEnd(register.getUsername(), auth);
    }

    public LoginEnd login(Login req) throws DataAccessException {
        UserData user = userDAO.getUser(req.getUsername());

        if(user == null) {
            throw new DataAccessException("Unauthorized Access");
        }

        if(!Objects.equals(user.getPassword(), req.getPassword())) {
            throw new DataAccessException("Unauthorized Access");
        }

        String auth = authDAO.createAuthToken(req.getUsername());
        return new LoginEnd(req.getUsername(), auth);
    }

    public void logout(Logout req) throws DataAccessException {
        AuthData authData = authDAO.getAuthToken(req.getAuthToken());

        if(authData == null) {
            throw new DataAccessException("Unauthorized Access");
        }

        authDAO.deleteAuthToken(req.getAuthToken());
    }
}
