package edu.sfu.os.chess;

import org.junit.Test;

import static org.junit.Assert.*;

/**Unit test for FENParser.
 * Currently not testing side, castling and en passant due to the methods being placeholder
 */
public class FENParserTest {

    @Test
    public void testBitBoards1(){
        String fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        FENParser parser = new FENParser(fenString);
        long[] bitBoards = parser.getBitBoards();
        long[] expectedBitBoards = {71776119061217280L, 4755801206503243776L, 2594073385365405696L, -9151314442816847872L
                , 576460752303423488L, 1152921504606846976L, 65280, 66, 36, 129, 8, 16 };
        assertArrayEquals(expectedBitBoards,bitBoards);
    }
    
    @Test
    public void testHalfmoveClock(){
        String fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        FENParser parser = new FENParser(fenString);
        int halfmoveClock = parser.getHalfmoveClock();
        assertEquals(0,halfmoveClock);
    }

    @Test
    public void testFullmoveCounter() {
        String fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        FENParser parser = new FENParser(fenString);
        int fullmoveCounter = parser.getFullmoveCounter();
        assertEquals(1,fullmoveCounter);
    }

    @Test
    public void testDrawArray() {
        String fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        FENParser parser = new FENParser(fenString);
        String[] draw = parser.drawArray();
        String[] expectedDraw = {"[r, n, b, q, k, b, n, r]","[p, p, p, p, p, p, p, p]","[ ,  ,  ,  ,  ,  ,  ,  ]"
                ,"[ ,  ,  ,  ,  ,  ,  ,  ]", "[ ,  ,  ,  ,  ,  ,  ,  ]", "[ ,  ,  ,  ,  ,  ,  ,  ]"
                , "[P, P, P, P, P, P, P, P]", "[R, N, B, Q, K, B, N, R]"};
        for (int i = 0; i < 8; i++) {
            System.out.println(draw[i]);
        }
        assertArrayEquals(expectedDraw,draw);
    }
}
