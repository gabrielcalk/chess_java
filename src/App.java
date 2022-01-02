import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import UI.UI;
import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class App {
    public static void main(String[] args) throws Exception {

        /**
         * Scanner and Lists
         */
        Scanner sc = new Scanner(System.in);
        ChessMatch chessMatch = new ChessMatch();
        List<ChessPiece> capturedPieces = new ArrayList<>();

        // The Game keep going if the variable check mate is false
        while(!chessMatch.getCheckMate()){
            try{
                UI.clearScreen();
                UI.printMatch(chessMatch, capturedPieces);
                System.out.println();

                // Source Position (from)
                System.out.print("Source: ");
                ChessPosition source = UI.readChessPosition(sc);

                boolean[][] possibleMoves = chessMatch.possibleMoves(source);
                UI.clearScreen();
                UI.printBoard(chessMatch.getPieces(), possibleMoves);
    
                System.out.println();
    
                // Target Position (to)
                System.out.print("Target: ");
                ChessPosition target = UI.readChessPosition(sc);
    
                ChessPiece capturedPiece = chessMatch.performChessMovie(source, target);

                if(capturedPiece != null){
                    capturedPieces.add(capturedPiece);
                }

                // Checking if the pawn get on the last row and promoting it
                if(chessMatch.getPromoted() != null){
                    System.out.print("Enter Piece For Promotion: B / N / R / Q: ");
                    String type = sc.nextLine().toUpperCase();
                    while(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")){
                        System.out.print("Invalid! Enter Piece For Promotion: B / N / R / Q: ");
                        type = sc.nextLine().toUpperCase();   
                    }
                    chessMatch.replacePromotedPiece(type);
                }

            } catch(ChessException e){
                System.out.println(e.getMessage());
                sc.nextLine();
            } catch(InputMismatchException e){
                System.out.println(e.getMessage());
                sc.nextLine();
            }
        }
        UI.clearScreen();
        UI.printMatch(chessMatch, capturedPieces); 
    }
}
