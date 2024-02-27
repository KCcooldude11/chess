package dataAccess;

import model.GameData;

import java.util.Collection;

public interface IGameDAO {
    Integer createGame(String gameName);
    void updateGame(GameData game);
    GameData getGame(int gameID);
    Collection<GameData> listGames();
    void clearAllGames();

}
