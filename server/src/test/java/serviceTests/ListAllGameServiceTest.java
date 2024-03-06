package serviceTests;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.GameDAO;
import model.request.ListGames;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.util.UUID;

public class ListAllGameServiceTest {

    @Test
    void listGamesServiceSuccess() throws DataAccessException {
        GameDAO listGame = new GameDAO(DatabaseManager.getConnection());
        AuthDAO listAuthDAO = new AuthDAO(DatabaseManager.getConnection());
        GameService listGameService = new GameService(listGame, listAuthDAO);
        String authToken = listAuthDAO.createAuthToken("ExampleUsername");
        ChessGame defaultGame = new ChessGame();
        listGame.createGame("Game1");
        listGame.createGame("Game2");
        listGame.createGame("Game3");
        listGame.createGame("Game4");
        ListGames req = new ListGames(authToken);
        var res = listGameService.listGames(req);
        Assertions.assertEquals(4, res.games().size());
        listGame.clearAllGames();
    }

    @Test
    void listGamesServiceErrors() throws DataAccessException {
        GameDAO ListError = new GameDAO(DatabaseManager.getConnection());
        AuthDAO authError = new AuthDAO(DatabaseManager.getConnection());
        GameService listGameService = new GameService(ListError, authError);
        authError.createAuthToken("ExampleUsername");
        ListGames req = new ListGames(UUID.randomUUID().toString());
        Assertions.assertThrows(DataAccessException.class, () -> listGameService.listGames(req));
        try {
            ListError.clearAllGames();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
