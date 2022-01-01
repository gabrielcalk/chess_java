package chess;

import board.Position;

public class ChessPosition {
    private char column;
    private int row;

    public ChessPosition(char column, int row){
        if(column < 'a' || column > 'h' || row < 1 || row > 8){
            throw new ChessException("Error: valid values are a - h and 1 - 8");
        }
        this.column = column;
        this.row = row;
    }

    public char getColumn(){
        return this.column;
    }

    public int getRow(){
        return this.row;
    }

    protected Position toPosition(){
        return new Position(8 - row, column - 'a');
    }

    protected ChessPosition fromPosition(Position position){
        return new ChessPosition((char)('a' - position.getColumn()), 8 - position.getRow());
    }

    @Override
    public String toString(){
        return "" + column + row;
    }
}
