package serviceTests;

import java.util.List;
import dataAccess.*;
import service.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;

class GameServiceTest {

    private IGameDAO gameDAO;
    private IAuthDAO authDAO;
    private IUserDAO userDAO;
    private GameService gameService;
    private UserService userService;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = mock(IGameDAO.class);
        authDAO = mock(IAuthDAO.class);
        userDAO = mock(IUserDAO.class);
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);

        UserData validUser = new UserData("testUser", "password", "testUser@example.com");
        when(userDAO.getUser("testUser")).thenReturn(validUser);
    }

    @Test
    void createGame_InvalidAuthToken() throws DataAccessException {
        when(authDAO.validateAuthToken("invalidToken")).thenThrow(new DataAccessException("Invalid token"));
        assertThrows(ServiceException.class, () -> gameService.createGame("invalidToken", "ChessGame"));
    }

    @Test
    void joinGame_InvalidGameID() throws DataAccessException {
        when(authDAO.validateAuthToken("validToken")).thenReturn("user");
        doThrow(new DataAccessException("Game not found")).when(gameDAO).addPlayerToGame(anyString(), eq(999), anyString());
        assertThrows(ServiceException.class, () -> gameService.joinGame("validToken", 999, "WHITE"));
    }
    @Test
    void createGame_ValidAuthToken_GameCreated() throws DataAccessException, ServiceException {
        // Setup
        String validToken = "validToken";
        String username = "user";
        String gameName = "ChessGame";
        GameData expectedGame = new GameData(1, username, null, gameName, null);

        // Mock the behavior
        when(authDAO.validateAuthToken(validToken)).thenReturn(username);
        doAnswer(invocation -> {
            GameData game = invocation.getArgument(0);
            game.setGameID(1); // Simulate setting an ID on game creation
            return null;
        }).when(gameDAO).insertGame(any(GameData.class));

        // Execute
        GameData actualGame = gameService.createGame(validToken, gameName);

        // Verify
        assertNotNull(actualGame);
        assertEquals(1, actualGame.getGameID());
        assertEquals(username, actualGame.getWhiteUsername());
        assertEquals(gameName, actualGame.getGameName());

        // Ensure the interactions happened as expected
        verify(authDAO).validateAuthToken(validToken);
        verify(gameDAO).insertGame(any(GameData.class));
    }

    @Test
    void joinGame_ValidTokenAndGameID_PlayerJoined() throws DataAccessException, ServiceException, TeamColorUnavailableException {
        // Setup
        String validToken = "validToken";
        String username = "user";
        int validGameID = 1;
        String playerColor = "WHITE";

        // Mock the behavior
        when(authDAO.validateAuthToken(validToken)).thenReturn(username);
        doNothing().when(gameDAO).addPlayerToGame(username, validGameID, playerColor);

        // Execute
        gameService.joinGame(validToken, validGameID, playerColor);

        // Verify
        verify(authDAO).validateAuthToken(validToken);
        verify(gameDAO).addPlayerToGame(username, validGameID, playerColor);
    }

    @Test
    void listGames_Success() throws DataAccessException, ServiceException {
        // Setup
        GameData game1 = new GameData(1, "user1", null, "ChessGame1", null);
        GameData game2 = new GameData(2, "user2", "user3", "ChessGame2", null);
        when(gameDAO.listAllGames()).thenReturn(List.of(game1, game2));

        // Execute
        var gamesList = gameService.listGames();

        // Verify
        assertNotNull(gamesList);
        assertEquals(2, gamesList.size());
        assertTrue(gamesList.contains(game1));
        assertTrue(gamesList.contains(game2));

        // Ensure the interactions happened as expected
        verify(gameDAO).listAllGames();
    }
    @Test
    @DisplayName("Unique Authentication Token Each Login")
    void uniqueAuthenticationTokenEachLogin() throws DataAccessException, ServiceException {
        String username = "testUser";
        String password = "password";
        // Simulate two different auth tokens for two login attempts
        String authToken1 = "authToken1";
        String authToken2 = "authToken2";
        // Simulate game creation
        String gameName = "ChessGame";
        GameData gameData = new GameData(1, username, null, gameName, null);

        // Set up the first login attempt
        when(authDAO.validateAuthToken(authToken1)).thenReturn(username);
        when(authDAO.validateAuthToken(authToken2)).thenReturn(username);
        doNothing().when(authDAO).createAuthToken(eq(username), anyString());
        doNothing().when(gameDAO).insertGame(any(GameData.class));

        // First login attempt
        String firstLoginToken = userService.login(username, password).getAuthToken();

        // Second login attempt
        String secondLoginToken = userService.login(username, password).getAuthToken();

        when(authDAO.validateAuthToken(firstLoginToken)).thenReturn(username);
        when(authDAO.validateAuthToken(secondLoginToken)).thenReturn(username);

        // Ensure different tokens were generated
        assertNotEquals(firstLoginToken, secondLoginToken, "Auth tokens should be unique for each login");

        // Use first token to create a game
        GameData createdGame = gameService.createGame(firstLoginToken, gameName);
        assertNotNull(createdGame, "Game creation should succeed with valid auth token");

        // Logout using first token
        userService.logout(firstLoginToken);
        when(authDAO.validateAuthToken(firstLoginToken)).thenThrow(new DataAccessException("Invalid or expired authToken."));

        // Attempt to use the first token after logout should fail
        ServiceException exception = assertThrows(ServiceException.class, () -> gameService.createGame(firstLoginToken, "AnotherGame"),
                "Using an auth token after logout should not be allowed");

        // Verify the auth token was invalidated after logout
        assertTrue(exception.getMessage().contains("Invalid or expired authToken"), "Exception message should indicate invalid or expired auth token");

        // List games using the second token
        List<GameData> gamesList = gameService.listGames();
        assertFalse(gamesList.isEmpty(), "Games list should not be empty after creating a game");

        // Verify interactions
        verify(authDAO, times(2)).validateAuthToken(anyString()); // Two validations: one for each login
        verify(gameDAO).insertGame(any(GameData.class)); // One game insertion
    }
}