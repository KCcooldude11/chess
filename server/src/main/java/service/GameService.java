package service;

import dataAccess.IGameDAO;
import dataAccess.DataAccessException;
import model.GameData;

import java.util.List;

public class GameService {
    private IGameDAO gameDAO; // Assume this is initialized elsewhere

    public GameService(IGameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public List<GameData> listGames() throws ServiceException {
        try {
            return gameDAO.getGames(); // Assuming getGames() returns a List<GameData>
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to list games", e);
        }
    }

    public int createGame(String authToken, String gameName) throws ServiceException {
        try {
            // Assuming validateAuth method returns a username associated with the authToken
            String username = validateAuth(authToken);
            GameData game = new GameData(0, username, null, gameName, null); // Adjust based on your GameData constructor
            gameDAO.insertGame(game);
            return game.getGameID(); // Assuming GameData has a getGameID() method
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to create a new game.", e);
        }
    }

    // Add other methods as needed...

    // Placeholder method for auth token validation
    private String validateAuth(String authToken) throws DataAccessException {
        // Implement your actual authentication validation logic here
        return "username"; // Placeholder return
    }
}
