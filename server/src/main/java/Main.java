import chess.*;
import server.Server;
import dataAccess.*;
import server.Server;
import ui.*;
import java.io.IOException;

//public class Main {
//    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Server: " + piece);
//
//        Server server = new Server();
//        try {
//            server.run(8080);
//        } catch (Exception e) { // Catching Exception instead of DataAccessException
//            System.err.println("Failed to start the server: " + e.getMessage());
//            System.exit(1);
//        }
//    }
//}
public class Main {
    public static void main(String[] args) {
        // Start the Server
        Server server = new Server();
        try {
            // Assuming server.run returns the port it started on for confirmation/logging
            int port = server.run(8080);
            System.out.println("Server started on port: " + port);
        } catch (Exception e) { // Broad catch for simplicity, consider catching more specific exceptions
            System.err.println("Failed to start the server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1); // Exit if the server fails to start
        }

        // Now run the PreLoginUI for client-side interaction
        String serverUrl = "http://localhost:8080"; // Adjust as necessary, should match the server's port
        ServerFacade facade = new ServerFacade(serverUrl);
        PreLoginUI preLoginUI = new PreLoginUI(facade);

        try {
            preLoginUI.run();
        } catch (IOException e) {
            System.out.println("An error occurred in PreLoginUI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}