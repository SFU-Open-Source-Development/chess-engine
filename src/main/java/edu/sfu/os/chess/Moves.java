package edu.sfu.os.chess;


import java.util.ArrayList;
import java.util.List;

public class Moves {

    /**
     * Given an Board and last position of black pawns, returns all possible moves for white
     *
     * @return returns all possible moves for white
     */
    public static List<Long> generateAllWhiteMoves(Board currentPosition){

        List<Long> whiteMoves = new ArrayList<>();

        whiteMoves.addAll(generateMovesWP(currentPosition));
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
    public static List<Long> generateAllBlackMoves(Board currentPosition){

        List<Long> blackMoves = new ArrayList<>();

        blackMoves.addAll(generateMovesBP(currentPosition));
        blackMoves.addAll(generateMovesBN(currentPosition));
        blackMoves.addAll(generateMovesBK(currentPosition));
        blackMoves.addAll(generateMovesBR(currentPosition));
        blackMoves.addAll(generateMovesBB(currentPosition));
        blackMoves.addAll(generateMovesBQ(currentPosition));

        return blackMoves;
    }

    /**
     * Given a bit board, returns a list of bitmasks
     *
     * @param bb a bit board that marks all possible move destinations from originalPosition
     * @param originalPosition denotes a bitmask of the start location of the piece
     *
     * @return a list of move bitmasks
     */
    private static List<Long> bitboardToBitMask(long bb, long originalPosition){

        List<Long> possibleMoves = new ArrayList<>();
        if(bb == 0){
            // Empty board
            return possibleMoves;
        }
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            possibleMoves.add(pieceMask | originalPosition);
            bb ^= pieceMask;
        }
        return possibleMoves;
    }

    /**
     * Given a bit board, returns a list of bitmasks
     *
     * @param bb a bit board that marks all possible move destinations from an original position(from offset)
     * @param offset denotes the amount of shift required for a destination index to get a piece's original position.
     *
     * @return a list of move bitmasks
     */
    private static List<Long> bitboardToBitMaskWithOffset(long bb, int offset){

        List<Long> possibleMoves = new ArrayList<>();
        if(bb == 0){
            // Empty board
            return possibleMoves;
        }
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            possibleMoves.add(pieceMask | (1L >>> offset));
            bb ^= pieceMask;
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
        long capturedByLeft = kingPosition >>> 9 & ~(BitMasks.FILE_H) & BP;
        if(capturedByLeft != 0L){ return false; }

        long capturedByRight = kingPosition >>> 7 & ~(BitMasks.FILE_A) & BP;
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
        return attackByAntiDiagonal == 0L;
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
        return attackByAntiDiagonal == 0L;
    }

    // Moves Generation
    public static List<Long> generateMovesWP(Board currentPosition) {

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

        long lastMove = currentPosition.lastMove;


        final long BP_INITIAL = BitMasks.RANK_1 >>> 48; // Black's initial pawn position
        final long WP_INITIAL = BitMasks.RANK_1 >>> 8; // white's initial pawn position
        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position
        final long ALL_PIECES = WHITE_PIECES | BLACK_PIECES;

        List<Long> possibleMoves = new ArrayList<>();

        // moves
        long moveUpOne = WP>>>8 & ~(ALL_PIECES); // check for 1 step up
        long moveUpTwo = (WP&WP_INITIAL)>>16 & ~((ALL_PIECES) | ((ALL_PIECES) << 8) ) ; // check for 2 steps up
        // En passant
        // TODO: Separate out enPassant to a different function
        long enPassant = BP & (lastMove & BP_INITIAL) << 16;
        // Attacks
        long captureLeft = WP >>> 9 & (BLACK_PIECES | enPassant >>> 8) & ~(BitMasks.FILE_H);
        long captureRight = WP >>> 7 & (BLACK_PIECES | enPassant >>> 8) & ~(BitMasks.FILE_A);

        possibleMoves.addAll(bitboardToBitMaskWithOffset(moveUpOne, 8));
        possibleMoves.addAll(bitboardToBitMaskWithOffset(moveUpTwo, 16));
        possibleMoves.addAll(bitboardToBitMaskWithOffset(captureLeft, 9));
        possibleMoves.addAll(bitboardToBitMaskWithOffset(captureRight, 7));

        return possibleMoves;
    }

