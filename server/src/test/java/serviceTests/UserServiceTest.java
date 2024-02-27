package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.IUserDAO;
import dataAccess.UserDAO;
import dataAccess.IAuthDAO;
import model.UserData;
import service.UserService;
import service.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private IUserDAO userDAO;
    private IAuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = mock(IUserDAO.class);
        userService = new UserService(userDAO, authDAO);
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
        verify(userDAO).createAuth(anyString(), anyString());
    }
}

