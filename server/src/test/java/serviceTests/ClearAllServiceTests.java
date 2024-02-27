package serviceTests;
import dataAccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.ClearService;
public class ClearAllServiceTests {

    @Test
    void createGameServiceSuccess() throws DataAccessException {GameDAO gameDAO = new GameDAO();
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        userDAO.createUser("ExampleUser1", "TestPassword1","Test@Email1");
        userDAO.createUser("ExampleUser2", "TestPassword2","Test@Email2");
        userDAO.createUser("ExampleUser3", "TestPassword3","Test@Email3");
        userDAO.createUser("ExampleUser4", "TestPassword4","Test@Email4");
        String authToken1 = authDAO.createAuthToken("ExampleUser1");
        String authToken2 =authDAO.createAuthToken("ExampleUser2");
        String authToken3 =authDAO.createAuthToken("ExampleUser3");
        String authToken4 =authDAO.createAuthToken("ExampleUser4");
        int gameID1 = gameDAO.createGame("Game1");
        int gameID2 = gameDAO.createGame("Game2");
        int gameID3 = gameDAO.createGame("Game3");
        int gameID4 = gameDAO.createGame("Game4");

        clearService.clearAll();
        Assertions.assertNull(userDAO.getUser("ExampleUser1"));
        Assertions.assertNull(userDAO.getUser("ExampleUser2"));
        Assertions.assertNull(userDAO.getUser("ExampleUser3"));
        Assertions.assertNull(userDAO.getUser("ExampleUser4"));
        Assertions.assertNull(authDAO.getAuthToken(authToken1));
        Assertions.assertNull(authDAO.getAuthToken(authToken2));
        Assertions.assertNull(authDAO.getAuthToken(authToken3));
        Assertions.assertNull(authDAO.getAuthToken(authToken4));
        Assertions.assertNull(gameDAO.getGame(gameID1));
        Assertions.assertNull(gameDAO.getGame(gameID2));
        Assertions.assertNull(gameDAO.getGame(gameID3));
        Assertions.assertNull(gameDAO.getGame(gameID4));
    }
}

