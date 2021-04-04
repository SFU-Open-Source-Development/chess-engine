package edu.sfu.os.chess;


import org.junit.Test;

import java.util.List;

import static edu.sfu.os.chess.Moves.*;

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

    @Test
    public void testGenerateMovesKnightWhite(){

        String[][] chessBoard ={
                {"r","n","b","q","k","b"," "," "},
                {" ","P"," ","p"," "," ","P"," "},
                {" "," "," "," "," "," "," "," "},
                {" ","p"," "," ","p","P"," "," "},
                {" "," ","P","P","N"," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P"," "," "," "," "," ","P","P"},
                {"R"," ","B","Q","K","B"," ","R"}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);
        List<String> arr = generateMovesNW(BitMasks.RANK_1 >>> 48,currentPosition);
        System.out.println(String.join(", ", arr));

    }

    @Test
    public void testGenerateMovesKingWhite(){

        String[][] chessBoard ={
                {"r","n","b","q","k","b"," "," "},
                {" ","P"," ","p"," "," ","P"," "},
                {" "," "," "," "," ","b"," "," "},
                {" ","p","b"," "," "," "," "," "},
                {" "," ","P","P"," ","K"," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P"," "," ","B"," ","P","P","P"},
                {"R","N","B","Q"," ","B","N","R"}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);
        List<String> arr = generateMovesKW(BitMasks.RANK_1 >>> 48,currentPosition);
        System.out.println(String.join(", ", arr));

    }

    @Test
    public void testGenerateMovesRookWhite(){

        String[][] chessBoard ={
                {" ","n","b","q","k","b"," "," "},
                {" ","P"," ","p","R"," ","P"," "},
                {" "," "," "," "," ","b"," "," "},
                {" ","p","b"," ","p","P"," "," "},
                {" "," ","P","P"," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P"," ","R"," "," "," ","P","P"},
                {" ","N","B","Q"," ","B","N","K"}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);

        List<String> arr = Moves.generateMovesWR(currentPosition);
        System.out.println(arr);
    }
}