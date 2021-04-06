package edu.sfu.os.chess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Moves {

    private static String indexToNotation(long ind){
        // Given an index number, returns the coordinate of the index number
        char file = (char)('a' + ind % 8);
        long rank = 8 - ind/8;
        return ("" + file + rank);
    }

    private static List<String> indexToNotationOffset(long ind, int offset){
        // Used by the pawn
        // Given an index number, returns the notation. Offset denotes the starting position of the piece.
        List<String> possibleMoves = new ArrayList<>();
        char startFile = (char)('a' + (ind + offset) % 8);
        long startRank = 8 - (ind + offset) / 8;
        char endFile = (char)('a' + ind % 8);
        long endRank = 8 - ind / 8;

        // handle promotions
        if(endRank == 8 || endRank == 1){
            possibleMoves.add("" + startFile + startRank + endFile + endRank + "q");
            possibleMoves.add("" + startFile + startRank + endFile + endRank + "r");
            possibleMoves.add("" + startFile + startRank + endFile + endRank + "b");
            possibleMoves.add("" + startFile + startRank + endFile + endRank + "n");
        }
        else{
            possibleMoves.add("" + startFile + startRank + endFile + endRank);
        }

        return possibleMoves;
    }

    private static List<String> bitboardToNotation(long bb){
        // Given a bit board, returns a list of all coordinates marked
        List<String> coordinates = new ArrayList<>();
        if(bb == 0){
            // Empty board
            return coordinates;
        }
        // Copy of the board to shift
        long trails = bb;
        // Keep track of the index value
        int index = 0;
        while(trails != 0){
            // Get next shift amount
            int shift = Long.numberOfTrailingZeros(trails);
            // Get index of next marked coordinate
            index += shift;
            coordinates.add(indexToNotation(index));
            // Split the shift up, to avoid shifting by 64 bits
            trails = trails >>> shift;
            trails = trails >>> 1;
            index++;
        }
        return coordinates;
    }

    private static List<String> bitboardToNotationOffset(long bb, int offset){
        // Used by the pawn
        // Given a bit board, returns a list of all coordinates marked.
        // Offset denotes the starting position of the piece.
        List<String> possibleMoves = new ArrayList<>();
        if(bb == 0){
            // Empty board
            return possibleMoves;
        }
        // Copy of the board to shift
        long trails = bb;
        // Keep track of the index value
        int index = 0;
        while(trails != 0){
            // Get next shift amount
            int shift = Long.numberOfTrailingZeros(trails);
            // Get index of next marked coordinate
            index += shift;
            possibleMoves.addAll(indexToNotationOffset(index, offset));
            // Split the shift up, to avoid shifting by 64 bits
            trails = trails >>> shift;
            trails = trails >>> 1;
            index++;
        }
        return possibleMoves;
    }

    public static List<String> generateMovesWP(long lastBP,Board currentPosition) {

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
        long captureLeft = WP >> 9 & (BLACK_PIECES | enPassant >> 8) & ~(BitMasks.FILE_H);
        long captureRight = WP >> 7 & (BLACK_PIECES | enPassant >> 8) & ~(BitMasks.FILE_A);

        possibleMoves.addAll(bitboardToNotationOffset(moveUpOne, 8));
        possibleMoves.addAll(bitboardToNotationOffset(moveUpTwo, 16));
        possibleMoves.addAll(bitboardToNotationOffset(captureLeft, 9));
        possibleMoves.addAll(bitboardToNotationOffset(captureRight, 7));

        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesWN(long lastBP,Board currentPosition) {
        // Retrieve bitmap from Board;
        long WP = currentPosition.WP;
        long WN = currentPosition.WN;
        long WB = currentPosition.WB;
        long WR = currentPosition.WR;
        long WQ = currentPosition.WQ;
        long WK = currentPosition.WK;

        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position

        List<String> possibleMoves = new ArrayList<>();

        if(WN == 0){
            // No knight exists
            return possibleMoves;
        }
        long trails = WN;
        int index = 0;
        while(trails != 0){
            // Get next shift amount
            int shift = Long.numberOfTrailingZeros(trails);
            // Get next index of piece
            index += shift;
            // Recreate the piece location
            long knightPosition = 1L << index;
            long moves = 0L;

            /*  Ordering of the knight moves
             *  * 8 * 0 *
             *  7 * * * 1
             *  * * N * *
             *  6 * * * 2
             *  * 5 * 4 *
             */

            moves |= (knightPosition >>> 15 & ~(WHITE_PIECES | BitMasks.FILE_A));
            moves |= (knightPosition >>> 6 & ~(WHITE_PIECES | BitMasks.FILE_AB));
            moves |= (knightPosition << 10 & ~(WHITE_PIECES | BitMasks.FILE_AB));
            moves |= (knightPosition << 17 & ~(WHITE_PIECES | BitMasks.FILE_A));
            moves |= (knightPosition << 15 & ~(WHITE_PIECES | BitMasks.FILE_H));
            moves |= (knightPosition << 6 & ~(WHITE_PIECES | BitMasks.FILE_GH));
            moves |= (knightPosition >>> 10 & ~(WHITE_PIECES | BitMasks.FILE_GH));
            moves |= (knightPosition >>> 17 & ~(WHITE_PIECES | BitMasks.FILE_H));

            List<String> end = bitboardToNotation(moves);
            String start = indexToNotation(index);
            for(var s : end){
                possibleMoves.add(start + s);
            }
            trails = trails >>> shift;
            trails = trails >>> 1;
            index++;
        }
        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesWK(long lastBP,Board currentPosition) {
        // Retrieve bitmap from Board;
        long WP = currentPosition.WP;
        long WN = currentPosition.WN;
        long WB = currentPosition.WB;
        long WR = currentPosition.WR;
        long WQ = currentPosition.WQ;
        long WK = currentPosition.WK;

        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position

        List<String> possibleMoves = new ArrayList<>();

        if (WK == 0) {
            // No king exists
            return possibleMoves;
        }

        long trails = WK;
        int index = 0;
        while (trails != 0) {
            // Get next index of piece
            index += Long.numberOfTrailingZeros(trails);
            // Recreate the piece location
            long kingPosition = 1L << index;
            long moves = 0L;
            /* Order of the king moves
             *  0 1 2
             *  3 K 4
             *  5 6 7
             */

            moves |= (kingPosition >>> 9 & ~(WHITE_PIECES | BitMasks.FILE_H));
            moves |= (kingPosition >>> 8 & ~(WHITE_PIECES));
            moves |= (kingPosition >>> 7 & ~(WHITE_PIECES | BitMasks.FILE_A));
            moves |= (kingPosition >>> 1 & ~(WHITE_PIECES | BitMasks.FILE_H));
            moves |= (kingPosition << 1 & ~(WHITE_PIECES | BitMasks.FILE_A));
            moves |= (kingPosition << 7 & ~(WHITE_PIECES | BitMasks.FILE_H));
            moves |= (kingPosition << 8 & ~(WHITE_PIECES));
            moves |= (kingPosition << 9 & ~(WHITE_PIECES | BitMasks.FILE_A));

            List<String> end = bitboardToNotation(moves);
            String start = indexToNotation(index);
            for (var s : end) {
                possibleMoves.add(start + s);
            }
            trails = trails >>> Long.numberOfTrailingZeros(trails);
            trails = trails >>> 1;
            index++;
        }
        return possibleMoves;
    }

    public static List<String> generateMovesWR(Board currentPosition){
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

        final long ALL_PIECES = BP | BN | BB | BR | BQ | BK | WP | WN | WB | WR | WQ | WK;
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position

        List<String> possibleMoves = new ArrayList<>();
        /*
            reference: https://www.youtube.com/watch?v=bCH4YK6oq8M&ab_channel=LogicCrazyChess

            ========= The Trick ===========
            occupied=11000101 as (o)
            slider=00000100   as (s)
            o-s=11000001
            o-2s=10111101     // equals to shifting to left by 1
            left=o^(o-2s)=01111000
            ===============================

            Combining left and right
            lineAttacks=(((o&m)-2s) ^ ((o&m)'-2s')')&m
        */
        if(WR == 0){
            // No rook exists
            return possibleMoves;
        }

        long trails = WR;
        int index = 0;
        while(trails != 0){
            // Get next index of piece
            int shift = Long.numberOfTrailingZeros(trails);
            // Get next index of piece
            index += shift;
            // Recreate the piece location
            long rookPosition = 1L << index;
            long rookPositionReversed = BitMasks.reverse64bits(rookPosition);

            // Get the masks
            long rankMask = BitMasks.RANK[index];
            long fileMask = BitMasks.FILE[index];

            // search Horizontally / in the Rank
            long occupiedRank = ALL_PIECES & rankMask;
            long occupiedRankReversed = BitMasks.reverse64bits(occupiedRank);
            long horizontalMoves = ((occupiedRank - (2 * rookPosition)) ^ BitMasks.reverse64bits(occupiedRankReversed - 2 * rookPositionReversed)) & rankMask;
            // search Vertically / in the File
            long occupiedFile = ALL_PIECES & fileMask;
            long occupiedFileReversed = BitMasks.reverse64bits(occupiedFile);
            long verticalMoves = ((occupiedFile - (2 * rookPosition)) ^ BitMasks.reverse64bits(occupiedFileReversed - 2 * rookPositionReversed)) & fileMask;

            long moves = horizontalMoves | verticalMoves;
            moves = moves & ~WHITE_PIECES;

            List<String> end = bitboardToNotation(moves);
            String start = indexToNotation(index);
            for(var s : end){
                possibleMoves.add(start + s);
            }
            trails = trails >>> shift;
            trails = trails >>> 1;
            index++;
        }
        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesWB(Board currentPosition){
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

        final long ALL_PIECES = BP | BN | BB | BR | BQ | BK | WP | WN | WB | WR | WQ | WK;
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position

        List<String> possibleMoves = new ArrayList<>();
        /*
            reference: https://www.youtube.com/watch?v=bCH4YK6oq8M&ab_channel=LogicCrazyChess

            ========= The Trick ===========
            occupied=11000101 as (o)
            slider=00000100   as (s)
            o-s=11000001
            o-2s=10111101     // equals to shifting to left by 1
            left=o^(o-2s)=01111000
            ===============================

            Combining left and right
            lineAttacks=(((o&m)-2s) ^ ((o&m)'-2s')')&m
        */
        if(WB == 0){
            // No bishop exists
            return possibleMoves;
        }

        long trails = WB;
        int index = 0;
        while(trails != 0){
            // Get next index of piece
            int shift = Long.numberOfTrailingZeros(trails);
            // Get next index of piece
            index += shift;
            // Recreate the piece location
            long bishopPosition = 1L << index;
            long bishopPositionReversed = BitMasks.reverse64bits(bishopPosition);

            // Get the masks
            long diagMask = BitMasks.DIAG[index];
            long antiDiagMask = BitMasks.ANTIDIAG[index];

            // search Diagonal,
            long occupiedDiag = ALL_PIECES & diagMask;
            long occupiedDiagReversed = BitMasks.reverse64bits(occupiedDiag);
            long diagMoves = ((occupiedDiag - (2 * bishopPosition)) ^ BitMasks.reverse64bits(occupiedDiagReversed - 2 * bishopPositionReversed)) & diagMask;
            // search Vertically / in the File
            long occupiedAntiDiag = ALL_PIECES & antiDiagMask;
            long occupiedAntiDiagReversed = BitMasks.reverse64bits(occupiedAntiDiag);
            long antiDiagMoves = ((occupiedAntiDiag - (2 * bishopPosition)) ^ BitMasks.reverse64bits(occupiedAntiDiagReversed - 2 * bishopPositionReversed)) & antiDiagMask;

            long moves = diagMoves | antiDiagMoves;
            moves = moves & ~WHITE_PIECES;

            List<String> end = bitboardToNotation(moves);
            String start = indexToNotation(index);
            for(var s : end){
                possibleMoves.add(start + s);
            }
            trails = trails >>> shift;
            trails = trails >>> 1;
            index++;
        }
        Collections.sort(possibleMoves);
        return possibleMoves;
    }
}
