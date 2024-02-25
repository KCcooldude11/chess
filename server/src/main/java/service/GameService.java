package service;

import dataAccess.IGameDAO;
import dataAccess.IAuthDAO;
import dataAccess.DataAccessException;
import model.GameData;
import java.util.*;

public class GameService {
    private IGameDAO gameDAO;
    private IAuthDAO authDAO;

    public GameService(IGameDAO gameDAO, IAuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    // Use the validateAuthToken method that returns a username
    private String getUsernameFromAuthToken(String authToken) throws ServiceException {
        try {
            // This now returns a username if the token is valid, or throws an exception if not
            return authDAO.validateAuthToken(authToken);
        } catch (DataAccessException e) {
            throw new ServiceException("Authentication failed: " + e.getMessage(), e);
        }
    }

    // Example use case: Creating a game now involves validating the auth token to get a username
    public int createGame(String authToken, String gameName) throws ServiceException {
        try {
            String username = getUsernameFromAuthToken(authToken); // Validate auth token and get username
            GameData game = new GameData(0, username, null, gameName, null); // Adjust based on your GameData constructor
            gameDAO.insertGame(game);
            return game.getGameID();
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to create a new game.", e);
        }
    }

    public List<GameData> listGames() throws ServiceException {
        try {
            return gameDAO.listAllGames(); // Assuming there's a method listAllGames() in your IGameDAO
        } catch (DataAccessException e) {
            throw new ServiceException("Error fetching list of games", e);
        }
    }
}
