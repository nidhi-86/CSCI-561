
public class Result {
    double value;
    Move move;

    public Result(double value, Move move) {
        this.value = value;
        this.move = move;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }
}
