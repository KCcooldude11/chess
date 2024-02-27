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
            // Attempt to validate the authToken and retrieve the associated username
            String username = getUsernameFromAuthToken(authToken);
            if (username == null || username.isEmpty()) {
                throw new ServiceException("Authentication failed: Invalid authToken.");
            }

            // Proceed with game creation using the validated username
            GameData game = new GameData(0, username, null, gameName, null); // Adjust according to your GameData structure
            gameDAO.insertGame(game);
            return game.getGameID(); // Ensure this ID is properly generated/set within insertGame
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to create game due to authentication issues.", e);
        }
    }

    public List<GameData> listGames() throws ServiceException {
        try {
            return gameDAO.listAllGames();
        } catch (DataAccessException e) {
            throw new ServiceException("Error fetching list of games", e);
        }
    }
    public void joinGame(String authToken, int gameID, String playerColor) throws ServiceException {
        // Validate authToken and get username
        String username = getUsernameFromAuthToken(authToken); // Method to validate token and extract username

        // Logic to add player to the game, e.g., using IGameDAO's addPlayerToGame method
        try {
            gameDAO.addPlayerToGame(username, gameID, playerColor);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to join game", e);
        }
    }

}
