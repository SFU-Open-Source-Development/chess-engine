package edu.sfu.os.chess;


import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static edu.sfu.os.chess.MovesAsStrings.*;

/**
 * Unit test for MovesAsStrings
 */
public class MovesAsStringsTest {

    /**
     * This is run before running other tests
     */
    @Before
    public void setUpBeforeTest(){
        BitMasks.initBitMasks();
    }

    @Test
    public void testGenerateMovesPawnWhite(){

        String[][] chessBoard ={
                {"r","n","b","q","k","b"," "," "},
                {" ","P"," ","p"," "," "," "," "},
                {" "," "," "," "," ","b"," "," "},
                {" ","p","b"," ","p","P","p"," "},
                {" "," ","P","P"," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P"," "," "," "," "," ","P","P"},
                {"R","N","B","Q","K","B","N","R"}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);
        currentPosition.lastMove = (BitMasks.RANK_1 >>> 48 | BitMasks.RANK_1 >>> 32) & BitMasks.FILE_A << 6;
        BoardGeneration.drawBitboard(currentPosition.lastMove);
        List<String> arr = generateMovesWPAsStrings(currentPosition);
        System.out.println(String.join(", ", arr));

    }

    @Test
    public void testGenerateMovesKnightWhite(){

        String[][] chessBoard ={
                {"r","n","b","q","k","b"," "," "},
                {" ","P"," ","p"," "," ","P"," "},
                {" "," "," "," "," "," "," "," "},
                {" ","p"," "," ","p","P"," "," "},
                {" "," ","P","P"," "," "," "," "},
                {"N"," "," "," "," "," "," ","N"},
                {"P"," "," "," "," "," ","P","P"},
                {"R"," ","B","Q","K","B"," "," "}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);
        List<String> arr = generateMovesWNAsStrings(currentPosition);
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
        List<String> arr = generateMovesWKAsStrings(currentPosition);
        System.out.println(String.join(", ", arr));

    }
    @Test
    public void testGenerateCastlingKingWhite(){

        String[][] chessBoard ={
                {" ","n","b","q","k","b"," "," "},
                {" ","P"," ","p","R"," ","P"," "},
                {" "," "," "," "," ","b"," "," "},
                {" ","p","b","B","p","P","r"," "},
                {" "," ","p","P"," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P"," ","R","R","R","R"," ","P"},
                {"R"," "," "," ","K"," "," ","R"}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);
        List<String> arr = generateMovesWKAsStrings(currentPosition);
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
                {" ","N","B","Q"," ","B","R"," "}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);
        List<String> arr = generateMovesWRAsStrings(currentPosition);
        System.out.println(arr);
    }

    @Test
    public void testGenerateMovesBishopWhite(){

        String[][] chessBoard ={
                {" ","n","b","q","k","b"," "," "},
                {" ","P"," ","p","R"," ","P"," "},
                {" "," "," "," "," ","b"," "," "},
                {" ","p","b","B","p","P"," "," "},
                {" "," ","p","P"," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P"," ","R"," "," "," ","p","P"},
                {" ","N","B","Q"," ","B","R","B"}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);

        List<String> arr = generateMovesWBAsStrings(currentPosition);
        System.out.println(arr);
    }

    @Test
    public void testGenerateMovesWhiteQueens(){

        String[][] chessBoard ={
                {" ","n","b","q","k","b"," "," "},
                {" ","P"," ","p","R"," ","P"," "},
                {" "," "," "," "," ","b"," "," "},
                {" ","p","b","Q","p","P"," "," "},
                {" "," ","p","P"," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P"," ","R"," "," "," ","p","P"},
                {" ","N","B","Q"," ","B","R","B"}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);

        List<String> arr = generateMovesWQAsStrings(currentPosition);
        System.out.println(arr);
    }

    @Test
    public void testBitMasks(){

        BitMasks.initBitMasks();

        final int n = 64;
        for(int i = 0; i < n; i++){
            System.out.printf("File Index %d\n", i);
            BoardGeneration.drawBitboard(BitMasks.FILE[i]);
        }
        for(int i = 0; i < n; i++){
            System.out.printf("Rank Index %d\n", i);
            BoardGeneration.drawBitboard(BitMasks.RANK[i]);
        }
        for(int i = 0; i < n; i++){
            System.out.printf("Diag Index %d\n", i);
            BoardGeneration.drawBitboard(BitMasks.DIAG[i]);
        }
        for(int i = 0; i < n; i++){
            System.out.printf("AntiDiag Index %d\n", i);
            BoardGeneration.drawBitboard(BitMasks.ANTIDIAG[i]);
        }
    }
}