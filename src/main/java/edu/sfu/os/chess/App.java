package edu.sfu.os.chess;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        FENParser parser = new FENParser("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        System.out.println( "Hello World!" );
    }
}
