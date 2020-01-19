
import java.io.Serializable;

public class CalibrateData
    implements Serializable {

    int depth;
    float timeTaken;

    public CalibrateData(int depth, float timeTaken) {
        this.depth = depth;
        this.timeTaken = timeTaken;
    }
}
