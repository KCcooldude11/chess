package dataAccess;

import model.UserData;
import model.GameData;

public interface IGameDAO {
    void insertGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;
    // Add more methods as needed
}