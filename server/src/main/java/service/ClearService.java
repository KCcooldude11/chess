package service;

import dataAccess.IDataAccess;

public class ClearService {
    private IDataAccess dataAccess; // Assume this is initialized elsewhere

    public void clearAllData() throws ServiceException {
        dataAccess.clearUsers();
        dataAccess.clearAuthTokens();
        dataAccess.clearGames();
    }
}