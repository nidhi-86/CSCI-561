
import java.util.Objects;

public class Node {

    Point point;
    int cost;
    Node parent;
    int gCost;

    public Node(Point point, int cost) {
        this.point = point;
        this.cost = cost;
    }

    public Node(Point point, int cost, int gCost) {
        this.point = point;
        this.cost = cost;
        this.gCost = gCost;
    }

    public Node() {
    }

    public Node(Point point) {
        this.point = point;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public int getgCost() {
        return gCost;
    }

    public void setgCost(int gCost) {
        this.gCost = gCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(point, node.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point);
    }

    @Override
    public String toString() {
        return "Node{" +
                "point=" + point +
                ", cost=" + cost +
                '}';
    }
}
