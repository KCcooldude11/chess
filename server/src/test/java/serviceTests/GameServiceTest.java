package serviceTests;

import dataAccess.*;
import service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private IGameDAO gameDAO;
    private IAuthDAO authDAO;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameDAO = mock(IGameDAO.class);
        authDAO = mock(IAuthDAO.class);
        gameService = new GameService(gameDAO, authDAO);
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
}