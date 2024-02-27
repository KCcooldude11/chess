package serviceTests;

import dataAccess.*;
import model.*;
import service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private IUserDAO userDAO;
    private IAuthDAO authDAO;
    private IGameDAO gameDAO;
    private UserService userService;
    private GameService gameService;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = mock(IUserDAO.class);
        authDAO = mock(IAuthDAO.class);
        gameDAO = mock(IGameDAO.class);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO); // Initialize GameService with mocked DAOs

        when(userDAO.getUser("testUser")).thenReturn(new UserData("testUser", "testPassword", "test@example.com"));

        // Mock auth token validation to return the username
        when(authDAO.validateAuthToken(anyString())).thenReturn("testUser");
        when(userDAO.getUser("testUser")).thenReturn(new UserData("testUser", "testPassword", "test@example.com"));


    }

    @Test
    void registerUser_UserAlreadyExists() throws DataAccessException {
        when(userDAO.getUser("existingUser")).thenReturn(new UserData("existingUser", "password", "email@example.com"));
        assertThrows(ServiceException.class, () -> userService.register("existingUser", "password", "email@example.com"));
    }

    @Test
    void loginUser_IncorrectPassword() throws DataAccessException {
        when(userDAO.getUser("user")).thenReturn(new UserData("user", "correctPassword", "email@example.com"));
        assertThrows(ServiceException.class, () -> userService.login("user", "incorrectPassword"));
    }

    @Test
    void loginUser_NonexistentUser() throws DataAccessException {
        when(userDAO.getUser("nonexistentUser")).thenReturn(null);
        assertThrows(ServiceException.class, () -> userService.login("nonexistentUser", "password"));
    }
    @Test
    void registerUser_Success() throws DataAccessException {
        when(userDAO.getUser("newUser")).thenReturn(null);
        assertDoesNotThrow(() -> userService.register("newUser", "password", "email@example.com"));
        verify(userDAO).insertUser(any(UserData.class));
    }
    @Test
    void loginUser_Success() throws DataAccessException {
        when(userDAO.getUser("existingUser")).thenReturn(new UserData("existingUser", "password", "email@example.com"));
        assertDoesNotThrow(() -> userService.login("existingUser", "password"));
        verify(authDAO).createAuthToken(anyString(), anyString());
    }
    @Test
    public void testLoginCreatesValidAuthToken() throws DataAccessException, ServiceException {
        String username = "testUser";
        String password = "testPassword";
        // Assume userService is an instance of UserService
        AuthData authData = userService.login(username, password);
        assertNotNull(authData.getAuthToken(), "Auth token should not be null after login");
        assertEquals(username, authDAO.validateAuthToken(authData.getAuthToken()), "The auth token should be valid and associated with the correct username");
    }
    @Test
    public void testCreateGameWithValidToken() throws DataAccessException, ServiceException {
        String username = "testUser";
        String password = "testPassword";
        AuthData authData = userService.login(username, password);
        String gameName = "ChessMatch";
        // Assume gameService is an instance of GameService
        GameData gameData = gameService.createGame(authData.getAuthToken(), gameName);
        assertNotNull(gameData, "Game creation should return a valid GameData object");
        assertEquals(username, gameData.getWhiteUsername(), "The game should be associated with the user who created it");
    }
    @Test
    public void testLogoutInvalidatesAuthToken() throws DataAccessException, ServiceException {
        String authToken = "testAuthToken";
        doNothing().when(authDAO).deleteAuthToken(authToken);

        // Action
        userService.logout(authToken);

        // Verify
        verify(authDAO).deleteAuthToken(authToken);
    }
}

