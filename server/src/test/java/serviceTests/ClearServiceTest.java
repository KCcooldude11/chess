package serviceTests;
import dataAccess.*;
import service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    private IUserDAO userDAO;
    private IGameDAO gameDAO;
    private IAuthDAO authDAO;
    private ClearService clearService;

    @BeforeEach
    void setUp() {
        userDAO = mock(IUserDAO.class);
        gameDAO = mock(IGameDAO.class);
        authDAO = mock(IAuthDAO.class);
        clearService = new ClearService(userDAO, gameDAO, authDAO);
    }

    @Test
    void clearAllData_Success() throws DataAccessException {
        assertDoesNotThrow(() -> clearService.clearAllData());
        verify(userDAO).clearUsers();
        verify(gameDAO).clearGames();
        verify(authDAO).clearAuthTokens();
    }
}
