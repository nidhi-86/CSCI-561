
import java.io.*;

/**
 * @author Nidhi Chaudhary
 * Game playing agent to play Halma
 */
public class homework {
    public static void main(String[] args) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("input.txt"));
            String line = br.readLine().trim();
            // SINGLE or GAME
            String gameType = line;
            line = br.readLine().trim();
            // WHITE or BLACK
            String color = line;
            line = br.readLine().trim();
            // Play time remaining
            float playTime = Float.parseFloat(line);
            char[][] board = new char[16][16];
            for (int i = 0; i < 16; i++) {
                String[] row = br.readLine().trim().split("");
                for (int j = 0; j < row.length; j++) {
                    board[j][i] = row[j].charAt(0);
                }
            }
            char playerColor = 'B';
            if (color.equalsIgnoreCase("WHITE")) {
                playerColor = 'W';
            }
//            MasterAgent masterAgent = new MasterAgent();
//            masterAgent.compete(playTime);
//            MovesGenerator movesGenerator = new MovesGenerator();
//            movesGenerator.generateNextMove(board, playTime, playerColor, gameType);
//            System.exit(0);
            //callMinimax(board, playTime, playerColor, gameType);
            MinimaxWithAlphaBeta minimaxWithAlphaBeta = new MinimaxWithAlphaBeta();
            minimaxWithAlphaBeta.minimaxWithAlphaBeta(board, playTime, playerColor, gameType);

        } catch (IOException e) {
            System.out.println("IO Exception occurred");
            e.printStackTrace();
        }
    }

//    private static void callMinimax(char[][] board, float playTime, char playerColor, String gameType) {
//        Minimax minimax = new Minimax();
//        minimax.minimax(board, playTime, playerColor, gameType);
//    }
}
