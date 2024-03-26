package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame;
import static ui.EscapeSequences.*;

public class ChessBoardDisplay {
    private static String getPieceSymbol(ChessPiece piece, boolean isBlackSquare) {
        String symbol;
        String pieceColor;
        String squareColor = isBlackSquare ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY;

        if (piece == null) {
            // For an empty square, just set the square color
            return squareColor + "   " + RESET_BG_COLOR; // Three spaces for an empty square
        }

        // Determine the color of the chess piece
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            pieceColor = SET_TEXT_COLOR_WHITE;
        } else {
            pieceColor = SET_TEXT_COLOR_BLACK; // Or any other color you wish to use for black pieces
        }

        // Assign symbols based on the piece type
        switch (piece.getPieceType()) {
            case PAWN:
                symbol = "P";
                break;
            case ROOK:
                symbol = "R";
                break;
            case KNIGHT:
                symbol = "N";
                break;
            case BISHOP:
                symbol = "B";
                break;
            case QUEEN:
                symbol = "Q";
                break;
            case KING:
                symbol = "K";
                break;
            default:
                symbol = " ";
        }

        // Return the colored piece on its square
        // Adding squareColor at the beginning colors the background of the square
        // pieceColor sets the color of the piece symbol
        // RESET_BG_COLOR at the end resets the background color for subsequent text
        return squareColor + pieceColor + " " + symbol + " " + RESET_BG_COLOR;
    }
    public static void printChessBoard(ChessBoard chessBoard, boolean isWhitePerspective) {
        final String whitePieceColor = SET_TEXT_COLOR_WHITE;
        final String blackPieceColor = SET_TEXT_COLOR_YELLOW; // Yellow for contrast, change as needed
        final String blackColorSquare = SET_BG_COLOR_BLACK;
        final String whiteColorSquare = SET_BG_COLOR_WHITE;
        final String resetColor = RESET_TEXT_COLOR + RESET_BG_COLOR;
        System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        if (isWhitePerspective) {
            printBoardFromPerspective(chessBoard, true);
        } else {
            printBoardFromPerspective(chessBoard, false);
        }
    }

    private static void printBoardFromPerspective(ChessBoard chessBoard, boolean isWhitePerspective) {
        System.out.print("  "); // Align with row numbers
        char[] topColumns = isWhitePerspective ? new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'} : new char[]{'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};
        for (char column : topColumns) {
            System.out.print(SET_TEXT_COLOR_WHITE + " " + column + " ");
        }
        System.out.print("\n");

        // Determine the starting and ending row numbers based on perspective
        int startRow = isWhitePerspective ? 1 : 8;
        int endRow = isWhitePerspective ? 8 : 1;
        int rowIncrement = isWhitePerspective ? 1 : -1;

        for (int row = startRow; isWhitePerspective ? row <= endRow : row >= endRow; row += rowIncrement) {
            System.out.print(SET_TEXT_COLOR_WHITE + row + " "); // Print row number
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = chessBoard.getPiece(position);
                boolean isBlackSquare = (row + col) % 2 == 0;
                String pieceSymbol = getPieceSymbol(piece, isBlackSquare);
                System.out.print(pieceSymbol);
            }
            System.out.println(SET_TEXT_COLOR_WHITE + " " + row + RESET_BG_COLOR); // Print row number again and reset background color
        }

        // Print bottom column labels based on perspective
        System.out.print("  "); // Align with row numbers
        char[] bottomColumns = isWhitePerspective ? new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'} : new char[]{'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};
        for (char column : bottomColumns) {
            System.out.print(SET_TEXT_COLOR_WHITE + " " + column + " ");
        }
        System.out.println(RESET_BG_COLOR);
    }


    private static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) return EMPTY; // Use the empty space symbol
        switch (piece.getPieceType()) {
            case PAWN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
            case ROOK:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case KNIGHT:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case BISHOP:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case QUEEN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case KING:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            default:
                return EMPTY; // Fallback for any unexpected cases
        }
    }

    public static void main(String[] args) {

        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();

        System.out.println("Chess board from White's perspective:");
        printChessBoard(chessBoard, true);

        System.out.println("\nChess board from Black's perspective:");
        printChessBoard(chessBoard, false);
    }
}