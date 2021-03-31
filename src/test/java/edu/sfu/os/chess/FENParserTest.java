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
        Board bitboards = parser.getBitboards();
        assertEquals(71776119061217280L,bitboards.WP);
        assertEquals(4755801206503243776L,bitboards.WN);
        assertEquals(2594073385365405696L,bitboards.WB);
        assertEquals(-9151314442816847872L,bitboards.WR);
        assertEquals(576460752303423488L, bitboards.WQ);
        assertEquals(1152921504606846976L,bitboards.WK);
        assertEquals(65280,bitboards.BP);
        assertEquals(66,bitboards.BN);
        assertEquals(36,bitboards.BB);
        assertEquals(129,bitboards.BR);
        assertEquals(8, bitboards.BQ);
        assertEquals(16,bitboards.BK);
        long[] expectedBitBoards = {71776119061217280L, 4755801206503243776L, 2594073385365405696L, -9151314442816847872L
                , 576460752303423488L, 1152921504606846976L, 65280, 66, 36, 129, 8, 16 };
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
        Board bitboards = parser.getBitboards();
        assertEquals(4513649850843136L,bitboards.WP);
        assertEquals(70368744177664L,bitboards.WN);
        assertEquals(4398046511104L,bitboards.WB);
        assertEquals(2748779069440L,bitboards.WR);
        assertEquals(288230376151711744L, bitboards.WQ);
        assertEquals(9007199254740992L,bitboards.WK);
        assertEquals(554172416,bitboards.BP);
        assertEquals(64,bitboards.BN);
        assertEquals(1,bitboards.BB);
        assertEquals(32,bitboards.BR);
        assertEquals(4096, bitboards.BQ);
        assertEquals(4,bitboards.BK);
        long[] expectedBitBoards = {4513649850843136L, 70368744177664L, 4398046511104L, 2748779069440L
                , 288230376151711744L, 9007199254740992L, 554172416, 64, 1, 32, 4096, 4 };
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
}
