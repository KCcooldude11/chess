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
    public RegisterEnd register(RegisterReq register) throws DataAccessException {
        if(register.username() == null || register.username().isEmpty() || register.password() == null || register.password().isEmpty() || register.email() == null || register.email().isEmpty()) {
            throw new DataAccessException("Bad request");
        }
        UserData user = userDAO.getUser(register.username());
        if(user != null) {
            throw new DataAccessException("User already exists");
        }

        userDAO.createUser(register.username(), register.password(), register.email());
        String auth = authDAO.createAuthToken(register.username());
        return new RegisterEnd(register.username(), auth);
    }
    public LoginEnd login(LoginReq req) throws DataAccessException {
        UserData user = userDAO.getUser(req.username());

        if(user == null) {
            throw new DataAccessException("Unauthorized Access");
        }

        if(!Objects.equals(user.password(), req.password())) {
            throw new DataAccessException("Unauthorized Access");
        }

        String auth = authDAO.createAuthToken(req.username());
        return new LoginEnd(req.username(), auth);
    }
    public void logout(LogoutReq req) throws DataAccessException {
        AuthData authData = authDAO.getAuthToken(req.authToken());

        if(authData == null) {
            throw new DataAccessException("Unauthorized Access");
        }

        authDAO.deleteAuthToken(req.authToken());
    }

}
