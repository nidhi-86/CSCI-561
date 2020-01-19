
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class GamePlayer {

    public double evaluate(char[][] board, char color) {
        EvaluationFunction evaluationFunction = new EvaluationFunction(color);
        return evaluationFunction.getEvaluationScore(board);
    }

    public char[][] changeBoardConfig(char[][] board, Move move, char color) {
        char[][] newBoard = deepCopy(board);
        if(move == null) {
            return newBoard;
        }
        Point initialPoint  = move.getCurrentPoint();
        Point finalPoint = move.getMovePoint();
        newBoard[initialPoint.getX()][initialPoint.getY()] = '.';
        newBoard[finalPoint.getX()][finalPoint.getY()] = color;
        return newBoard;
    }

    public char[][] deepCopy(char[][] input) {
        char[][] target = new char[input.length][];
        for (int i=0; i <input.length; i++) {
            target[i] = Arrays.copyOf(input[i], input[i].length);
        }
        return target;
    }

    public boolean isWinner(char[][] board, char playerColor) {
        // For all the destination points, check if my color pawn occupies them or black
        int count = 0;
        int oppositionInCamp = 0;
        char opposingPlayerColor =  Constants.getOpposingPlayerColor(playerColor);
        Set<Point> destinationCamp = CampCoordinates.getOpposingCamp(playerColor);
        for(Point point : destinationCamp) {
            if(board[point.getX()][point.getY()] == playerColor) {
                count++;
            } else if(board[point.getX()][point.getY()] == opposingPlayerColor) {
                oppositionInCamp++;
            }
        }

        boolean res =  count > 0 && ((19 - oppositionInCamp) == count);
//        if(res) {
//            System.out.println("Winning state reached for-" + playerColor);
//        }
        return res;
    }

    public String getBoardString(char[][] board) {
        StringBuilder boardStr = new StringBuilder();
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                boardStr.append(board[i][j]);
            }
        }
        return boardStr.toString();
    }

    public String formatOutputMoveToString(Move selectedMove, char[][] board, char color, float remainingPlayTime, String gameType) {
        if(isWinner(board, color)) {
            System.out.println("Yay! Found winner !" + color);
        }
        char[][] newBoard = changeBoardConfig(board, selectedMove, color);
        String outputBoard = "";
        for(int i = 0; i < 16; i++) {
            for(int j = 0; j < 16; j++) {
                outputBoard += newBoard[j][i];
            }
            outputBoard += "\n";
        }
        String next = "WHITE";
        if(color == 'W') {
            next = "BLACK";
        }
        System.out.println("Move selected by " + color + " -> ");
        System.out.println(formatOutputMoveToString(selectedMove));
        return gameType + "\n" + next + "\n" + remainingPlayTime + "\n" + outputBoard;
    }
    public void printBoard(char[][] board) {
        for(int i = 0; i < 16; i++) {
            for(int j = 0; j < 16; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
    public static String formatOutputMoveToString(Move move) {
        StringBuilder moveStr = new StringBuilder();
        if(move == null) return moveStr.toString();
        if(move.isJump()) {
            List<String> parentPoint = move.getParentsList();
            if(parentPoint.size() > 1) {
                for (int i = 0; i < parentPoint.size() - 1; i++) {
                    moveStr.append("J " + parentPoint.get(i) + " " + parentPoint.get(i + 1) + "\n");
                }
            }
            moveStr.append("J " + parentPoint.get(parentPoint.size()-1) + " " + move.getMovePoint().getX() + "," + move.getMovePoint().getY());
        } else {
            moveStr.append("E " + move.getCurrentPoint().getX() + "," + move.getCurrentPoint().getY() + " " + move.getMovePoint().getX() + "," + move.getMovePoint().getY());
        }
        return moveStr.toString();
    }

    public static void writeOutputToFile(String content, String fileName) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(fileName));
            bw.write(content);
        } catch (IOException e) {
            System.out.println("IO Exception while writing to output file");
            e.printStackTrace();
        } finally {
            try {
                if(bw != null)
                    bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
