import chess.*;
import server.Server;
import dataAccess.*;
import ui.*;
import java.io.IOException;
import model.*;

public class Main {
    public static void main(String[] args) {
        // Start the Server
        Server server = new Server();
        try {
            int port = server.run(8080);
            System.out.println("Server started on port: " + port);
        } catch (Exception e) {
            System.err.println("Failed to start the server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        runClientApplication("http://localhost:8080");
    }

    private static void runClientApplication(String serverUrl) {
        boolean continueRunning = true;
        ServerFacade facade = new ServerFacade(serverUrl);

        while (continueRunning) {
            PreLoginUI preLoginUI = new PreLoginUI(facade);
            try {
                AuthData authData = preLoginUI.run();
                if (authData != null) {
                    System.out.println("Proceeding to the game lobby...");
                    PostLoginUI postLoginUI = new PostLoginUI(facade, authData.getAuthToken());
                    continueRunning = postLoginUI.run(); // Start the post-login UI interaction
                } else {
                    continueRunning = false; // User chose to quit in PreLoginUI
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
