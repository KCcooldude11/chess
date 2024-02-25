import static spark.Spark.*;

import com.google.gson.Gson;
import service.UserService;
import service.ServiceException;
import model.UserData;

public class UserHandler {

    public static void main(String[] args) {
        UserService userService = new UserService();
        Gson gson = new Gson();

        post("/user", (request, response) -> {
            try {
                UserData newUser = gson.fromJson(request.body(), UserData.class);
                return userService.registerUser(newUser);
            } catch (ServiceException e) {
                response.status(400); // Bad Request
                return gson.toJson(new ErrorMessage(e.getMessage()));
            }
        }, gson::toJson);
    }

    private static class ErrorMessage {
        String message;

        public ErrorMessage(String message) {
            this.message = message;
        }
    }
}