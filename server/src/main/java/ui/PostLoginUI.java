package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PostLoginUI {
    private final ServerFacade facade;
    private final BufferedReader reader;
    private final String authToken; // Assuming you store the authToken after logging in

    public PostLoginUI(ServerFacade facade, String authToken) {
        this.facade = facade;
        this.authToken = authToken;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void run() throws IOException, InterruptedException {
        System.out.println("Welcome to the game lobby! Type 'help' to see available commands.");
        while (true) {
            String input = reader.readLine().trim();
            switch (input.toLowerCase()) {
                case "help":
                    displayHelp();
                    break;
                case "create game":
                    createGame();
                    break;
                case "list games":
                    listGames();
                    break;
                case "join game":
                    joinGame();
                    break;
                case "observe game":
                    observeGame();
                    break;
                case "logout":
                    logout();
                    return; // Exit the method to return to the pre-login UI
                default:
                    System.out.println("Unknown command. Type 'help' for a list of commands.");
            }
        }
    }

    private void displayHelp() {
        System.out.println("create game - to create a new game");
        System.out.println("list games - to list all available games");
        System.out.println("join game - to join an existing game");
        System.out.println("observe game - to observe an ongoing game");
        System.out.println("logout - to log out and return to the main menu");
    }

    private void createGame() throws IOException, InterruptedException {
        System.out.print("Enter game name: ");
        String gameName = reader.readLine();
        // Assume createGame in facade returns the game ID if successful
        Integer gameId = facade.createGame(gameName, authToken);
        if (gameId != null) {
            System.out.println("Game created successfully with ID: " + gameId);
        } else {
            System.out.println("Failed to create game.");
        }
    }

    private void listGames() throws IOException {
        // Implementation for listing games
        // Use facade.listGames(authToken) and display the result
    }

    private void joinGame() throws IOException {
        // Prompt for game ID and player color, then use facade.joinGame
    }

    private void observeGame() throws IOException {
        // Prompt for game ID, then use facade.observeGame
    }

    private void logout() throws IOException, InterruptedException {
        facade.logout(authToken);
        System.out.println("You have been logged out.");
    }
}
