package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Random;
import java.util.HashMap;
import java.util.Collection;

public class GameDAO implements IGameDAO {

    static private final HashMap<Integer, GameData> gamesMap = new HashMap<>();

    @Override
    public Integer createGame(String gameName) {
        int gameID;
        do {
            gameID = (int) (Math.random() * 10000);
        } while (gamesMap.containsKey(gameID));

        gamesMap.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }
    @Override
    public GameData getGame(int gameID) {
        return gamesMap.get(gameID);
    }
    @Override
    public Collection<GameData> listGames() {
        return gamesMap.values();
    }
    @Override
    public void updateGame(GameData game) {
        gamesMap.put(game.getGameID(), game);
    }
    @Override
    public void clearAllGames() {
        gamesMap.clear();
    }
}
