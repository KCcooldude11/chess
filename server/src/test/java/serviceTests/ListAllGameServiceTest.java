package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.request.ListGamesReq;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.util.UUID;

public class ListAllGameServiceTest {

    @Test
    void listGamesServiceSuccess() throws DataAccessException {
        GameDAO listGame = new GameDAO();
        AuthDAO listAuthDAO = new AuthDAO();
        GameService listGameService = new GameService(listGame, listAuthDAO);
        String authToken = listAuthDAO.createAuthToken("ExampleUsername");
        listGame.createGame("Game1");
        listGame.createGame("Game2");
        listGame.createGame("Game3");
        listGame.createGame("Game4");
        ListGamesReq req = new ListGamesReq(authToken);
        var res = listGameService.listGames(req);
        Assertions.assertEquals(4, res.games().size());
        listGame.ClearAllGames();
    }

    @Test
    void listGamesServiceErrors() {
        GameDAO ListError = new GameDAO();
        AuthDAO authError = new AuthDAO();
        GameService listGameService = new GameService(ListError, authError);
        authError.createAuthToken("ExampleUsername");
        ListGamesReq req = new ListGamesReq(UUID.randomUUID().toString());
        Assertions.assertThrows(DataAccessException.class, () -> listGameService.listGames(req));
        ListError.ClearAllGames();
    }
}
