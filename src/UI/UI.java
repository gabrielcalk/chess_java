package UI;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ColorChess;

public class UI{

    // Color and backcolor available to use
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

    // Method to clean the screen everytime that one move is maked
    public static void clearScreen(){
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * 
     * @param sc
     * @return ChessPosition
     */
    public static ChessPosition readChessPosition(Scanner sc){
        try{
            // Reading the Chess Position Provide By The User
            String s = sc.nextLine();
            char column = s.charAt(0);
            int row = Integer.parseInt(s.substring(1));
            return new ChessPosition(column, row);
        } catch(RuntimeException e){
            // Exception if the user provide one wrong input
            throw new InputMismatchException("Error: valid values are from 1 to 8 and A to H"); 
        }
    }

    /**
     * 
     * @param chessMatch
     * @param capturedPieces
     * @method Generate the Game
     */
    public static void printMatch(ChessMatch chessMatch, List<ChessPiece> capturedPieces){
        // Printing the board
        printBoard(chessMatch.getPieces());
        System.out.println();

        // Printing the captured pieces (black and white)
        printCapturedPieces(capturedPieces);
        System.out.println();

        // Showing who has to play next
        System.out.println("Turn: " + chessMatch.getTurn());

        /**
         * Conditional to Check the "Check" and "CheckMate"
         */
        if(!chessMatch.getCheckMate()){
            System.out.println("Player Turn: " + chessMatch.getCurrentPlayer());
            // if the check variable is true, then print "check"
            if(chessMatch.getCheck()){
                System.out.println(ANSI_RED);
                System.out.println("CHECK!");
                System.out.print(ANSI_RESET);
            }
        } else{
            System.out.println(ANSI_RED);
            System.out.println("CHECKMATE!");
            System.out.print(ANSI_RESET);
            System.out.println("Winner: " + chessMatch.getCurrentPlayer());
        }
    }

    /**
     * 
     * @param pieces Matrix
     */
    public static void printBoard(ChessPiece[][] pieces){
        // Printing the board
        for(int i = 0; i < pieces.length; i++){
            System.out.print((8 - i) + "    ");
            for(int j = 0; j < pieces.length; j++){
                
                // Adding the background stardand or black on the page and printing the piece on the cl
                if(i % 2 == 1){
                    if(j % 2 == 1){
                        System.out.print(ANSI_RESET);
                        printPiece(pieces[i][j], false);
                        System.out.print(ANSI_RESET);
                    } else if(j % 2 == 0){
                        System.out.print(ANSI_BLACK_BACKGROUND);
                        printPiece(pieces[i][j], false);
                        System.out.print(ANSI_RESET);
                    }
                } else if(i % 2 == 0){
                    if(j % 2 == 1){
                        System.out.print(ANSI_BLACK_BACKGROUND);
                        printPiece(pieces[i][j], false);
                        System.out.print(ANSI_RESET);
                    } else if(j % 2 == 0){
                        System.out.print(ANSI_RESET);
                        printPiece(pieces[i][j], false);
                        System.out.print(ANSI_RESET);
                    }
                } else{
                    System.out.print(ANSI_RESET);
                    printPiece(pieces[i][j], false);
                    System.out.print(ANSI_RESET);
                }
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("      A  B  C  D  E  F  G  H");
        System.out.println();
    }

// Printing the board with the possible moves (After the user select one piece)
    public static void printBoard(ChessPiece[][] pieces, boolean[][] possibleMoves){
        for(int i = 0; i < pieces.length; i++){
            // Adding the background stardand or black on the page, printing the piece on the cl and the possible moves
            System.out.print((8 - i) + "    ");
            for(int j = 0; j < pieces.length; j++){
                if(i % 2 == 1){
                    if(j % 2 == 1){
                        System.out.print(ANSI_RESET);
                        printPiece(pieces[i][j], possibleMoves[i][j]);
                        System.out.print(ANSI_RESET);
                    } else if(j % 2 == 0){
                        System.out.print(ANSI_BLACK_BACKGROUND);
                        printPiece(pieces[i][j], possibleMoves[i][j]);
                        System.out.print(ANSI_RESET);
                    }
                } else if(i % 2 == 0){
                    if(j % 2 == 1){
                        System.out.print(ANSI_BLACK_BACKGROUND);
                        printPiece(pieces[i][j], possibleMoves[i][j]);
                        System.out.print(ANSI_RESET);
                    } else if(j % 2 == 0){
                        System.out.print(ANSI_RESET);
                        printPiece(pieces[i][j], possibleMoves[i][j]);
                        System.out.print(ANSI_RESET);
                    }
                } else{
                    System.out.print(ANSI_RESET);
                    printPiece(pieces[i][j], possibleMoves[i][j]);
                    System.out.print(ANSI_RESET);
                }
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("      A  B  C  D  E  F  G  H");
        System.out.println();
    }

    /**
     * Printing Each Piece
     * @param piece
     * @param background
     */
    private static void printPiece(ChessPiece piece, boolean background){
        if(background){
            System.out.print(ANSI_BLUE_BACKGROUND);
        }
        if(piece == null){
            System.out.print("   " + ANSI_RESET);
        } else{
            if (piece.getColor() == ColorChess.WHITE) {
                // https://stackoverflow.com/questions/48402025/unicode-output-java-windows-cmd
                try{
                    PrintStream outStream = new PrintStream(System.out, true, "UTF-8");
                    outStream.print(ANSI_WHITE + " " + piece + " " + ANSI_RESET);
                }catch(UnsupportedEncodingException e){
                    System.out.println("Caught exception: " + e.getMessage());
                }
            }
            else {
                try{
                    PrintStream outStream = new PrintStream(System.out, true, "UTF-8");
                    outStream.print(ANSI_YELLOW + " " + piece + " " + ANSI_RESET);
                }catch(UnsupportedEncodingException e){
                    System.out.println("Caught exception: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Printing the Captured Pieces
     * @param piecesCaptured
     */
    private static void printCapturedPieces(List<ChessPiece> piecesCaptured){
        // Putting in two different lists the pieces white and black that had been captured.
        List<ChessPiece> white = piecesCaptured.stream().filter(x -> x.getColor() == ColorChess.WHITE).collect(Collectors.toList());
        List<ChessPiece> black = piecesCaptured.stream().filter(x -> x.getColor() == ColorChess.BLACK).collect(Collectors.toList());
        System.out.println("Captured Pieces: ");

        // Printing the pieces
        try{
            PrintStream outStream = new PrintStream(System.out, true, "UTF-8");
            outStream.print("White: ");
            outStream.print(ANSI_WHITE);
            outStream.print(Arrays.toString(white.toArray()));
            System.out.println(ANSI_RESET);
        }catch(UnsupportedEncodingException e){
            System.out.println("Caught exception: " + e.getMessage());
        }

        try{
            PrintStream outStream = new PrintStream(System.out, true, "UTF-8");
            outStream.print("Black: ");
            outStream.print(ANSI_YELLOW);
            outStream.print(Arrays.toString(black.toArray()));
            System.out.println(ANSI_RESET);
        }catch(UnsupportedEncodingException e){
            System.out.println("Caught exception: " + e.getMessage());
        }
    }
}
