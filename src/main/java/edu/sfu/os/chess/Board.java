package edu.sfu.os.chess;

/**
 * Represents a given board position.
 * */
public class Board {

    public long WP=0L;
    public long WN=0L;
    public long WB=0L;
    public long WR=0L;
    public long WQ=0L;
    public long WK=0L;
    public long BP=0L;
    public long BN=0L;
    public long BB=0L;
    public long BR=0L;
    public long BQ=0L;
    public long BK=0L;

    public Board(){};

    public Board(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK) {
        this.WP = WP;
        this.WN = WN;
        this.WB = WB;
        this.WR = WR;
        this.WQ = WQ;
        this.WK = WK;
        this.BP = BP;
        this.BN = BN;
        this.BB = BB;
        this.BR = BR;
        this.BQ = BQ;
        this.BK = BK;
    }
}
