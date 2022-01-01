package chess;

import board.BoardException;

public class ChessException extends BoardException {
    public ChessException(String message){
        super(message);
    }
}
