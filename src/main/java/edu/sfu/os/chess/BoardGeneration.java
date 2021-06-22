package edu.sfu.os.chess;

import java.util.*;
/**
 * This class is instantiated once at the beginning of a new game.
 */

// TODO: initiateStandardChessFEN by using the FEN class

public class BoardGeneration {

    public static Board initiateStandardChessFEN(String fenString) {
        FENParser fenParser = new FENParser(fenString);
        Board bitboards = fenParser.getBitboards();
        drawArray(bitboards);
        return bitboards;
    }

    public static Board initiateStandardChess() {

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

        return arrayToBitboards(chessBoard);
    }

    /**
     * Initiates the the bitboards using the standard starting position.
     * @param chessBoard array of strings representing the chessboard.
     * @return bitboards the updated boards for the starting position.
     */
    public static Board arrayToBitboards(String[][] chessBoard) {
        Board bitboards = new Board();
        String BinaryStr;
        for (int i=0;i<64;i++) {
            BinaryStr="0000000000000000000000000000000000000000000000000000000000000000";
            BinaryStr=BinaryStr.substring(i+1)+"1"+BinaryStr.substring(0, i); //moves the "1" one bit to the left with each iteration
            switch (chessBoard[i/8][i%8]) {
                //since there should be no overlap we can add the longs individually with each bit representing a piece of that type
                case "P": bitboards.WP+=convertStringToBitboard(BinaryStr);
                    break;
                case "N": bitboards.WN+=convertStringToBitboard(BinaryStr);
                    break;
                case "B": bitboards.WB+=convertStringToBitboard(BinaryStr);
                    break;
                case "R": bitboards.WR+=convertStringToBitboard(BinaryStr);
                    break;
                case "Q": bitboards.WQ+=convertStringToBitboard(BinaryStr);
                    break;
                case "K": bitboards.WK+=convertStringToBitboard(BinaryStr);
                    break;
                case "p": bitboards.BP+=convertStringToBitboard(BinaryStr);
                    break;
                case "n": bitboards.BN+=convertStringToBitboard(BinaryStr);
                    break;
                case "b": bitboards.BB+=convertStringToBitboard(BinaryStr);
                    break;
                case "r": bitboards.BR+=convertStringToBitboard(BinaryStr);
                    break;
                case "q": bitboards.BQ+=convertStringToBitboard(BinaryStr);
                    break;
                case "k": bitboards.BK+=convertStringToBitboard(BinaryStr);
                    break;
            }
        }
        drawArray(bitboards);
        return bitboards;
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
     * @param bitboards contains all the bitboards that reflect a chess position
     * @return Nothing.
     */
    public static void drawArray(Board bitboards) {
        String[][] chessBoard =new String[8][8];
        for (int i=0;i<64;i++) {
            chessBoard[i/8][i%8]=" ";
        }
        for (int i=0;i<64;i++) {
            //the bitwise operation ((LONG>>i)&1)==1) shifts the long to the "i" bit and checks if it's 1
            // if it is then it adds the appropriate piece to the chessBoard
            if (((bitboards.WP>>i)&1)==1) {chessBoard[i/8][i%8]="P";}
            if (((bitboards.WN>>i)&1)==1) {chessBoard[i/8][i%8]="N";}
            if (((bitboards.WB>>i)&1)==1) {chessBoard[i/8][i%8]="B";}
            if (((bitboards.WR>>i)&1)==1) {chessBoard[i/8][i%8]="R";}
            if (((bitboards.WQ>>i)&1)==1) {chessBoard[i/8][i%8]="Q";}
            if (((bitboards.WK>>i)&1)==1) {chessBoard[i/8][i%8]="K";}
            if (((bitboards.BP>>i)&1)==1) {chessBoard[i/8][i%8]="p";}
            if (((bitboards.BN>>i)&1)==1) {chessBoard[i/8][i%8]="n";}
            if (((bitboards.BB>>i)&1)==1) {chessBoard[i/8][i%8]="b";}
            if (((bitboards.BR>>i)&1)==1) {chessBoard[i/8][i%8]="r";}
            if (((bitboards.BQ>>i)&1)==1) {chessBoard[i/8][i%8]="q";}
            if (((bitboards.BK>>i)&1)==1) {chessBoard[i/8][i%8]="k";}
        }
        for (int i=0;i<8;i++) {
            System.out.println(Arrays.toString(chessBoard[i]));
        }
        System.out.println();
    }

    /**
     * This method draws to stdout (for debugging purposes).
     * @param bitboard long containing bitboard to draw.
     * @return Nothing.
     */
    public static void drawBitboard(long bitboard) {
        String[][] chessBoard = new String[8][8];
        for (int i = 0; i < 64; i++) {
            chessBoard[i / 8][i % 8] = "";
        }
        for (int i = 0; i < 64; i++) {
            if (((bitboard >>> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "P";//P meaning piece
            }
            if ("".equals(chessBoard[i / 8][i % 8])) {
                chessBoard[i / 8][i % 8] = " ";
            }
        }
        for (int i = 0; i < 8; i++) {
            System.out.println(Arrays.toString(chessBoard[i]));
        }
        System.out.println();
    }
}