package edu.sfu.os.chess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Moves {

    /**
     * Given an Board and last position of black pawns, returns all possible moves for white
     *
     * @return returns all possible moves for white
     */
    public static List<String> generateAllWhiteMoves(Board currentPosition, long lastBP){

        List<String> whiteMoves = new ArrayList<>();

        whiteMoves.addAll(generateMovesWP(lastBP, currentPosition));
        whiteMoves.addAll(generateMovesWN(currentPosition));
        whiteMoves.addAll(generateMovesWK(currentPosition));
        whiteMoves.addAll(generateMovesWR(currentPosition));
        whiteMoves.addAll(generateMovesWB(currentPosition));
        whiteMoves.addAll(generateMovesWQ(currentPosition));

        return whiteMoves;
    }
    /**
     * Given an Board and last position of white pawns, returns all possible moves for black
     *
     * @return returns all possible moves for black
     */
    public static List<String> generateAllBlackMoves(Board currentPosition, long lastWP){

        List<String> blackMoves = new ArrayList<>();

        blackMoves.addAll(generateMovesBP(lastWP, currentPosition));
        blackMoves.addAll(generateMovesBN(currentPosition));
        blackMoves.addAll(generateMovesBK(currentPosition));
        blackMoves.addAll(generateMovesBR(currentPosition));
        blackMoves.addAll(generateMovesBB(currentPosition));
        blackMoves.addAll(generateMovesBQ(currentPosition));

        return blackMoves;
    }

    // Helper functions for generating result Strings from derived Bitboards
    /**
     * Given an index number, returns a string denoting the corresponding rank and file on the chess board
     *
     * @param index represents a position on the chess board
     *
     * @return a string denoting the corresponding rank and file
     */
    private static String indexToNotation(long index){
        // Given an index number, returns the coordinate of the index number
        char file = (char)('a' + index % 8);
        long rank = 8 - index/8;
        return ("" + file + rank);
    }

    /**
     * Given an index number, returns all possible notations with promotion taking in account.
     * Used by {@link #bitboardToNotationWithOffset(long, int)}
     *
     * @param index is the destination of a move
     * @param offset is used to calculate the the starting index of the piece, where starting index = index + offset
     *
     * @return 4 possible moves if there is promotion and 1 otherwise in string
     */
    private static List<String> indexToNotationWithOffset(long index, int offset){
        
        List<String> possibleMoves = new ArrayList<>();
        char startFile = (char)('a' + (index + offset) % 8);
        long startRank = 8 - (index + offset) / 8;
        char endFile = (char)('a' + index % 8);
        long endRank = 8 - index / 8;

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

    /**
     * Given a bit board, returns a list of notations (with rank and file, eg. "e1e2") for moves in a list of string.
     * @param bb a input bit board
     *
     * @return a list of rank and file position in a list of string
     */
    private static List<String> bitboardToNotation(long bb){
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

    /**
     * Given a bit board, returns a list of notations (with rank and file, eg. "e1e2") for moves in a list of string.
     *
     * @param bb a bit board that marks all possible move destinations from an original position(from offset)
     * @param offset denotes the amount of shift required for a destination index to get a piece's original position.
     *
     * @return a list of rank and file position in a list of string
     */
    private static List<String> bitboardToNotationWithOffset(long bb, int offset){

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
            possibleMoves.addAll(indexToNotationWithOffset(index, offset));
            // Split the shift up, to avoid shifting by 64 bits
            trails = trails >>> shift;
            trails = trails >>> 1;
            index++;
        }
        return possibleMoves;
    }

    /**
     * Given a Chess Board Object, checks whether the white king is safe from being checked by Black pieces
     * calls {@link #whiteKingSafety(int, Board)} with the white king position on the Board current Position
     *
     * @param currentPosition a Chess Board
     *
     * @return a boolean to indicate whether the white king is safe on this board
     */
    public static boolean whiteKingSafety(Board currentPosition){
        return whiteKingSafety(BitMasks.getIndexFromBitboard(currentPosition.WK),currentPosition);
    }
    /**
     * Given a Chess Board Object and a position of white king, checks whether the white king is safe from being checked by Black pieces
     *
     * @param currentPosition a Chess Board
     * @param kingIndex an index representing a position of white king
     *
     * @return a boolean to indicate whether the white king is safe
     */
    public static boolean whiteKingSafety(int kingIndex, Board currentPosition){
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

        long kingPosition = 1L << kingIndex;

        // check if can be attacked by pawn
        /*
         *  p*p
         *  *K*
         */
        long capturedByLeft = kingPosition >> 9 & ~(BitMasks.FILE_H) & BP;
        if(capturedByLeft != 0L){ return false; }

        long capturedByRight = kingPosition >> 7 & ~(BitMasks.FILE_A) & BP;
        if(capturedByRight != 0L){ return false; }

        // check if can be attacked by knight
        /*
         *  * 7 * 0 *
         *  6 * * * 1
         *  * * K * *
         *  5 * * * 2
         *  * 4 * 3 *
         */
        long position0 = kingPosition >>> 15 & ~(BitMasks.FILE_A) & BN;
        if(position0 != 0L){ return false; }

        long position1 = kingPosition >>> 6 & ~(BitMasks.FILE_AB) & BN;
        if(position1 != 0L){ return false; }

        long position2 =kingPosition << 10 & ~(BitMasks.FILE_AB) & BN;
        if(position2 != 0L){ return false; }

        long position3 =kingPosition << 17 & ~(BitMasks.FILE_A) & BN;
        if(position3 != 0L){ return false; }

        long position4 =kingPosition << 15 & ~(BitMasks.FILE_H) & BN;
        if(position4 != 0L){ return false; }

        long position5 =kingPosition << 6 & ~(BitMasks.FILE_GH) & BN;
        if(position5 != 0L){ return false; }

        long position6 =kingPosition >>> 10 & ~(BitMasks.FILE_GH) & BN;
        if(position6 != 0L){ return false; }

        long position7 =kingPosition >>> 17 & ~(BitMasks.FILE_H) & BN;
        if(position7 != 0L){ return false; }

        // Sliding
        long kingPositionReversed = BitMasks.reverse64bits(kingPosition);
        final long ALL_PIECES = BP | BN | BB | BR | BQ | BK | WP | WN | WB | WR | WQ | kingPosition ;

        // todo: possible improvement, since module approach is used, we may optimise so that the "reverse64bit" is called less/ and or later.

        // search Vertically / in the File
        long fileMask = BitMasks.FILE[kingIndex];

        long occupiedFile = ALL_PIECES & fileMask;
        long occupiedFileReversed = BitMasks.reverse64bits(occupiedFile);
        long verticalMoves = ((occupiedFile - (2 * kingPosition)) ^ BitMasks.reverse64bits(occupiedFileReversed - 2 * kingPositionReversed)) & fileMask;

        long attackByVertical = verticalMoves & BR | verticalMoves & BQ;
        if(attackByVertical != 0L){ return false; }

        // search Horizontally / in the Rank
        long rankMask = BitMasks.RANK[kingIndex];

        long occupiedRank = ALL_PIECES & rankMask;
        long occupiedRankReversed = BitMasks.reverse64bits(occupiedRank);
        long horizontalMoves = ((occupiedRank - (2 * kingPosition)) ^ BitMasks.reverse64bits(occupiedRankReversed - 2 * kingPositionReversed)) & rankMask;

        long attackByHorizontal = horizontalMoves & BR | horizontalMoves & BQ;
        if(attackByHorizontal != 0L){ return false; }

        // search Diagonally
        long diagonalMask = BitMasks.DIAG[kingIndex];

        long occupiedDiagonal = ALL_PIECES & diagonalMask;
        long occupiedDiagonalReversed = BitMasks.reverse64bits(occupiedDiagonal);
        long diagonalMoves = ((occupiedDiagonal - (2 * kingPosition)) ^ BitMasks.reverse64bits(occupiedDiagonalReversed - 2 * kingPositionReversed)) & diagonalMask;

        long attackByDiagonal = diagonalMoves & BB | diagonalMoves & BQ;
        if(attackByDiagonal != 0L){ return false; }

        // search AntiDiagonally
        long antiDiagonalMask = BitMasks.ANTIDIAG[kingIndex];

        long occupiedAntiDiagonal = ALL_PIECES & antiDiagonalMask;
        long occupiedAntiDiagonalReversed = BitMasks.reverse64bits(occupiedAntiDiagonal);
        long antiDiagonalMoves = ((occupiedAntiDiagonal - (2 * kingPosition)) ^ BitMasks.reverse64bits(occupiedAntiDiagonalReversed - 2 * kingPositionReversed)) & antiDiagonalMask;

        long attackByAntiDiagonal = antiDiagonalMoves & BB | antiDiagonalMoves & BQ;
        if(attackByAntiDiagonal != 0L){ return false; }

        return true;
    }
    /**
     * Given a Chess Board Object, checks whether the black king is safe from being checked by White pieces
     * calls {@link #whiteKingSafety(int, Board)} with the black king position on the Board current Position
     *
     * @param currentPosition a Chess Board
     *
     * @return a boolean to indicate whether the black king is safe on this board
     */
    public static boolean blackKingSafety(Board currentPosition){
        return blackKingSafety(BitMasks.getIndexFromBitboard(currentPosition.BK),currentPosition);
    }
    /**
     * Given a Chess Board Object and a position of black king, checks whether the black king is safe from being checked by White pieces
     *
     * @param currentPosition a Chess Board
     * @param kingIndex an index representing a position of black king
     *
     * @return a boolean to indicate whether the black king is safe
     */
    public static boolean blackKingSafety(int kingIndex, Board currentPosition){

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

        long kingPosition = 1L << kingIndex;

        // check if can be attacked by pawn
        /*
         *  p*p
         *  *K*
         */
        long capturedByLeft = kingPosition << 7 & ~(BitMasks.FILE_H) & WP;
        if(capturedByLeft != 0L){ return false; }


        long capturedByRight = kingPosition << 9 & ~(BitMasks.FILE_A) & WP;
        if(capturedByRight != 0L){ return false; }

        // check if can be attacked by knight
        /*
         *  * 7 * 0 *
         *  6 * * * 1
         *  * * K * *
         *  5 * * * 2
         *  * 4 * 3 *
         */
        long position0 = kingPosition >>> 15 & ~(BitMasks.FILE_A) & WN;
        if(position0 != 0L){ return false; }

        long position1 = kingPosition >>> 6 & ~(BitMasks.FILE_AB) & WN;
        if(position1 != 0L){ return false; }

        long position2 =kingPosition << 10 & ~(BitMasks.FILE_AB) & WN;
        if(position2 != 0L){ return false; }

        long position3 =kingPosition << 17 & ~(BitMasks.FILE_A) & WN;
        if(position3 != 0L){ return false; }

        long position4 =kingPosition << 15 & ~(BitMasks.FILE_H) & WN;
        if(position4 != 0L){ return false; }

        long position5 =kingPosition << 6 & ~(BitMasks.FILE_GH) & WN;
        if(position5 != 0L){ return false; }

        long position6 =kingPosition >>> 10 & ~(BitMasks.FILE_GH) & BN;
        if(position6 != 0L){ return false; }

        long position7 =kingPosition >>> 17 & ~(BitMasks.FILE_H) & BN;
        if(position7 != 0L){ return false; }

        // Sliding
        long kingPositionReversed = BitMasks.reverse64bits(kingPosition);
        final long ALL_PIECES = BP | BN | BB | BR | BQ | BK | WP | WN | WB | WR | WQ | kingPosition ;

        // search Vertically / in the File
        long fileMask = BitMasks.FILE[kingIndex];

        long occupiedFile = ALL_PIECES & fileMask;
        long occupiedFileReversed = BitMasks.reverse64bits(occupiedFile);
        long verticalMoves = ((occupiedFile - (2 * kingPosition)) ^ BitMasks.reverse64bits(occupiedFileReversed - 2 * kingPositionReversed)) & fileMask;

        long attackByVertical = verticalMoves & WR | verticalMoves & WQ;
        if(attackByVertical != 0L){ return false; }

        // search Horizontally / in the Rank
        long rankMask = BitMasks.RANK[kingIndex];

        long occupiedRank = ALL_PIECES & rankMask;
        long occupiedRankReversed = BitMasks.reverse64bits(occupiedRank);
        long horizontalMoves = ((occupiedRank - (2 * kingPosition)) ^ BitMasks.reverse64bits(occupiedRankReversed - 2 * kingPositionReversed)) & rankMask;

        long attackByHorizontal = horizontalMoves & WR | horizontalMoves & WQ;
        if(attackByHorizontal != 0L){ return false; }

        // search Diagonally
        long diagonalMask = BitMasks.DIAG[kingIndex];

        long occupiedDiagonal = ALL_PIECES & diagonalMask;
        long occupiedDiagonalReversed = BitMasks.reverse64bits(occupiedDiagonal);
        long diagonalMoves = ((occupiedDiagonal - (2 * kingPosition)) ^ BitMasks.reverse64bits(occupiedDiagonalReversed - 2 * kingPositionReversed)) & diagonalMask;

        long attackByDiagonal = diagonalMoves & WB | diagonalMoves & WQ;
        if(attackByDiagonal != 0L){ return false; }

        // search AntiDiagonally
        long antiDiagonalMask = BitMasks.ANTIDIAG[kingIndex];

        long occupiedAntiDiagonal = ALL_PIECES & antiDiagonalMask;
        long occupiedAntiDiagonalReversed = BitMasks.reverse64bits(occupiedAntiDiagonal);
        long antiDiagonalMoves = ((occupiedAntiDiagonal - (2 * kingPosition)) ^ BitMasks.reverse64bits(occupiedAntiDiagonalReversed - 2 * kingPositionReversed)) & antiDiagonalMask;

        long attackByAntiDiagonal = antiDiagonalMoves & WB | antiDiagonalMoves & WQ;
        if(attackByAntiDiagonal != 0L){ return false; }

        return true;
    }

    // Moves Generation
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

        possibleMoves.addAll(bitboardToNotationWithOffset(moveUpOne, 8));
        possibleMoves.addAll(bitboardToNotationWithOffset(moveUpTwo, 16));
        possibleMoves.addAll(bitboardToNotationWithOffset(captureLeft, 9));
        possibleMoves.addAll(bitboardToNotationWithOffset(captureRight, 7));

        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesWN(Board currentPosition) {
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

    public static List<String> generateMovesWK(Board currentPosition) {
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

            // Castling
            // King-Side
            if(!currentPosition.whiteKingMoved() && !currentPosition.whiteHRookMoved()){
                if(whiteKingSafety(index + 1, currentPosition)){
                    if(whiteKingSafety(index + 2, currentPosition)){
                        possibleMoves.add("e1g1c");
                    }
                }
            }
            // Queen-Side
            if(!currentPosition.whiteKingMoved() && !currentPosition.whiteHRookMoved()){
                if(whiteKingSafety(index - 1, currentPosition)){
                    if(whiteKingSafety(index - 2, currentPosition)){
                        if(whiteKingSafety(index - 3, currentPosition)){
                            possibleMoves.add("e1c1c");
                        }
                    }
                }
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

            // search Diagonally
            long occupiedDiag = ALL_PIECES & diagMask;
            long occupiedDiagReversed = BitMasks.reverse64bits(occupiedDiag);
            long diagMoves = ((occupiedDiag - (2 * bishopPosition)) ^ BitMasks.reverse64bits(occupiedDiagReversed - 2 * bishopPositionReversed)) & diagMask;
            // search AntiDiagonally
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

    public static List<String> generateMovesWQ(Board currentPosition){
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

        if(WQ == 0){
            // No Queen exist exists
            return possibleMoves;
        }

        long trails = WQ;
        int index = 0;
        while(trails != 0){
            // Get next index of piece
            int shift = Long.numberOfTrailingZeros(trails);
            // Get next index of piece
            index += shift;
            // Recreate the piece location
            long queenPosition = 1L << index;
            long queenPositionReversed = BitMasks.reverse64bits(queenPosition);

            // Get the masks
            long rankMask = BitMasks.RANK[index];
            long fileMask = BitMasks.FILE[index];
            long diagonalMask = BitMasks.DIAG[index];
            long antiDiagonalMask = BitMasks.ANTIDIAG[index];

            // search Horizontally / in the Rank
            long occupiedRank = ALL_PIECES & rankMask;
            long occupiedRankReversed = BitMasks.reverse64bits(occupiedRank);
            long horizontalMoves = ((occupiedRank - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedRankReversed - 2 * queenPositionReversed)) & rankMask;
            // search Vertically / in the File
            long occupiedFile = ALL_PIECES & fileMask;
            long occupiedFileReversed = BitMasks.reverse64bits(occupiedFile);
            long verticalMoves = ((occupiedFile - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedFileReversed - 2 * queenPositionReversed)) & fileMask;
            // search Diagonally
            long occupiedDiagonal = ALL_PIECES & diagonalMask;
            long occupiedDiagonalReversed = BitMasks.reverse64bits(occupiedDiagonal);
            long diagonalMoves = ((occupiedDiagonal - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedDiagonalReversed - 2 * queenPositionReversed)) & diagonalMask;
            // search AntiDiagonally
            long occupiedAntiDiagonal = ALL_PIECES & antiDiagonalMask;
            long occupiedAntiDiagonalReversed = BitMasks.reverse64bits(occupiedAntiDiagonal);
            long antiDiagonalMoves = ((occupiedAntiDiagonal - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedAntiDiagonalReversed - 2 * queenPositionReversed)) & antiDiagonalMask;


            long moves = horizontalMoves | verticalMoves | diagonalMoves | antiDiagonalMoves;
            moves = moves & ~WHITE_PIECES;


            List<String> destinationsStr = bitboardToNotation(moves);
            String startingLocations = indexToNotation(index);
            for(var s : destinationsStr){
                possibleMoves.add(startingLocations + s);
            }
            trails = trails >>> shift; // traverse the empty space
            trails = trails >>> 1; // get onto the next index
            index++;
        }



        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesBP(long lastWP,Board currentPosition) {

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
        final long ALL_PIECES = BLACK_PIECES | WHITE_PIECES;

        List<String> possibleMoves = new ArrayList<>();

        // moves
        long moveDownOne = BP<<8 & ~(ALL_PIECES); // check for 1 step up
        long moveDownTwo = (BP&BP_INITIAL)<<16 & ~((ALL_PIECES) | ((ALL_PIECES) << 8)) ; // check for 2 steps down
        // En passant
        long enPassant = WP & (lastWP & WP_INITIAL) >> 16;
        // Attacks
        long captureLeft = BP << 7 & (WHITE_PIECES | enPassant << 8) & ~(BitMasks.FILE_A);
        long captureRight = BP << 9 & (WHITE_PIECES | enPassant << 8) & ~(BitMasks.FILE_H);


        possibleMoves.addAll(bitboardToNotationWithOffset(moveDownOne, -8));
        possibleMoves.addAll(bitboardToNotationWithOffset(moveDownTwo, -16));
        possibleMoves.addAll(bitboardToNotationWithOffset(captureLeft, -7));
        possibleMoves.addAll(bitboardToNotationWithOffset(captureRight, -9));

        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesBN(Board currentPosition) {
        // Retrieve bitmap from Board;
        long BP = currentPosition.BP;
        long BN = currentPosition.BN;
        long BB = currentPosition.BB;
        long BR = currentPosition.BR;
        long BQ = currentPosition.BQ;
        long BK = currentPosition.BK;

        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position

        List<String> possibleMoves = new ArrayList<>();

        if(BN == 0){
            // No knight exists
            return possibleMoves;
        }
        long trails = BN;
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

            moves |= (knightPosition >>> 15 & ~(BLACK_PIECES | BitMasks.FILE_A));
            moves |= (knightPosition >>> 6 & ~(BLACK_PIECES | BitMasks.FILE_AB));
            moves |= (knightPosition << 10 & ~(BLACK_PIECES | BitMasks.FILE_AB));
            moves |= (knightPosition << 17 & ~(BLACK_PIECES | BitMasks.FILE_A));
            moves |= (knightPosition << 15 & ~(BLACK_PIECES | BitMasks.FILE_H));
            moves |= (knightPosition << 6 & ~(BLACK_PIECES | BitMasks.FILE_GH));
            moves |= (knightPosition >>> 10 & ~(BLACK_PIECES | BitMasks.FILE_GH));
            moves |= (knightPosition >>> 17 & ~(BLACK_PIECES | BitMasks.FILE_H));

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

    public static List<String> generateMovesBK(Board currentPosition) {
        // Retrieve bitmap from Board;
        long BP = currentPosition.BP;
        long BN = currentPosition.BN;
        long BB = currentPosition.BB;
        long BR = currentPosition.BR;
        long BQ = currentPosition.BQ;
        long BK = currentPosition.BK;

        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position

        List<String> possibleMoves = new ArrayList<>();

        if (BK == 0) {
            // No king exists
            return possibleMoves;
        }

        long trails = BK;
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

            moves |= (kingPosition >>> 9 & ~(BLACK_PIECES | BitMasks.FILE_H));
            moves |= (kingPosition >>> 8 & ~(BLACK_PIECES));
            moves |= (kingPosition >>> 7 & ~(BLACK_PIECES | BitMasks.FILE_A));
            moves |= (kingPosition >>> 1 & ~(BLACK_PIECES | BitMasks.FILE_H));
            moves |= (kingPosition << 1 & ~(BLACK_PIECES | BitMasks.FILE_A));
            moves |= (kingPosition << 7 & ~(BLACK_PIECES | BitMasks.FILE_H));
            moves |= (kingPosition << 8 & ~(BLACK_PIECES));
            moves |= (kingPosition << 9 & ~(BLACK_PIECES | BitMasks.FILE_A));

            List<String> end = bitboardToNotation(moves);
            String start = indexToNotation(index);
            for (var s : end) {
                possibleMoves.add(start + s);
            }

            // Castling
            // King-Side
            if(!currentPosition.blackKingMoved() && !currentPosition.blackHRookMoved()){
                if(blackKingSafety(index + 1, currentPosition)){
                    if(blackKingSafety(index + 2, currentPosition)){
                        possibleMoves.add("e8g8");
                    }
                }
            }
            // Queen-Side
            if(!currentPosition.blackKingMoved() && !currentPosition.blackHRookMoved()){
                if(blackKingSafety(index - 1, currentPosition)){
                    if(blackKingSafety(index - 2, currentPosition)){
                        if(blackKingSafety(index - 3, currentPosition)){
                            possibleMoves.add("e8c8");
                        }
                    }
                }
            }

            trails = trails >>> Long.numberOfTrailingZeros(trails);
            trails = trails >>> 1;
            index++;
        }

        return possibleMoves;
    }

    public static List<String> generateMovesBR(Board currentPosition){
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
        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position

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
        if(BR == 0){
            // No rook exists
            return possibleMoves;
        }

        long trails = BR;
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
            moves = moves & ~BLACK_PIECES;

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

    public static List<String> generateMovesBB(Board currentPosition){
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
        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position

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
        if(BB == 0){
            // No bishop exists
            return possibleMoves;
        }

        long trails = BB;
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

            // search Diagonally
            long occupiedDiag = ALL_PIECES & diagMask;
            long occupiedDiagReversed = BitMasks.reverse64bits(occupiedDiag);
            long diagMoves = ((occupiedDiag - (2 * bishopPosition)) ^ BitMasks.reverse64bits(occupiedDiagReversed - 2 * bishopPositionReversed)) & diagMask;
            // search AntiDiagonally
            long occupiedAntiDiag = ALL_PIECES & antiDiagMask;
            long occupiedAntiDiagReversed = BitMasks.reverse64bits(occupiedAntiDiag);
            long antiDiagMoves = ((occupiedAntiDiag - (2 * bishopPosition)) ^ BitMasks.reverse64bits(occupiedAntiDiagReversed - 2 * bishopPositionReversed)) & antiDiagMask;

            long moves = diagMoves | antiDiagMoves;
            moves = moves & ~BLACK_PIECES;

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

    public static List<String> generateMovesBQ(Board currentPosition){
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
        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position

        List<String> possibleMoves = new ArrayList<>();

        if(BQ == 0){
            // No Queen exist exists
            return possibleMoves;
        }

        long trails = BQ;
        int index = 0;
        while(trails != 0){
            // Get next index of piece
            int shift = Long.numberOfTrailingZeros(trails);
            // Get next index of piece
            index += shift;
            // Recreate the piece location
            long queenPosition = 1L << index;
            long queenPositionReversed = BitMasks.reverse64bits(queenPosition);

            // Get the masks
            long rankMask = BitMasks.RANK[index];
            long fileMask = BitMasks.FILE[index];
            long diagonalMask = BitMasks.DIAG[index];
            long antiDiagonalMask = BitMasks.ANTIDIAG[index];

            // search Horizontally / in the Rank
            long occupiedRank = ALL_PIECES & rankMask;
            long occupiedRankReversed = BitMasks.reverse64bits(occupiedRank);
            long horizontalMoves = ((occupiedRank - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedRankReversed - 2 * queenPositionReversed)) & rankMask;
            // search Vertically / in the File
            long occupiedFile = ALL_PIECES & fileMask;
            long occupiedFileReversed = BitMasks.reverse64bits(occupiedFile);
            long verticalMoves = ((occupiedFile - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedFileReversed - 2 * queenPositionReversed)) & fileMask;
            // search Diagonally
            long occupiedDiagonal = ALL_PIECES & diagonalMask;
            long occupiedDiagonalReversed = BitMasks.reverse64bits(occupiedDiagonal);
            long diagonalMoves = ((occupiedDiagonal - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedDiagonalReversed - 2 * queenPositionReversed)) & diagonalMask;
            // search AntiDiagonally
            long occupiedAntiDiagonal = ALL_PIECES & antiDiagonalMask;
            long occupiedAntiDiagonalReversed = BitMasks.reverse64bits(occupiedAntiDiagonal);
            long antiDiagonalMoves = ((occupiedAntiDiagonal - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedAntiDiagonalReversed - 2 * queenPositionReversed)) & antiDiagonalMask;

            long moves = horizontalMoves | verticalMoves | diagonalMoves | antiDiagonalMoves;
            moves = moves & ~BLACK_PIECES;

            List<String> destinationsStr = bitboardToNotation(moves);
            String startingLocations = indexToNotation(index);
            for(var s : destinationsStr){
                possibleMoves.add(startingLocations + s);
            }
            trails = trails >>> shift; // traverse the empty space
            trails = trails >>> 1; // get onto the next index
            index++;
        }



        Collections.sort(possibleMoves);
        return possibleMoves;
    }


}
