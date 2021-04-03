package edu.sfu.os.chess;

import java.util.List;

import static edu.sfu.os.chess.Moves.generateMovesPW;

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
        //currentPosition = BoardGeneration.initiateStandardChess();

        String[][] chessBoard ={
                {" ","n","b","q","k","b"," "," "},
                {" ","P"," ","p"," "," ","P"," "},
                {" "," "," ","R"," ","b"," "," "},
                {" ","p","b"," ","p","P"," "," "},
                {" "," ","P","P"," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P"," "," "," "," "," ","P","P"},
                {" ","N","B","Q"," ","B","N","K"}};

        System.out.println(" a  b  c  d  e  f  g  h ");
        Board currentPosition = BoardGeneration.arrayToBitboards(chessBoard);

       // List<String> arr = Moves.generateMovesWR(currentPosition);

        //System.out.println(String.join(", ", arr));
    }
}
