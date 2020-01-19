
import java.util.*;

/**
 * Performs BFS on the search space and returns the path
 */
public class BFS
    extends Search {

    public String search(int W, int H, Point landingSite, int Z, int[][] terrainMap, List<Point> targetSites) {
        //long startTS = System.currentTimeMillis();
        StringBuilder path = new StringBuilder();
        for(Point target : targetSites) {
            path.append(BFSSearch(W, H, landingSite, target, Z, terrainMap) + "\n");
        }
        //System.out.println("time to execute-" + (System.currentTimeMillis() - startTS));
        return path.toString().trim();
    }

    /**
     * Use a queue to store entire path in the queue, while polling take the last point from the polled path
     * @param W
     * @param H
     * @param landingSite
     * @param target
     * @param Z
     * @param terrainMap
     * @return
     */
    private StringBuilder BFSSearch(int W, int H, Point landingSite, Point target, int Z, int[][] terrainMap) {

        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();
        if(landingSite.equals(target)) {
            return new StringBuilder(landingSite.getX() + "," + landingSite.getY());
        }
        queue.offer(new Node(landingSite)); // Added source point to the queue

        while(true) {
            if(queue.isEmpty()) return new StringBuilder("FAIL");
            Node currentPoint = queue.poll();
            visited.add(currentPoint);
            List<Node> neighbours = getValidNeighboursOfNode(W, H, currentPoint.getPoint().getX(), currentPoint.getPoint().getY(), Z, terrainMap, target, "BFS");
            if(!neighbours.isEmpty()) {
                for (Node neighbour : neighbours) {
                    if (!visited.contains(neighbour) && !queue.contains(neighbour)) { // Checks for loops
                        neighbour.setParent(currentPoint);
                        // Target found
                        if(target.equals(neighbour.getPoint())) {
                            return formatPath(neighbour);
                        }
                        queue.offer(neighbour);
                    }
                }
            }
        }
    }
}
