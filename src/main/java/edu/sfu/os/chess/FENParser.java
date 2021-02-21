package edu.sfu.os.chess;

import java.util.Arrays;

/** FENParser is a class that parses FEN strings into bitboards.
 * Since bitboards has not been implemented, most of FENParser methods are place holder.
 */
public class FENParser {
    /** A placeholder bitboard that will be result of the parse
     * 0 Wpawn, 1 Wknight, 2 Wbishop, 3 Wrook, 4 Wqueen, 5 Wking, 6 Bpawn, 7 Bknight, 8 Bbishop,
     * 9 Brook, 10 Bqueen, 11 Bking.
     */
    private long[] bitBoards = new long[12];
    private int halfmoveClock;
    private int fullmoveCounter;

    /** Constructor for FENParser, most of the work will be performed inside
     * @param fenString string following FEN
     */
    public FENParser(String fenString) {
        String[] fenElements = fenString.split(" ");

        piecePlacement(fenElements[0]);
        currentSide(fenElements[1]);
        checkCastling(fenElements[2]);
        checkEnPassant(fenElements[3]);
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
                    case 'P' -> bitBoards[0] += convertStringToBitboard(binary);
                    case 'N' -> bitBoards[1] += convertStringToBitboard(binary);
                    case 'B' -> bitBoards[2] += convertStringToBitboard(binary);
                    case 'R' -> bitBoards[3] += convertStringToBitboard(binary);
                    case 'Q' -> bitBoards[4] += convertStringToBitboard(binary);
                    case 'K' -> bitBoards[5] += convertStringToBitboard(binary);
                    case 'p' -> bitBoards[6] += convertStringToBitboard(binary);
                    case 'n' -> bitBoards[7] += convertStringToBitboard(binary);
                    case 'b' -> bitBoards[8] += convertStringToBitboard(binary);
                    case 'r' -> bitBoards[9] += convertStringToBitboard(binary);
                    case 'q' -> bitBoards[10] += convertStringToBitboard(binary);
                    case 'k' -> bitBoards[11] += convertStringToBitboard(binary);
                }
                currentBits++;
            }
        }
    }

    /** currentSide check the current playing side from FEN, placeholder
     * @param side string obtain from FEN
     */
    private void currentSide(String side) {
        if (side.equals("w")){
            System.out.println("Current mover is white");
        }
        else {
            System.out.println("Current mover is black");
        }
    }

    /** checkCastling check available Castling Rights, placeholder
     * @param castling string obtain from FEN
     */
    private void checkCastling(String castling) {
        if (castling.equals("-")) {
            System.out.println("Neither side can castle");
        }
        else {
            String info = "Castling Rights is possible for :";
            if (castling.contains("K")) {
                 info = info.concat(" white king,");
            }
            if (castling.contains("Q")) {
                info = info.concat(" white queen,");
            }
            if (castling.contains("k")) {
                info = info.concat(" black king,");
            }
            if (castling.contains("q")){
                info = info.concat(" black queen,");
            }
            info = info.substring(0,info.length() - 1);
            System.out.println(info);
        }
    }

    /** checkEnPassant check possible en passant square, placeholder
     * @param enPassant string obtain from FEN
     */
    private void checkEnPassant(String enPassant) {
        if (enPassant.equals("-")) {
            System.out.println("No en passant can happen");
        }
        else {
            System.out.println("A pawn can be captured if move into " + enPassant);
        }
    }

    /** drawArray visualizes the generated bit board. Will be removed when bit board is implemented
     */
    public String[] drawArray() {
        String[][] createdChessBoard = new String[8][8];
        String[] finalChessBoard = new String[8];
        for (int i=0;i<64;i++) {
            createdChessBoard[i/8][i%8]=" ";
        }
        for (int i=0;i<64;i++) {
            if (((bitBoards[0]>>i)&1)==1) {createdChessBoard[i/8][i%8]="P";}
            if (((bitBoards[1]>>i)&1)==1) {createdChessBoard[i/8][i%8]="N";}
            if (((bitBoards[2]>>i)&1)==1) {createdChessBoard[i/8][i%8]="B";}
            if (((bitBoards[3]>>i)&1)==1) {createdChessBoard[i/8][i%8]="R";}
            if (((bitBoards[4]>>i)&1)==1) {createdChessBoard[i/8][i%8]="Q";}
            if (((bitBoards[5]>>i)&1)==1) {createdChessBoard[i/8][i%8]="K";}
            if (((bitBoards[6]>>i)&1)==1) {createdChessBoard[i/8][i%8]="p";}
            if (((bitBoards[7]>>i)&1)==1) {createdChessBoard[i/8][i%8]="n";}
            if (((bitBoards[8]>>i)&1)==1) {createdChessBoard[i/8][i%8]="b";}
            if (((bitBoards[9]>>i)&1)==1) {createdChessBoard[i/8][i%8]="r";}
            if (((bitBoards[10]>>i)&1)==1) {createdChessBoard[i/8][i%8]="q";}
            if (((bitBoards[11]>>i)&1)==1) {createdChessBoard[i/8][i%8]="k";}
        }
        for (int i=0;i<8;i++) {
            finalChessBoard[i] =  Arrays.toString(createdChessBoard[i]);
        }
        return finalChessBoard;
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

    public long[] getBitBoards() {
        return bitBoards;
    }

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public int getFullmoveCounter() {
        return fullmoveCounter;
    }
}
