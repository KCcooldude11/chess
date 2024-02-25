package server;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import service.UserService;
import service.ServiceException;
import spark.ResponseTransformer;

// Assuming UserService is initialized and passed to the constructor of UserHandler
public class UserHandler {
    private final UserService userService;
    private final Gson gson;

    public UserHandler(UserService userService) {
        this.userService = userService;
        this.gson = new Gson();
    }

    public void initializeRoutes() {
        // Register route
        spark.Spark.post("/user/register", "application/json", (request, response) -> {
            UserData newUser = gson.fromJson(request.body(), UserData.class);
            try {
                AuthData authData = userService.register(newUser.getUsername(), newUser.getPassword(), newUser.getEmail());
                response.status(200);
                return authData;
            } catch (ServiceException e) {
                response.status(400);
                return new ErrorMessage("Registration failed: " + e.getMessage());
            }
        }, gson::toJson);

        // Login route
        spark.Spark.post("/user/login", "application/json", (request, response) -> {
            UserData userData = gson.fromJson(request.body(), UserData.class);
            try {
                AuthData authData = userService.login(userData.getUsername(), userData.getPassword());
                response.status(200);
                return authData;
            } catch (ServiceException e) {
                response.status(401);
                return new ErrorMessage("Login failed: " + e.getMessage());
            }
        }, gson::toJson);
    }

    // Assuming an ErrorMessage class exists to format error messages
    private class ErrorMessage {
        String message;

        public ErrorMessage(String message) {
            this.message = message;
        }
    }
}
