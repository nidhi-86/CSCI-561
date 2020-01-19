
import java.util.*;

public class AStar
    extends Search {

    public String search(int W, int H, Point landingSite, int Z, int[][] terrainMap, List<Point> targetSites) {
        //long startTS = System.currentTimeMillis();
        StringBuilder path = new StringBuilder();
        for (Point target : targetSites) {
            path.append(AStarSearch(W, H, landingSite, target, Z, terrainMap) + "\n");
        }
        //System.out.println("Time-" + (System.currentTimeMillis() - startTS));
        return path.toString().trim();
    }

    private StringBuilder AStarSearch(int W, int H, Point landingSite, Point target, int Z, int[][] terrainMap) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(1, Comparator.comparingInt(Node::getCost));
        Set<Node> visited = new HashSet<>();
        priorityQueue.offer(new Node(landingSite, 0));
        int step = 0;
        while(true) {
            step++;
            if(priorityQueue.isEmpty()) return new StringBuilder("FAIL");

            Node currentPoint = priorityQueue.poll();

            if (currentPoint.getPoint().equals(target)) {
                //System.out.println("Cost-" + currentPoint.getCost() + " number of steps- " + step);
                return formatPath(currentPoint);
            }
            visited.add(currentPoint);

            List<Node> neighbourList = getValidNeighboursOfNode(W, H, currentPoint.getPoint().getX(), currentPoint.getPoint().getY(), Z, terrainMap, target, "A*");
            for (Node node : neighbourList) {
                node.setgCost(currentPoint.getgCost() + node.getgCost());
                node.setCost(currentPoint.getgCost() + node.getCost());
                if (!visited.contains(node) && !priorityQueue.contains(node)) {
                    node.setParent(currentPoint);
                    priorityQueue.offer(node);
                } else if (priorityQueue.contains(node)) {
                    for(Node element : priorityQueue) {
                        if(element.getPoint().equals(node.getPoint()) && element.getCost() > node.getCost()) {
                            priorityQueue.remove(element);
                            node.setParent(currentPoint);
                            priorityQueue.offer(node);
                            break;
                        }
                    }
//                } else if(visited.contains(node)) {
//                    Node existingNode = visited.get(visited.indexOf(node));
//                    if(existingNode.getCost() > node.getCost()) {
//                        visited.remove(existingNode);
//                        node.setParent(currentPoint);
//                        priorityQueue.offer(node);
//                        break;
//                    }
                }
            }
        }
    }
}
