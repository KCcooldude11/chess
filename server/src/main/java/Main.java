import chess.*;
import server.Server;
import dataAccess.*;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        Server server = new Server();
        try {
            server.run(8080);
        } catch (Exception e) { // Catching Exception instead of DataAccessException
            System.err.println("Failed to start the server: " + e.getMessage());
            System.exit(1);
        }
    }
}
