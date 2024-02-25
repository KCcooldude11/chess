package service;

import dataAccess.IGameDAO;
import model.GameData;

import java.util.List;

public class GameService {
    private IGameDAO gameDAO; // Assume this is initialized elsewhere

    public List<GameData> listGames() throws ServiceException {
        return gameDAO.getGames();
    }

    public int createGame(String username, String gameName) throws ServiceException {
        int gameId = gameDAO.createGame(new GameData(username, gameName));
        return gameId;
    }

    public void joinGame(String username, int gameID, String playerColor) throws ServiceException {
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new ServiceException("Game not found.");
        }
        // Simplified: Assuming a method to add a player to a game
        gameDAO.addPlayerToGame(username, gameID, playerColor);
    }
}
