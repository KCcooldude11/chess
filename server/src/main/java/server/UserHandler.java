package server;

import model.AuthData;
import util.MessageResponse;
import model.UserData;
import service.UserService;
import service.ServiceException;
import util.JsonUtil; // Ensure you have the JsonUtil class created

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void initializeRoutes() {
        // Register route
        spark.Spark.post("/user", "application/json", (request, response) -> {
            UserData newUser = JsonUtil.fromJson(request.body(), UserData.class);
            try {
                // Check if user already exists
                if (userService.userExists(newUser.getUsername())) {
                    response.status(403); // Forbidden
                    return JsonUtil.toJson(new ErrorMessage("Error: User already exists."));
                }

                AuthData authData = userService.register(newUser.getUsername(), newUser.getPassword(), newUser.getEmail());
                response.status(200);
                return JsonUtil.toJson(authData);
            } catch (ServiceException e) {
                response.status(400);
                return JsonUtil.toJson(new ErrorMessage("Error: " + e.getMessage()));
            }
        });

        // Login route
        spark.Spark.post("/session", "application/json", (request, response) -> {
            System.out.println("Login request received: " + request.body()); // Debug statement
            UserData userData = JsonUtil.fromJson(request.body(), UserData.class);
            System.out.println("Attempting login for username: " + userData.getUsername());
            try {
                AuthData authData = userService.login(userData.getUsername(), userData.getPassword());
                response.status(200);
                return JsonUtil.toJson(authData);
            } catch (ServiceException e) {
                System.err.println("Service exception during login: " + e.getMessage()); // Debug statement
                response.status(401);
                return JsonUtil.toJson(new ErrorMessage("Error: " + e.getMessage()));
            }
        });
        spark.Spark.delete("/session", (req, res) -> {
            String authToken = req.headers("Authorization");
            try {
                userService.logout(authToken);
                res.status(200); // HTTP 200 OK
                res.type("application/json"); // Ensure the response content-type is set to application/json
                return JsonUtil.toJson(new MessageResponse("User logged out successfully."));
            } catch (ServiceException e) {
                res.status(401); // HTTP 401 Unauthorized
                res.type("application/json");
                return JsonUtil.toJson(new ErrorMessage("Error: " + e.getMessage()));
            }
        });
    }

    // Assuming an ErrorMessage class exists to format error messages
    private class ErrorMessage {
        String message;

        public ErrorMessage(String message) {
            this.message = message;
        }
    }
}
