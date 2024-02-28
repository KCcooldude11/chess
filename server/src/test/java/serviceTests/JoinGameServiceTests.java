package serviceTests;

import dataAccess.*;
import model.request.JoinGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import org.junit.jupiter.api.Assertions;

import java.util.Random;

public class JoinGameServiceTests {

    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService gameService;

    @BeforeEach
    void setup() throws DataAccessException {
        gameDAO = new GameDAO();
        authDAO = new AuthDAO();
        gameService = new GameService(gameDAO, authDAO);
    }

    @Test
    void verifySuccessfulGameJoining() throws DataAccessException {
        String userToken = authDAO.createAuthToken("UniqueUser");
        int uniqueGameID = gameDAO.createGame("UniqueGame");
        JoinGame joinRequest = new JoinGame("WHITE", uniqueGameID);

        gameService.joinGame(joinRequest, userToken);

        Assertions.assertTrue(gameDAO.getGame(uniqueGameID).getWhiteUsername().equals("UniqueUser"), "Game joining should correctly assign user to the WHITE role.");

        gameDAO.clearAllGames();
    }

    @Test
    void handleErrorsWhenJoiningGame() throws DataAccessException {
        String userAuthToken = authDAO.createAuthToken("PlayerOne");
        int gameIdentity = gameDAO.createGame("ChallengeGame");
        JoinGame initialJoinRequest = new JoinGame("WHITE", gameIdentity);

        gameService.joinGame(initialJoinRequest, userAuthToken);

        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(initialJoinRequest, userAuthToken), "Joining a game with an already occupied role should not be allowed.");

        int nonExistentGameID = new Random().nextInt(10000) + 10000;
        JoinGame invalidGameRequest = new JoinGame("BLACK", nonExistentGameID);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(invalidGameRequest, userAuthToken), "Joining a non-existent game should not be allowed.");

        gameDAO.clearAllGames();
    }
}
