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
    public static Board currentPosition;

    public static void main( String[] args )
    {
        BitMasks.initBitMasks();
        //currentPosition = BoardGeneration.initiateStandardChess();
    }
}
