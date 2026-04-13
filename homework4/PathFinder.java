import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.io.File;

public class PathFinder {
    public static final String COMMA_DELIMITER = ",";

    static class Node {
        int id;
        String name;
        double lat, lon;
        double d = Double.MAX_VALUE; // initial distance, infinite
        double f = Double.MAX_VALUE; // estimate
        Integer predecessor = null;

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

    public PathFinder(String nodesFile, String edgesFile) throws FileNotFoundException {
        loadNodes(nodesFile);
        loadEdges(edgesFile);
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

    private void initAlgo(int startId, int endId) {
        for (Node v : nodes.values()) {
            v.d = Double.MAX_VALUE;
            v.f = Double.MAX_VALUE;
            v.predecessor = null;
        }
        Node s = nodes.get(startId);
        s.d = 0;
        s.f = haversine(s, nodes.get(endId));
    }

    private void relaxAlgo(Node u, Node v, double weight, int endId, PriorityQueue<Node> Q) {
        if (v.d > u.d + weight) {
            v.d = u.d + weight;
            v.f = v.d + haversine(v, nodes.get(endId));
            v.predecessor = u.id;

            Q.remove(v);
            Q.add(v);
        }
    }

    public List<Integer> algo(int startId, int endId) {

        if (!nodes.containsKey(startId) || !nodes.containsKey(endId)) {
            return null;
        }
        
        if (!graph.containsKey(startId)) {
            return null;
        }

        initAlgo(startId, endId);

        PriorityQueue<Node> Q = new PriorityQueue<>((a, b) -> {
            int comp = Double.compare(a.f, b.f);
            if (comp != 0) return comp;
            return Integer.toString(a.id).compareTo(Integer.toString(b.id));
        });

        Q.addAll(nodes.values());

        Set<Integer> S = new HashSet<>();

        while (!Q.isEmpty()) {
            Node u = Q.poll();

            if (u.id == endId) {
                return reconstructPath(endId);
            }

            S.add(u.id);

            for (Edge edge : graph.getOrDefault(u.id, Collections.emptyList())) {
                Node v = nodes.get(edge.target);
                if (S.contains(v.id)) continue;
                relaxAlgo(u, v, edge.weight, endId, Q);
            }
        }
        return null; // no path
    }

    private List<Integer> reconstructPath(int endId) {
        LinkedList<Integer> path = new LinkedList<>();
        Integer current = endId;

        while (current != null) {
            path.addFirst(current);
            current = nodes.get(current).predecessor;
        }
        return path;
    }

    public static void main(String[] args) {
        try {
            if (args.length < 5) {
                System.out.println("Not enough arguments");
                return;
            }
    
            String nodesFile = args[0];
            String edgesFile = args[1];
            int source = Integer.parseInt(args[3]);
            int goal = Integer.parseInt(args[4]);
    
            PathFinder pf = new PathFinder(nodesFile, edgesFile);
            List<Integer> path = pf.algo(source, goal);
    
            if (path == null) {
                System.out.println("NONE");
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < path.size(); i++) {
                    if (i > 0) sb.append("->");
                    sb.append(path.get(i));
                }
                System.out.println(sb);
    
                double total = 0;
                for (int i = 0; i < path.size() - 1; i++) {
                    total += pf.haversine(pf.nodes.get(path.get(i)), pf.nodes.get(path.get(i + 1)));
                }
                System.out.printf("%.2f%n", total);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }
}
