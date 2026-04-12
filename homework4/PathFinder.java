import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.File;

public class PathFinder {
    public static final String COMMA_DELIMITER = ",";

    static class Node {
        int id;
        String name;
        double lat, lon;

        Node(int id, String name, double lat, double lon) {
            this.id = id;
            this.name = name;
            this.lat = lat;
            this.lon = lon;
        }
    }

    static class Edge {
        int target;
        double weight; 

        Edge(int target, double weight) {
            this.target = target;
            this.weight = weight;
        }
    }

    Map<Integer, List<Edge>> graph = new HashMap<>();
    Map<Integer, Node> nodes = new HashMap<>();

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
                int src = Integer.parseInt(values[0].trim());
                int target = Integer.parseInt(values[1].trim());

                double distance = haversine(nodes.get(src), nodes.get(target));
                
                if (!graph.containsKey(src)) {
                    graph.put(src, new ArrayList<>());
                }
                graph.get(src).add(new Edge(target, distance));
                if (!graph.containsKey(target)) {
                    graph.put(target, new ArrayList<>());
                }
                graph.get(target).add(new Edge(src, distance));
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
                nodes.put(stopId, new Node(
                    stopId, values[1].trim(), Double.parseDouble(values[2].trim()), Double.parseDouble(values[3].trim())
                ));
            }
        }
    }

    private double haversine(Node a, Node b) {
        // convert to radians 
        double lat = Math.toRadians(a.lat);
        double lat2 = Math.toRadians(b.lat);
        double lonDiff = Math.toRadians(b.lon - a.lon);
        
        //inside square root
        double beforeSquare = (Math.pow(Math.sin((lat - lat2) / 2), 2)) + (Math.cos(lat)* Math.cos(lat2) * (Math.pow(Math.sin((lonDiff) / 2), 2)));
        return 6371 * 2 * Math.asin(Math.sqrt(beforeSquare));
    }

    public List<Integer> 
}
