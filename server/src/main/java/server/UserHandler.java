package server;

import model.AuthData;
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

            // Validation for null values
            if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty() ||
                    newUser.getPassword() == null || newUser.getPassword().trim().isEmpty() ||
                    newUser.getEmail() == null || newUser.getEmail().trim().isEmpty()) {
                response.status(400); // Bad Request
                return JsonUtil.toJson(new ErrorMessage("Username, password, and email are required"));
            }

            try {
                AuthData authData = userService.register(newUser.getUsername(), newUser.getPassword(), newUser.getEmail());
                response.status(200);
                return JsonUtil.toJson(authData);
            } catch (ServiceException e) {
                response.status(400); // Here, you might want to differentiate between different types of errors
                return JsonUtil.toJson(new ErrorMessage("Registration failed: " + e.getMessage()));
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
                return "User logged out successfully.";
            } catch (ServiceException e) {
                res.status(401); // HTTP 401 Unauthorized
                return "Failed to log out: " + e.getMessage();
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
