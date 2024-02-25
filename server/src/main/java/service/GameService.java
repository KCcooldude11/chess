package service;

import dataAccess.DataAccessException;
import dataAccess.IGameDAO;
import dataAccess.GameDAO;
import model.GameData;
import chess.ChessGame;

public class GameService {
    private IGameDAO gameDAO;

    public GameService() {
        this.gameDAO = new GameDAO(); // In a real scenario, consider dependency injection.
    }

    public GameData createGame(String gameName, String username) throws ServiceException {
        try {
            // Validation: Ensure game name and username are provided
            if (gameName == null || username == null) {
                throw new ServiceException("Game name or username is missing.");
            }

            // Create a new ChessGame instance to manage the game state
            ChessGame newChessGame = new ChessGame();

            // Create a GameData object for persistence
            GameData newGame = new GameData(0, username, null, gameName, newChessGame);

            // Persist the new game
            gameDAO.insertGame(newGame);

            return newGame;

        } catch (DataAccessException e) {
            throw new ServiceException("Failed to create a new game.", e);
        }
    }
}
