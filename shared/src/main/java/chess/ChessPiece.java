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
        if (pieceType == PieceType.ROOK) {
            validMoves.addAll(getRookMoves(board, myPosition));
            return validMoves;
        }
        if (pieceType == PieceType.KNIGHT) {
            validMoves.addAll(getKnightMoves(board, myPosition));
            return validMoves;
        }
        if (pieceType == PieceType.QUEEN) {
            validMoves.addAll(getQueenMoves(board, myPosition));
            return validMoves;
        }
        if (pieceType == PieceType.KING) {
            validMoves.addAll(getKingMoves(board, myPosition));
            return validMoves;
        }
        if (pieceType == PieceType.PAWN) {
            validMoves.addAll(getPawnMoves(board, myPosition));
            return validMoves;
        }
        return validMoves;
    }

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> pawnMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int direction = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int newRow = row + direction;

        if (isValidPosition(newRow, col) && board.getPiece(new ChessPosition(newRow, col)) == null) {
            if (newRow == 8 || newRow == 1) {
                // Add promotion moves (for example, promoting to a queen)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, col), ChessPiece.PieceType.QUEEN));
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, col), ChessPiece.PieceType.ROOK));
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, col), ChessPiece.PieceType.KNIGHT));
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, col), ChessPiece.PieceType.BISHOP));
            } else {
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, col), null));
            }
        }

        if ((row == 2 && teamColor == ChessGame.TeamColor.WHITE) || (row == 7 && teamColor == ChessGame.TeamColor.BLACK)) {
            int doubleRow = row + (2 * direction);
            if (isValidPosition(doubleRow, col) && board.getPiece(new ChessPosition(doubleRow, col)) == null && board.getPiece(new ChessPosition(newRow, col)) == null) {
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(doubleRow, col), null));
            }
        }

        int[] captureCols = {col - 1, col + 1};

        for (int captureCol : captureCols) {
            int captureRow = newRow;

            if (isValidPosition(captureRow, captureCol)) {
                ChessPosition capturePosition = new ChessPosition(captureRow, captureCol);
                ChessPiece pieceAtNewPosition = board.getPiece(capturePosition);

                if (pieceAtNewPosition != null && pieceAtNewPosition.getTeamColor() != teamColor) {
                    if (newRow == 8 || newRow == 1) {
                        // Add promotion moves (for example, promoting to a queen)
                        pawnMoves.add(new ChessMove(myPosition, capturePosition, ChessPiece.PieceType.QUEEN));
                        pawnMoves.add(new ChessMove(myPosition, capturePosition, ChessPiece.PieceType.ROOK));
                        pawnMoves.add(new ChessMove(myPosition, capturePosition, ChessPiece.PieceType.KNIGHT));
                        pawnMoves.add(new ChessMove(myPosition, capturePosition, ChessPiece.PieceType.BISHOP));
                    } else {
                        pawnMoves.add(new ChessMove(myPosition, capturePosition, null));
                    }
                }
            }
        }

        return pawnMoves;
    }


    private Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> kingMoves = new ArrayList<>();
        kingMoves.addAll(kingMovement(board, myPosition, 1, 0));
        kingMoves.addAll(kingMovement(board, myPosition, 1, -1));
        kingMoves.addAll(kingMovement(board, myPosition, 1, 1));
        kingMoves.addAll(kingMovement(board, myPosition, 0, -1));
        kingMoves.addAll(kingMovement(board, myPosition, 0, 1));
        kingMoves.addAll(kingMovement(board, myPosition, -1, 0));
        kingMoves.addAll(kingMovement(board, myPosition, -1, -1));
        kingMoves.addAll(kingMovement(board, myPosition, -1, 1));
        return kingMoves;
    }

    private Collection<ChessMove> kingMovement(ChessBoard board, ChessPosition myPosition, int rowIncrement, int colIncrement) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
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
            } else {
                System.out.println("can't move here");
                // there is your own piece in that position.
            }
        }
        return moves;
    }

    private Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> queenMoves = new ArrayList<>();
        queenMoves.addAll(getMovesInDirection(board, myPosition, 1, 1));
        queenMoves.addAll(getMovesInDirection(board, myPosition, 1, -1));
        queenMoves.addAll(getMovesInDirection(board, myPosition, -1, 1));
        queenMoves.addAll(getMovesInDirection(board, myPosition, -1, -1));
        queenMoves.addAll(getMovesInDirection(board, myPosition, 1, 0));
        queenMoves.addAll(getMovesInDirection(board, myPosition, -1, 0));
        queenMoves.addAll(getMovesInDirection(board, myPosition, 0, 1));
        queenMoves.addAll(getMovesInDirection(board, myPosition, 0, -1));
        return queenMoves;
    }

    private Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> lMoves = new ArrayList<>();
        lMoves.addAll(getMovesKnight(board, myPosition, 2, -1));
        lMoves.addAll(getMovesKnight(board, myPosition, 2, 1));
        lMoves.addAll(getMovesKnight(board, myPosition, -2, 1));
        lMoves.addAll(getMovesKnight(board, myPosition, -2, -1));
        lMoves.addAll(getMovesKnight(board, myPosition, 1, 2));
        lMoves.addAll(getMovesKnight(board, myPosition, 1, -2));
        lMoves.addAll(getMovesKnight(board, myPosition, -1, -2));
        lMoves.addAll(getMovesKnight(board, myPosition, -1, 2));

        return lMoves;
    }

    private Collection<ChessMove> getMovesKnight(ChessBoard board, ChessPosition myPosition, int rowIncrement, int colIncrement) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
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
            } else {
                System.out.println("can't move here");
                // there is your own piece in that position.
            }
        }
        return moves;
    }

    private Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> sideMoves = new ArrayList<>();
//        System.out.println("Rook at: " + myPosition.getRow() + "," + myPosition.getColumn());
        sideMoves.addAll(getMovesInDirection(board, myPosition, 1, 0));
        sideMoves.addAll(getMovesInDirection(board, myPosition, -1, 0));
        sideMoves.addAll(getMovesInDirection(board, myPosition, 0, 1));
        sideMoves.addAll(getMovesInDirection(board, myPosition, 0, -1));
//        for (ChessMove move : sideMoves) {
//            System.out.println("Rook move: " + move.getStartPosition().getRow() + "," + move.getStartPosition().getColumn() + " to " + move.getEndPosition().getRow() + "," + move.getEndPosition().getColumn());
//        }
        return sideMoves;
    }
    //test stuff

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

        while (true) {
            row += rowIncrement;
            col += colIncrement;

            if (!isValidPosition(row, col)) {
                break; // Out of board bounds
            }

            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

            if (pieceAtNewPosition == null) {
                moves.add(new ChessMove(myPosition, newPosition, null)); // Empty space, can move
            } else {
                if (pieceAtNewPosition.getTeamColor() != this.teamColor) {
                    moves.add(new ChessMove(myPosition, newPosition, null)); // Can capture enemy piece
                }
                break; // Blocked by a piece, can't move further
            }
        }

        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }

    //    private boolean isValidPosition(int row, int col) {
//        return row > 0 && row <= 8 && col > 0 && col <= 8;
//    }
    private boolean isValidPosition(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
