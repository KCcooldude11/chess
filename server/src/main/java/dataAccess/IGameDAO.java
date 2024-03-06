package dataAccess;

import model.GameData;
import java.util.Collection;

public interface IGameDAO {
    Integer createGame(String gameName) throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void clearAllGames() throws DataAccessException;

}
