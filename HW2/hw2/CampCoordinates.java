
import java.util.*;

public class CampCoordinates {
    static int[] count = {5, 5, 4, 3, 2};

    /**
     * BLACK CAMP is on the top-left
     *
     * @return
     */
    public static Set<Point> getBlackCamp() {
        Set<Point> blackCampCoordinates = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            for (int c = 0; c < count[i]; c++) {
                blackCampCoordinates.add(new Point(c, i));
            }
        }
        return blackCampCoordinates;
    }

    public static Set<Point> getWhiteCamp() {
        Set<Point> whiteCampCoordinates = new HashSet<>();
        for (int i = 15; i >= 11; i--) {
            for (int c = 0; c < count[15 - i]; c++) {
                whiteCampCoordinates.add(new Point(15 - c, i));
            }
        }

        return whiteCampCoordinates;
    }

    /**
     * When the player is white, and it reaches black's camp, the play cannot result in it leaving the camp
     * When the player is black and it reaches white's territory it cannot leave from that camp
     *
     * @param playerColor
     * @return
     */
    public static Set<Point> getOpposingCamp(char playerColor) {
        if (playerColor == 'W') return getBlackCamp();
        else return getWhiteCamp();
    }

    public static Set<Point> getCamp(char playerColor) {
        if (playerColor == 'W') return getWhiteCamp();
        else return getBlackCamp();
    }

    public static Map<Point, Integer> cellToWeightMap(char playerColor) {
        Map<Point, Integer> cellToWeightMap = new HashMap<>();
        Set<Point> destinationCamp = getOpposingCamp(playerColor);
        Set<Point> baseCamp = getCamp(playerColor);

        List<List<Point>> elements = getDiagonalMatrix();
        for (List<Point> pointList : elements) {
            int mid = pointList.size() / 2;
            // Prioritize moves to middle
            if (pointList.size() > 7) {
                for (int x = mid; x >= mid - 3; x--) {
                    cellToWeightMap.put(pointList.get(x), 2);
                }
                for (int x = mid; x < mid + 3; x++) {
                    cellToWeightMap.put(pointList.get(x), 2);
                }
            } else {
                for (Point p : pointList) {
                    cellToWeightMap.put(p, 2);
                }
            }
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Point p = new Point(i, j);
                if (destinationCamp.contains(p)) cellToWeightMap.put(p, 3);
                else if (baseCamp.contains(p)) cellToWeightMap.put(p, -1);
            }
        }
        return cellToWeightMap;
    }

    private static List<List<Point>> getDiagonalMatrix() {
        List<List<Point>> elements = new ArrayList<>();
        int i = 0, j = 0;
        boolean upper = true;
        for (int k = 0; k < 256; ) {
            if (upper) {
                List<Point> list = new ArrayList<>();
                for (; i >= 0 && j < 16; j++, i--) {
                    list.add(new Point(i, j));
                    k++;
                }
                elements.add(list);
                if (i < 0 && j <= 15)
                    i = 0;
                if (j == 16) {
                    i = i + 2;
                    j--;
                }
            } else {
                List<Point> list = new ArrayList<>();
                for (; j >= 0 && i < 16; i++, j--) {
                    list.add(new Point(i, j));
                    k++;
                }
                elements.add(list);
                if (j < 0 && i <= 15)
                    j = 0;
                if (i == 16) {
                    j = j + 2;
                    i--;
                }
            }
            upper = !upper;
        }
        return elements;
    }
}
