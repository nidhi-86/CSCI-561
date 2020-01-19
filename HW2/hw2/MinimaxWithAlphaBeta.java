
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class MinimaxWithAlphaBeta extends GamePlayer {

    MovesGenerator movesGenerator;
    char colorOfMaximiser;

    public MinimaxWithAlphaBeta() {
        this.movesGenerator = new MovesGenerator();
    }

    /**
     * Step-1 Get all legal moves for the board and create tree of depth 1
     * Step-2 Get all legal moves at the next level for the minimizing player after changing the board with the move selected in step1 and create the
     * depth2 of the tree
     * @param board
     * @param playTime
     * @param color
     * @param gameType
     */
    public void minimaxWithAlphaBeta(char[][] board, float playTime, char color, String gameType) {
        //long startTS = System.currentTimeMillis();
        int depth = 1;
        if(gameType.equalsIgnoreCase("SINGLE")) {
            // In single game utilize the entire time for selecting the move
            if(playTime < 4)
                depth = 1;
            else
                depth = Constants.MIN_DEPTH;
        } else if(gameType.equalsIgnoreCase("GAME")) {
            if(playTime < 10) {
                depth = 1;
            } else {
                depth = Constants.MIN_DEPTH;
            }
        }
        if(depth == -1) depth = Constants.MIN_DEPTH;
        Map<String, Double> boardConfigToValueCache = new HashMap<>();
        this.colorOfMaximiser = color;
        Result result = minimaxWithAlphaBeta(board, boardConfigToValueCache, color, gameType, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        Move selectedMove = result.getMove();
        writeOutputToFile(formatOutputMoveToString(selectedMove), Constants.OUTPUT_FILE_NAME);
        //writeOutputToFile(formatOutputMoveToString(selectedMove, board, color, playTime, gameType), "output.txt");
        //System.out.println("Time taken by minimax algorithm with alpha beta pruning - " + (System.currentTimeMillis() - startTS));
    }

    public Result minimaxWithAlphaBeta(char[][] board, Map<String, Double> boardConfigToValueCache, char color, String gameType, int depth, double alpha, double beta, boolean isMaximisingPlayer) {
        // return the value obtained by evaluation function and modify the board according to the move selected with highest evaluation
        if(isWinner(board, Constants.getOpposingPlayerColor(color))) {
            return isMaximisingPlayer ? new Result(Constants.LOSING_VALUE, null) : new Result(Constants.WINNING_VALUE, null);
        }

        if(depth == 0) {
            String boardStr = getBoardString(board);
            double evaluation;
            if(boardConfigToValueCache.containsKey(boardStr)) {
                evaluation = boardConfigToValueCache.get(boardStr);
            } else {
                evaluation = evaluate(board, colorOfMaximiser);
                boardConfigToValueCache.put(getBoardString(board), evaluation);
            }
            return new Result(evaluation, null);
        }
        Move finalMove = null;
        if(isMaximisingPlayer) {
            double value = Integer.MIN_VALUE;
            List<Move> moves = movesGenerator.generateNextMove(board, 0, color, gameType);
            /*if(depth == Constants.MIN_DEPTH) {
                movesGenerator.printMoves(moves);
            }*/
            for(Move move : moves) {
                // Change configuration of the board according to the move taken
                char[][] boardAfterMove = changeBoardConfig(board, move, color);
                if(isWinner(boardAfterMove, color)) {
                    return new Result(Constants.WINNING_VALUE, move);
                }
                double minPlayerValue = minimaxWithAlphaBeta(boardAfterMove, boardConfigToValueCache, Constants.getOpposingPlayerColor(color), gameType, depth-1,
                        alpha, beta, false).getValue();
                //System.out.println("MAX " + move.currentPoint + " -> " + move.movePoint + " = "+ minPlayerValue);
                if(minPlayerValue > value) {
                    value = minPlayerValue;
                    finalMove = move;
                }
                if(value > alpha) alpha = value;
                if(alpha >= beta) break;
            }
            //System.out.println("MAX " + finalMove.currentPoint + " -> " + finalMove.movePoint + " = "+ value);
            return new Result(value, finalMove);
        } else {
            double value = Integer.MAX_VALUE;
            List<Move> moves = movesGenerator.generateNextMove(board, 0, color, gameType);
            for(Move move : moves) {
                // Change configuration of the board according to the move taken
                char[][] boardAfterMove = changeBoardConfig(board, move, color);
                if(isWinner(boardAfterMove, color)) {
                    return new Result(Constants.LOSING_VALUE, move);
                }
                double maxPlayerValue = minimaxWithAlphaBeta(boardAfterMove, boardConfigToValueCache, Constants.getOpposingPlayerColor(color), gameType, depth-1,
                        alpha, beta, true).getValue();
                if(maxPlayerValue < value) {
                    value = maxPlayerValue;
                    finalMove = move;
                }
                if(value < beta) beta = value;
                if(alpha >= beta) break;
            }
            //System.out.println("MIN " + finalMove.currentPoint + " -> " + finalMove.movePoint + " = "+ value);
            return new Result(value, finalMove);
        }
    }

//    public void minimaxWithAlphaBeta(char[][] board, float playTime, char color, String gameType) {
//        long startTS = System.currentTimeMillis();
//        Map<String, Double> boardConfigToValueCache = new HashMap<>();
//        // Adapt the depth according to time left
//        int depth = 1;
//        while (playTime > 0) {
//            System.out.println(playTime);
//            long startTime = System.currentTimeMillis();
//            Result result = minimaxWithAlphaBeta(board, boardConfigToValueCache, color, gameType, depth++, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
//            if(result != null) {
//                Move selectedMove = result.getMove();
//                //writeOutputToFile(formatOutputMoveToString(selectedMove));
//                writeOutputToFile(formatOutputMoveToString(selectedMove, board, color, playTime, gameType));
//            } else {
//                break;
//            }
//            playTime -= (System.currentTimeMillis() - startTime) / 1000F;
//        }
//        System.out.println("Time taken by minimax algorithm with alpha beta pruning - " + (System.currentTimeMillis() - startTS));
//    }

}

