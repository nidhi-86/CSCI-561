
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Move {

    Point currentPoint;
    Point movePoint;
    List<String> parentsList;
    boolean isJump;

    public Move() {
    }

    public Move(Point currentPoint, Point movePoint, List<String> parentPointList) {
        this.currentPoint = currentPoint;
        this.movePoint = movePoint;
        this.parentsList = parentPointList;
    }


    public Move(Point currentPoint, Point movePoint, boolean isJump) {
        this.currentPoint = currentPoint;
        this.movePoint = movePoint;
        this.isJump = isJump;
        this.parentsList = new ArrayList<>();
    }


    public Move(Point currentPoint, Point movePoint, List<String> parentsList, boolean isJump) {
        this.currentPoint = currentPoint;
        this.movePoint = movePoint;
        this.parentsList = parentsList;
        this.isJump = isJump;
    }

    public Point getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(Point currentPoint) {
        this.currentPoint = currentPoint;
    }

    public Point getMovePoint() {
        return movePoint;
    }

    public void setMovePoint(Point movePoint) {
        this.movePoint = movePoint;
    }

    public List<String> getParentsList() {
        return parentsList;
    }

    public void setParentsList(List<String> parentsList) {
        this.parentsList = parentsList;
    }

    public boolean isJump() {
        return isJump;
    }

    public void setJump(boolean jump) {
        isJump = jump;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return isJump == move.isJump &&
                movePoint.equals(move.movePoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movePoint, isJump);
    }
}
