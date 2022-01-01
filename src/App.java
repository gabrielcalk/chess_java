import UI.UI;
import board.Board;
import board.Position;
import chess.ChessMatch;

public class App {
    public static void main(String[] args) throws Exception {

        ChessMatch chessMatch = new ChessMatch();
        UI.printBoard(chessMatch.getPieces());

        
    }
}
