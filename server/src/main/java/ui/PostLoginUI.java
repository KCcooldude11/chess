package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import chess.ChessBoard;

public class PostLoginUI {
    private final ServerFacade facade;
    private final BufferedReader reader;
    private final String authToken; // Assuming you store the authToken after logging in

    public PostLoginUI(ServerFacade facade, String authToken) {
        this.facade = facade;
        this.authToken = authToken;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public boolean run() throws IOException, InterruptedException {
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
                    return true; // Return true to indicate continuing the application
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
        try {
            var games = facade.listGames(authToken);
            if (games.isEmpty()) {
                System.out.println("No games available.");
            } else {
                System.out.println("Available games:");
                for (int i = 0; i < games.size(); i++) {
                    var game = games.get(i);
                    System.out.printf("%d. %s - Players: %s vs %s\n", i + 1, game.getGameName(), game.getWhiteUsername(), game.getBlackUsername());
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to list games: " + e.getMessage());
        }
    }

    private void joinGame() throws IOException {
        System.out.print("Enter the game ID you want to join: ");
        int gameId;
        try {
            gameId = Integer.parseInt(reader.readLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid game ID.");
            return;
        }

        System.out.print("Enter your color (WHITE/BLACK): ");
        String playerColor = reader.readLine().toUpperCase();
        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            System.out.println("Invalid color. Choose 'WHITE' or 'BLACK'.");
            return;
        }

        try {
            facade.joinGame(authToken, gameId, playerColor);
            System.out.println("Joined game " + gameId + " as " + playerColor);
            ChessBoard chessBoard = new ChessBoard();
            chessBoard.resetBoard(); // Initialize your chess board

            // Always show the chessboard from both perspectives when joining or observing a game
            System.out.println("\nChessboard from WHITE's perspective:");
            ChessBoardDisplay.printChessBoard(chessBoard, false); // true indicates white's perspective

            System.out.println("\nChessboard from BLACK's perspective:");
            ChessBoardDisplay.printChessBoard(chessBoard, true); // false indicates black's perspective
        } catch (Exception e) {
            System.out.println("Failed to join game: " + e.getMessage());
        }
    }

    private void observeGame() throws IOException {
        System.out.println("Observing game: (Full logic to be implemented in phase 6 with websockets)");
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard(); // Initialize your chess board

        // Print the board from WHITE's perspective (white pieces at the bottom)
        System.out.println("Chess board from WHITE's perspective:");
        ChessBoardDisplay.printChessBoard(chessBoard, false);

        // Adding some space between the two boards for clarity
        System.out.println("\n");

        // Print the board from BLACK's perspective (black pieces at the bottom)
        System.out.println("Chess board from BLACK's perspective:");
        ChessBoardDisplay.printChessBoard(chessBoard, true);
    }

    private void logout() throws IOException, InterruptedException {
        facade.logout(authToken);
        System.out.println("You have been logged out.");
    }
}
