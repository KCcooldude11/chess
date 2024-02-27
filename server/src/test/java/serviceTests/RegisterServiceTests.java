package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.request.RegisterReq;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.UserService;

public class RegisterServiceTests {

    @Test
    void registerServiceSuccess() throws DataAccessException {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterReq reg = new RegisterReq("ExampleUsernameReg", "TestPasswordReg", "EmailReg");
        var res = userService.register(reg);
        Assertions.assertEquals("ExampleUsernameReg", userDAO.getUser("ExampleUsernameReg").getUsername());
        Assertions.assertEquals("TestPasswordReg", userDAO.getUser("ExampleUsernameReg").getPassword());
        Assertions.assertEquals("EmailReg", userDAO.getUser("ExampleUsernameReg").getEmail());
        Assertions.assertEquals("ExampleUsernameReg", res.username());
        Assertions.assertNotEquals("", res.authToken());


    }

    @Test
    void registerServiceErrors() throws DataAccessException {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterReq reg = new RegisterReq("ExampleUsername1", "TestPassword1", "Email1");
        userService.register(reg);
        RegisterReq sameReg = new RegisterReq("ExampleUsername1", "TestPassword1", "Email1");
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(sameReg));
        RegisterReq newReg = new RegisterReq("", "TestPassword", "Email");
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(newReg));    }

}
