package dataAccess;

import model.UserData;
import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class GameDAO implements IGameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int gameIDCounter = 1; // Simple counter to simulate auto-incrementing IDs

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
}