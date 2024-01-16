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
        if(board.getPiece(myPosition) != this){
            throw new IllegalArgumentException("No matching piece at that given position");
        }
        Collection<ChessMove> validMoves = new ArrayList<>();
        validMoves.addAll(getDiagonalMoves(board, myPosition));
        return validMoves;
    }
    private Collection<ChessMove> getDiagonalMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> diagonalMoves = new ArrayList<>();
        diagonalMoves.addAll(getMovesInDirection(board, myPosition, 1, 1));
        diagonalMoves.addAll(getMovesInDirection(board, myPosition, 1, -1));
        diagonalMoves.addAll(getMovesInDirection(board, myPosition, -1, 1));
        diagonalMoves.addAll(getMovesInDirection(board, myPosition, -1, -1));

        return diagonalMoves;
    }
    private Collection<ChessMove> getMovesInDirection(ChessBoard board, ChessPosition myPosition, int rowIncrement, int colIncrement){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow() + rowIncrement;
        int col = myPosition.getColumn() + colIncrement;
        while (isValidPosition(row, col)){
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

            if(pieceAtNewPosition == null){
                moves.add(new ChessMove(myPosition, newPosition, null));
                //if the position is empty (null)
            }
            else if(pieceAtNewPosition.getTeamColor() != this.getTeamColor()){
                //if there is an opponnent piece to capture.
                moves.add(new ChessMove(myPosition, newPosition, null));
                break;
            }
            else{
                //there is your own piece in that position.
                break;
            }
            row += rowIncrement;
            col += colIncrement;
        }
        return moves;
    }
    private boolean isValidPosition(int row, int col){
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
