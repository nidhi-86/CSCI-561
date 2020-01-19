
public class Constants {

    public static final Point BLACK_BASE = new Point(0, 0);
    public static final Point WHITE_BASE = new Point(15,15);
    public static final double WINNING_VALUE = 19;
    public static final double LOSING_VALUE = -19;
    public static final int MIN_DEPTH = 3;
    public static final String OUTPUT_FILE_NAME = "output.txt";

    public static char getOpposingPlayerColor(char playerColor) {
        if(playerColor == 'W') return 'B';
        else return 'W';
    }

    public static Point getBase(char playerColor) {
        if(playerColor == 'W') {
            return WHITE_BASE;
        }
        return BLACK_BASE;
    }
}
