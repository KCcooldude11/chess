package service;

import dataAccess.*;

public class ClearService {

    private final IUserDAO userDAO;
    private final IAuthDAO authDAO;
    private final IGameDAO gameDAO;

    public ClearService(IUserDAO userDAO, IAuthDAO authDAO, IGameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    public void clearAll() throws DataAccessException {
        userDAO.clearAllUsers();
        authDAO.clearAuthTokens();
        gameDAO.clearAllGames();
    }
}
