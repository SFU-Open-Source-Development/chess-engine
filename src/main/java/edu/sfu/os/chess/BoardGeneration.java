package edu.sfu.os.chess;

import java.util.*;
/**
 * This class is instantiated once at the beginning of a new game.
 */

// TODO: initiateStandardChessFEN by using the FEN class

public class BoardGeneration {

    public static void initiateStandardChess() {

        // Uppercase is WHITE and lowercase is BLACK
        String[][] chessBoard ={
                {"r","n","b","q","k","b","n","r"},
                {"p","p","p","p","p","p","p","p"},
                {" "," "," "," "," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P","P","P","P","P","P","P","P"},
                {"R","N","B","Q","K","B","N","R"}};

        arrayToBitboards(chessBoard);
    }

    /**
     * Initiates the the bitboards using the standard starting position.
     * @param chessBoard array of strings representing the chessboard.
     * @return Nothing.
     */
    public static void arrayToBitboards(String[][] chessBoard) {
        long WP=0L,WN=0L,WB=0L,WR=0L,WQ=0L,WK=0L,BP=0L,BN=0L,BB=0L,BR=0L,BQ=0L,BK=0L;// L just guarantees enough precision(64 bits)
        String BinaryStr;
        for (int i=0;i<64;i++) {
            BinaryStr="0000000000000000000000000000000000000000000000000000000000000000";
            BinaryStr=BinaryStr.substring(i+1)+"1"+BinaryStr.substring(0, i); //moves the "1" one bit to the left with each iteration
            switch (chessBoard[i/8][i%8]) {
                //since there should be no overlap we can add the longs individually with each bit representing a piece of that type
                case "P": WP+=convertStringToBitboard(BinaryStr);
                    break;
                case "N": WN+=convertStringToBitboard(BinaryStr);
                    break;
                case "B": WB+=convertStringToBitboard(BinaryStr);
                    break;
                case "R": WR+=convertStringToBitboard(BinaryStr);
                    break;
                case "Q": WQ+=convertStringToBitboard(BinaryStr);
                    break;
                case "K": WK+=convertStringToBitboard(BinaryStr);
                    break;
                case "p": BP+=convertStringToBitboard(BinaryStr);
                    break;
                case "n": BN+=convertStringToBitboard(BinaryStr);
                    break;
                case "b": BB+=convertStringToBitboard(BinaryStr);
                    break;
                case "r": BR+=convertStringToBitboard(BinaryStr);
                    break;
                case "q": BQ+=convertStringToBitboard(BinaryStr);
                    break;
                case "k": BK+=convertStringToBitboard(BinaryStr);
                    break;
            }
        }

        Engine.WP=WP; Engine.WN=WN; Engine.WB=WB;
        Engine.WR=WR; Engine.WQ=WQ; Engine.WK=WK;
        Engine.BP=BP; Engine.BN=BN; Engine.BB=BB;
        Engine.BR=BR; Engine.BQ=BQ; Engine.BK=BK;

        drawArray(WP,WN,WB,WR,WQ,WK,BP,BN,BB,BR,BQ,BK);
    }

    /**
     * Converts a binary string to a bitboard
     * @param Binary the string to convert to a bitboard
     * @return long containing the bitboard
     */
    public static long convertStringToBitboard(String Binary) {
        if (Binary.charAt(0)=='0') {
            return Long.parseLong(Binary, 2);
        } else { //"remove" the signed bit
            return Long.parseLong("1"+Binary.substring(2), 2)*2;
        }
    }

    /**
     * Draws the board position to stdout from the provided bit boards
     * @param WP long representing white pawns positions
     * @param WN long representing white knights positions
     * @param WB long representing white bishops positions
     * @param WR long representing white rooks positions
     * @param WQ long representing white queen position
     * @param WK long representing white king position
     * @param BP long representing black pawns positions
     * @param BN long representing black knights positions
     * @param BB long representing black bishops positions
     * @param BR long representing black rooks positions
     * @param BQ long representing black queen position
     * @param BK long representing black king position
     * @return Nothing.
     */
    public static void drawArray(long WP,long WN,long WB,long WR,long WQ,long WK,long BP,long BN,long BB,long BR,long BQ,long BK) {
        String[][] chessBoard =new String[8][8];
        for (int i=0;i<64;i++) {
            chessBoard[i/8][i%8]=" ";
        }
        for (int i=0;i<64;i++) {
            //the bitwise operation ((LONG>>i)&1)==1) shifts the long to the "i" bit and checks if it's 1
            // if it is then it adds the appropriate piece to the chessBoard
            if (((WP>>i)&1)==1) {chessBoard[i/8][i%8]="P";}
            if (((WN>>i)&1)==1) {chessBoard[i/8][i%8]="N";}
            if (((WB>>i)&1)==1) {chessBoard[i/8][i%8]="B";}
            if (((WR>>i)&1)==1) {chessBoard[i/8][i%8]="R";}
            if (((WQ>>i)&1)==1) {chessBoard[i/8][i%8]="Q";}
            if (((WK>>i)&1)==1) {chessBoard[i/8][i%8]="K";}
            if (((BP>>i)&1)==1) {chessBoard[i/8][i%8]="p";}
            if (((BN>>i)&1)==1) {chessBoard[i/8][i%8]="n";}
            if (((BB>>i)&1)==1) {chessBoard[i/8][i%8]="b";}
            if (((BR>>i)&1)==1) {chessBoard[i/8][i%8]="r";}
            if (((BQ>>i)&1)==1) {chessBoard[i/8][i%8]="q";}
            if (((BK>>i)&1)==1) {chessBoard[i/8][i%8]="k";}
        }
        for (int i=0;i<8;i++) {
            System.out.println(Arrays.toString(chessBoard[i]));
        }
    }

    /**
     * This method draws to stdout (for debugging purposes).
     * @param bitBoard long containing bitboard to draw.
     * @return Nothing.
     */
    public static void drawBitboard(long bitBoard) {
        String[][] chessBoard = new String[8][8];
        for (int i = 0; i < 64; i++) {
            chessBoard[i / 8][i % 8] = "";
        }
        for (int i = 0; i < 64; i++) {
            if (((bitBoard >>> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "P";//P meaning piece
            }
            if ("".equals(chessBoard[i / 8][i % 8])) {
                chessBoard[i / 8][i % 8] = " ";
            }
        }
        for (int i = 0; i < 8; i++) {
            System.out.println(Arrays.toString(chessBoard[i]));
        }
    }
}