
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Search {

    /**
     * Moves backward from target to the node through parent pointers
     * @param target
     * @return
     */
    public StringBuilder formatPath(Node target) {
        StringBuilder result = new StringBuilder();
        Stack<String> stack = new Stack<>();
        for (Node node = target; node != null; node = node.parent) {
            stack.push(node.getPoint().getX() + "," + node.getPoint().getY() + " ");
        }
        while(!stack.isEmpty()) {
            result.append(stack.pop());
        }
        return result.deleteCharAt(result.lastIndexOf(" "));
    }

    /**
     * Gets all neighbours of a particular point-> we are allowed to move int 8 directions, with diagonal cost more than N,S,E,W movement cost
     * @param W
     * @param H
     * @param row
     * @param col
     * @param Z
     * @param terrainMap
     * @param target
     * @param algoType
     * @return
     */
    public List<Node> getValidNeighboursOfNode(int W, int H, int row, int col, int Z, int[][] terrainMap, Point target, String algoType) {
        List<Node> neighbours = new ArrayList<>();
        int curElevation = terrainMap[row][col];
        // Directions for each of the 8 directions
        int[][] directions = new int[][]{{0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}};
        //int minElevation = Integer.MAX_VALUE;
        for (int[] direction : directions) {
            int x = row + direction[0];
            int y = col + direction[1];
            // Invalid neighbour - out of bounds
            if (x < 0 || y < 0 || x >= W || y >= H) continue;
            int elevationAtNewPoint = terrainMap[x][y];
            int elevationDifference = Math.abs(elevationAtNewPoint - curElevation);
            if (elevationDifference <= Z) {
                Point point = new Point(x, y);
                Node node = new Node(point);
                // Check whether direction is North, South, East or West
                if(!algoType.equalsIgnoreCase("BFS")) {
                   // minElevation = Math.min(minElevation, Math.abs(elevationAtNewPoint - curElevation));
                    if (direction[0] == 0 || direction[1] == 0) {
                        node.setCost(10 + getHeuristic(point, target, elevationDifference, algoType));
                        if(algoType.equalsIgnoreCase("A*")) {
                            node.setgCost(10 + elevationDifference);
                        }
                    } else {
                        node.setCost(14 + getHeuristic(point, target, elevationDifference, algoType)); // For diagonal elements
                        if(algoType.equalsIgnoreCase("A*")) {
                            node.setgCost(14 + elevationDifference);
                        }
                    }
                }
                if (!neighbours.contains(node)) {
                    neighbours.add(node);
                }
            }
        }
        // For A* -> heuristic = Minimum absolute difference in elevation + Diagonal distance
//        if(algoType.equalsIgnoreCase("A*") && minElevation != Integer.MAX_VALUE) {
//            for(Node neighbour : neighbours) {
//                neighbour.setCost(neighbour.getCost() + minElevation);
//            }
//        }
        return neighbours;
    }

    /**
     * Calculates estimated cost from a node to goal-> Calculates diagonal distance
     * @param node
     * @param goal
     * @param elevationDifference
     * @param algoType
     * @return
     */
    private int getHeuristic(Point node, Point goal, int elevationDifference, String algoType) {
        if(!algoType.equalsIgnoreCase("A*")) {
            return 0;
        }
        int dx = Math.abs(node.getX() - goal.getX());
        int dy = Math.abs(node.getY() - goal.getY());
        return (10 * (dx + dy) + (14 - 2 * 10) * Math.min(dx, dy)) + elevationDifference;
        //return (int)Math.sqrt(dx * dx + dy * dy) * 10 + elevationDifference;
    }
}
