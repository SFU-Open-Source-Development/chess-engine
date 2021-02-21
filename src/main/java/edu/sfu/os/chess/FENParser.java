package edu.sfu.os.chess;

/** FENParser is a class that parses FEN strings into bitboards
 * Since a lot of current feature for the bit board has not exist yet,
 * most of FENParser methods are place holder.
 */
public class FENParser {
    /** A placeholder bitboard that will be result of the parse
     * 0 white, 1 black, 2 pawn, 3 knight, 4 bishop, 5 rook, 6 queen, 7 king
     */
    private long[] bitBoards = new long[8];

    public FENParser(String fenString) {
        String[] fenElements = fenString.split(" ");

        piecePlacement(fenElements[0]);
        currentSide(fenElements[1]);
        checkCastling(fenElements[2]);
        checkEnPassant(fenElements[3]);
        int halfmoveClock = Integer.parseInt(fenElements[4]);
        int fullmoveCounter = Integer.parseInt(fenElements[5]);
    }
    private void piecePlacement(String board) {
        String binary;
        String[] ranks = board.split("/");
        int currentBits = 1;
        for (String s : ranks) {
            for (int i = 0; i < s.length(); i++) {
                binary = "0000000000000000000000000000000000000000000000000000000000000000";
                binary = binary.substring(currentBits) + '1' + binary.substring(0, currentBits - 1);
                switch (s.charAt(i)) {
                    case '1', '2', '3', '4', '5', '6', '7', '8' -> currentBits += Character.getNumericValue(s.charAt(i)) - 1;
                    case 'P' -> {
                        bitBoards[0] += convertStringToBitboard(binary);
                        bitBoards[2] += convertStringToBitboard(binary);
                    }
                    case 'N' -> {
                        bitBoards[0] += convertStringToBitboard(binary);
                        bitBoards[3] += convertStringToBitboard(binary);
                    }
                    case 'B' -> {
                        bitBoards[0] += convertStringToBitboard(binary);
                        bitBoards[4] += convertStringToBitboard(binary);
                    }
                    case 'R' -> {
                        bitBoards[0] += convertStringToBitboard(binary);
                        bitBoards[5] += convertStringToBitboard(binary);
                    }
                    case 'Q' -> {
                        bitBoards[0] += convertStringToBitboard(binary);
                        bitBoards[6] += convertStringToBitboard(binary);
                    }
                    case 'K' -> {
                        bitBoards[0] += convertStringToBitboard(binary);
                        bitBoards[7] += convertStringToBitboard(binary);
                    }
                    case 'p' -> {
                        bitBoards[1] += convertStringToBitboard(binary);
                        bitBoards[2] += convertStringToBitboard(binary);
                    }
                    case 'n' -> {
                        bitBoards[1] += convertStringToBitboard(binary);
                        bitBoards[3] += convertStringToBitboard(binary);
                    }
                    case 'b' -> {
                        bitBoards[1] += convertStringToBitboard(binary);
                        bitBoards[4] += convertStringToBitboard(binary);
                    }
                    case 'r' -> {
                        bitBoards[1] += convertStringToBitboard(binary);
                        bitBoards[5] += convertStringToBitboard(binary);
                    }
                    case 'q' -> {
                        bitBoards[1] += convertStringToBitboard(binary);
                        bitBoards[6] += convertStringToBitboard(binary);
                    }
                    case 'k' -> {
                        bitBoards[1] += convertStringToBitboard(binary);
                        bitBoards[7] += convertStringToBitboard(binary);
                    }
                }
                currentBits++;
            }
        }
    }

    private void currentSide(String side) {
        if (side.equals("w")){
            System.out.println("Current mover is white");
        }
        else {
            System.out.println("Current mover is black");
        }
    }

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

    private void checkEnPassant(String enPassant) {
        if (enPassant.equals("-")) {
            System.out.println("No en passant can happen");
        }
        else {
            System.out.println("A pawn can be captured if move into " + enPassant);
        }
    }

    private long convertStringToBitboard(String binary) {
        if (binary.charAt(0)=='0') {
            return Long.parseLong(binary, 2);
        } else {
            return Long.parseUnsignedLong(binary, 2);
        }
    }
}
