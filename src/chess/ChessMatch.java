package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import board.Board;
import board.Piece;
import board.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;

    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.WHITE;
        check = false;
        initialSetup();
    }
    
    public int getTurn(){
        return this.turn;
    }

    public Color getCurrentPlayer(){
        return currentPlayer;
    }

    public boolean getCheck(){
        return check;
    }

    public boolean getCheckMate(){
        return checkMate;
    }

    public ChessPiece getEnPassantVulnerable(){
        return this.enPassantVulnerable;
    }

    public ChessPiece[][] getPieces(){
        ChessPiece[][] mat = new ChessPiece[board.getRow()][board.getColumn()];
        for (int i = 0; i < board.getRow(); i++){
            for(int j = 0; j < board.getColumn(); j++){
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition){
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMovie(ChessPosition sourcePosition, ChessPosition targetPosition){
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);

        if(testCheck(currentPlayer)){
            undoMove(source, target, capturedPiece);
            throw new ChessException("You Cannot Put YourSelf In Check");
        }

        ChessPiece movedPiece = (ChessPiece)board.piece(target);

        check = (testCheck(opponent(currentPlayer))) ? true : false;

        if(testCheckMate(opponent(currentPlayer))){
            this.checkMate = true;
        } else{
            nextTurn();
        }

        // special move: checking if it is in passant
        if(movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)){
            enPassantVulnerable = movedPiece;
        } else{
            enPassantVulnerable = null;
        }

        return (ChessPiece)capturedPiece;
    }

    private void validateSourcePosition(Position position){
        if(!board.thereIsAPiece(position)){
            throw new ChessException("There is no piece on source position");
        }

        if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()){
            throw new ChessException("The Chosen Piece is Not Yours");
        }

        if(!board.piece(position).isThereAnyPossibleMove()){
            throw new ChessException("There is no possible moves for the chosen piece");
        }
    }

    public void validateTargetPosition(Position source, Position target){
        if(!board.piece(source).possibleMoves(target)){
            throw new ChessException("The chosen piece can not move to the target position");
        }
    }

    private Piece makeMove(Position source, Position target){
        ChessPiece p = (ChessPiece)board.removePiece(source);
        p.increaseMoveCount();
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);

        if(capturedPiece != null){
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        // special move: small castling
        if(p instanceof King && target.getColumn() == source.getColumn() + 2){
            Position sourceR = new Position (source.getRow(), source.getColumn() + 3);
            Position targetR = new Position (source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceR);
            board.placePiece(rook, targetR);
            rook.increaseMoveCount();
        }

        // special move: big castling
        if(p instanceof King && target.getColumn() == source.getColumn() - 2){
            Position sourceR = new Position (source.getRow(), source.getColumn() - 4);
            Position targetR = new Position (source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceR);
            board.placePiece(rook, targetR);
            rook.increaseMoveCount();
        }

        // special move: passant
        if(p instanceof Pawn){
            if(source.getColumn() != target.getColumn() && capturedPiece == null){
                Position pawnPosition;
                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(target.getRow() + 1, target.getColumn());
                } else{
                    pawnPosition = new Position(target.getRow() - 1, target.getColumn());
                }
                capturedPiece = board.removePiece(pawnPosition);
                capturedPieces.add(capturedPiece);
                piecesOnTheBoard.remove(capturedPiece);
            }
        }
        
        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece){
        ChessPiece p = (ChessPiece)board.removePiece(target);
        p.decreaseMoveCount();
        board.placePiece(p, source);

        if (capturedPiece != null){
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }

        // special move: small castling
        if(p instanceof King && target.getColumn() == source.getColumn() + 2){
            Position sourceR = new Position (source.getRow(), source.getColumn() + 3);
            Position targetR = new Position (source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetR);
            board.placePiece(rook, sourceR);
            rook.decreaseMoveCount();
        }

        // special move: big castling
        if(p instanceof King && target.getColumn() == source.getColumn() - 2){
            Position sourceR = new Position (source.getRow(), source.getColumn() - 4);
            Position targetR = new Position (source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetR);
            board.placePiece(rook, sourceR);
            rook.decreaseMoveCount();
        }

        // special move: passant
        if(p instanceof Pawn){
            if(source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable){
                ChessPiece pawn = (ChessPiece)board.removePiece(target);
                Position pawnPosition;
                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(3, target.getColumn());
                } else{
                    pawnPosition = new Position(4, target.getColumn());
                }
                board.placePiece(pawn, pawnPosition);
            }
        }
    }

    // incrementing the turn and changing the current player color
    private void nextTurn(){
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private Color opponent(Color color){
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color){
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for(Piece p: list){
            if(p instanceof King){
                return (ChessPiece)p;
            }
        }
        throw new IllegalStateException("There is no " + color + " king on the board");
    }

    private boolean testCheck(Color color){
        // getting the king position on matrix format
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
        // looping to make sure that any opponent piece don't have one possible move that is equal to the king position.
        for(Piece p: opponentPieces){
            boolean[][] mat = p.possibleMoves();
            if(mat[kingPosition.getRow()][kingPosition.getColumn()]){
                return true;
            }
        }
        return false;
    }

    private boolean testCheckMate(Color color){
        if(!testCheck(color)){
            return false;
        }
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for(Piece p : list){
            boolean[][] mat = p.possibleMoves();
            for(int i = 0; i < board.getRow(); i++){
                for(int j = 0; j < board.getColumn(); j++){
                    if(mat[i][j]){
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheckTemp = testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if(!testCheckTemp){
                            return false;
                        }
                    }
                }   
            }
        }
        return true;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    private void initialSetup(){
        // Rook W
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));

        // Rook B
        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));

        // Bishop W
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));

        // Bishop B
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));

        // Knight W
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));

        // Knight B
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));

        // Queen W
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));

        // Queen B
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));

        // Kings W
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));

        // Kings B
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));

        // Paws W
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        // Paws B
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
    }
}
