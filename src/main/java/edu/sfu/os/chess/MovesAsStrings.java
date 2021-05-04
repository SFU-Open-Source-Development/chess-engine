package edu.sfu.os.chess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovesAsStrings {

    /**
     * Given an Board and last position of black pawns, returns all possible moves for white
     *
     * @return returns all possible moves for white
     */
    public static List<String> generateAllWhiteMovesAsStrings(Board currentPosition){

        List<String> whiteMoves = new ArrayList<>();

        whiteMoves.addAll(generateMovesWPAsStrings(currentPosition));
        whiteMoves.addAll(generateMovesWNAsStrings(currentPosition));
        whiteMoves.addAll(generateMovesWKAsStrings(currentPosition));
        whiteMoves.addAll(generateMovesWRAsStrings(currentPosition));
        whiteMoves.addAll(generateMovesWBAsStrings(currentPosition));
        whiteMoves.addAll(generateMovesWQAsStrings(currentPosition));

        return whiteMoves;
    }
    /**
     * Given an Board and last position of white pawns, returns all possible moves for black
     *
     * @return returns all possible moves for black
     */
    public static List<String> generateAllBlackMovesAsStrings(Board currentPosition){

        List<String> blackMoves = new ArrayList<>();

        blackMoves.addAll(generateMovesBPAsStrings(currentPosition));
        blackMoves.addAll(generateMovesBNAsStrings(currentPosition));
        blackMoves.addAll(generateMovesBKAsStrings(currentPosition));
        blackMoves.addAll(generateMovesBRAsStrings(currentPosition));
        blackMoves.addAll(generateMovesBBAsStrings(currentPosition));
        blackMoves.addAll(generateMovesBQAsStrings(currentPosition));

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
    private static String indexToNotation(int index){
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
    private static List<String> indexToNotationWithOffset(int index, int offset){
        
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
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            coordinates.add(indexToNotation(index));
            bb ^= (1L << index);
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
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            possibleMoves.addAll(indexToNotationWithOffset(index, offset));
            bb ^= (1L << index);
        }
        return possibleMoves;
    }

    /**
     * Given a Chess Board Object, returns all spaces the white king cannot move to
     *
     * @param currentPosition a Chess Board
     *
     * @return a bitmask of all spaces that are invalid
     */

    private static long whiteKingSafety(Board currentPosition){
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

        // Temp variable to use for sliding pieces
        long bb;

        // WK is excluded, to allow sliding pieces to pierce through the king
        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ; // White's current pieces position
        final long ALL_PIECES = BLACK_PIECES | WHITE_PIECES;

        long unsafeSquares = 0L;

        // check if can be attacked by pawn
        /*
         *  p*p
         *  *K*
         */
        unsafeSquares |= BP << 7 & ~BitMasks.FILE_H;
        unsafeSquares |= BP << 9 & ~BitMasks.FILE_A;

        // check if can be attacked by knight
        /*  Ordering of the knight moves
         *  * 8 * 0 *
         *  7 * * * 1
         *  * * N * *
         *  6 * * * 2
         *  * 5 * 4 *
         */

        unsafeSquares |= (BN >>> 15 & ~BitMasks.FILE_A);
        unsafeSquares |= (BN >>> 6 & ~BitMasks.FILE_AB);
        unsafeSquares |= (BN << 10 & ~BitMasks.FILE_AB);
        unsafeSquares |= (BN << 17 & ~BitMasks.FILE_A);
        unsafeSquares |= (BN << 15 & ~BitMasks.FILE_H);
        unsafeSquares |= (BN << 6 & ~BitMasks.FILE_GH);
        unsafeSquares |= (BN >>> 10 & ~BitMasks.FILE_GH);
        unsafeSquares |= (BN >>> 17 & ~BitMasks.FILE_H);

        // check if can be attacked by king
        /* Ordering of the king moves
         *  0 1 2
         *  3 K 4
         *  5 6 7
         */

        unsafeSquares |= (BK >>> 9 & ~BitMasks.FILE_H);
        unsafeSquares |= (BK >>> 8);
        unsafeSquares |= (BK >>> 7 & ~BitMasks.FILE_A);
        unsafeSquares |= (BK >>> 1 & ~BitMasks.FILE_H);
        unsafeSquares |= (BK << 1 & ~BitMasks.FILE_A);
        unsafeSquares |= (BK << 7 & ~BitMasks.FILE_H);
        unsafeSquares |= (BK << 8);
        unsafeSquares |= (BK << 9 & ~BitMasks.FILE_A);

        // check if can be attacked by rook
        bb = BR;
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

            unsafeSquares |= (horizontalMoves | verticalMoves);
            bb ^= pieceMask;
        }

        // check if can be attacked by bishop
        bb = BB;
        while(bb != 0) {
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

            unsafeSquares |= (diagMoves | antiDiagMoves);
            bb ^= pieceMask;
        }

        // check if can be attacked by queen
        bb = BQ;
        while(bb != 0) {
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            // Isolate piece from bitboard
            long queenPosition = bb & pieceMask;
            long queenPositionReversed = BitMasks.reverse64bits(queenPosition);

            // Get the masks
            long rankMask = BitMasks.RANK[index];
            long fileMask = BitMasks.FILE[index];
            long diagMask = BitMasks.DIAG[index];
            long antiDiagMask = BitMasks.ANTIDIAG[index];

            // search Horizontally / in the Rank
            long occupiedRank = ALL_PIECES & rankMask;
            long occupiedRankReversed = BitMasks.reverse64bits(occupiedRank);
            long horizontalMoves = ((occupiedRank - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedRankReversed - 2 * queenPositionReversed)) & rankMask;
            // search Vertically / in the File
            long occupiedFile = ALL_PIECES & fileMask;
            long occupiedFileReversed = BitMasks.reverse64bits(occupiedFile);
            long verticalMoves = ((occupiedFile - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedFileReversed - 2 * queenPositionReversed)) & fileMask;
            // search Diagonally
            long occupiedDiag = ALL_PIECES & diagMask;
            long occupiedDiagReversed = BitMasks.reverse64bits(occupiedDiag);
            long diagMoves = ((occupiedDiag - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedDiagReversed - 2 * queenPositionReversed)) & diagMask;
            // search AntiDiagonally
            long occupiedAntiDiag = ALL_PIECES & antiDiagMask;
            long occupiedAntiDiagReversed = BitMasks.reverse64bits(occupiedAntiDiag);
            long antiDiagMoves = ((occupiedAntiDiag - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedAntiDiagReversed - 2 * queenPositionReversed)) & antiDiagMask;

            unsafeSquares |= horizontalMoves | verticalMoves | diagMoves | antiDiagMoves;
            bb ^= pieceMask;
        }
        return unsafeSquares;
    }

    /**
     * Given a Chess Board Object, returns all spaces the black king cannot move to
     *
     * @param currentPosition a Chess Board
     *
     * @return a bitmask of all spaces that are invalid
     */

    private static long blackKingSafety(Board currentPosition){
        long BP = currentPosition.BP;
        long BN = currentPosition.BN;
        long BB = currentPosition.BB;
        long BR = currentPosition.BR;
        long BQ = currentPosition.BQ;
        long WP = currentPosition.WP;
        long WN = currentPosition.WN;
        long WB = currentPosition.WB;
        long WR = currentPosition.WR;
        long WQ = currentPosition.WQ;
        long WK = currentPosition.WK;

        // Temp variable to use for sliding pieces
        long bb;

        // BK is excluded, to allow sliding pieces to pierce through the king
        final long BLACK_PIECES = BP | BN | BB | BR | BQ; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position
        final long ALL_PIECES = BLACK_PIECES | WHITE_PIECES;

        long unsafeSquares = 0L;

        // check if can be attacked by pawn
        /*
         *  p*p
         *  *K*
         */
        unsafeSquares |= WP >>> 9 & ~BitMasks.FILE_H;
        unsafeSquares |= WP >>> 7 & ~BitMasks.FILE_A;

        // check if can be attacked by knight
        /*  Ordering of the knight moves
         *  * 8 * 0 *
         *  7 * * * 1
         *  * * N * *
         *  6 * * * 2
         *  * 5 * 4 *
         */

        unsafeSquares |= (WN >>> 15 & ~BitMasks.FILE_A);
        unsafeSquares |= (WN >>> 6 & ~BitMasks.FILE_AB);
        unsafeSquares |= (WN << 10 & ~BitMasks.FILE_AB);
        unsafeSquares |= (WN << 17 & ~BitMasks.FILE_A);
        unsafeSquares |= (WN << 15 & ~BitMasks.FILE_H);
        unsafeSquares |= (WN << 6 & ~BitMasks.FILE_GH);
        unsafeSquares |= (WN >>> 10 & ~BitMasks.FILE_GH);
        unsafeSquares |= (WN >>> 17 & ~BitMasks.FILE_H);

        // check if can be attacked by king
        /* Ordering of the king moves
         *  0 1 2
         *  3 K 4
         *  5 6 7
         */

        unsafeSquares |= (WK >>> 9 & ~BitMasks.FILE_H);
        unsafeSquares |= (WK >>> 8);
        unsafeSquares |= (WK >>> 7 & ~BitMasks.FILE_A);
        unsafeSquares |= (WK >>> 1 & ~BitMasks.FILE_H);
        unsafeSquares |= (WK << 1 & ~BitMasks.FILE_A);
        unsafeSquares |= (WK << 7 & ~BitMasks.FILE_H);
        unsafeSquares |= (WK << 8);
        unsafeSquares |= (WK << 9 & ~BitMasks.FILE_A);

        // check if can be attacked by rook
        bb = WR;
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

            unsafeSquares |= (horizontalMoves | verticalMoves);
            bb ^= pieceMask;
        }

        // check if can be attacked by bishop
        bb = WB;
        while(bb != 0) {
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

            unsafeSquares |= (diagMoves | antiDiagMoves);
            bb ^= pieceMask;
        }

        // check if can be attacked by queen
        bb = WQ;
        while(bb != 0) {
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            // Isolate piece from bitboard
            long queenPosition = bb & pieceMask;
            long queenPositionReversed = BitMasks.reverse64bits(queenPosition);

            // Get the masks
            long rankMask = BitMasks.RANK[index];
            long fileMask = BitMasks.FILE[index];
            long diagMask = BitMasks.DIAG[index];
            long antiDiagMask = BitMasks.ANTIDIAG[index];

            // search Horizontally / in the Rank
            long occupiedRank = ALL_PIECES & rankMask;
            long occupiedRankReversed = BitMasks.reverse64bits(occupiedRank);
            long horizontalMoves = ((occupiedRank - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedRankReversed - 2 * queenPositionReversed)) & rankMask;
            // search Vertically / in the File
            long occupiedFile = ALL_PIECES & fileMask;
            long occupiedFileReversed = BitMasks.reverse64bits(occupiedFile);
            long verticalMoves = ((occupiedFile - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedFileReversed - 2 * queenPositionReversed)) & fileMask;
            // search Diagonally
            long occupiedDiag = ALL_PIECES & diagMask;
            long occupiedDiagReversed = BitMasks.reverse64bits(occupiedDiag);
            long diagMoves = ((occupiedDiag - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedDiagReversed - 2 * queenPositionReversed)) & diagMask;
            // search AntiDiagonally
            long occupiedAntiDiag = ALL_PIECES & antiDiagMask;
            long occupiedAntiDiagReversed = BitMasks.reverse64bits(occupiedAntiDiag);
            long antiDiagMoves = ((occupiedAntiDiag - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedAntiDiagReversed - 2 * queenPositionReversed)) & antiDiagMask;

            unsafeSquares |= horizontalMoves | verticalMoves | diagMoves | antiDiagMoves;
            bb ^= pieceMask;
        }
        return unsafeSquares;
    }

    // Moves Generation
    public static List<String> generateMovesWPAsStrings(Board currentPosition){

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

        final long WP_INITIAL = BitMasks.RANK_1 >>> 8; // White's initial pawn position
        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position
        final long ALL_PIECES = WHITE_PIECES | BLACK_PIECES;

        List<String> possibleMoves = new ArrayList<>();

        // moves
        long moveUpOne = WP >>> 8 & ~(ALL_PIECES); // check for 1 step up
        long moveUpTwo = (WP&WP_INITIAL) >>> 16 & ~((ALL_PIECES) | ((ALL_PIECES) << 8) ) ; // check for 2 steps up
        // En passant
        long enPassant = lastMove == ((BP & lastMove) | ((BP & lastMove) >>> 16)) ? (BP & lastMove) : 0L;
        // Attacks
        long captureLeft = WP >>> 9 & (BLACK_PIECES | enPassant >>> 8) & ~(BitMasks.FILE_H);
        long captureRight = WP >>> 7 & (BLACK_PIECES | enPassant >>> 8) & ~(BitMasks.FILE_A);

        possibleMoves.addAll(bitboardToNotationWithOffset(moveUpOne, 8));
        possibleMoves.addAll(bitboardToNotationWithOffset(moveUpTwo, 16));
        possibleMoves.addAll(bitboardToNotationWithOffset(captureLeft, 9));
        possibleMoves.addAll(bitboardToNotationWithOffset(captureRight, 7));

        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesWNAsStrings(Board currentPosition){
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
            bb ^= pieceMask;
        }
        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesWKAsStrings(Board currentPosition){
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

        List<String> possibleMoves = new ArrayList<>();

        if (WK == 0) {
            // No king exists
            return possibleMoves;
        }

        // Get mask of all unsafe squares
        long unsafeSquares = whiteKingSafety(currentPosition);

        long bb = WK;
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            // Isolate piece from bitboard
            long kingPosition = bb & pieceMask;
            // Bitmask of all possible moves
            long moves = 0L;
            /* Ordering of the king moves
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

            moves &= ~unsafeSquares;

            List<String> end = bitboardToNotation(moves);
            String start = indexToNotation(index);
            for (var s : end) {
                possibleMoves.add(start + s);
            }
            bb ^= pieceMask;
        }

        // Castling
        // King-Side
        if((currentPosition.castleCheck & BitMasks.W_K_Castle) == 0 && (BitMasks.W_K_Castle_Inter & (unsafeSquares | (ALL_PIECES ^ WK))) == 0){
            possibleMoves.add("e1g1c");
        }
        // Queen-Side
        if((currentPosition.castleCheck & BitMasks.W_Q_Castle) == 0 && (BitMasks.W_Q_Castle_Inter & (unsafeSquares | (ALL_PIECES ^ WK))) == 0){
            possibleMoves.add("e1c1c");
        }
        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesWRAsStrings(Board currentPosition){
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

            List<String> end = bitboardToNotation(moves);
            String start = indexToNotation(index);
            for(var s : end){
                possibleMoves.add(start + s);
            }
            bb ^= pieceMask;
        }
        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesWBAsStrings(Board currentPosition){
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

            List<String> end = bitboardToNotation(moves);
            String start = indexToNotation(index);
            for(var s : end){
                possibleMoves.add(start + s);
            }
            bb ^= pieceMask;
        }
        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesWQAsStrings(Board currentPosition){
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

        List<String> possibleMoves = new ArrayList<>();

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
            long diagMask = BitMasks.DIAG[index];
            long antiDiagMask = BitMasks.ANTIDIAG[index];

            // search Horizontally / in the Rank
            long occupiedRank = ALL_PIECES & rankMask;
            long occupiedRankReversed = BitMasks.reverse64bits(occupiedRank);
            long horizontalMoves = ((occupiedRank - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedRankReversed - 2 * queenPositionReversed)) & rankMask;
            // search Vertically / in the File
            long occupiedFile = ALL_PIECES & fileMask;
            long occupiedFileReversed = BitMasks.reverse64bits(occupiedFile);
            long verticalMoves = ((occupiedFile - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedFileReversed - 2 * queenPositionReversed)) & fileMask;
            // search Diagonally
            long occupiedDiag = ALL_PIECES & diagMask;
            long occupiedDiagReversed = BitMasks.reverse64bits(occupiedDiag);
            long diagMoves = ((occupiedDiag - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedDiagReversed - 2 * queenPositionReversed)) & diagMask;
            // search AntiDiagonally
            long occupiedAntiDiag = ALL_PIECES & antiDiagMask;
            long occupiedAntiDiagReversed = BitMasks.reverse64bits(occupiedAntiDiag);
            long antiDiagMoves = ((occupiedAntiDiag - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedAntiDiagReversed - 2 * queenPositionReversed)) & antiDiagMask;

            // Bitmask of all possible moves
            long moves = horizontalMoves | verticalMoves | diagMoves | antiDiagMoves;
            moves = moves & ~WHITE_PIECES;

            List<String> destinationsStr = bitboardToNotation(moves);
            String startingLocations = indexToNotation(index);
            for(var s : destinationsStr){
                possibleMoves.add(startingLocations + s);
            }
            bb ^= pieceMask;
        }
        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesBPAsStrings(Board currentPosition){

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
        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position
        final long ALL_PIECES = BLACK_PIECES | WHITE_PIECES;

        List<String> possibleMoves = new ArrayList<>();

        // moves
        long moveDownOne = BP << 8 & ~(ALL_PIECES); // check for 1 step up
        long moveDownTwo = (BP&BP_INITIAL) << 16 & ~((ALL_PIECES) | ((ALL_PIECES) << 8)) ; // check for 2 steps down
        // En passant
        long enPassant = lastMove == ((WP & lastMove) | ((WP & lastMove) << 16)) ? (WP & lastMove) : 0L;
        // Attacks
        long captureLeft = BP << 7 & (WHITE_PIECES | enPassant << 8) & ~(BitMasks.FILE_H);
        long captureRight = BP << 9 & (WHITE_PIECES | enPassant << 8) & ~(BitMasks.FILE_A);


        possibleMoves.addAll(bitboardToNotationWithOffset(moveDownOne, -8));
        possibleMoves.addAll(bitboardToNotationWithOffset(moveDownTwo, -16));
        possibleMoves.addAll(bitboardToNotationWithOffset(captureLeft, -7));
        possibleMoves.addAll(bitboardToNotationWithOffset(captureRight, -9));

        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesBNAsStrings(Board currentPosition){
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

            List<String> end = bitboardToNotation(moves);
            String start = indexToNotation(index);
            for(var s : end){
                possibleMoves.add(start + s);
            }
            bb ^= pieceMask;
        }
        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesBKAsStrings(Board currentPosition){
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

        List<String> possibleMoves = new ArrayList<>();

        if (BK == 0) {
            // No king exists
            return possibleMoves;
        }

        // Get mask of all unsafe squares
        long unsafeSquares = blackKingSafety(currentPosition);

        long bb = BK;
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            // Isolate piece from bitboard
            long kingPosition = bb & pieceMask;
            // Bitmask of all possible moves
            long moves = 0L;
            /* Ordering of the king moves
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

            moves &= ~unsafeSquares;

            List<String> end = bitboardToNotation(moves);
            String start = indexToNotation(index);
            for (var s : end) {
                possibleMoves.add(start + s);
            }
            bb ^= pieceMask;
        }

        // Castling
        // King-Side
        if((currentPosition.castleCheck & BitMasks.B_K_Castle) == 0 && (BitMasks.B_K_Castle_Inter & (unsafeSquares | (ALL_PIECES ^ BK))) == 0){
            possibleMoves.add("e8g8c");
        }
        // Queen-Side
        if((currentPosition.castleCheck & BitMasks.B_Q_Castle) == 0 && (BitMasks.B_Q_Castle_Inter & (unsafeSquares | (ALL_PIECES ^ BK))) == 0){
            possibleMoves.add("e8c8c");
        }

        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesBRAsStrings(Board currentPosition){
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

            List<String> end = bitboardToNotation(moves);
            String start = indexToNotation(index);
            for(var s : end){
                possibleMoves.add(start + s);
            }
            bb ^= pieceMask;
        }
        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesBBAsStrings(Board currentPosition){
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

            List<String> end = bitboardToNotation(moves);
            String start = indexToNotation(index);
            for(var s : end){
                possibleMoves.add(start + s);
            }
            bb ^= pieceMask;
        }
        Collections.sort(possibleMoves);
        return possibleMoves;
    }

    public static List<String> generateMovesBQAsStrings(Board currentPosition){
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

        List<String> possibleMoves = new ArrayList<>();

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
            long diagMask = BitMasks.DIAG[index];
            long antiDiagMask = BitMasks.ANTIDIAG[index];

            // search Horizontally / in the Rank
            long occupiedRank = ALL_PIECES & rankMask;
            long occupiedRankReversed = BitMasks.reverse64bits(occupiedRank);
            long horizontalMoves = ((occupiedRank - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedRankReversed - 2 * queenPositionReversed)) & rankMask;
            // search Vertically / in the File
            long occupiedFile = ALL_PIECES & fileMask;
            long occupiedFileReversed = BitMasks.reverse64bits(occupiedFile);
            long verticalMoves = ((occupiedFile - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedFileReversed - 2 * queenPositionReversed)) & fileMask;
            // search Diagonally
            long occupiedDiag = ALL_PIECES & diagMask;
            long occupiedDiagReversed = BitMasks.reverse64bits(occupiedDiag);
            long diagMoves = ((occupiedDiag - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedDiagReversed - 2 * queenPositionReversed)) & diagMask;
            // search AntiDiagonally
            long occupiedAntiDiag = ALL_PIECES & antiDiagMask;
            long occupiedAntiDiagReversed = BitMasks.reverse64bits(occupiedAntiDiag);
            long antiDiagMoves = ((occupiedAntiDiag - (2 * queenPosition)) ^ BitMasks.reverse64bits(occupiedAntiDiagReversed - 2 * queenPositionReversed)) & antiDiagMask;

            // Bitmask of all possible moves
            long moves = horizontalMoves | verticalMoves | diagMoves | antiDiagMoves;
            moves = moves & ~BLACK_PIECES;

            List<String> destinationsStr = bitboardToNotation(moves);
            String startingLocations = indexToNotation(index);
            for(var s : destinationsStr){
                possibleMoves.add(startingLocations + s);
            }
            bb ^= pieceMask;
        }
        Collections.sort(possibleMoves);
        return possibleMoves;
    }
}
