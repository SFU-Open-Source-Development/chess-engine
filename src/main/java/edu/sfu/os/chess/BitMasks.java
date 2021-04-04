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

    /**
     * takes in a bitboard and find the first index that contains 1
     * @param bitboard
     * @return index of the first 1
     */
    public static int getIndexFromBitboard(long bitboard){
        // There might be a cheaper way:
        // we could first initialize a lookup array / hash /? that maps bitboards onto indices

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

    public static long reverse64bits(long bits){
        // uses Bit masks and swapping left and right
        // method 4 from https://aticleworld.com/5-way-to-reverse-bits-of-an-integer/

        // bit masks used:
        /*  0x00000000FFFFFFFFL: 0000000000000000000000000000000011111111111111111111111111111111
            0xFFFFFFFF00000000L: 1111111111111111111111111111111100000000000000000000000000000000

            0x0000FFFF0000FFFFL: 0000000000000000111111111111111100000000000000001111111111111111
            0xFFFF0000FFFF0000L: 1111111111111111000000000000000011111111111111110000000000000000

            0x00FF00FF00FF00FFL: 0000000011111111000000001111111100000000111111110000000011111111
            0xFF00FF00FF00FF00L: 1111111100000000111111110000000011111111000000001111111100000000

            0x0F0F0F0F0F0F0F0FL: 0000111100001111000011110000111100001111000011110000111100001111
            0xF0F0F0F0F0F0F0F0L: 1111000011110000111100001111000011110000111100001111000011110000

            0x3333333333333333L: 0011001100110011001100110011001100110011001100110011001100110011
            0xCCCCCCCCCCCCCCCCL: 1100110011001100110011001100110011001100110011001100110011001100

            0x5555555555555555L: 0101010101010101010101010101010101010101010101010101010101010101
            0xAAAAAAAAAAAAAAAAL: 1010101010101010101010101010101010101010101010101010101010101010

         */
        // each row swap bits in the two masks
        // in the end, all bits will be reversed
        //System.out.println(String.format("%64s", Long.toBinaryString(bits)).replace(" ", "0") + "(before)"); // prints "bits" in nice format
        bits = (bits & 0x00000000FFFFFFFFL) << 32 | (bits & 0xFFFFFFFF00000000L) >>> 32;
        bits = (bits & 0x0000FFFF0000FFFFL) << 16 | (bits & 0xFFFF0000FFFF0000L) >>> 16;
        bits = (bits & 0x00FF00FF00FF00FFL) << 8 | (bits & 0xFF00FF00FF00FF00L) >>> 8;
        bits = (bits & 0x0F0F0F0F0F0F0F0FL) << 4 | (bits & 0xF0F0F0F0F0F0F0F0L) >>> 4;
        bits = (bits & 0x3333333333333333L) << 2 | (bits & 0xCCCCCCCCCCCCCCCCL) >>> 2;
        bits = (bits & 0x5555555555555555L) << 1 | (bits & 0xAAAAAAAAAAAAAAAAL) >>> 1;
        //System.out.println(String.format("%64s", Long.toBinaryString(bits)).replace(" ", "0") + "(after)");
        return bits;
    }

}
