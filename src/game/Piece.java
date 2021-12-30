package game;

public class Piece {
    protected Position position;
    private Board board;

    public Piece(){}

    public Piece(Board board){
        this.board = board;
        this.position = null;
    }

    protected Board getBoard(){
        return board;
    }
}
