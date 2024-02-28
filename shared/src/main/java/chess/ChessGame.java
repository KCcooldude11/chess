package chess;

import java.util.stream.IntStream;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.teamTurn = TeamColor.WHITE;
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();

        for (ChessMove move : allMoves) {
            if (isMoveLegal(move, piece.getTeamColor())) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Invalid move: No piece at the start position or wrong team's turn.");
        }
        if (!isMoveLegal(move, teamTurn)) {
            throw new InvalidMoveException("Invalid move: Move is not allowed for this piece.");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves != null && validMoves.contains(move)) {
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                if ((piece.getTeamColor() == TeamColor.WHITE && move.getEndPosition().getRow() == 8) ||
                        (piece.getTeamColor() == TeamColor.BLACK && move.getEndPosition().getRow() == 1)) {
                    // Pawn Promotion
                    ChessPiece.PieceType promotionType = move.getPromotionPiece();
                    if (promotionType != null && promotionType != ChessPiece.PieceType.PAWN) {
                        piece = new ChessPiece(piece.getTeamColor(), promotionType);
                    } else {
                        throw new InvalidMoveException("Invalid promotion type.");
                    }
                }
            }


            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);
            toggleTurn();
        } else {
            throw new InvalidMoveException("Invalid move: Move is not allowed for this piece.");
        }
    }

    private void toggleTurn() {
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        if (kingPosition == null) return false; // If no king is found, cannot be in check.
        TeamColor opponentColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        // Using an enhanced for-loop for readability and direct access to board positions
        for (ChessPosition position : ChessPosition.allPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.getTeamColor() == opponentColor) {
                for (ChessMove move : piece.pieceMoves(board, position)) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

// Assuming ChessPosition.allPositions() method returns all possible positions on a chess board.


    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */

    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        return IntStream.rangeClosed(1, 8).boxed()
                .flatMap(row -> IntStream.rangeClosed(1, 8)
                        .mapToObj(col -> new ChessPosition(row, col))
                        .map(position -> new Object[]{position, board.getPiece(position)})
                        .filter(objects -> {
                            ChessPiece piece = (ChessPiece) objects[1];
                            return piece != null && piece.getTeamColor() == teamColor;
                        })
                        .flatMap(objects -> {
                            ChessPosition position = (ChessPosition) objects[0];
                            ChessPiece piece = (ChessPiece) objects[1];
                            return piece.pieceMoves(board, position).stream();
                        }))
                .noneMatch(move -> isMoveLegal(move, teamColor));
    }


    private boolean isMoveLegal(ChessMove move, TeamColor teamColor) {
        // Save the original state
        ChessPiece originalPieceAtStart = board.getPiece(move.getStartPosition());
        ChessPiece originalPieceAtEnd = board.getPiece(move.getEndPosition());


        ChessPosition kingPositionBefore = findKingPosition(teamColor);
        if (kingPositionBefore == null) {
        } else {
        }

        // Make the move
        board.addPiece(move.getEndPosition(), originalPieceAtStart); // Move the piece to the new position
        board.removePiece(move.getStartPosition()); // Remove the piece from its original position

        ChessPosition kingPositionAfter = findKingPosition(teamColor);
        if (kingPositionAfter == null) {
        } else {
        }


        boolean isInCheckAfterMove = isInCheck(teamColor);
        board.addPiece(move.getStartPosition(), originalPieceAtStart);
        board.addPiece(move.getEndPosition(), originalPieceAtEnd);

        return !isInCheckAfterMove;
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // If the team is in check, it's not a stalemate
        if (isInCheck(teamColor)) {
            return false;
        }

        // Check if any piece of the team has a legal move
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move : moves) {
                        if (isMoveLegal(move, teamColor)) {
                            return false; // Found at least one legal move, not a stalemate
                        }
                    }
                }
            }
        }

        // No legal moves found, it's a stalemate
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.deepEquals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
