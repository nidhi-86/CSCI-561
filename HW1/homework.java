
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Nidhi Chaudhary
 */
public class homework {

    public static void main(String[] args) {
        // Step 1 :  Read the input from input1.txt and parse it
        // Step 2 : Run the desired algorithm
        // Step 3 : Write the output into output.txt
        //long startTS = System.currentTimeMillis();
        List<Point> targetSites = new ArrayList<>();
        //long startTS = System.currentTimeMillis();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("input.txt"));
            String line = br.readLine().trim();
            String algoType = line;
            line = br.readLine().trim();
            String[] values = line.trim().split(" ");
            int W = Integer.parseInt(values[0]);
            int H = Integer.parseInt(values[1]);

            line = br.readLine().trim();
            values = line.split(" ");
            Point landingSite = new Point(Integer.parseInt(values[0]), Integer.parseInt(values[1]));

            line = br.readLine().trim();
            int Z = Integer.parseInt(line);

            line = br.readLine().trim();
            int n = Integer.parseInt(line);
            while (n-- > 0) {
                String[] x = br.readLine().split(" ");
                Point coordinates = new Point(Integer.parseInt(x[0]), Integer.parseInt(x[1]));
                targetSites.add(coordinates);
            }
            int[][] terrainMap = new int[W][H];
            for (int i = 0; i < H; i++) {
                String[] x = br.readLine().split(" ");
                for (int j = 0; j < W; j++) {
                    terrainMap[j][i] = Integer.parseInt(x[j]);
                }
            }
//            Random random = new Random();
//            for (int i = 0; i < H; i++) {
//                for (int j = 0; j < W; j++) {
//                    terrainMap[j][i] = 1;
//                }
//            }
            if(algoType.equalsIgnoreCase("BFS")) {
                BFS bfs = new BFS();
                String path = bfs.search(W, H, landingSite, Z, terrainMap, targetSites);
                writeOutput(path);
            } else if(algoType.equalsIgnoreCase("UCS")) {
                UCS ucs = new UCS();
                String path = ucs.search(W, H, landingSite, Z, terrainMap, targetSites);
                writeOutput(path);
            } else if(algoType.equalsIgnoreCase("A*")) {
                AStar a_star = new AStar();
                String path = a_star.search(W, H, landingSite, Z, terrainMap, targetSites);
                writeOutput(path);
            } else {
                System.out.println("Wrong input choice");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("Total time taken-" + (System.currentTimeMillis() - startTS));
    }

    /**
     * Writes output to output.txt
     * @param content
     */
    private static void writeOutput(String content) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("output.txt"));
            bw.write(content);
        } catch (IOException e) {
            System.out.println("IO Exception while writing to output file");
            e.printStackTrace();
        } finally {
            try {
                if(bw != null)
                    bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
