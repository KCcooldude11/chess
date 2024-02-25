package dataAccess;

import model.GameData;
import java.util.List;

public interface IGameDAO {
    void insertGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;

    // Method to retrieve all games
    List<GameData> getGames() throws DataAccessException;

    // Method to add a player to a game
    void addPlayerToGame(String username, int gameID, String playerColor) throws DataAccessException;
}
