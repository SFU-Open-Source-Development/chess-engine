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

    public long lastMove=0L;

    private boolean whiteKingMoved = false;
    private boolean whiteARookMoved = false;
    private boolean whiteHRookMoved = false;

    private boolean blackKingMoved = false;
    private boolean blackARookMoved = false;
    private boolean blackHRookMoved = false;

    public Board(){}

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

    public boolean whiteKingMoved() {
        return whiteKingMoved;
    }

    public boolean whiteARookMoved() {
        return whiteARookMoved;
    }

    public boolean whiteHRookMoved() {
        return whiteHRookMoved;
    }

    public boolean blackKingMoved() {
        return blackKingMoved;
    }

    public boolean blackARookMoved() {
        return blackARookMoved;
    }

    public boolean blackHRookMoved() {
        return blackHRookMoved;
    }

    public void setWhiteKingMoved() {
        this.whiteKingMoved = true;
    }

    public void setWhiteARookMoved() {
        this.whiteARookMoved = true;
    }

    public void setWhiteHRookMoved() {
        this.whiteHRookMoved = true;
    }

    public void setBlackKingMoved() {
        this.blackKingMoved = true;
    }

    public void setBlackARookMoved() {
        this.blackARookMoved = true;
    }

    public void setBlackHRookMoved() {
        this.blackHRookMoved = true;
    }
}
