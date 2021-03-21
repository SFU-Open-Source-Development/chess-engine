package edu.sfu.os.chess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Moves {

    public static List<String> generateMovesPW(long lastBP,Board currentPosition) {

        // Retrieve bitmap from Board;
        long BP = currentPosition.BP;
        long BN = currentPosition.BN;
        long BB = currentPosition.BB;
        long BR = currentPosition.BR;
        long BQ = currentPosition.BQ;
        long BK = currentPosition.BK;
        long WP = currentPosition.WP;
        long WN = currentPosition.WN;
        long WB = currentPosition.WB;
        long WR = currentPosition.WR;
        long WQ = currentPosition.WQ;
        long WK = currentPosition.WK;


        final long BP_INITIAL = BitMasks.RANK_1 >>> 48; // Black's initial pawn position
        final long WP_INITIAL = BitMasks.RANK_1 >>> 8; // white's initial pawn position
        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position

        List<String> possibleMoves = new ArrayList<>();

        // moves
        long moveUpOne = WP>>8 & ~(BLACK_PIECES | WHITE_PIECES); // check for 1 step up
        long moveUpTwo = (WP&WP_INITIAL)>>16 & ~((BLACK_PIECES | WHITE_PIECES) | ((BLACK_PIECES | WHITE_PIECES) << 8) ) ; // check for 2 steps up
        // En passant
        long enPassant = BP & (lastBP & BP_INITIAL) << 16;
        // Attacks
        long captureTopLeft = WP >> 9 & (BLACK_PIECES | enPassant >> 8) & ~(BitMasks.FILE_H);
        long captureTopRight = WP >> 7 & (BLACK_PIECES | enPassant >> 8) & ~(BitMasks.FILE_A);

        // Iterate through rank 5 to check for pawn moving up by 2
        for(int i = 32; i < 40; i++) {
            if((moveUpTwo >> i & 1) == 1){
                possibleMoves.add( //"up2:" +
                        "" +  (char)('a' + i % 8) +"2" + (char)('a' + i % 8) + "4");
            }
        }

        // Iterate through entire board and check for moves
        for (int i = 0; i < 64; i++) {
            char newFile = (char)('a' + i % 8);
            int newRank = 8 - i/8;

            // Check for promotion eligibility
            char promotion = i <= 8? 'q':'\0';

            if((moveUpOne >> i & 1) == 1){
                possibleMoves.add( //"up1:" +
                        "" + newFile + (newRank - 1) + newFile + (newRank) + promotion);
            }
            if((captureTopLeft >> i & 1) == 1){
                possibleMoves.add( //"capLeft:" +
                        "" +  (char)(newFile + 1) + (newRank - 1) + newFile + (newRank) + promotion);
            }
            if((captureTopRight >> i & 1) == 1){
                possibleMoves.add( //"capRight:" +
                        "" + (char)(newFile - 1) + (newRank - 1) + newFile + (newRank) + promotion);
            }

        }

        // Debug
        /*
        System.out.println("\nmoveUpOne destination");
        System.out.println(" a  b  c  d  e  f  g  h ");
        BoardGeneration.drawBitboard(moveUpOne);

        System.out.println("\nmoveUpTwo destination");
        System.out.println(" a  b  c  d  e  f  g  h ");
        BoardGeneration.drawBitboard(moveUpTwo);

        System.out.println("\ncaptureTopLeft destination");
        System.out.println(" a  b  c  d  e  f  g  h ");
        BoardGeneration.drawBitboard(captureTopLeft);

        System.out.println("\ncaptureTopRight destination");
        System.out.println(" a  b  c  d  e  f  g  h ");
        BoardGeneration.drawBitboard(captureTopRight);

        System.out.println(captureTopLeft);
         */

        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesNW(long lastBP,Board currentPosition) {
        // Retrieve bitmap from Board;
        long WP = currentPosition.WP;
        long WN = currentPosition.WN;
        long WB = currentPosition.WB;
        long WR = currentPosition.WR;
        long WQ = currentPosition.WQ;
        long WK = currentPosition.WK;

        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position

        List<String> possibleMoves = new ArrayList<>();

        long[] knightMoves = new long[8];

        /*  * 8 * 0 *
            7 * * * 1
            * * N * *
            6 * * * 2
            * 5 * 4 *
         */

        knightMoves[0] = WN >> 15 & ~(WHITE_PIECES | BitMasks.FILE_A);
        knightMoves[1] = WN >> 6 & ~(WHITE_PIECES | BitMasks.FILE_AB);
        knightMoves[2] = WN << 10 & ~(WHITE_PIECES | BitMasks.FILE_AB);
        knightMoves[3] = WN << 17 & ~(WHITE_PIECES | BitMasks.FILE_A);
        knightMoves[4] = WN << 15 & ~(WHITE_PIECES | BitMasks.FILE_H);
        knightMoves[5] = WN << 6 & ~(WHITE_PIECES | BitMasks.FILE_GH);
        knightMoves[6] = WN >> 10 & ~(WHITE_PIECES | BitMasks.FILE_H);
        knightMoves[7] = WN >> 17 & ~(WHITE_PIECES | BitMasks.FILE_GH);

        for (int i = 0; i < 64; i++) {
            char newFile = (char)('a' + i % 8);
            int newRank = 8 - i/8;

            for(int j = 0; j < 8; j++) {
                switch (j) {
                    case 0:
                        if ((knightMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + (char)(newFile - 1) + (newRank - 2) + newFile + newRank);
                        }
                        break;
                    case 1:
                        if ((knightMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + (char)(newFile - 2) + (newRank - 1) + newFile + newRank);
                        }
                        break;
                    case 2:
                        if ((knightMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + (char)(newFile - 2) + (newRank + 1) + newFile + newRank);
                        }
                        break;
                    case 3:
                        if ((knightMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + (char)(newFile - 1) + (newRank + 2) + newFile + newRank);
                        }
                        break;
                    case 4:
                        if ((knightMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + (char)(newFile + 1) + (newRank + 2) + newFile + newRank);
                        }
                        break;
                    case 5:
                        if ((knightMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + (char)(newFile + 2) + (newRank + 1) + newFile + newRank);
                        }
                        break;
                    case 6:
                        if ((knightMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + (char)(newFile + 2) + (newRank - 1) + newFile + newRank);
                        }
                        break;
                    case 7:
                        if ((knightMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + (char)(newFile + 1) + (newRank - 2) + newFile + newRank);
                        }
                        break;
                }
            }
        }

        return possibleMoves;
    }

    public static List<String> generateMovesKW(long lastBP,Board currentPosition) {
        // Retrieve bitmap from Board;
        long BP = currentPosition.BP;
        long BN = currentPosition.BN;
        long BB = currentPosition.BB;
        long BR = currentPosition.BR;
        long BQ = currentPosition.BQ;
        long BK = currentPosition.BK;
        long WP = currentPosition.WP;
        long WN = currentPosition.WN;
        long WB = currentPosition.WB;
        long WR = currentPosition.WR;
        long WQ = currentPosition.WQ;
        long WK = currentPosition.WK;

        // KING NOT INCLUDED
        final long WHITE_PIECES = WP | WN | WB | WR | WQ; // White's current pieces position

        List<String> possibleMoves = new ArrayList<>();

        long[] kingMoves = new long[8];

        /*
            0 1 2
            3 K 4
            5 6 7
         */

        kingMoves[0] = WK >> 9 & ~(WHITE_PIECES | BitMasks.FILE_H);
        kingMoves[1] = WK >> 8 & ~(WHITE_PIECES);
        kingMoves[2] = WK >> 7 & ~(WHITE_PIECES | BitMasks.FILE_A);
        kingMoves[3] = WK >> 1 & ~(WHITE_PIECES | BitMasks.FILE_H);
        kingMoves[4] = WK << 1 & ~(WHITE_PIECES | BitMasks.FILE_A);
        kingMoves[5] = WK << 7 & ~(WHITE_PIECES | BitMasks.FILE_H);
        kingMoves[6] = WK << 8 & ~(WHITE_PIECES);
        kingMoves[7] = WK << 9 & ~(WHITE_PIECES | BitMasks.FILE_A);

        for (int i = 0; i < 64; i++) {
            char newFile = (char)('a' + i % 8);
            int newRank = 8 - i/8;

            for(int j = 0; j < 8; j++) {
                switch (j) {
                    case 0:
                        if ((kingMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + (char)(newFile + 1) + (newRank - 1) + newFile + newRank);
                        }
                        break;
                    case 1:
                        if ((kingMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + newFile + (newRank - 1) + newFile + newRank);
                        }
                        break;
                    case 2:
                        if ((kingMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + (char)(newFile - 1) + (newRank - 1) + newFile + newRank);
                        }
                        break;
                    case 3:
                        if ((kingMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + (char)(newFile + 1) + newRank + newFile + newRank);
                        }
                        break;
                    case 4:
                        if ((kingMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + (char)(newFile - 1) + newRank + newFile + newRank);
                        }
                        break;
                    case 5:
                        if ((kingMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + (char)(newFile + 1) + (newRank + 1) + newFile + newRank);
                        }
                        break;
                    case 6:
                        if ((kingMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + newFile + (newRank + 1) + newFile + newRank);
                        }
                        break;
                    case 7:
                        if ((kingMoves[j] >> i & 1) == 1) {
                            possibleMoves.add(
                                    "" + (char)(newFile - 1) + (newRank + 1) + newFile + newRank);
                        }
                        break;
                }
            }
        }

        return possibleMoves;
    }
}
