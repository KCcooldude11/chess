package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.end.LoginEnd;
import model.request.LoginReq;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.UserService;

public class LogoutServiceTests {
    @Test
    void loginServiceSuccess() throws DataAccessException {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        userDAO.createUser("ExampleUsername", "TestPassword", "Test@Email");
        LoginReq reg = new LoginReq("ExampleUsername", "TestPassword");
        var res = userService.login(reg);
        Assertions.assertEquals("ExampleUsername", res.username());
        Assertions.assertEquals(LoginEnd.class, res.getClass());
        Assertions.assertNotEquals("", res.authToken());
        Assertions.assertNotEquals(null, res.authToken());
    }

    @Test
    void loginServiceErrors() {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        userDAO.createUser("ExampleUsername", "TestPassword", "Email");
        LoginReq reg = new LoginReq("ExampleUsername", "TestWrongPassword");
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(reg));
        LoginReq newReg = new LoginReq("TestWrongUsername", "TestPassword");
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(newReg));
    }
}
