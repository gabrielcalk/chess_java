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

    public void increaseMoveCount(){
        this.moveCount++;
    }

    public void decreaseMoveCount(){
        this.moveCount--;
    }


    public ChessPosition getChessPosition(){
        return ChessPosition.fromPosition(position);
    }

    protected boolean isThereOpponentPiece(Position position){
        ChessPiece p = (ChessPiece) getBoard().piece(position);
        return p != null && p.getColor() != color;
    }
}
