package ui;

public class ChessBoardDisplay {
    private static final String EMPTY = "   ";
    private static final String[] BOARD_FILES = {"a", "b", "c", "d", "e", "f", "g", "h"};

    // Unicode symbols for chess pieces
    private static final String WHITE_PAWN = "\u2659";
    private static final String WHITE_ROOK = "\u2656";
    private static final String WHITE_KNIGHT = "\u2658";
    private static final String WHITE_BISHOP = "\u2657";
    private static final String WHITE_QUEEN = "\u2655";
    private static final String WHITE_KING = "\u2654";
    private static final String BLACK_PAWN = "\u265F";
    private static final String BLACK_ROOK = "\u265C";
    private static final String BLACK_KNIGHT = "\u265E";
    private static final String BLACK_BISHOP = "\u265D";
    private static final String BLACK_QUEEN = "\u265B";
    private static final String BLACK_KING = "\u265A";

    // Method to print the initial chess board
    public static void printInitialChessBoard() {
        String[][] board = new String[8][8];

        // Setting up black pieces
        board[0] = new String[]{BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};
        java.util.Arrays.fill(board[1], BLACK_PAWN);

        // Setting up empty squares
        for (int i = 2; i <= 5; i++) {
            java.util.Arrays.fill(board[i], EMPTY);
        }

        // Setting up white pieces
        java.util.Arrays.fill(board[6], WHITE_PAWN);
        board[7] = new String[]{WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};

        // Print board from white's perspective
        System.out.println("Chess board from White's perspective:");
        printBoard(board);

        // Print board from black's perspective (simply reverse the order)
        System.out.println("\nChess board from Black's perspective:");
        java.util.Collections.reverse(java.util.Arrays.asList(board)); // Reverse the board array
        printBoard(board);
    }

    private static void printBoard(String[][] board) {
        for (int i = 0; i < board.length; i++) {
            System.out.print((8 - i) + " "); // Rank numbers
            for (int j = 0; j < board[i].length; j++) {
                System.out.print("|" + board[i][j]);
            }
            System.out.println("|");
        }
        // Print file letters
        System.out.print("   "); // Align with rank numbers
        for (String file : BOARD_FILES) {
            System.out.print(" " + file + " ");
        }
        System.out.println(); // Ensure the next line in the console is empty
    }

    public static void main(String[] args) {
        printInitialChessBoard();
    }
}
