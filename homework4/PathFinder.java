import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.File;

public class PathFinder {
    public static final String COMMA_DELIMITER = ",";

    List<int[]> edges = new ArrayList<>();
    Map<Integer, String[]> nodes = new HashMap<>();

    public PathFinder() throws FileNotFoundException {
        loadEdges("homework4/septa_edges.csv");
        loadNodes("homework4/septa_nodes.csv");
    }

    private void loadEdges(String filename) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(filename))) {
            if (scanner.hasNextLine()) scanner.nextLine(); // header
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] values = line.split(COMMA_DELIMITER);
                edges.add(new int[] {
                    Integer.parseInt(values[0].trim()), Integer.parseInt(values[1].trim())
                });
            }
        }
    }

    private void loadNodes(String filename) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(filename))) {
            if (scanner.hasNextLine()) scanner.nextLine(); // header
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] values = line.split(COMMA_DELIMITER);
                int stopId = Integer.parseInt(values[0].trim());
                nodes.put(stopId, new String[]{
                    values[1].trim(), // name
                    values[2].trim(), // lat
                    values[3].trim() // long
                });
            }
        }
    }
}
