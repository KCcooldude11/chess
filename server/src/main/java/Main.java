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
        } catch (DataAccessException e) {
            System.err.println("Failed to start the server due to database access issue: " + e.getMessage());
            e.printStackTrace();
        }
    }
}