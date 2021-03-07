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
    public void testCurrentMove1() {
        String fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        FENParser parser = new FENParser(fenString);
        String currentMove = parser.getCurrentMove();
        assertEquals("w",currentMove);
    }
    
    @Test
    public void testHalfmoveClock1(){
        String fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        FENParser parser = new FENParser(fenString);
        int halfmoveClock = parser.getHalfmoveClock();
        assertEquals(0,halfmoveClock);
    }

    @Test
    public void testFullmoveCounter1() {
        String fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        FENParser parser = new FENParser(fenString);
        int fullmoveCounter = parser.getFullmoveCounter();
        assertEquals(1,fullmoveCounter);
    }

    @Test
    public void testCastling1() {
        String fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        FENParser parser = new FENParser(fenString);

        boolean castleBlackKing = parser.isCastleBlackKing();
        boolean castleBlackQueen = parser.isCastleBlackQueen();
        boolean castleWhiteKing = parser.isCastleWhiteKing();
        boolean castleWhiteQueen = parser.isCastleWhiteQueen();
        assertTrue(castleBlackKing);
        assertTrue(castleBlackQueen);
        assertTrue(castleWhiteKing);
        assertTrue(castleWhiteQueen);
    }

    @Test
    public void testEnPassant1() {
        String fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        FENParser parser = new FENParser(fenString);

        String enPassant = parser.getEnPassant();
        assertEquals("-",enPassant);
    }

    @Test
    public void testBitBoard2(){
        String fenString = "b1k2rn1/4q3/3p4/p4p2/2P2P1R/PRBP2N1/4PK2/2Q5 b - f4 10 30";
        FENParser parser = new FENParser(fenString);
        long[] bitBoards = parser.getBitBoards();
        long[] expectedBitBoards = {4513649850843136L, 70368744177664L, 4398046511104L, 2748779069440L
                , 288230376151711744L, 9007199254740992L, 554172416, 64, 1, 32, 4096, 4 };
        assertArrayEquals(bitBoards,expectedBitBoards);
    }

    @Test
    public void testCurrentMove2() {
        String fenString = "b1k2rn1/4q3/3p4/p4p2/2P2P1R/PRBP2N1/4PK2/2Q5 b - f4 10 30";
        FENParser parser = new FENParser(fenString);
        String currentMove = parser.getCurrentMove();
        assertEquals("b",currentMove);
    }

    @Test
    public void testHalfmoveClock2(){
        String fenString = "b1k2rn1/4q3/3p4/p4p2/2P2P1R/PRBP2N1/4PK2/2Q5 b - f4 10 30";
        FENParser parser = new FENParser(fenString);
        int halfmoveClock = parser.getHalfmoveClock();
        assertEquals(10,halfmoveClock);
    }

    @Test
    public void testFullmoveCounter2() {
        String fenString = "b1k2rn1/4q3/3p4/p4p2/2P2P1R/PRBP2N1/4PK2/2Q5 b - f4 10 30";
        FENParser parser = new FENParser(fenString);
        int fullmoveCounter = parser.getFullmoveCounter();
        assertEquals(30,fullmoveCounter);
    }

    @Test
    public void testCastling2() {
        String fenString = "b1k2rn1/4q3/3p4/p4p2/2P2P1R/PRBP2N1/4PK2/2Q5 b - f4 10 30";
        FENParser parser = new FENParser(fenString);

        boolean castleBlackKing = parser.isCastleBlackKing();
        boolean castleBlackQueen = parser.isCastleBlackQueen();
        boolean castleWhiteKing = parser.isCastleWhiteKing();
        boolean castleWhiteQueen = parser.isCastleWhiteQueen();
        assertFalse(castleBlackKing);
        assertFalse(castleBlackQueen);
        assertFalse(castleWhiteKing);
        assertFalse(castleWhiteQueen);
    }

    @Test
    public void testEnPassant2() {
        String fenString = "b1k2rn1/4q3/3p4/p4p2/2P2P1R/PRBP2N1/4PK2/2Q5 b - f4 10 30";
        FENParser parser = new FENParser(fenString);

        String enPassant = parser.getEnPassant();
        assertEquals("f4",enPassant);
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
