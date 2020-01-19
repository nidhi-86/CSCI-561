
import java.io.*;
import java.util.HashMap;
import java.util.Set;

public class calibrate {
    public static void main(String args[]) {
        //System.out.println("Running Calibrate.java");
        char[][] initialBoard = new char[16][16];
        Set<Point> blackCamp = CampCoordinates.getBlackCamp();
        Set<Point> whiteCamp = CampCoordinates.getWhiteCamp();
        for(int i = 0; i < 16; i++) {
            for(int j = 0; j < 16; j++) {
                Point p = new Point(i, j);
                if(blackCamp.contains(p)) {
                    initialBoard[i][j] = 'B';
                } else if(whiteCamp.contains(p)) {
                    initialBoard[i][j] = 'W';
                } else {
                    initialBoard[i][j] = '.';
                }
            }
        }
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            MinimaxWithAlphaBeta minimaxWithAlphaBeta = new MinimaxWithAlphaBeta();
            fos = new FileOutputStream("calibration.txt");
            out = new ObjectOutputStream(fos);
            int depth = 1;
            float playTime = 300.0F;
            long startTime = System.currentTimeMillis();
            while (playTime > 0) {
                //TODO: Color of player
                if(depth > 5) break;
                minimaxWithAlphaBeta.minimaxWithAlphaBeta(initialBoard, new HashMap<>(), 'W', "GAME", depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
                float timeTaken = (System.currentTimeMillis() - startTime) / 1000F;
                //System.out.println(depth + " " + timeTaken);
                CalibrateData calibrateData = new CalibrateData(depth, timeTaken);
                out.writeObject(calibrateData);
                playTime -= timeTaken;
                depth++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fos != null)
                    fos.close();
                if(out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
