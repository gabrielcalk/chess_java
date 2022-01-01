package UI;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.Color;

public class UI {

    public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public static void clearScreen(){
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

    public static ChessPosition readChessPosition(Scanner sc){
        try{
            String s = sc.nextLine();
            char column = s.charAt(0);
            int row = Integer.parseInt(s.substring(1));
            return new ChessPosition(column, row);
        } catch(RuntimeException e){
            throw new InputMismatchException("Error: valid values are from 1 to 8 and A to H");
        }
    }

    public static void printMatch(ChessMatch chessMatch, List<ChessPiece> capturedPieces){
        printBoard(chessMatch.getPieces());
        System.out.println();
        printCapturedPieces(capturedPieces);
        System.out.println();
        System.out.println("Turn: " + chessMatch.getTurn());
        System.out.println("Player Turn: " + chessMatch.getCurrentPlayer());
        // if the check variable is true, then print "check"
        if(chessMatch.getCheck()){
            System.out.println(ANSI_RED);
            System.out.println("CHECK!");
            System.out.println(ANSI_RESET);
        }
    }

// Printing the board
    public static void printBoard(ChessPiece[][] pieces){
        for(int i = 0; i < pieces.length; i++){
            System.out.print((8 - i) + "   ");
            for(int j = 0; j < pieces.length; j++){
                printPiece(pieces[i][j], false);
            }
            System.out.println();
            System.out.println();
        }
        System.out.println("    A  B  C  D  E  F  G  H");
    }

// Printing the board with the possible moves
    public static void printBoard(ChessPiece[][] pieces, boolean[][] possibleMoves){
        for(int i = 0; i < pieces.length; i++){
            System.out.print((8 - i) + "   ");
            for(int j = 0; j < pieces.length; j++){
                printPiece(pieces[i][j], possibleMoves[i][j]);
            }
            System.out.println();
            System.out.println();
        }
        System.out.println("    A  B  C  D  E  F  G  H");
    }

    private static void printPiece(ChessPiece piece, boolean background){
        if(background){
            System.out.print(ANSI_BLUE_BACKGROUND);
        }
        if(piece == null){
            System.out.print("-" + ANSI_RESET);
        } else{
            if (piece.getColor() == Color.WHITE) {
                System.out.print(ANSI_WHITE + piece + ANSI_RESET);
            }
            else {
                System.out.print(ANSI_YELLOW + piece + ANSI_RESET);
            }
        }
        System.out.print("  ");
    }
    
    private static void printCapturedPieces(List<ChessPiece> piecesCaptured){
        List<ChessPiece> white = piecesCaptured.stream().filter(x -> x.getColor() == Color.WHITE).collect(Collectors.toList());
        List<ChessPiece> black = piecesCaptured.stream().filter(x -> x.getColor() == Color.BLACK).collect(Collectors.toList());
        System.out.println("Captured Pieces: ");

        System.out.print("White: ");
        System.out.print(ANSI_WHITE);
        System.out.print(Arrays.toString(white.toArray()));
        
        System.out.println(ANSI_RESET);

        System.out.print("Black: ");
        System.out.print(ANSI_YELLOW);
        System.out.print(Arrays.toString(black.toArray()));

        System.out.println(ANSI_RESET);
    }
}
