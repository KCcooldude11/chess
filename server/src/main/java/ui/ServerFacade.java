package ui;
import model.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import model.request.*;
import java.util.List;
import java.util.Map;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public AuthData register(String username, String password, String email) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(new Register(username, password, email))))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // Assuming successful registration returns AuthData
            return new Gson().fromJson(response.body(), AuthData.class);
        }
        else {
            // Handle errors or unsuccessful registration
            throw new IOException("Registration failed: " + response.body());
        }
    }
    public AuthData login(String username, String password) throws IOException, InterruptedException {
        var requestPayload = new Login(username, password); // Assuming Login class exists
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(requestPayload)))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return new Gson().fromJson(response.body(), AuthData.class);
        } else {
            throw new IOException("Login failed: " + response.body());
        }
    }
    public void logout(String authToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Authorization", authToken)
                .DELETE()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Logout failed: " + response.body());
        }
    }
    public Integer createGame(String gameName, String authToken) throws IOException, InterruptedException {
        CreateGame requestPayload = new CreateGame(gameName);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(requestPayload)))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // Assuming the server returns a JSON object with the game ID
            var responseMap = new Gson().fromJson(response.body(), Map.class);
            // Extract the game ID from the response. Adjust the key if necessary based on your actual response format.
            Number gameId = (Number) responseMap.get("gameID"); // Gson parses numbers as Double, Long, or other Number types depending on the actual value
            if (gameId != null) { // Check if gameId is not null before attempting to use it
                return gameId.intValue(); // Convert to Integer
            } else {
                System.out.println("Game ID was not returned by the server.");
                return null; // Return null if gameId is not present in the response
            }
        } else {
            throw new IOException("Failed to create game: " + response.body());
        }
    }
    public List<GameData> listGames(String authToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Authorization", authToken)
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // Explicitly declare the type of the responseObject
            Type responseType = new TypeToken<Map<String, List<GameData>>>(){}.getType();
            Map<String, List<GameData>> responseObject = new Gson().fromJson(response.body(), responseType);

            // Now you can safely call .get("games") on responseObject
            return responseObject.get("games"); // Extract the array from the object
        } else {
            throw new IOException("Failed to list games: " + response.body());
        }
    }
    public void joinGame(String authToken, int gameId, String playerColor) throws IOException, InterruptedException {
        JoinGame requestPayload = new JoinGame(playerColor, gameId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game")) // Adjust if your endpoint differs
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(new Gson().toJson(requestPayload)))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to join game: " + response.body());
        }
    }
//    public void observeGame(String authToken, int gameId) throws IOException, InterruptedException {
//        // Assuming the server expects a simple GET request with the game ID in the query string or path
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(serverUrl + "/game" + gameId)) // Example URL structure
//                .header("Authorization", authToken)
//                .GET()
//                .build();
//
//        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() != 200) {
//            throw new IOException("Failed to observe game: " + response.body());
//        }
//    }
}