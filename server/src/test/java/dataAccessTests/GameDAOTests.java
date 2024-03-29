package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataAccess.UserDAO;

import java.sql.Connection;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {

    private Connection conn;
    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() throws Exception {
        conn = DatabaseManager.getConnection();
        DatabaseManager.clearDatabase();
        gameDAO = new GameDAO(conn);
        UserDAO userDAO = new UserDAO(conn);
        userDAO.createUser("player1", "password1", "player1@example.com");
        userDAO.createUser("player2", "password2", "player2@example.com");
    }

    @AfterEach
    public void tearDown() throws Exception {
        DatabaseManager.clearDatabase();
        if (conn != null) conn.close();
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        Integer gameID = gameDAO.createGame("TestGame");
        assertNotNull(gameID, "Game should be created with a valid ID");
    }
    @Test
    public void createGameFailure() {
        assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(null);
        }, "Creating a game with a null name should throw DataAccessException.");

        assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame("");
        }, "Creating a game with an empty name should throw DataAccessException.");
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        Integer gameID = gameDAO.createGame("TestGame");
        GameData gameData = gameDAO.getGame(gameID);
        assertNotNull(gameData, "GameData should be retrieved successfully");
        assertEquals("TestGame", gameData.getGameName(), "Game name should match the created game");
    }

    @Test
    public void getGameFailure() throws DataAccessException {
        Integer invalidGameID = -1;
        GameData gameData = gameDAO.getGame(invalidGameID);
        assertNull(gameData, "Should return null for a non-existent game ID");
    }
    @Test
    public void listGamesFailure() throws Exception {
        DatabaseManager.clearDatabase(); // Clear database to ensure isolation
        conn.close();

        assertThrows(DataAccessException.class, () -> {
            gameDAO.listGames();
        }, "Listing games with a closed database connection should throw DataAccessException.");
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        Integer gameID = gameDAO.createGame("TestGame");
        GameData gameData = gameDAO.getGame(gameID);
        assertNotNull(gameData, "Precondition failed: GameData should not be null");

        gameData.setWhiteUsername("player1");
        gameData.setBlackUsername("player2");
        gameDAO.updateGame(gameData);

        GameData updatedGameData = gameDAO.getGame(gameID);
        assertNotNull(updatedGameData, "Updated GameData should be retrieved successfully");
        assertEquals("player1", updatedGameData.getWhiteUsername(), "White player username should be updated");
        assertEquals("player2", updatedGameData.getBlackUsername(), "Black player username should be updated");
    }
    @Test
    public void updateGameFailure() throws DataAccessException {
        // Attempting to update a game that does not exist
        GameData nonExistentGame = new GameData(-1, null, null, "NonExistentGame", null);
        assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGame(nonExistentGame);
        }, "Attempting to update a non-existent game should throw DataAccessException.");
    }


    @Test
    public void listGamesSuccess() throws DataAccessException {
        gameDAO.createGame("Game1");
        gameDAO.createGame("Game2");

        Collection<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size(), "Should list all created games");
    }

    @Test
    public void clearAllGamesSuccess() throws DataAccessException {
        gameDAO.createGame("Game1");
        gameDAO.createGame("Game2");

        gameDAO.clearAllGames();

        Collection<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty(), "All games should be cleared successfully");
    }
}
