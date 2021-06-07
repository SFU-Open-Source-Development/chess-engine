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
        Board currentPosition = BoardGeneration.initiateStandardChess();
        BoardGeneration.drawArray(currentPosition);
        perft(currentPosition,true,0);
        System.out.println("depth " + perftMaxDepth + " total possible moves "+ possibleMovesTotal);
        System.out.println("rejected "+ rejectedMovesCount);
    }
    static int perftMaxDepth = 3;//number of layers to calculate for perft testing
    static List<Integer> possibleMovesTotal = new ArrayList<>(Collections.nCopies(perftMaxDepth, 0));
    static int rejectedMovesCount = 0;

    public static void perft(Board currentBoard, Boolean WhiteToMove, int depth) {
        if (depth < perftMaxDepth) {
            if (WhiteToMove) {
                List<Long> arr = Moves.generateAllWhiteMoves(currentBoard);
                for (var i : arr) {//try to do every possible move
                    Board tmpBoard = Moves.moveWhite(currentBoard, i);
                    System.out.println("White Move to attempt:");
                    BoardGeneration.drawArray(tmpBoard);
                    if((Moves.whiteKingSafety(tmpBoard)&tmpBoard.WK)==0) {//compute king safety after each move
                        //System.out.println("^Move Accepted");
                        possibleMovesTotal.set(depth, possibleMovesTotal.get(depth) + 1);
                        perft(tmpBoard, false, depth + 1);
                    }else{
                        rejectedMovesCount++;
                        System.out.println("^Move rejected");
                    }
                }
            } else {
                List<Long> arr = Moves.generateAllBlackMoves(currentBoard);
                for (var i : arr) {//try to do every possible move
                    Board tmpBoard = Moves.moveBlack(currentBoard, i);
                    System.out.println("Black Move to attempt:");
                    BoardGeneration.drawArray(tmpBoard);
                    if((Moves.blackKingSafety(tmpBoard)&tmpBoard.BK)==0) {//compute king safety after each move
                        //System.out.println("^Move Accepted");
                        possibleMovesTotal.set(depth, possibleMovesTotal.get(depth) + 1);
                        perft(tmpBoard, true, depth + 1);
                    }else{
                        rejectedMovesCount++;
                        System.out.println("^Move rejected");
                    }
                }
            }

        }

    }

}
