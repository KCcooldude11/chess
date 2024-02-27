package service;

import dataAccess.IUserDAO;
import dataAccess.IGameDAO;
import dataAccess.IAuthDAO;

import dataAccess.DataAccessException;

public class ClearService {
    private IUserDAO userDAO;
    private IGameDAO gameDAO;
    private IAuthDAO authDAO;

    // Constructor injection is used for simplicity
    public ClearService(IUserDAO userDAO, IGameDAO gameDAO, IAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void clearAllData() throws ServiceException {
        try {
            userDAO.clearUsers();
            authDAO.clearAuthTokens();
            gameDAO.clearGames();
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to clear all data", e);
        }
    }
}
