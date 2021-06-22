package edu.sfu.os.chess;

/** FENParser is a class that parses FEN strings into bitboards.
 * Since bitboards has not been implemented, most of FENParser methods are place holder.
 */
public class FENParser {
    /** A placeholder bitboard that will be result of the parse
     * 0 Wpawn, 1 Wknight, 2 Wbishop, 3 Wrook, 4 Wqueen, 5 Wking, 6 Bpawn, 7 Bknight, 8 Bbishop,
     * 9 Brook, 10 Bqueen, 11 Bking.
     */
    private Board bitboards = new Board();
    private int halfmoveClock;
    private int fullmoveCounter;
    private String currentMove;
    private String enPassant;
    private boolean castleBlackKing;
    private boolean castleBlackQueen;
    private boolean castleWhiteKing;
    private boolean castleWhiteQueen;

    /** Constructor for FENParser, most of the work will be performed inside
     * @param fenString string following FEN
     */
    public FENParser(String fenString) {
        String[] fenElements = fenString.split(" ");

        piecePlacement(fenElements[0]);
        currentMove = fenElements[1];
        checkCastling(fenElements[2]);
        enPassant = fenElements[3];
        halfmoveClock = Integer.parseInt(fenElements[4]);
        fullmoveCounter = Integer.parseInt(fenElements[5]);
    }

    /**
     * piecePlacement method created bitboards from the parse information
     * @param board string obtain from FEN
     */
    private void piecePlacement(String board) {
        String binary;
        String[] ranks = board.split("/");
        int currentBits = 0;
        for (String s : ranks) {
            for (int i = 0; i < s.length(); i++) {
                binary = "0000000000000000000000000000000000000000000000000000000000000000";
                binary = binary.substring(currentBits+1) + "1" + binary.substring(0, currentBits);
                switch (s.charAt(i)) {
                    case '1', '2', '3', '4', '5', '6', '7', '8' -> currentBits += Character.getNumericValue(s.charAt(i)) - 1;
                    case 'P' -> bitboards.WP += convertStringToBitboard(binary);
                    case 'N' -> bitboards.WN += convertStringToBitboard(binary);
                    case 'B' -> bitboards.WB += convertStringToBitboard(binary);
                    case 'R' -> bitboards.WR += convertStringToBitboard(binary);
                    case 'Q' -> bitboards.WQ += convertStringToBitboard(binary);
                    case 'K' -> bitboards.WK += convertStringToBitboard(binary);
                    case 'p' -> bitboards.BP += convertStringToBitboard(binary);
                    case 'n' -> bitboards.BN += convertStringToBitboard(binary);
                    case 'b' -> bitboards.BB += convertStringToBitboard(binary);
                    case 'r' -> bitboards.BR += convertStringToBitboard(binary);
                    case 'q' -> bitboards.BQ += convertStringToBitboard(binary);
                    case 'k' -> bitboards.BK += convertStringToBitboard(binary);
                }
                currentBits++;
            }
        }
    }

    /** checkCastling check available Castling Rights, placeholder
     * @param castling string obtain from FEN
     */
    private void checkCastling(String castling) {
        castleBlackKing = false;
        castleBlackQueen = false;
        castleWhiteKing = false;
        castleWhiteQueen = false;
        if (castling.contains("K")) {
            castleWhiteKing = true;
        }
        if (castling.contains("Q")) {
            castleWhiteQueen = true;
        }
        if (castling.contains("k")) {
            castleBlackKing = true;
        }
        if (castling.contains("q")){
            castleBlackQueen = true;
        }
    }

    /** convertStringToBitboard convert binary string to unsigned long value
     * @param binary long value in binary string form
     * @return unsigned long value
     */
    private long convertStringToBitboard(String binary) {
        if (binary.charAt(0)=='0') {
            return Long.parseLong(binary, 2);
        } else {
            return Long.parseUnsignedLong(binary, 2);
        }
    }

    public Board getBitboards() {
        return bitboards;
    }

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public int getFullmoveCounter() {
        return fullmoveCounter;
    }

    public String getCurrentMove() {
        return currentMove;
    }

    public boolean isCastleBlackKing() {
        return castleBlackKing;
    }

    public boolean isCastleBlackQueen() {
        return castleBlackQueen;
    }

    public boolean isCastleWhiteKing() {
        return castleWhiteKing;
    }

    public boolean isCastleWhiteQueen() {
        return castleWhiteQueen;
    }

    public String getEnPassant() {
        return enPassant;
    }
}
