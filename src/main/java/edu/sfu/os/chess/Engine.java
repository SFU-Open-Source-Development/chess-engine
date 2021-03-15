package edu.sfu.os.chess;

/**
 * Entry point for the engine.
 *
 * @author  SFU Open Source
 * @version 1.0.0
 * @since   2020-02-14
 */
public class Engine
{
    // Represents the current position
    static long WP=0L,WN=0L,WB=0L,WR=0L,WQ=0L,WK=0L,BP=0L,BN=0L,BB=0L,BR=0L,BQ=0L,BK=0L;

    public static void main( String[] args )
    {
        BoardGeneration.initiateStandardChess();

    }
}
