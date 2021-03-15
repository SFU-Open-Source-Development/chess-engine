package edu.sfu.os.chess;


import org.junit.Test;

import java.util.List;

import static edu.sfu.os.chess.Moves.generateMovesPW;

/**
 * Unit test for Moves
 */
public class MovesTest {

    @Test
    public void testGenerateMovesPawnWhite(){

        String[][] chessBoard ={
                {"r","n","b","q","k","b"," "," "},
                {" ","P"," ","p"," "," ","P"," "},
                {" "," "," "," "," ","b"," "," "},
                {" ","p","b"," ","p","P"," "," "},
                {" "," ","P","P"," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P"," "," "," "," "," ","P","P"},
                {"R","N","B","Q","K","B","N","R"}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);
        List<String> arr = generateMovesPW(BitMasks.RANK_1 >>> 48,currentPosition);
        System.out.println(String.join(", ", arr));

    }
}