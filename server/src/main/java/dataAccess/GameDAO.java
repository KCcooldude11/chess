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

    // Assume these method signatures exist in IGameDAO

    // Gets all games
    public List<GameData> getGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    // Adds a player to a game; simplified for this example
    public void addPlayerToGame(String username, int gameID, String playerColor) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found.");
        }
        // Logic to add player to game, based on color or as an observer
        // This is very simplified and would need to be fleshed out based on your game logic
    }
}
