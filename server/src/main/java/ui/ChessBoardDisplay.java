package ui;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessBoardDisplay {

    // Assuming EscapeSequences class is defined in the same package
    private static final int BOARD_SIZE = 10; // Total board size including labels
    public static void printInitialChessBoard() {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String[][] board = setupInitialBoard(); // Initialize the chess board with pieces
        printBoard(out, board); // Print the initialized chess board
    }
    private static void printBoard(PrintStream out, String[][] board) {
        // Print top file labels
        printFileLabels(out, true);

        for (int i = 1; i <= 8; i++) {
            // Adjust for the offset due to outer file and rank labels
            int boardRow = i - 1;
            out.printf("%d ", 9 - i); // Print the rank number on the left

            for (int j = 1; j <= 8; j++) {
                int boardCol = j - 1;
                String bg = (boardRow + boardCol) % 2 == 0 ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLACK;
                String fg = (boardRow + boardCol) % 2 == 0 ? EscapeSequences.SET_TEXT_COLOR_BLACK : EscapeSequences.SET_TEXT_COLOR_WHITE;

                out.print(bg + fg + board[boardRow][boardCol] + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            }

            out.println(" " + (9 - i)); // Print the rank number again on the right
        }

        // Print bottom file labels
        printFileLabels(out, false);
    }

    private static void printFileLabels(PrintStream out, boolean top) {
        if (top) {
            out.print("   "); // Initial spaces for alignment
        } else {
            out.print("  "); // Adjust for left margin alignment
        }

        for (char file = 'a'; file <= 'h'; file++) {
            out.print(" " + file + " "); // Center each file label under its column
        }

        if (!top) {
            out.print("  "); // Ending spaces for alignment
        }
        out.println(); // Newline after file labels
    }

    public static void main(String[] args) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String[][] board = setupInitialBoard(); // Assume setupInitialBoard method initializes the chess board
        printBoard(out, board);
    }

    // Setup the initial board with pieces, similar to previous implementations
    private static String[][] setupInitialBoard() {
        String[][] board = new String[8][8];

        // Setup black pieces
        board[0][0] = EscapeSequences.BLACK_ROOK;
        board[0][1] = EscapeSequences.BLACK_KNIGHT;
        board[0][2] = EscapeSequences.BLACK_BISHOP;
        board[0][3] = EscapeSequences.BLACK_QUEEN;
        board[0][4] = EscapeSequences.BLACK_KING;
        board[0][5] = EscapeSequences.BLACK_BISHOP;
        board[0][6] = EscapeSequences.BLACK_KNIGHT;
        board[0][7] = EscapeSequences.BLACK_ROOK;
        for (int i = 0; i < 8; i++) {
            board[1][i] = EscapeSequences.BLACK_PAWN;
        }

        // Setup empty squares
        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = EscapeSequences.EMPTY;
            }
        }

        // Setup white pieces
        for (int i = 0; i < 8; i++) {
            board[6][i] = EscapeSequences.WHITE_PAWN;
        }
        board[7][0] = EscapeSequences.WHITE_ROOK;
        board[7][1] = EscapeSequences.WHITE_KNIGHT;
        board[7][2] = EscapeSequences.WHITE_BISHOP;
        board[7][3] = EscapeSequences.WHITE_QUEEN;
        board[7][4] = EscapeSequences.WHITE_KING;
        board[7][5] = EscapeSequences.WHITE_BISHOP;
        board[7][6] = EscapeSequences.WHITE_KNIGHT;
        board[7][7] = EscapeSequences.WHITE_ROOK;

        return board;
    }
}

