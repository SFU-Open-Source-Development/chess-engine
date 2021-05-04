package edu.sfu.os.chess;


import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static edu.sfu.os.chess.Moves.*;
import static edu.sfu.os.chess.MovesAsStrings.generateMovesWPAsStrings;

/**
 * Unit test for Moves
 */
public class MovesTest{

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
                {" ","P"," ","p"," "," ","P"," "},
                {" "," "," "," "," ","b"," "," "},
                {" ","p","b"," ","p","P"," "," "},
                {" "," ","P","P"," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P"," "," "," "," "," ","P","P"},
                {"R","N","B","Q","K","B","N","R"}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);
        currentPosition.lastMove = BitMasks.RANK_1 >>> 48 | BitMasks.RANK_1 >>> 32;
        List<Long> arr = generateMovesWP(currentPosition);
        for(var bb : arr){
            System.out.println(" a  b  c  d  e  f  g  h ");
            BoardGeneration.drawBitboard(bb);
        }
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
        List<Long> arr = generateMovesWN(currentPosition);
        for(var bb : arr){
            System.out.println(" a  b  c  d  e  f  g  h ");
            BoardGeneration.drawBitboard(bb);
        }
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
        List<Long> arr = generateMovesWK(currentPosition);
        for(var bb : arr){
            System.out.println(" a  b  c  d  e  f  g  h ");
            BoardGeneration.drawBitboard(bb);
        }
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
        List<Long> arr = generateMovesWR(currentPosition);
        for(var bb : arr){
            System.out.println(" a  b  c  d  e  f  g  h ");
            BoardGeneration.drawBitboard(bb);
        }
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

        List<Long> arr = generateMovesWB(currentPosition);
        for(var bb : arr){
            System.out.println(" a  b  c  d  e  f  g  h ");
            BoardGeneration.drawBitboard(bb);
        }
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

        List<Long> arr = generateMovesWQ(currentPosition);
        for(var bb : arr){
            System.out.println(" a  b  c  d  e  f  g  h ");
            BoardGeneration.drawBitboard(bb);
        }
    }

    @Test
    public void testGenerateCastlingKingWhite(){

        String[][] chessBoard ={
                {" ","n","b","q","k","b"," "," "},
                {" ","P"," ","p","R"," ","P"," "},
                {" "," "," "," "," ","b"," "," "},
                {" ","p","b","B","p","P"," "," "},
                {" "," ","p","P"," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P"," ","R","R","R","R"," ","P"},
                {"R"," "," "," ","K"," "," ","R"}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);

        List<List<Long>> arr = generateMovesWCastle(currentPosition);
        for(var e : arr){
            for(var bb : e){
                System.out.println(" a  b  c  d  e  f  g  h ");
                BoardGeneration.drawBitboard(bb);
            }
        }
    }

    @Test
    public void testGeneratePromotionWhite(){

        String[][] chessBoard ={
                {"r","n","b","q","k","b"," "," "},
                {" ","P"," ","p"," "," ","P"," "},
                {" "," "," "," "," ","b"," "," "},
                {" ","p","b"," ","p","P","p"," "},
                {" "," ","P","P"," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P"," "," "," "," "," ","P","P"},
                {"R","N","B","Q","K","B","N","R"}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);

        List<List<Long>> arr = generateMovesWPromotion(currentPosition);
        for(var e : arr){
            System.out.println(" a  b  c  d  e  f  g  h ");
            BoardGeneration.drawBitboard(e.get(0));
        }

    }

    @Test
    public void testMovesWhite(){

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

        List<Long> arr = generateMovesWP(currentPosition);
        for(var bb : arr){
            System.out.println(" a  b  c  d  e  f  g  h ");
            BoardGeneration.drawBitboard(bb);
            System.out.println(" a  b  c  d  e  f  g  h ");
            BoardGeneration.drawArray(moveWhite(currentPosition, bb));
        }
    }

    @Test
    public void testCastleWhite(){

        String[][] chessBoard ={
                {" ","n","b","q","k","b"," "," "},
                {" ","P"," ","p","R"," ","P"," "},
                {" "," "," "," "," ","b"," "," "},
                {" ","p","b","B","p","P"," "," "},
                {" "," ","p","P"," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P"," ","R","R","R","R"," ","P"},
                {"R","B"," "," ","K"," "," ","R"}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);

        List<List<Long>> arr = generateMovesWCastle(currentPosition);
        for(var e : arr){
            for(var bb : e){
                System.out.println(" a  b  c  d  e  f  g  h ");
                BoardGeneration.drawBitboard(bb);
            }
            System.out.println(" a  b  c  d  e  f  g  h ");
            BoardGeneration.drawArray(castleWhite(currentPosition, e));
        }
    }

    @Test
    public void testEnPassantWhite(){

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

        List<List<Long>> arr = generateMovesWEnPassant(currentPosition);
        for(var e : arr){
            for(var bb : e){
                System.out.println(" a  b  c  d  e  f  g  h ");
                BoardGeneration.drawBitboard(bb);
            }
            System.out.println(" a  b  c  d  e  f  g  h ");
            BoardGeneration.drawArray(enPassantWhite(currentPosition, e));
        }

    }

    @Test
    public void testPromotionWhite(){

        String[][] chessBoard ={
                {"r","n","b","q","k","b"," "," "},
                {" ","P"," ","p"," "," ","P"," "},
                {" "," "," "," "," ","b"," "," "},
                {" ","p","b"," ","p","P","p"," "},
                {" "," ","P","P"," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P"," "," "," "," "," ","P","P"},
                {"R","N","B","Q","K","B","N","R"}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);

        List<List<Long>> arr = generateMovesWPromotion(currentPosition);
        for(var e : arr){
            System.out.println(" a  b  c  d  e  f  g  h ");
            BoardGeneration.drawBitboard(e.get(0));
            System.out.println(" a  b  c  d  e  f  g  h ");
            BoardGeneration.drawArray(promotionWhite(currentPosition, e));
        }

    }
}