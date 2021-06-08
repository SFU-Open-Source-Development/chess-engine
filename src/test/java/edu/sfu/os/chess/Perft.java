package edu.sfu.os.chess;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** This class will run the Perft test on the engine by recursively running all possible moves to a specified depth
TODO: Add special moves once regular moves are working
 */


public class Perft {
    public static void main( String[] args ) {
        BitMasks.initBitMasks();

        String[][] chessBoard ={
                {"r"," "," "," ","k"," "," ","r"},
                {"p"," ","p","p","q","p","b"," "},
                {"b","n"," "," ","p","n","p"," "},
                {" "," "," ","P","N"," "," "," "},
                {" ","p"," "," ","P"," "," "," "},
                {" "," ","N"," "," ","Q"," ","p"},
                {"P","P","P","B","B","P","P","P"},
                {"R"," "," "," ","K"," "," ","R"}};

        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);

        //Board currentPosition = BoardGeneration.initiateStandardChess();
        BoardGeneration.drawArray(currentPosition);
        perft(currentPosition,true,0);
        System.out.println("depth " + perftMaxDepth + " total possible moves " + possibleMovesTotal);
        System.out.println("depth " + perftMaxDepth + " castle possible moves " + castleMovesTotal);
        System.out.println("depth " + perftMaxDepth + " enPassant possible moves " + enPassantMovesTotal);
        System.out.println("depth " + perftMaxDepth + " promotion possible moves " + promotionMovesTotal);
        System.out.println("rejected "+ rejectedMovesCount);
    }
    static int perftMaxDepth = 5;//number of layers to calculate for perft testing
    static List<Long> possibleMovesTotal = new ArrayList<>(Collections.nCopies(perftMaxDepth, 0L));
    static List<Long> castleMovesTotal = new ArrayList<>(Collections.nCopies(perftMaxDepth, 0L));
    static List<Long> enPassantMovesTotal = new ArrayList<>(Collections.nCopies(perftMaxDepth, 0L));
    static List<Long> promotionMovesTotal = new ArrayList<>(Collections.nCopies(perftMaxDepth, 0L));
    static int rejectedMovesCount = 0;

    public static void perft(Board currentBoard, Boolean WhiteToMove, int depth) {
        if (depth < perftMaxDepth) {
            if (WhiteToMove) {
                List<Long> moves = Moves.generateAllWhiteMoves(currentBoard);
                for(var i : moves) {//try to do every possible move
                    Board tmpBoard = Moves.moveWhite(currentBoard, i);
                    //System.out.println("White Move to attempt:");
                    //BoardGeneration.drawArray(tmpBoard);
                    if((Moves.whiteKingSafety(tmpBoard)&tmpBoard.WK)==0) {//compute king safety after each move
                        //System.out.println("^Move Accepted");
                        possibleMovesTotal.set(depth, possibleMovesTotal.get(depth) + 1);
                        perft(tmpBoard, false, depth + 1);
                    } else{
                        rejectedMovesCount++;
                        //System.out.println("^Move rejected");
                    }
                }
                List<SpecialMoves> specialMoves = Moves.generateAllWhiteSpecialMoves(currentBoard);
                for(var i : specialMoves) {//try to do every possible move
                    Board tmpBoard = Moves.specialMoveWhite(currentBoard, i);
                    //System.out.println("White Move to attempt:");
                    //BoardGeneration.drawArray(tmpBoard);
                    if((Moves.whiteKingSafety(tmpBoard) & tmpBoard.WK)==0) {//compute king safety after each move
                        //System.out.println("^Move Accepted");
                        possibleMovesTotal.set(depth, possibleMovesTotal.get(depth) + 1);
                        switch(i.moveType){
                            case CASTLE -> castleMovesTotal.set(depth, castleMovesTotal.get(depth) + 1);
                            case ENPASSANT -> enPassantMovesTotal.set(depth, enPassantMovesTotal.get(depth) + 1);
                            case PROMOTION -> promotionMovesTotal.set(depth, promotionMovesTotal.get(depth) + 1);
                        }
                        perft(tmpBoard, false, depth + 1);
                    } else{
                        rejectedMovesCount++;
                        //System.out.println("^Move rejected");
                    }
                }
            } else {
                List<Long> moves = Moves.generateAllBlackMoves(currentBoard);
                for(var i : moves) {//try to do every possible move
                    Board tmpBoard = Moves.moveBlack(currentBoard, i);
                    //System.out.println("Black Move to attempt:");
                    //BoardGeneration.drawArray(tmpBoard);
                    if((Moves.blackKingSafety(tmpBoard) & tmpBoard.BK)==0) {//compute king safety after each move
                        //System.out.println("^Move Accepted");
                        possibleMovesTotal.set(depth, possibleMovesTotal.get(depth) + 1);
                        perft(tmpBoard, true, depth + 1);
                    } else{
                        rejectedMovesCount++;
                        //System.out.println("^Move rejected");
                    }
                }
                List<SpecialMoves> specialMoves = Moves.generateAllBlackSpecialMoves(currentBoard);
                for(var i : specialMoves){
                    Board tmpBoard = Moves.specialMoveBlack(currentBoard, i);
                    //System.out.println("Black Move to attempt:");
                    //BoardGeneration.drawArray(tmpBoard);
                    if((Moves.blackKingSafety(tmpBoard) & tmpBoard.BK)==0) {//compute king safety after each move
                        //System.out.println("^Move Accepted");
                        possibleMovesTotal.set(depth, possibleMovesTotal.get(depth) + 1);
                        switch(i.moveType){
                            case CASTLE -> castleMovesTotal.set(depth, castleMovesTotal.get(depth) + 1);
                            case ENPASSANT -> enPassantMovesTotal.set(depth, enPassantMovesTotal.get(depth) + 1);
                            case PROMOTION -> promotionMovesTotal.set(depth, promotionMovesTotal.get(depth) + 1);
                        }
                        perft(tmpBoard, true, depth + 1);
                    } else{
                        rejectedMovesCount++;
                        //System.out.println("^Move rejected");
                    }
                }
            }

        }

    }

}
