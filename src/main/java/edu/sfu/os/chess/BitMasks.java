package edu.sfu.os.chess;


/**
 * Bit masks used for move generation.
 * */
public class BitMasks {
    static final long FILE_A = 72340172838076673L;
    static final long FILE_H = -9187201950435737472L;
    static final long FILE_AB = 217020518514230019L;
    static final long FILE_GH = -4557430888798830400L;
    static final long RANK_1 = -72057594037927936L;
    static final long RANK_4 = 1095216660480L;
    static final long RANK_5 = 4278190080L;
    static final long RANK_8 = 255L;
    static final long CENTRE = 103481868288L;
    static final long EXTENDED_CENTRE = 66229406269440L;

    static private long[] FILE_LOOKUP;

    public static int getIndexFromBitboard(long bitboard){
        // There might be a cheaper way:
        // we could first initialize a lookup array / hash /? that maps bitboards onto indices

        // takes in a bitboard containing 1 single bit and return's it's position on the bitboard
        int index = 0;

        while(index < 64 && (1L << index != bitboard)){
            index ++;
        }

        return index;
    }

    public static int getRankFromBitboard(long bitboard){
        // takes in a bitboard containing 1 single bit and return's it's Rank on the bitboard
        int index = getIndexFromBitboard(bitboard);
        return index / 8 + 1;
    }

    public static int getFileFromBitboard(long bitboard){
        // takes in a bitboard containing 1 single bit and return's it's File on the bitboard
        // where
        // a = 1, h = 8
        int index = getIndexFromBitboard(bitboard);
        return index % 8 + 1;
    }

}