    public static List<Long> generateMovesWN(Board currentPosition) {
        // Retrieve bitmap from Board;
        long WP = currentPosition.WP;
        long WN = currentPosition.WN;
        long WB = currentPosition.WB;
        long WR = currentPosition.WR;
        long WQ = currentPosition.WQ;
        long WK = currentPosition.WK;

        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position

        List<Long> possibleMoves = new ArrayList<>();

        if(WN == 0){
            // No knight exists
            return possibleMoves;
        }
        long bb = WN;
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            // Isolate piece from bitboard
            long knightPosition = bb & pieceMask;
            // Bitmask of all possible moves
            long moves = 0L;

            /*  Ordering of the knight moves
             *  * 8 * 0 *
             *  7 * * * 1
             *  * * N * *
             *  6 * * * 2
             *  * 5 * 4 *
             */
            moves |= (knightPosition >>> 15 & ~(WHITE_PIECES | BitMasks.FILE_A));
            moves |= (knightPosition >>> 15 & ~(WHITE_PIECES | BitMasks.FILE_A));
            moves |= (knightPosition >>> 6 & ~(WHITE_PIECES | BitMasks.FILE_AB));
            moves |= (knightPosition << 10 & ~(WHITE_PIECES | BitMasks.FILE_AB));
            moves |= (knightPosition << 17 & ~(WHITE_PIECES | BitMasks.FILE_A));
            moves |= (knightPosition << 15 & ~(WHITE_PIECES | BitMasks.FILE_H));
            moves |= (knightPosition << 6 & ~(WHITE_PIECES | BitMasks.FILE_GH));
            moves |= (knightPosition >>> 10 & ~(WHITE_PIECES | BitMasks.FILE_GH));
            moves |= (knightPosition >>> 17 & ~(WHITE_PIECES | BitMasks.FILE_H));

            possibleMoves.addAll(bitboardToBitMask(moves, knightPosition));
            bb ^= pieceMask;
        }
        return possibleMoves;
    }

    public static List<Long> generateMovesWK(Board currentPosition) {
        // TODO: NO CASTLING
        // Retrieve bitmap from Board;
        long WP = currentPosition.WP;
        long WN = currentPosition.WN;
        long WB = currentPosition.WB;
        long WR = currentPosition.WR;
        long WQ = currentPosition.WQ;
        long WK = currentPosition.WK;

        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position

        List<Long> possibleMoves = new ArrayList<>();

        if (WK == 0) {
            // No king exists
            return possibleMoves;
        }
        long bb = WK;
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            // Isolate piece from bitboard
            long kingPosition = bb & pieceMask;
            // Bitmask of all possible moves
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

            possibleMoves.addAll(bitboardToBitMask(moves, kingPosition));
            bb ^= pieceMask;
        }
        return possibleMoves;
    }

    public static List<Long> generateMovesWR(Board currentPosition){
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

        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position
        final long ALL_PIECES = BLACK_PIECES | WHITE_PIECES;

        List<Long> possibleMoves = new ArrayList<>();
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
        long bb = WR;
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            // Isolate piece from bitboard
            long rookPosition = bb & pieceMask;
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

            // Bitmask of all possible moves
            long moves = horizontalMoves | verticalMoves;
            moves = moves & ~WHITE_PIECES;

            possibleMoves.addAll(bitboardToBitMask(moves, rookPosition));
            bb ^= pieceMask;
        }
        return possibleMoves;
    }

    public static List<Long> generateMovesWB(Board currentPosition){
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

        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position
        final long ALL_PIECES = BLACK_PIECES | WHITE_PIECES;

        List<Long> possibleMoves = new ArrayList<>();
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
        long bb = WB;
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            // Isolate piece from bitboard
            long bishopPosition = bb & pieceMask;
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

            // Bitmask of all possible moves
            long moves = diagMoves | antiDiagMoves;
            moves = moves & ~WHITE_PIECES;

            possibleMoves.addAll(bitboardToBitMask(moves, bishopPosition));
            bb ^= pieceMask;
        }
        return possibleMoves;
    }

    public static List<Long> generateMovesWQ(Board currentPosition){
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

        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position
        final long ALL_PIECES = BLACK_PIECES | WHITE_PIECES;

        List<Long> possibleMoves = new ArrayList<>();

        if(WQ == 0){
            // No Queen exist exists
            return possibleMoves;
        }
        long bb = WQ;
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            // Isolate piece from bitboard
            long queenPosition = bb & pieceMask;
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

            // Bitmask of all possible moves
            long moves = horizontalMoves | verticalMoves | diagonalMoves | antiDiagonalMoves;
            moves = moves & ~WHITE_PIECES;

            possibleMoves.addAll(bitboardToBitMask(moves, queenPosition));
            bb ^= pieceMask;
        }
        return possibleMoves;
    }

    public static List<Long> generateMovesBP(Board currentPosition) {

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

        long lastMove = currentPosition.lastMove;


        final long BP_INITIAL = BitMasks.RANK_1 >>> 48; // Black's initial pawn position
        final long WP_INITIAL = BitMasks.RANK_1 >>> 8; // white's initial pawn position
        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position
        final long ALL_PIECES = BLACK_PIECES | WHITE_PIECES;

        List<Long> possibleMoves = new ArrayList<>();

        // moves
        long moveDownOne = BP<<8 & ~(ALL_PIECES); // check for 1 step up
        long moveDownTwo = (BP&BP_INITIAL)<<16 & ~((ALL_PIECES) | ((ALL_PIECES) << 8)) ; // check for 2 steps down
        // En passant
        // TODO: Separate out enPassant to a different function
        long enPassant = WP & (lastMove & WP_INITIAL) >>> 16;
        // Attacks
        long captureLeft = BP << 7 & (WHITE_PIECES | enPassant << 8) & ~(BitMasks.FILE_A);
        long captureRight = BP << 9 & (WHITE_PIECES | enPassant << 8) & ~(BitMasks.FILE_H);


        possibleMoves.addAll(bitboardToBitMaskWithOffset(moveDownOne, -8));
        possibleMoves.addAll(bitboardToBitMaskWithOffset(moveDownTwo, -16));
        possibleMoves.addAll(bitboardToBitMaskWithOffset(captureLeft, -7));
        possibleMoves.addAll(bitboardToBitMaskWithOffset(captureRight, -9));

        return possibleMoves;
    }

    public static List<Long> generateMovesBN(Board currentPosition) {
        // Retrieve bitmap from Board;
        long BP = currentPosition.BP;
        long BN = currentPosition.BN;
        long BB = currentPosition.BB;
        long BR = currentPosition.BR;
        long BQ = currentPosition.BQ;
        long BK = currentPosition.BK;

        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position

        List<Long> possibleMoves = new ArrayList<>();

        if(BN == 0){
            // No knight exists
            return possibleMoves;
        }
        long bb = BN;
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            // Isolate piece from bitboard
            long knightPosition = bb & pieceMask;
            // Bitmask of all possible moves
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

            possibleMoves.addAll(bitboardToBitMask(moves, knightPosition));
            bb ^= pieceMask;
        }
        return possibleMoves;
    }

    public static List<Long> generateMovesBK(Board currentPosition) {
        // TODO: NO CASTLING
        // Retrieve bitmap from Board;
        long BP = currentPosition.BP;
        long BN = currentPosition.BN;
        long BB = currentPosition.BB;
        long BR = currentPosition.BR;
        long BQ = currentPosition.BQ;
        long BK = currentPosition.BK;

        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position

        List<Long> possibleMoves = new ArrayList<>();

        if (BK == 0) {
            // No king exists
            return possibleMoves;
        }
        long bb = BK;
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            // Isolate piece from bitboard
            long kingPosition = bb & pieceMask;
            // Bitmask of all possible moves
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

            possibleMoves.addAll(bitboardToBitMask(moves, kingPosition));
            bb ^= pieceMask;
        }
        return possibleMoves;
    }

    public static List<Long> generateMovesBR(Board currentPosition){
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

        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position
        final long ALL_PIECES = BLACK_PIECES | WHITE_PIECES;

        List<Long> possibleMoves = new ArrayList<>();
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
        long bb = WR;
        while(bb != 0){
            /// Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            // Isolate piece from bitboard
            long rookPosition = bb & pieceMask;
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

            // Bitmask of all possible moves
            long moves = horizontalMoves | verticalMoves;
            moves = moves & ~BLACK_PIECES;

            possibleMoves.addAll(bitboardToBitMask(moves, rookPosition));
            bb ^= pieceMask;
        }
        return possibleMoves;
    }

    public static List<Long> generateMovesBB(Board currentPosition){
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

        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position
        final long ALL_PIECES = BLACK_PIECES | WHITE_PIECES;

        List<Long> possibleMoves = new ArrayList<>();
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
        long bb = WB;
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            // Isolate piece from bitboard
            long bishopPosition = bb & pieceMask;
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

            // Bitmask of all possible moves
            long moves = diagMoves | antiDiagMoves;
            moves = moves & ~BLACK_PIECES;

            possibleMoves.addAll(bitboardToBitMask(moves, bishopPosition));
            bb ^= pieceMask;
        }
        return possibleMoves;
    }

    public static List<Long> generateMovesBQ(Board currentPosition){
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

        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position
        final long ALL_PIECES = BLACK_PIECES | WHITE_PIECES;

        List<Long> possibleMoves = new ArrayList<>();

        if(BQ == 0){
            // No Queen exist exists
            return possibleMoves;
        }
        long bb = BQ;
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            // Isolate piece from bitboard
            long queenPosition = bb & pieceMask;
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

            // Bitmask of all possible moves
            long moves = horizontalMoves | verticalMoves | diagonalMoves | antiDiagonalMoves;
            moves = moves & ~BLACK_PIECES;

            possibleMoves.addAll(bitboardToBitMask(moves, queenPosition));
            bb ^= pieceMask;
        }
        return possibleMoves;
    }

    public static Board moveWhite(Board currentPosition, long moveMask){
        Board newBoard = new Board(currentPosition);
        if((newBoard.WP & moveMask) != 0){
            newBoard.WP ^= moveMask;
        }
        else if((newBoard.WN & moveMask) != 0){
            newBoard.WN ^= moveMask;
        }
        else if((newBoard.WB & moveMask) != 0){
            newBoard.WB ^= moveMask;
        }
        else if((newBoard.WR & moveMask) != 0){
            newBoard.WR ^= moveMask;
        }
        else if((newBoard.WQ & moveMask) != 0){
            newBoard.WQ ^= moveMask;
        }
        else{
            newBoard.WK ^= moveMask;
        }
        newBoard.BP &= ~moveMask;
        newBoard.BN &= ~moveMask;
        newBoard.BB &= ~moveMask;
        newBoard.BR &= ~moveMask;
        newBoard.BQ &= ~moveMask;
        newBoard.BK &= ~moveMask;
        newBoard.lastMove = moveMask;
        return newBoard;
    }

    public static Board moveBlack(Board currentPosition, long moveMask){
        Board newBoard = new Board(currentPosition);
        if((newBoard.BP & moveMask) != 0){
            newBoard.BP ^= moveMask;
        }
        else if((newBoard.BN & moveMask) != 0){
            newBoard.BN ^= moveMask;
        }
        else if((newBoard.BB & moveMask) != 0){
            newBoard.BB ^= moveMask;
        }
        else if((newBoard.BR & moveMask) != 0){
            newBoard.BR ^= moveMask;
        }
        else if((newBoard.BQ & moveMask) != 0){
            newBoard.BQ ^= moveMask;
        }
        else{
            newBoard.BK ^= moveMask;
        }
        newBoard.WP &= ~moveMask;
        newBoard.WN &= ~moveMask;
        newBoard.WB &= ~moveMask;
        newBoard.WR &= ~moveMask;
        newBoard.WQ &= ~moveMask;
        newBoard.WK &= ~moveMask;
        newBoard.lastMove = moveMask;
        return newBoard;
    }
}
