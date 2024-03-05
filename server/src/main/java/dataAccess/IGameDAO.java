package dataAccess;

import model.GameData;
import java.util.Collection;
import chess.ChessGame;
public interface IGameDAO {
    Integer createGame(String gameName, ChessGame game);
    void updateGame(GameData game);
    GameData getGame(int gameID);
    Collection<GameData> listGames();
    void clearAllGames();

}
