
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Nidhi Chaudhary
 */
public class homework {

    public static void main(String[] args) {
        BufferedReader br = null;
        long startTS = System.currentTimeMillis();
        try {
            br = new BufferedReader(new FileReader("input.txt"));
            String line = br.readLine().trim();
            int n = Integer.parseInt(line);
            List<String> queries = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                queries.add(br.readLine().trim());
            }
            int k = Integer.parseInt(br.readLine().trim());
            List<String> knowledgeBase = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                knowledgeBase.add(br.readLine().trim());
            }
            CNFConvertor kbProcessor = new CNFConvertor();
            Map<String, List<String>> processedKB = kbProcessor.convertKBSentencestoCNF(knowledgeBase);
            Resolution resolution = new Resolution(processedKB);
            String answer = resolution.checkIfEntails(queries);
            writeToOutputFile(answer);
            //System.out.println(answer);
            System.out.println("Total Time taken - " + (System.currentTimeMillis() - startTS) + " milliseconds.");
        } catch (IOException e) {
            System.out.println("IO exception while reading input.txt");
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeToOutputFile(String answer) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("output.txt"));
            bw.write(answer);
        } catch (IOException e) {
            System.out.println("IO Exception while writing to output file");
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
