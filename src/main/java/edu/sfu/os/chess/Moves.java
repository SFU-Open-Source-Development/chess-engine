package edu.sfu.os.chess;


import java.util.ArrayList;
import java.util.List;

public class Moves {

    /**
     * Given an Board, returns all possible basic for white
     *
     * @return returns all possible basic moves for white
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
     * Given an Board, returns all possible basic moves for black
     *
     * @return returns all possible basic moves for black
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
            if(offset < 0){
                possibleMoves.add(pieceMask | (pieceMask >>> (-offset)));
            }
            else{
                possibleMoves.add(pieceMask | (pieceMask << offset));
            }
            bb ^= pieceMask;
        }
        return possibleMoves;
    }

    /**
     * Given a bit board, returns a list of bitmasks with promotion flag
     *
     * @param bb a bit board that marks all possible move destinations from an original position(from offset)
     * @param offset denotes the amount of shift required for a destination index to get a piece's original position.
     *
     * @return a list of move bitmasks and promotion type
     */
    private static List<List<Long>> bitboardToBitMaskWithOffsetPromotion(long bb, int offset){

        // 0 = Knight
        // 1 = Bishop
        // 2 = Rook
        // 3 = Queen
        List<List<Long>> possibleMoves = new ArrayList<>();
        if(bb == 0){
            // Empty board
            return possibleMoves;
        }
        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            long pieceMask = 1L << index;
            for(long i = 0; i < 4; i++){
                List<Long> pair = List.of(pieceMask | (pieceMask << offset), i);
                possibleMoves.add(pair);
            }
            bb ^= pieceMask;
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

    public static long whiteKingSafety(Board currentPosition){
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

    public static long blackKingSafety(Board currentPosition){
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

    public static List<Long> generateMovesWP(Board currentPosition){

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

        //final long WP_INITIAL = BitMasks.RANK_1 >>> 8; // white's initial pawn position
        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position
        final long ALL_PIECES = WHITE_PIECES | BLACK_PIECES;

        List<Long> possibleMoves = new ArrayList<>();

        // moves
        long moveUpOne = WP >>> 8 & ~(ALL_PIECES) & ~(BitMasks.RANK_8); // check for 1 step up, remove pieces that hit promotion
        long moveUpTwo = (moveUpOne & BitMasks.RANK_1 >>> 16) >>> 8 & ~(ALL_PIECES);
        // Attacks
        long captureLeft = WP >>> 9 & BLACK_PIECES & ~(BitMasks.FILE_H) & ~(BitMasks.RANK_8);
        long captureRight = WP >>> 7 & BLACK_PIECES & ~(BitMasks.FILE_A) & ~(BitMasks.RANK_8);

        possibleMoves.addAll(bitboardToBitMaskWithOffset(moveUpOne, 8));
        possibleMoves.addAll(bitboardToBitMaskWithOffset(moveUpTwo, 16));
        possibleMoves.addAll(bitboardToBitMaskWithOffset(captureLeft, 9));
        possibleMoves.addAll(bitboardToBitMaskWithOffset(captureRight, 7));

        return possibleMoves;
    }

    public static List<Long> generateMovesWN(Board currentPosition){
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

    public static List<Long> generateMovesWK(Board currentPosition){
        /// Retrieve bitmap from Board;
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

            possibleMoves.addAll(bitboardToBitMask(moves, queenPosition));
            bb ^= pieceMask;
        }
        return possibleMoves;
    }

    public static List<List<Long>> generateMovesWCastle(Board currentPosition){
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

        List<List<Long>> possibleMoves = new ArrayList<>();

        // Get mask of all unsafe squares
        long unsafeSquares = whiteKingSafety(currentPosition);

        // Castling
        // King-Side
        if((currentPosition.castleCheck & BitMasks.W_K_Castle) == 0 && (BitMasks.W_K_Castle_Block & ALL_PIECES) == 0 && (BitMasks.W_K_Castle_Inter & unsafeSquares) == 0){
            List<Long> pair = List.of(BitMasks.WK_K_Castle_Move, BitMasks.WR_K_Castle_Move);
            possibleMoves.add(pair);
        }
        // Queen-Side
        if((currentPosition.castleCheck & BitMasks.W_Q_Castle) == 0 && (BitMasks.W_Q_Castle_Block & ALL_PIECES) == 0 && (BitMasks.W_Q_Castle_Inter & unsafeSquares) == 0){
            List<Long> pair = List.of(BitMasks.WK_Q_Castle_Move, BitMasks.WR_Q_Castle_Move);
            possibleMoves.add(pair);
        }
        return possibleMoves;
    }

    public static List<List<Long>> generateMovesWEnPassant(Board currentPosition){
        // Retrieve bitmap from Board;
        long BP = currentPosition.BP;
        long WP = currentPosition.WP;

        long lastMove = currentPosition.lastMove;

        List<List<Long>> possibleMoves = new ArrayList<>();

        long enPassant = lastMove == ((BP & lastMove) | ((BP & lastMove) >>> 16)) ? (BP & lastMove) : 0L;

        if(enPassant == 0){
            return possibleMoves;
        }
        else{
            long captureLeft = WP >>> 9 & enPassant >>> 8 & ~(BitMasks.FILE_H);
            long captureRight = WP >>> 7 & enPassant >>> 8 & ~(BitMasks.FILE_A);
            if(captureLeft != 0){
                List<Long> pair = List.of(captureLeft | (captureLeft << 9), BP & lastMove);
                possibleMoves.add(pair);
            }
            if(captureRight != 0) {
                List<Long> pair = List.of(captureRight | (captureRight << 7), BP & lastMove);
                possibleMoves.add(pair);
            }
        }

        return possibleMoves;
    }

    public static List<List<Long>> generateMovesWPromotion(Board currentPosition){

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

        List<List<Long>> possibleMoves = new ArrayList<>();

        // moves
        long moveUpOne = WP >>> 8 & ~(ALL_PIECES) & BitMasks.RANK_8; // check for 1 step up
        // Attacks
        long captureLeft = WP >>> 9 & BLACK_PIECES & ~(BitMasks.FILE_H) & BitMasks.RANK_8;
        long captureRight = WP >>> 7 & BLACK_PIECES & ~(BitMasks.FILE_A) & BitMasks.RANK_8;

        possibleMoves.addAll(bitboardToBitMaskWithOffsetPromotion(moveUpOne, 8));
        possibleMoves.addAll(bitboardToBitMaskWithOffsetPromotion(captureLeft, 9));
        possibleMoves.addAll(bitboardToBitMaskWithOffsetPromotion(captureRight, 7));

        return possibleMoves;
    }

    public static List<Long> generateMovesBP(Board currentPosition){

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

        //final long BP_INITIAL = BitMasks.RANK_8 << 8; // Black's initial pawn position
        final long BLACK_PIECES = BP | BN | BB | BR | BQ | BK; // Black's current pieces position
        final long WHITE_PIECES = WP | WN | WB | WR | WQ | WK; // White's current pieces position
        final long ALL_PIECES = BLACK_PIECES | WHITE_PIECES;

        List<Long> possibleMoves = new ArrayList<>();

        // moves
        long moveDownOne = BP << 8 & ~(ALL_PIECES) & ~(BitMasks.RANK_1); // check for 1 step up, remove pieces that hit promotion
        long moveDownTwo = (moveDownOne & BitMasks.RANK_8 << 16) << 8 & ~(ALL_PIECES);
        // Attacks
        long captureLeft = BP << 7 & WHITE_PIECES & ~(BitMasks.FILE_H) & ~(BitMasks.RANK_1);
        long captureRight = BP << 9 & WHITE_PIECES & ~(BitMasks.FILE_A) & ~(BitMasks.RANK_1);


        possibleMoves.addAll(bitboardToBitMaskWithOffset(moveDownOne, -8));
        possibleMoves.addAll(bitboardToBitMaskWithOffset(moveDownTwo, -16));
        possibleMoves.addAll(bitboardToBitMaskWithOffset(captureLeft, -7));
        possibleMoves.addAll(bitboardToBitMaskWithOffset(captureRight, -9));

        return possibleMoves;
    }

    public static List<Long> generateMovesBN(Board currentPosition){
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

    public static List<Long> generateMovesBK(Board currentPosition){
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
        long bb = BR;
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
        long bb = BB;
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

            possibleMoves.addAll(bitboardToBitMask(moves, queenPosition));
            bb ^= pieceMask;
        }
        return possibleMoves;
    }

    public static List<List<Long>> generateMovesBCastle(Board currentPosition){
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

        List<List<Long>> possibleMoves = new ArrayList<>();

        // Get mask of all unsafe squares
        long unsafeSquares = blackKingSafety(currentPosition);

        // Castling
        // King-Side
        if((currentPosition.castleCheck & BitMasks.B_K_Castle) == 0 && (BitMasks.B_K_Castle_Block & ALL_PIECES) == 0 && (BitMasks.B_K_Castle_Inter & unsafeSquares) == 0){
            List<Long> pair = List.of(BitMasks.BK_K_Castle_Move, BitMasks.BR_K_Castle_Move);
            possibleMoves.add(pair);
        }
        // Queen-Side
        if((currentPosition.castleCheck & BitMasks.B_Q_Castle) == 0 && (BitMasks.B_Q_Castle_Block & ALL_PIECES) == 0 && (BitMasks.B_Q_Castle_Inter & unsafeSquares) == 0){
            List<Long> pair = List.of(BitMasks.BK_Q_Castle_Move, BitMasks.BR_Q_Castle_Move);
            possibleMoves.add(pair);
        }
        return possibleMoves;
    }

    public static List<List<Long>> generateMovesBEnPassant(Board currentPosition){
        // Retrieve bitmap from Board;
        long BP = currentPosition.BP;
        long WP = currentPosition.WP;

        long lastMove = currentPosition.lastMove;

        List<List<Long>> possibleMoves = new ArrayList<>();

        long enPassant = lastMove == ((WP & lastMove) | ((WP & lastMove) << 16)) ? (WP & lastMove) : 0L;

        if(enPassant == 0){
            return possibleMoves;
        }
        else{
            long captureLeft = BP << 7 & enPassant << 8 & ~(BitMasks.FILE_H);
            long captureRight = BP << 9 & enPassant << 8 & ~(BitMasks.FILE_A);
            if(captureLeft != 0){
                List<Long> pair = List.of(captureLeft | (captureLeft >>> 7), WP & lastMove);
                possibleMoves.add(pair);
            }
            if(captureRight != 0) {
                List<Long> pair = List.of(captureRight | (captureRight >>> 9), WP & lastMove);
                possibleMoves.add(pair);
            }
        }

        return possibleMoves;
    }

    public static List<List<Long>> generateMovesBPromotion(Board currentPosition){

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

        List<List<Long>> possibleMoves = new ArrayList<>();

        // moves
        long moveDownOne = BP << 8 & ~(ALL_PIECES) & BitMasks.RANK_1; // check for 1 step up
        // Attacks
        long captureLeft = BP << 7 & WHITE_PIECES & ~(BitMasks.FILE_H) & BitMasks.RANK_1;
        long captureRight = BP << 9 & WHITE_PIECES & ~(BitMasks.FILE_A) & BitMasks.RANK_1;

        possibleMoves.addAll(bitboardToBitMaskWithOffsetPromotion(moveDownOne, -8));
        possibleMoves.addAll(bitboardToBitMaskWithOffsetPromotion(captureLeft, -7));
        possibleMoves.addAll(bitboardToBitMaskWithOffsetPromotion(captureRight, -9));

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
        newBoard.castleCheck |= moveMask;
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
        newBoard.castleCheck |= moveMask;
        return newBoard;
    }

    public static Board castleWhite(Board currentPosition, List<Long> moveMask){
        Board newBoard = new Board(currentPosition);
        long kingMove = moveMask.get(0);
        long rookMove = moveMask.get(1);
        newBoard.WK ^= kingMove;
        newBoard.WR ^= rookMove;
        newBoard.lastMove = kingMove;
        newBoard.castleCheck |= kingMove;
        return newBoard;
    }

    public static Board castleBlack(Board currentPosition, List<Long> moveMask){
        Board newBoard = new Board(currentPosition);
        long kingMove = moveMask.get(0);
        long rookMove = moveMask.get(1);
        newBoard.BK ^= kingMove;
        newBoard.BR ^= rookMove;
        newBoard.lastMove = kingMove;
        newBoard.castleCheck |= kingMove;
        return newBoard;
    }

    public static Board enPassantWhite(Board currentPosition, List<Long> moveMask){
        Board newBoard = new Board(currentPosition);
        long move = moveMask.get(0);
        long capture = moveMask.get(1);
        newBoard.WP ^= move;
        newBoard.BP &= ~capture;
        newBoard.lastMove = move;
        newBoard.castleCheck |= move;
        return newBoard;
    }

    public static Board enPassantBlack(Board currentPosition, List<Long> moveMask){
        Board newBoard = new Board(currentPosition);
        long move = moveMask.get(0);
        long capture = moveMask.get(1);
        newBoard.BP ^= move;
        newBoard.WP &= ~capture;
        newBoard.lastMove = move;
        newBoard.castleCheck |= move;
        return newBoard;
    }

    public static Board promotionWhite(Board currentPosition, List<Long> moveMask){
        Board newBoard = new Board(currentPosition);
        long move = moveMask.get(0);
        long promotionType = moveMask.get(1);
        long promotionMask = move & BitMasks.RANK_8;
        newBoard.WP &= ~move;
        newBoard.BN &= ~move;
        newBoard.BB &= ~move;
        newBoard.BR &= ~move;
        newBoard.BQ &= ~move;
        newBoard.BK &= ~move;
        // 0 = Knight
        // 1 = Bishop
        // 2 = Rook
        // 3 = Queen
        switch ((int) promotionType) {
            case 0 -> newBoard.WN |= promotionMask;
            case 1 -> newBoard.WB |= promotionMask;
            case 2 -> newBoard.WR |= promotionMask;
            case 3 -> newBoard.WQ |= promotionMask;
        }
        newBoard.lastMove = move;
        newBoard.castleCheck |= move;
        return newBoard;
    }

    public static Board promotionBlack(Board currentPosition, List<Long> moveMask){
        Board newBoard = new Board(currentPosition);
        long move = moveMask.get(0);
        long promotionType = moveMask.get(1);
        long promotionMask = move & BitMasks.RANK_1;
        newBoard.BP &= ~move;
        newBoard.WN &= ~move;
        newBoard.WB &= ~move;
        newBoard.WR &= ~move;
        newBoard.WQ &= ~move;
        newBoard.WK &= ~move;
        // 0 = Knight
        // 1 = Bishop
        // 2 = Rook
        // 3 = Queen
        switch ((int) promotionType) {
            case 0 -> newBoard.BN |= promotionMask;
            case 1 -> newBoard.BB |= promotionMask;
            case 2 -> newBoard.BR |= promotionMask;
            case 3 -> newBoard.BQ |= promotionMask;
        }
        newBoard.lastMove = move;
        newBoard.castleCheck |= move;
        return newBoard;
    }

    private static List<Integer> pieceIndex(long bb){

        List<Integer> indexList = new ArrayList<>();

        while(bb != 0){
            // Get index of next piece
            int index = Long.numberOfTrailingZeros(bb);
            // Remove from bitboard
            bb ^= 1L << index;
            indexList.add(index);
        }

        return indexList;
    }

    private static long hq(final long pieceMask, final long pieceMaskReversed, final long mask, final long ALL_PIECES) {

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

        long occupiedMask = ALL_PIECES & mask;
        long occupiedMaskReversed = BitMasks.reverse64bits(occupiedMask);
        return ((occupiedMask - (2 * pieceMask)) ^ BitMasks.reverse64bits(occupiedMaskReversed- 2 * pieceMaskReversed) & mask);

    }

    public static List<Long> generateMovesN(final long PLAYER, final long PLAYER_PIECES){

        List<Long> possibleMoves = new ArrayList<>();

        pieceIndex(PLAYER).forEach(i -> {
            long pieceMask = 1L << i;
            long moves = 0L;

            /*  Ordering of the knight moves
             *  * 8 * 0 *
             *  7 * * * 1
             *  * * N * *
             *  6 * * * 2
             *  * 5 * 4 *
             */
            moves |= (pieceMask >>> 15 & ~(PLAYER_PIECES | BitMasks.FILE_A));
            moves |= (pieceMask >>> 15 & ~(PLAYER_PIECES | BitMasks.FILE_A));
            moves |= (pieceMask >>> 6 & ~(PLAYER_PIECES | BitMasks.FILE_AB));
            moves |= (pieceMask << 10 & ~(PLAYER_PIECES | BitMasks.FILE_AB));
            moves |= (pieceMask << 17 & ~(PLAYER_PIECES | BitMasks.FILE_A));
            moves |= (pieceMask << 15 & ~(PLAYER_PIECES | BitMasks.FILE_H));
            moves |= (pieceMask << 6 & ~(PLAYER_PIECES | BitMasks.FILE_GH));
            moves |= (pieceMask >>> 10 & ~(PLAYER_PIECES | BitMasks.FILE_GH));
            moves |= (pieceMask >>> 17 & ~(PLAYER_PIECES | BitMasks.FILE_H));

            possibleMoves.addAll(bitboardToBitMask(moves, pieceMask));
        });

        return possibleMoves;
    }

    public static List<Long> generateMovesK(final long PLAYER, final long PLAYER_PIECES){

        List<Long> possibleMoves = new ArrayList<>();

        pieceIndex(PLAYER).forEach(i -> {
            long pieceMask = 1L << i;
            long moves = 0L;

            /* Ordering of the king moves
             *  0 1 2
             *  3 K 4
             *  5 6 7
             */

            moves |= (pieceMask >>> 9 & ~(PLAYER_PIECES | BitMasks.FILE_H));
            moves |= (pieceMask >>> 8 & ~(PLAYER_PIECES));
            moves |= (pieceMask >>> 7 & ~(PLAYER_PIECES | BitMasks.FILE_A));
            moves |= (pieceMask >>> 1 & ~(PLAYER_PIECES | BitMasks.FILE_H));
            moves |= (pieceMask << 1 & ~(PLAYER_PIECES | BitMasks.FILE_A));
            moves |= (pieceMask << 7 & ~(PLAYER_PIECES | BitMasks.FILE_H));
            moves |= (pieceMask << 8 & ~(PLAYER_PIECES));
            moves |= (pieceMask << 9 & ~(PLAYER_PIECES | BitMasks.FILE_A));

            possibleMoves.addAll(bitboardToBitMask(moves, pieceMask));
        });

        return possibleMoves;
    }

    public static List<Long> generateMovesR(final long PLAYER, final long PLAYER_PIECES, final long OPP_PIECES){
        final long ALL_PIECES = PLAYER_PIECES | OPP_PIECES;

        List<Long> possibleMoves = new ArrayList<>();

        pieceIndex(PLAYER).forEach(i -> {
            long pieceMask = 1L << i;
            final long pieceMaskReversed = BitMasks.reverse64bits(pieceMask);

            final long mask1 = BitMasks.RANK[i];
            final long mask2 = BitMasks.FILE[i];

            // search Horizontally / in the Rank
            possibleMoves.addAll(bitboardToBitMask(hq(pieceMask, pieceMaskReversed, mask1, ALL_PIECES), pieceMask));
            // search Vertically / in the File
            possibleMoves.addAll(bitboardToBitMask(hq(pieceMask, pieceMaskReversed, mask2, ALL_PIECES), pieceMask));

        });

        return possibleMoves;
    }

    public static List<Long> generateMovesB(final long PLAYER, final long PLAYER_PIECES, final long OPP_PIECES){
        final long ALL_PIECES = PLAYER_PIECES | OPP_PIECES;

        List<Long> possibleMoves = new ArrayList<>();

        pieceIndex(PLAYER).forEach(i -> {
            long pieceMask = 1L << i;
            final long pieceMaskReversed = BitMasks.reverse64bits(pieceMask);

            final long mask1 = BitMasks.DIAG[i];
            final long mask2 = BitMasks.ANTIDIAG[i];

            // search Horizontally / in the Rank
            possibleMoves.addAll(bitboardToBitMask(hq(pieceMask, pieceMaskReversed, mask1, ALL_PIECES), pieceMask));
            // search Vertically / in the File
            possibleMoves.addAll(bitboardToBitMask(hq(pieceMask, pieceMaskReversed, mask2, ALL_PIECES), pieceMask));

        });

        return possibleMoves;
    }

    public static List<Long> generateMovesQ(final long PLAYER, final long PLAYER_PIECES, final long OPP_PIECES){
        final long ALL_PIECES = PLAYER_PIECES | OPP_PIECES;

        List<Long> possibleMoves = new ArrayList<>();

        pieceIndex(PLAYER).forEach(i -> {
            long pieceMask = 1L << i;
            final long pieceMaskReversed = BitMasks.reverse64bits(pieceMask);

            final long mask1 = BitMasks.RANK[i];
            final long mask2 = BitMasks.FILE[i];
            final long mask3 = BitMasks.DIAG[i];
            final long mask4 = BitMasks.ANTIDIAG[i];

            // search Horizontally / in the Rank
            possibleMoves.addAll(bitboardToBitMask(hq(pieceMask, pieceMaskReversed, mask1, ALL_PIECES), pieceMask));
            // search Vertically / in the File
            possibleMoves.addAll(bitboardToBitMask(hq(pieceMask, pieceMaskReversed, mask2, ALL_PIECES), pieceMask));
            // search Diagonally
            possibleMoves.addAll(bitboardToBitMask(hq(pieceMask, pieceMaskReversed, mask3, ALL_PIECES), pieceMask));
            // search AntiDiagonally
            possibleMoves.addAll(bitboardToBitMask(hq(pieceMask, pieceMaskReversed, mask4, ALL_PIECES), pieceMask));
        });

        return possibleMoves;
    }

    enum PieceType {
        KNIGHT,
        BISHOP,
        ROOK,
        QUEEN,
        KING,
    }

    public static List<Long> generateMovesGeneric(final long PLAYER, final long PLAYER_PIECES, final long OPP_PIECES, final PieceType PIECE_TYPE){
        final long ALL_PIECES = PLAYER_PIECES | OPP_PIECES;

        List<Long> possibleMoves = new ArrayList<>();
        long bb = PLAYER;
        while(bb != 0){
            // Get index of next piece
            final int index = Long.numberOfTrailingZeros(bb);
            final long pieceMask = 1L << index;
            // Remove from bitboard
            bb ^= pieceMask;
            long moves = 0L;
            switch(PIECE_TYPE){
                case KNIGHT -> {
                    moves |= (pieceMask >>> 15 & ~(PLAYER_PIECES | BitMasks.FILE_A));
                    moves |= (pieceMask >>> 15 & ~(PLAYER_PIECES | BitMasks.FILE_A));
                    moves |= (pieceMask >>> 6 & ~(PLAYER_PIECES | BitMasks.FILE_AB));
                    moves |= (pieceMask << 10 & ~(PLAYER_PIECES | BitMasks.FILE_AB));
                    moves |= (pieceMask << 17 & ~(PLAYER_PIECES | BitMasks.FILE_A));
                    moves |= (pieceMask << 15 & ~(PLAYER_PIECES | BitMasks.FILE_H));
                    moves |= (pieceMask << 6 & ~(PLAYER_PIECES | BitMasks.FILE_GH));
                    moves |= (pieceMask >>> 10 & ~(PLAYER_PIECES | BitMasks.FILE_GH));
                    moves |= (pieceMask >>> 17 & ~(PLAYER_PIECES | BitMasks.FILE_H));

                    possibleMoves.addAll(bitboardToBitMask(moves, pieceMask));
                }
                case BISHOP -> {
                    long pieceMaskReversed = BitMasks.reverse64bits(pieceMask);

                    final long mask1 = BitMasks.DIAG[index];
                    final long mask2 = BitMasks.ANTIDIAG[index];

                    final long occupiedMask1 = ALL_PIECES & mask1;
                    final long occupiedMask1Reversed = BitMasks.reverse64bits(occupiedMask1);
                    moves |= ((occupiedMask1 - (2 * pieceMask)) ^ BitMasks.reverse64bits(occupiedMask1Reversed- 2 * pieceMaskReversed) & mask1);

                    final long occupiedMask2 = ALL_PIECES & mask2;
                    final long occupiedMask2Reversed = BitMasks.reverse64bits(occupiedMask2);
                    moves |= ((occupiedMask2 - (2 * pieceMask)) ^ BitMasks.reverse64bits(occupiedMask2Reversed- 2 * pieceMaskReversed) & mask2);

                    moves = moves & ~PLAYER_PIECES;

                    // search Horizontally / in the Rank
                    possibleMoves.addAll(bitboardToBitMask(moves, pieceMask));
                }
                case ROOK -> {

                }
                case QUEEN -> {

                }
                case KING -> {

                }
            }
        }
        return possibleMoves;
    }
}
