package server;

import com.google.gson.Gson;
import service.UserService;
import model.UserData;
import spark.ResponseTransformer;

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
            return userService.registerUser(newUser);
        }, gson::toJson);

        // Login route (assuming a method exists in UserService)
        spark.Spark.post("/user/login", "application/json", (request, response) -> {
            UserData user = gson.fromJson(request.body(), UserData.class);
            return userService.loginUser(user);
        }, gson::toJson);
    }
}
