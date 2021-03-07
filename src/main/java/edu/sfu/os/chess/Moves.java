package edu.sfu.os.chess;

import java.util.Arrays;
public class Moves {
    static long FILE_A = 72340172838076673L;
    static long FILE_H = -9187201950435737472L;
    static long FILE_AB = 217020518514230019L;
    static long FILE_GH = -4557430888798830400L;
    static long RANK_1 = -72057594037927936L;
    static long RANK_4 = 1095216660480L;
    static long RANK_5 = 4278190080L;
    static long RANK_8 = 255L;
    static long CENTRE = 103481868288L;
    static long EXTENDED_CENTRE = 66229406269440L;
    static long KING_SIDE = -1085102592571150096L;
    static long QUEEN_SIDE = 1085102592571150095L;
    static long KING_B7 = 460039L;
    static long KNIGHT_C6 = 43234889994L;
    static long NOT_WHITE_PIECES;
    static long BLACK_PIECES;
    static long EMPTY;

    public static String posibleMovesW(String history, long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK) {
        NOT_WHITE_PIECES = ~(WP | WN | WB | WR | WQ | WK | BK);//added BK to avoid illegal capture
        BLACK_PIECES = BP | BN | BB | BR | BQ;//omitted BK to avoid illegal capture
        EMPTY = ~(WP | WN | WB | WR | WQ | WK | BP | BN | BB | BR | BQ | BK);
        String list = posiblePW(history, WP)/*+
                posibleNW(WP,WN,WB,WR,WQ,WK,BP,BN,BB,BR,BQ,BK)+
                posibleBW(WP,WN,WB,WR,WQ,WK,BP,BN,BB,BR,BQ,BK)+
                posibleRW(WP,WN,WB,WR,WQ,WK,BP,BN,BB,BR,BQ,BK)+
                posibleQW(WP,WN,WB,WR,WQ,WK,BP,BN,BB,BR,BQ,BK)+
                posibleKW(WP,WN,WB,WR,WQ,WK,BP,BN,BB,BR,BQ,BK)*/;
        return list;
    }

    public static String posiblePW(String history, long WP) {
        String list = "";
        //x1,y1,x2,y2
        long PAWN_MOVES = (WP >> 7) & BLACK_PIECES & ~RANK_8 & ~FILE_A;//capture right
        return list;
    }

    public static void drawBitboard(long bitBoard) {
        String chessBoard[][] = new String[8][8];
        for (int i = 0; i < 64; i++) {
            chessBoard[i / 8][i % 8] = "";
        }
        for (int i = 0; i < 64; i++) {
            if (((bitBoard >>> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "P";
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