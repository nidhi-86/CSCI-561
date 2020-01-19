
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MovesGenerator {

    private GamePlayer gamePlayer = new GamePlayer();

    public List<Move> generateNextMove(char[][] board, float playTime, char playerColor, String gameType) {
        List<Move> possibleLegalMoves = new ArrayList<>();
        List<Move> importantMoves = new ArrayList<>();
        Point base = Constants.getBase(playerColor);
        Set<Point> ownCampCoordinates = CampCoordinates.getCamp(playerColor);
        for(int row = 0; row < 16; row++) {
            for(int col = 0; col < 16; col++) {
                if(board[row][col] == playerColor) {
//                    if(row == 13 && col == 12) {
//                        System.out.println("debug point");
//                    }
                    List<Move> moves = generateMoves(row, col, board, playerColor);
                    possibleLegalMoves.addAll(moves);
                    // If any pawn is in its own camp, then we move it out first or move it away from the corner - Rule 2a & 2b
                    if(ownCampCoordinates.contains(new Point(row, col))) {
                        double maxDistance = Integer.MIN_VALUE;
                        Move finalMove = null;
                        for(Move move : moves) {
                            if(!ownCampCoordinates.contains(move.getMovePoint())) {
                                importantMoves.add(move);
                            } else {
                                Point movePoint = move.getMovePoint();
                                Point initialPoint = move.getCurrentPoint();
                                int distanceFromBase = Math.abs(movePoint.getX() - base.getX()) + Math.abs(movePoint.getY() - base.getY());
                                int distanceOfInitialFromBase = Math.abs(initialPoint.getX() - base.getX()) + Math.abs(initialPoint.getY() - base.getY());

                                if(distanceFromBase > distanceOfInitialFromBase && distanceFromBase > maxDistance) {
                                    maxDistance = distanceFromBase;
                                    finalMove = move;
                                }
                            }
                        }
                        if(finalMove != null) {
                            importantMoves.add(finalMove);
                        }
                    }
                }
            }
        }
        //printMoves(possibleLegalMoves);
        // If we have pawns still in base camp, they are moved first
        if(!importantMoves.isEmpty()) {
            boolean remove = false;
            for(Move move: importantMoves) {
                if(ownCampCoordinates.contains(move.getCurrentPoint()) && !ownCampCoordinates.contains(move.getMovePoint())) {
                    remove = true;
                    break;
                }
            }
            if(remove) {
                Iterator<Move> moveItr = importantMoves.iterator();
                while (moveItr.hasNext()) {
                    Move move = moveItr.next();
                    if(ownCampCoordinates.contains(move.getCurrentPoint()) && ownCampCoordinates.contains(move.getMovePoint())) {
                        moveItr.remove();
                    }
                }
            }
            Collections.shuffle(importantMoves);
            //printMoves(importantMoves);
            return importantMoves;
        }
        Point destinationBase = Constants.getBase(Constants.getOpposingPlayerColor(playerColor));
        // Shuffling to cause randomness into the data
        Collections.sort(possibleLegalMoves, (o1, o2) -> {
            Point o2MovePoint = o2.getMovePoint();
            Point o1MovePoint = o1.getMovePoint();
            return (Math.abs(o1MovePoint.getX() - destinationBase.getX()) + Math.abs(o1MovePoint.getY() - destinationBase.getY())) -
                    (Math.abs(o2MovePoint.getX() - destinationBase.getX()) + Math.abs(o2MovePoint.getY() - destinationBase.getY()));
        });
        //printMoves(possibleLegalMoves);
        return possibleLegalMoves;
    }

    private List<Move> generateMoves(int row, int col, char[][] board, char playerColor) {
        List<Move> moves = new ArrayList<>();
        Set<Point> campCoordinates = CampCoordinates.getOpposingCamp(playerColor);
        Set<Point> ownCampCoordinates = CampCoordinates.getCamp(playerColor);
        // Generate normal adjacent moves
        generateAdjacentLegalMoves(row, col, board, moves, campCoordinates, ownCampCoordinates);
        // Generate jump moves
        Point initialPoint = new Point(row, col);
        generateJumpMoves(row, col, board, moves, initialPoint, campCoordinates, ownCampCoordinates, new ArrayList<>());
        return moves;
    }

    private void generateAdjacentLegalMoves(int row, int col, char[][] board, List<Move> moves, Set<Point> campCoordinates, Set<Point> ownCampCoordinates) {
        int[][] directions = new int[][]{{0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}};
        for(int[] dir : directions) {
            Point newPoint = new Point(row + dir[0],col + dir[1]);
            Point initialPoint = new Point(row, col);
            if(isValid(newPoint, initialPoint, board, campCoordinates, ownCampCoordinates)) {
                Move move = new Move(initialPoint, newPoint, null, false);
                moves.add(move);
            }
        }
    }

    private boolean isValid(Point point, Point initialPoint, char[][] board, Set<Point> oppositionCampCoordinates, Set<Point> ownCampCoordinates) {
        if(point.getX() < 0 || point.getY() < 0 || point.getX() > 15 || point.getY() > 15)
            return false;
        if(board[point.getX()][point.getY()] != '.')
            return false;
        // Once entered into opposing camp, cannot leave it
        if(canLeaveOpposingCamp(oppositionCampCoordinates, initialPoint, point)) {
            return false;
        }
        if(isMovingFromOutsideToOwnCamp(ownCampCoordinates, initialPoint, point)) {
            return false;
        }
        return true;
    }
    /**
     * Generate all possible jump nodes from starting node and add all of them into list of possible moves
     * @param row
     * @param col
     * @param board
     * @param moves
     * @param initialPoint
     * @param oppositionCampCoordinates
     */
    private void generateJumpMoves(int row, int col, char[][] board, List<Move> moves, Point initialPoint, Set<Point> oppositionCampCoordinates, Set<Point> ownCampCoordinates,
                                   List<String> parentMoves) {
        if(row < 0 || col < 0 || row > 15 || col > 15) return;
        int[][] directions = new int[][]{{0, -1, 0, -2}, {-1, -1, -2, -2}, {-1, 0, -2, 0}, {-1, 1, -2, 2}, {0, 1, 0, 2}, {1, 1, 2, 2}, {1, 0, 2, 0}, {1, -1, 2, -2}};
        String parent = row + "," + col;
        parentMoves.add(parent);

        for(int[] direction : directions) {
            int r = row + direction[0];
            int c = col + direction[1];
            int jr = row + direction[2];
            int jc = col + direction[3];
            if (r < 0 || c < 0 || r > 15 || c > 15 || jr < 0 || jc < 0 || jr > 15 || jc > 15) {
                continue;
            }
            if ((board[r][c] == 'B' || board[r][c] == 'W') && board[jr][jc] == '.') {
                Point point = new Point(jr, jc);
                Move move = new Move(initialPoint, point, true);
                if (!moves.contains(move)) {
                    // Once entered into opposing camp, cannot leave it
                    if (!canLeaveOpposingCamp(oppositionCampCoordinates, initialPoint, point) && !isMovingFromOutsideToOwnCamp(ownCampCoordinates, initialPoint, point)) {
                        move.getParentsList().addAll(parentMoves);
                        moves.add(move);
                        // TODO: Problem identified- > It is going to back to where it started fix it (13,15)->(11,13)->(15,15)->(15,13)->(13,11)
                        //char[][] newBoard = gamePlayer.changeBoardConfig(board, move, playerColor);
                        generateJumpMoves(jr, jc, board, moves, initialPoint, oppositionCampCoordinates, ownCampCoordinates, parentMoves);
                        parentMoves.remove(parentMoves.get(parentMoves.size()-1));
                    }
                }
            }
        }
    }

    /**
     * For checking whether a pawn is in opposing player's territory, it can't leave it
     * @param campCoordinates
     * @param initialPoint
     * @param point
     * @return
     */
    private boolean canLeaveOpposingCamp(Set<Point> campCoordinates, Point initialPoint, Point point) {
        return campCoordinates.contains(initialPoint) && !campCoordinates.contains(point);
    }

    /**
     * If moving from outside own camp to inside camp-> Its an invalid move
     * @param ownCampCoordinates
     * @param initialPoint
     * @param point
     * @return
     */
    private boolean isMovingFromOutsideToOwnCamp(Set<Point> ownCampCoordinates, Point initialPoint, Point point) {
        return !ownCampCoordinates.contains(initialPoint) && ownCampCoordinates.contains(point);
    }


    public void printMoves(List<Move> possibleLegalMoves) {
        Map<Point, List<Move>> moveMap = possibleLegalMoves.stream().collect(Collectors.groupingBy(Move::getCurrentPoint));
        for(Point p : moveMap.keySet()) {
            System.out.println("For point - " + p);
            for(Move m : moveMap.get(p)) {
                System.out.println(m.getMovePoint() + " " + m.getParentsList());
            }
            System.out.println("---------");
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter("move.txt", true);
            for (Move move : possibleLegalMoves) {
                fw.write(move.currentPoint + " -> (" + move.movePoint.x + ", " + move.movePoint.getY() + ", " + move.isJump() + " " + move.parentsList + ") ");
                fw.write("\n");
            }
            fw.write("END " + possibleLegalMoves.size());
        } catch(IOException e) {
            e.printStackTrace();;
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
