package clientTests;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import java.io.IOException;
import java.util.List;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static final String SERVER_URL = "http://localhost:8080";


    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0); // Dynamically allocate a port
        System.out.println("Started test HTTP server on port: " + port);
        String serverUrl = "http://localhost:" + port; // Construct the server URL with the dynamic port
        facade = new ServerFacade(serverUrl); // Initialize the facade with the correct server URL
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabaseBeforeTest() {
        // Use the clearDatabase method to ensure a clean database state before each test
        try {
            DatabaseManager.clearDatabase();
        } catch (DataAccessException e) {
            e.printStackTrace();
            Assertions.fail("Failed to clear the database before a test.");
        }
    }
    @Test
    void loginSuccess() throws Exception {
        // First, register a new user
        String username = "testUser" + System.currentTimeMillis(); // Ensure uniqueness
        String password = "testPass";
        String email = "testEmail" + System.currentTimeMillis() + "@example.com"; // Ensure uniqueness
        facade.register(username, password, email);

        // Attempt to log in with the same credentials
        AuthData authData = Assertions.assertDoesNotThrow(() -> facade.login(username, password));

        // Assert that a valid auth token is received
        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.getAuthToken());
        Assertions.assertFalse(authData.getAuthToken().isEmpty());
    }
    @Test
    void loginFailureWithInvalidCredentials() {
        // Attempt to log in with a non-existent user or wrong credentials
        String username = "nonExistentUser";
        String password = "wrongPassword";

        // This attempt should fail
        Exception exception = Assertions.assertThrows(IOException.class, () -> facade.login(username, password));

        // Optionally check the exception message if your implementation provides specific error messages
        Assertions.assertTrue(exception.getMessage().contains("Login failed") || exception.getMessage().contains("Invalid credentials"));
    }
    @Test
    void registerSuccess() throws Exception {
        // Assuming register method returns an AuthData object on success
        AuthData authData = facade.register("testUser", "password123", "test@example.com");
        Assertions.assertNotNull(authData);
        Assertions.assertTrue(authData.getAuthToken().length() > 10); // Example assertion
    }
    @Test
    void registerFailureWithExistingUser() {
        // Assuming you have a way to generate unique user details or you can manually specify them
        String username = "testUser" + System.currentTimeMillis(); // Ensure uniqueness
        String password = "testPass";
        String email = "testEmail" + System.currentTimeMillis() + "@example.com"; // Ensure uniqueness

        // First attempt to register should succeed
        Assertions.assertDoesNotThrow(() -> facade.register(username, password, email));

        // Second attempt to register the same user should fail
        Exception exception = Assertions.assertThrows(IOException.class, () -> facade.register(username, password, email));

        // Optionally check the exception message if your implementation provides specific error messages
        Assertions.assertTrue(exception.getMessage().contains("User already exists") || exception.getMessage().contains("Registration failed"));
    }
    @Test
    void logoutSuccess() throws Exception {
        // Register and log in a new user to get an auth token
        String uniqueSuffix = Long.toString(System.currentTimeMillis());
        String username = "user" + uniqueSuffix;
        String password = "pass" + uniqueSuffix;
        String email = "email" + uniqueSuffix + "@example.com";
        AuthData authData = facade.register(username, password, email);
        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.getAuthToken());

        // Perform logout with the obtained auth token
        Assertions.assertDoesNotThrow(() -> facade.logout(authData.getAuthToken()));

        // If your server provides additional means to verify the logout was successful (e.g., by
        // checking if the token is invalidated), you can add further assertions here.
        // Note: The effectiveness of such a test might depend on your server's implementation details.
    }
    @Test
    void logoutFailureWithInvalidToken() {
        // Use an invalid auth token
        String invalidToken = "invalidToken";

        // Attempt to logout with the invalid token and expect an IOException to be thrown
        Exception exception = Assertions.assertThrows(IOException.class, () -> facade.logout(invalidToken));

        // Optionally, you can further assert on the exception message if your server returns a consistent error message for such failures
        Assertions.assertTrue(exception.getMessage().contains("Logout failed"));
    }
    @Test
    void createGameSuccess() throws IOException, InterruptedException {
        // Assuming you have a method to get a valid auth token for testing
        facade.register("testUser", "testPassword", "testEmail");
        String validToken = facade.login("testUser", "testPassword").getAuthToken();
        // Attempt to create a new game
        Integer gameId = facade.createGame("New Game", validToken);

        // Assert that a game ID was returned, indicating success
        Assertions.assertNotNull(gameId, "Game creation should return a valid game ID.");
    }
    @Test
    void createGameFailureDueToUnauthorized() {
        // Attempt to create a game without a valid authorization token
        String invalidAuthToken = "invalidToken";
        String gameName = "Test Game";

        // Use assertThrows to expect a specific exception type
        // Adjust the exception class if your method throws a different exception on failure
        Exception exception = Assertions.assertThrows(IOException.class, () -> {
            facade.createGame(gameName, invalidAuthToken);
        });
    }
    @Test
    void joinGameSuccess() throws Exception {
        // Register a user and login to get a valid auth token
        String username = "user_" + System.currentTimeMillis(); // Ensures a unique username
        String password = "password";
        String email = username + "@example.com";
        AuthData authData = facade.register(username, password, email);
        String authToken = authData.getAuthToken();

        // Create a game to join
        String gameName = "TestGame";
        Integer gameId = facade.createGame(gameName, authToken);

        // Attempt to join the game
        Assertions.assertDoesNotThrow(() -> facade.joinGame(authToken, gameId, "WHITE"));

    }
    @Test
    void joinGameFailureDueToUnauthorized() {
        String invalidAuthToken = "invalid-token";
        int gameId = 123; // Assuming this is a valid game ID for testing purposes

        // Attempt to join the game with the invalid token, expecting an IOException due to unauthorized access
        Exception exception = Assertions.assertThrows(IOException.class, () -> {
            facade.joinGame(invalidAuthToken, gameId, "WHITE");
        });
    }
    @Test
    void listGamesSuccess() throws Exception {
        // Register and login to get a valid auth token
        String username = "user_" + System.currentTimeMillis(); // Ensures a unique username
        String password = "password";
        String email = username + "@example.com";
        AuthData authData = facade.register(username, password, email);
        String authToken = authData.getAuthToken();

        // Create a game to ensure there's at least one game to list
        String gameName = "TestGame";
        facade.createGame(gameName, authToken);

        // Attempt to list games
        List<GameData> games = facade.listGames(authToken);

        // Verify that the list contains at least one game
        Assertions.assertFalse(games.isEmpty());
        // Optionally, verify that the created game is in the list
        Assertions.assertTrue(games.stream().anyMatch(game -> game.getGameName().equals(gameName)));
    }
    @Test
    void listGamesFailureDueToUnauthorized() {
        String invalidAuthToken = "invalid-token";

        // Attempt to list games with an invalid token, expecting an IOException due to unauthorized access
        Exception exception = Assertions.assertThrows(IOException.class, () -> {
            facade.listGames(invalidAuthToken);
        });
    }
    @Test
    void observeGameSuccess() throws Exception {
        // Setup prerequisites for observing a game, such as creating a user, logging in, and creating a game.
        String username = "testUser" + System.currentTimeMillis();
        String password = "testPass";
        String email = "testEmail" + System.currentTimeMillis() + "@example.com";
        facade.register(username, password, email);
        AuthData authData = facade.login(username, password);
        Integer gameId = facade.createGame("Test Game", authData.getAuthToken());

        // Attempt to observe the game
        GameData observedGame = facade.observeGame(authData.getAuthToken(), gameId);

        // Asserts
        Assertions.assertNotNull(observedGame);
        Assertions.assertEquals(gameId, observedGame.getGameID());
        // Add more assertions as necessary to validate the observed game data
    }
    @Test
    void observeGameFailureWithNonexistentGame() throws IOException, InterruptedException {
        // Assuming an auth token is required even for observing a game
        String username = "observerUser" + System.currentTimeMillis();
        String password = "observerPass";
        facade.register(username, password, "observerEmail" + System.currentTimeMillis() + "@example.com");
        AuthData authData = facade.login(username, password);

        // Use an arbitrary game ID that is unlikely to exist
        int nonexistentGameId = 999999;

        // Attempt to observe the non-existent game, expecting an IOException to be thrown
        Exception exception = Assertions.assertThrows(IOException.class, () -> facade.observeGame(authData.getAuthToken(), nonexistentGameId));

        // Optionally, you can further assert on the exception message
        Assertions.assertTrue(exception.getMessage().contains("Failed to observe game"));
    }
}
