
import java.util.Map;
import java.util.Set;

public class EvaluationFunction {

    char playerColor;

    public EvaluationFunction(char playerColor) {
        this.playerColor = playerColor;
    }

    public double getEvaluationScore(char[][] board) {
        double score = 0.8 * getManhattanDistanceScore(board) + 0.1 * getLeaveCampScore(board) + 0.1 * assignWeightToMove(board);
        return score;
    }

    private double getManhattanDistanceScore(char[][] board) {
        Point destinationBase;
        Point opponentBase;
        if(playerColor == 'B') {
            destinationBase = Constants.WHITE_BASE;
            opponentBase = Constants.BLACK_BASE;
        } else {
            destinationBase = Constants.BLACK_BASE;
            opponentBase = Constants.WHITE_BASE;
        }
        int selfManhattanDistance = 0;
        int opponentManhattanDistance = 0;
        for(int x = 0; x < board.length; x++) {
            for(int y = 0; y < board[x].length; y++) {
                if(board[x][y] == playerColor) {
                    selfManhattanDistance += (32 - getManhattanDistance(destinationBase.getX(), destinationBase.getY(), x, y));
                } else if(board[x][y] != '.') {
                    opponentManhattanDistance += (32 - getManhattanDistance(opponentBase.getX(), opponentBase.getY(), x, y));
                }
            }
        }
        //System.out.println(selfManhattanDistance + " " + opponentManhattanDistance);
        double normalizedSelfDistance = selfManhattanDistance / (19 * 30.0);
        double normalizedOpponentDistance = opponentManhattanDistance / (19 * 30.0);
        return (normalizedSelfDistance - normalizedOpponentDistance);
    }

    private int getManhattanDistance(int finalX, int finalY, int initialX, int initialY) {
        return Math.abs(finalX - initialX) + Math.abs(finalY - initialY);
    }

    /**
     * Pawns should leave the camp to prevent spoiling
     * @param board
     * @return
     */
    private double getLeaveCampScore(char[][] board) {
        Set<Point> ownCampCoordinates = CampCoordinates.getCamp(playerColor);
        int pawnsInCamp = 0;
        for(Point point : ownCampCoordinates) {
            if(board[point.getX()][point.getY()] == playerColor) {
                pawnsInCamp++;
            }
        }
        return (-1 * pawnsInCamp) / 19.0;
    }

    /**
     * Assign weights to each cell in halma, its own camp weight is -1, in mid it is 2, in the destination it is 3
     * @param board
     * @return
     */
    private double assignWeightToMove(char[][] board) {
        Map<Point, Integer> cellToWeightValue = CampCoordinates.cellToWeightMap(playerColor);
        int totalWeight = 0;
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                if(board[i][j] == playerColor) {
                    totalWeight += cellToWeightValue.getOrDefault(new Point(i, j), 0);
                }
            }
        }
        return totalWeight / 19.0;
    }
    /**
     * Each pawn should be closer to other pawns in order to make jumps easily
     * @param board
     * @return
     */
    private double closeToOtherPawns(char[][] board) {
        return 0.0;
    }
}
