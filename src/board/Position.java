package board;

public class Position {
    private int row;
    private int column;

    public Position(){}

    public Position(int row, int columns){
        this.row = row;
        this.column = columns;
    }

    public int getRow(){
        return this.row;
    }

    public void setRow(int row){
        this.row = row;
    }

    public int getColumn(){
        return this.column;
    }

    public void setColumn(int column){
        this.column = column;
    }

    @Override
    public String toString(){
        return this.row + ", " + this.column;
    }
}
