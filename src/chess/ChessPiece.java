package chess;

import board.Board;
import board.Piece;
import board.Position;

public abstract class ChessPiece extends Piece{
    private ColorChess color;
    private int moveCount;

    public ChessPiece(Board board, ColorChess color){
        super(board);
        this.color = color;
    }

    public ColorChess getColor(){
        return this.color;
    }

    public int getMoveCount(){
        return this.moveCount;
    }

    /**
     * @method to increase and decrease movements to allow some special moves
     */
    public void increaseMoveCount(){
        this.moveCount++;
    }

    public void decreaseMoveCount(){
        this.moveCount--;
    }

    public ChessPosition getChessPosition(){
        return ChessPosition.fromPosition(position);
    }

    // Method that will be use to check if has one piece in one specific place
    protected boolean isThereOpponentPiece(Position position){
        ChessPiece p = (ChessPiece) getBoard().piece(position);
        return p != null && p.getColor() != color;
    }
}
