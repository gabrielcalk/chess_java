package chess;

import java.security.InvalidParameterException;
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
    private ColorChess currentPlayer;
    private Board board;
    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;
    private ChessPiece promoted;

    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        board = new Board(8, 8);
        turn = 1;
        currentPlayer = ColorChess.WHITE;
        check = false;
        initialSetup();
    }
    
    public int getTurn(){
        return this.turn;
    }

    public ColorChess getCurrentPlayer(){
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

    public ChessPiece getPromoted(){
        return this.promoted;
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

    /**
     * @param sourcePosition
     * @param targetPosition
     */
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

        // special move: promoted
        promoted = null;
        if(movedPiece instanceof Pawn){
            if((movedPiece.getColor() == ColorChess.WHITE && target.getRow() == 0) || (movedPiece.getColor() == ColorChess.BLACK && target.getRow() == 7)){
                promoted = (ChessPiece)board.piece(target);
                promoted = replacePromotedPiece("Q");
            }
        } 

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
    
    /**
     * @REMEBER
     * @method replaces the pawn and add the new piece
     */
    public ChessPiece replacePromotedPiece(String type){
        if(promoted == null){
            throw new IllegalStateException("There Is No Piece To Be Promoted");
        }

        Position pos = promoted.getChessPosition().toPosition();
        Piece p = board.removePiece(pos);
        piecesOnTheBoard.remove(p);

        ChessPiece newPiece = newPiece(type, promoted.getColor());
        board.placePiece(newPiece, pos);
        piecesOnTheBoard.add(newPiece);

        return newPiece;
    }

    /**
     * @REMEBER
     * Creating one new piece (used in promotion)
     */
    private ChessPiece newPiece(String type, ColorChess color){
        if(type.equals("B"))
            return new Bishop(board, color);
        if(type.equals("N"))
            return new Knight(board, color);
        if(type.equals("R"))
            return new Rook(board, color);
        return new Queen(board, color);
    }

    // Exceptions to show to the user when he/she select one wrong position or piece
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

    /**
     * 
     * @param source
     * @param target
     * @return capturedPiece if has one, 
     * this method is used on every moved
     */
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
                if(p.getColor() == ColorChess.WHITE){
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
                if(p.getColor() == ColorChess.WHITE){
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
        currentPlayer = (currentPlayer == ColorChess.WHITE) ? ColorChess.BLACK : ColorChess.WHITE;
    }

    private ColorChess opponent(ColorChess color){
        return (color == ColorChess.WHITE) ? ColorChess.BLACK : ColorChess.WHITE;
    }

    private ChessPiece king(ColorChess color){
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for(Piece p: list){
            if(p instanceof King){
                return (ChessPiece)p;
            }
        }
        throw new IllegalStateException("There is no " + color + " king on the board");
    }

    private boolean testCheck(ColorChess color){
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

    private boolean testCheckMate(ColorChess color){
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
        placeNewPiece('a', 1, new Rook(board, ColorChess.WHITE));
        placeNewPiece('h', 1, new Rook(board, ColorChess.WHITE));

        // Rook B
        placeNewPiece('a', 8, new Rook(board, ColorChess.BLACK));
        placeNewPiece('h', 8, new Rook(board, ColorChess.BLACK));

        // Bishop W
        placeNewPiece('c', 1, new Bishop(board, ColorChess.WHITE));
        placeNewPiece('f', 1, new Bishop(board, ColorChess.WHITE));

        // Bishop B
        placeNewPiece('c', 8, new Bishop(board, ColorChess.BLACK));
        placeNewPiece('f', 8, new Bishop(board, ColorChess.BLACK));

        // Knight W
        placeNewPiece('b', 1, new Knight(board, ColorChess.WHITE));
        placeNewPiece('g', 1, new Knight(board, ColorChess.WHITE));

        // Knight B
        placeNewPiece('b', 8, new Knight(board, ColorChess.BLACK));
        placeNewPiece('g', 8, new Knight(board, ColorChess.BLACK));

        // Queen W
        placeNewPiece('d', 1, new Queen(board, ColorChess.WHITE));

        // Queen B
        placeNewPiece('d', 8, new Queen(board, ColorChess.BLACK));

        // Kings W
        placeNewPiece('e', 1, new King(board, ColorChess.WHITE, this));

        // Kings B
        placeNewPiece('e', 8, new King(board, ColorChess.BLACK, this));

        // Paws W
        placeNewPiece('a', 2, new Pawn(board, ColorChess.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, ColorChess.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, ColorChess.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, ColorChess.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, ColorChess.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, ColorChess.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, ColorChess.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, ColorChess.WHITE, this));

        // Paws B
        placeNewPiece('a', 7, new Pawn(board, ColorChess.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, ColorChess.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, ColorChess.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, ColorChess.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, ColorChess.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, ColorChess.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, ColorChess.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, ColorChess.BLACK, this));
    }
}
