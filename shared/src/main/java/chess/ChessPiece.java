package chess;

import java.util.Collection;
import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor teamColor;
    private PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition) != this) {
            throw new IllegalArgumentException("No matching piece at that given position");
        }
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (pieceType == PieceType.BISHOP) {
            validMoves.addAll(getDiagonalMoves(board, myPosition));
            return validMoves;
        }
//        if (pieceType == PieceType.PAWN) {
//            validMoves.addAll(getPawnMoves(board, myPosition));
//            return validMoves;
//        }
        if (pieceType == PieceType.ROOK) {
            validMoves.addAll(getRookMoves(board, myPosition));
            return validMoves;
        }
        return validMoves;
    }

    //    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition){
//
//    }
    private Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> sideMoves = new ArrayList<>();
        sideMoves.addAll(getMovesInLine(board, myPosition, 1, 0));
        sideMoves.addAll(getMovesInLine(board, myPosition, -1, 0));
        sideMoves.addAll(getMovesInLine(board, myPosition, 0, 1));
        sideMoves.addAll(getMovesInLine(board, myPosition, 0, -1));

        return sideMoves;
    }

    private Collection<ChessMove> getMovesInLine(ChessBoard board, ChessPosition myPosition, int rowIncrement, int colIncrement) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while (isValidPosition(row, col)) {
            row += rowIncrement;
            col += colIncrement;
            if (isValidPosition(row, col)) {
                ChessPosition newPosition = new ChessPosition(row, col);  // Create the new position first

                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    System.out.println("added null move");
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    // if the position is empty (null)
                } else if (pieceAtNewPosition.getTeamColor() != this.getTeamColor()) {
                    // if there is an opponent piece to capture.
                    System.out.println("added capture move");
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                } else {
                    System.out.println("can't move here");
                    // there is your own piece in that position.
                    break;
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> getDiagonalMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> diagonalMoves = new ArrayList<>();
        diagonalMoves.addAll(getMovesInDirection(board, myPosition, 1, 1));
        diagonalMoves.addAll(getMovesInDirection(board, myPosition, 1, -1));
        diagonalMoves.addAll(getMovesInDirection(board, myPosition, -1, 1));
        diagonalMoves.addAll(getMovesInDirection(board, myPosition, -1, -1));

        return diagonalMoves;
    }

    private Collection<ChessMove> getMovesInDirection(ChessBoard board, ChessPosition myPosition, int rowIncrement, int colIncrement) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        while (isValidPosition(row, col)) {
            row += rowIncrement;
            col += colIncrement;
            if (isValidPosition(row, col)) {
                ChessPosition newPosition = new ChessPosition(row, col);  // Create the new position first

                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    System.out.println("added null move");
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    // if the position is empty (null)
                } else if (pieceAtNewPosition.getTeamColor() != this.getTeamColor()) {
                    // if there is an opponent piece to capture.
                    System.out.println("added capture move");
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                } else {
                    System.out.println("can't move here");
                    // there is your own piece in that position.
                    break;
                }
            } else {
                break;
            }
        }
        return moves;
    }

    private boolean isValidPosition(int row, int col) {
        return row > 0 && row <= 8 && col > 0 && col <= 8;
    }
}
