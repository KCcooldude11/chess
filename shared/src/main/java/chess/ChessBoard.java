package chess;

import java.util.Arrays;

public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {

    }

    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow() - 1;
        int col = position.getColumn() - 1;
        return squares[row][col];
    }

    public void resetBoard() {
        clearBoard();
        placeInitialPieces();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }



    public void clearBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                removePiece(position);
            }
        }
    }

    public void removePiece(ChessPosition position) {
        int row = position.getRow() - 1;
        int col = position.getColumn() - 1;
        squares[row][col] = null;
    }

    private void placeInitialPieces() {
        placePawns(2, ChessGame.TeamColor.WHITE);
        placePawns(7, ChessGame.TeamColor.BLACK);

        placeRooks(1, ChessGame.TeamColor.WHITE);
        placeRooks(8, ChessGame.TeamColor.BLACK);

        placeKnights(1, ChessGame.TeamColor.WHITE);
        placeKnights(8, ChessGame.TeamColor.BLACK);

        placeBishops(1, ChessGame.TeamColor.WHITE);
        placeBishops(8, ChessGame.TeamColor.BLACK);

        placeKings(1, ChessGame.TeamColor.WHITE);
        placeKings(8, ChessGame.TeamColor.BLACK);

        placeQueens(1, ChessGame.TeamColor.WHITE);
        placeQueens(8, ChessGame.TeamColor.BLACK);
    }

    private void placePawns(int row, ChessGame.TeamColor teamColor) {
        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(row, col), new ChessPiece(teamColor, ChessPiece.PieceType.PAWN));
        }
    }

    private void placeRooks(int row, ChessGame.TeamColor teamColor) {
        addPiece(new ChessPosition(row, 1), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 8), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));
    }

    private void placeKnights(int row, ChessGame.TeamColor teamColor) {
        addPiece(new ChessPosition(row, 2), new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 7), new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT));
    }

    private void placeBishops(int row, ChessGame.TeamColor teamColor) {
        addPiece(new ChessPosition(row, 3), new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 6), new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP));
    }

    private void placeKings(int row, ChessGame.TeamColor teamColor) {
        addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
    }

    private void placeQueens(int row, ChessGame.TeamColor teamColor) {
        addPiece(new ChessPosition(row, 4), new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN));
    }
}