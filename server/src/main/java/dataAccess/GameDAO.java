package dataAccess;

import model.GameData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameDAO implements IGameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int gameIDCounter = 1;

    @Override
    public void insertGame(GameData game) throws DataAccessException {
        game.setGameID(gameIDCounter++);
        games.put(game.getGameID(), game);
    }


    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game does not exist.");
        }
        return games.get(gameID);
    }
    public List<GameData> listAllGames() throws DataAccessException {
        // Implementation to return a list of all games
        return new ArrayList<>(games.values());
    }

    // Assume these method signatures exist in IGameDAO

    // Gets all games
    public List<GameData> getGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    public void updateGame(GameData updatedGame) throws DataAccessException {
        if (!games.containsKey(updatedGame.getGameID())) {
            throw new DataAccessException("Attempted to update a non-existent game.");
        }
        games.put(updatedGame.getGameID(), updatedGame);
    }

    // Adds a player to a game; simplified for this example
    @Override
    public void addPlayerToGame(String username, int gameID, String playerColor) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found.");
        }

        // Example logic for setting player based on color
        if ("WHITE".equalsIgnoreCase(playerColor) && game.getWhiteUsername() == null) {
            game.setWhiteUsername(username);
        } else if ("BLACK".equalsIgnoreCase(playerColor) && game.getBlackUsername() == null) {
            game.setBlackUsername(username);
        } else {
            // Handle observers or error out if the color is taken or invalid
            throw new DataAccessException("Player color already taken or invalid.");
        }

        // Save the updated game
        updateGame(game);
    }


    @Override
    public void clearGames() throws DataAccessException {
        games.clear();
    }
}
