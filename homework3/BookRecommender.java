import java.io.*;
import java.util.*;

public class BookRecommender {

    // adjacency list: bookId -> (neighborBookId -> sharedUserCount)
    private Map<String, Map<String, Integer>> coLikeGraph;

    private Map<String, Set<String>> userToBooks;   // userId  -> Set<bookId>
    private Map<String, Set<String>> bookToUsers;   // bookId  -> Set<userId>

    // parse CSV and build both graphs 
    public BookRecommender(String filename) throws IOException {
        coLikeGraph = new HashMap<>();
        userToBooks  = new HashMap<>();
        bookToUsers  = new HashMap<>();
        parseCSV(filename);
        buildCoLikeGraph();
    }

    private void parseCSV(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            int comma = line.indexOf(',');
            if (comma == -1) continue;
            String userId = line.substring(0, comma).trim();
            String bookId = line.substring(comma + 1).trim();
            userToBooks.computeIfAbsent(userId, k -> new HashSet<>()).add(bookId);
            bookToUsers.computeIfAbsent(bookId, k -> new HashSet<>()).add(userId);
        }
        br.close();
    }

    private void buildCoLikeGraph() {

        for (String bookId : bookToUsers.keySet()) {
            coLikeGraph.put(bookId, new HashMap<>());
        }
        // increment edge weight for every pair of books they liked
        for (Set<String> books : userToBooks.values()) {
            List<String> bookList = new ArrayList<>(books);
            for (int i = 0; i < bookList.size(); i++) {
                for (int j = i + 1; j < bookList.size(); j++) {
                    String a = bookList.get(i);
                    String b = bookList.get(j);
                    coLikeGraph.get(a).merge(b, 1, Integer::sum);
                    coLikeGraph.get(b).merge(a, 1, Integer::sum);
                }
            }
        }
    }

    // highest weight first, alphabetical tie-break
    private static int byWeightDesc(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
        if (!a.getValue().equals(b.getValue())) {
            return Integer.compare(b.getValue(), a.getValue()); // higher weight first
        }
        return a.getKey().compareTo(b.getKey());               // alphabetical tie-break
    }

    public String singleBookMN(String bookId) {
        if (!coLikeGraph.containsKey(bookId)) return "NONE";
        Map<String, Integer> neighbors = coLikeGraph.get(bookId);
        if (neighbors.isEmpty()) return "NONE";

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(neighbors.entrySet());
        sorted.sort(BookRecommender::byWeightDesc);

        return buildCommaList(sorted, 5);
    }

    public String likeHistoryMN(List<String> inputBooks) {
        Set<String> inputSet = new HashSet<>(inputBooks);
        Map<String, Integer> cumulative = new HashMap<>();

        for (String bookId : inputBooks) {
            if (!coLikeGraph.containsKey(bookId)) continue;
            for (Map.Entry<String, Integer> e : coLikeGraph.get(bookId).entrySet()) {
                if (!inputSet.contains(e.getKey())) {
                    cumulative.merge(e.getKey(), e.getValue(), Integer::sum);
                }
            }
        }

        if (cumulative.isEmpty()) return "NONE";

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(cumulative.entrySet());
        sorted.sort(BookRecommender::byWeightDesc);

        return buildCommaList(sorted, 5);
    }

    public String userCF(String targetUserId) {
        if (!userToBooks.containsKey(targetUserId)) return "NONE";
        Set<String> targetBooks = userToBooks.get(targetUserId);

        Set<String> candidateUsers = new HashSet<>();
        for (String book : targetBooks) {
            if (bookToUsers.containsKey(book)) {
                candidateUsers.addAll(bookToUsers.get(book));
            }
        }
        candidateUsers.remove(targetUserId);
        if (candidateUsers.isEmpty()) return "NONE";

        Map<String, Double> jaccardMap = new HashMap<>();
        for (String user : candidateUsers) {
            Set<String> otherBooks = userToBooks.get(user);
            int intersection = 0;
            for (String b : targetBooks) {
                if (otherBooks.contains(b)) intersection++;
            }
            int union = targetBooks.size() + otherBooks.size() - intersection;
            jaccardMap.put(user, (double) intersection / union);
        }

        List<String> userList = new ArrayList<>(candidateUsers);
        userList.sort((a, b) -> {
            int cmp = Double.compare(jaccardMap.get(b), jaccardMap.get(a));
            return cmp != 0 ? cmp : a.compareTo(b);
        });
        List<String> tasteTwins = userList.subList(0, Math.min(5, userList.size()));

        Map<String, Integer> twinLikeCount = new HashMap<>();
        for (String twin : tasteTwins) {
            for (String book : userToBooks.get(twin)) {
                if (!targetBooks.contains(book)) {
                    twinLikeCount.merge(book, 1, Integer::sum);
                }
            }
        }
        if (twinLikeCount.isEmpty()) return "NONE";

        List<String> bookCandidates = new ArrayList<>(twinLikeCount.keySet());
        bookCandidates.sort((a, b) -> {
            double scoreA = (double) twinLikeCount.get(a) / bookToUsers.get(a).size();
            double scoreB = (double) twinLikeCount.get(b) / bookToUsers.get(b).size();
            int cmp = Double.compare(scoreB, scoreA);
            return cmp != 0 ? cmp : a.compareTo(b);
        });

        StringJoiner sj = new StringJoiner(",");
        for (int i = 0; i < Math.min(5, bookCandidates.size()); i++) {
            sj.add(bookCandidates.get(i));
        }
        return sj.toString();
    }

    public String shortestPath(String sourceId, String targetId) {
        if (!coLikeGraph.containsKey(sourceId) || !coLikeGraph.containsKey(targetId)) {
            return "NONE";
        }

        List<Integer> weights = new ArrayList<>();
        Set<String> seenEdges = new HashSet<>();
        for (Map.Entry<String, Map<String, Integer>> entry : coLikeGraph.entrySet()) {
            String node = entry.getKey();
            for (Map.Entry<String, Integer> nbr : entry.getValue().entrySet()) {
                String neighbor = nbr.getKey();
                // key to avoid counting each undirected edge twice
                String edgeKey = node.compareTo(neighbor) < 0
                    ? node + "\0" + neighbor
                    : neighbor + "\0" + node;
                if (seenEdges.add(edgeKey)) {
                    weights.add(nbr.getValue());
                }
            }
        }

        if (weights.isEmpty()) return "NONE";

        Collections.sort(weights);
        int n = weights.size();
        double median = (n % 2 == 1)
            ? weights.get(n / 2)
            : (weights.get(n / 2 - 1) + weights.get(n / 2)) / 2.0;

        Queue<String> queue = new LinkedList<>();
        Map<String, String> parent = new HashMap<>();
        queue.add(sourceId);
        parent.put(sourceId, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(targetId)) {
                return reconstructPath(parent, targetId);
            }
            // Sort neighbors alphabetically for deterministic shortest path
            List<String> neighbors = new ArrayList<>(coLikeGraph.get(current).keySet());
            Collections.sort(neighbors);
            for (String nbr : neighbors) {
                int w = coLikeGraph.get(current).get(nbr);
                if (w >= median && !parent.containsKey(nbr)) {
                    parent.put(nbr, current);
                    queue.add(nbr);
                }
            }
        }

        return "NONE";
    }

    private String reconstructPath(Map<String, String> parent, String target) {
        LinkedList<String> path = new LinkedList<>();
        String node = target;
        while (node != null) {
            path.addFirst(node);
            node = parent.get(node);
        }
        return String.join("->", path);
    }

    private static String buildCommaList(List<Map.Entry<String, Integer>> sorted, int limit) {
        StringJoiner sj = new StringJoiner(",");
        for (int i = 0; i < Math.min(limit, sorted.size()); i++) {
            sj.add(sorted.get(i).getKey());
        }
        return sj.toString();
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Too little arguments");
            return;
        }

        String csvFile = args[0];
        String command = args[1];
        BookRecommender br;
        try {
            br = new BookRecommender(csvFile);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        switch (command) {
            case "single_book_mn":
                if (args.length < 3) { System.out.println("NONE"); return; }
                System.out.println(br.singleBookMN(args[2]));
                break;

            case "like_history_mn":
                List<String> books = new ArrayList<>();
                for (int i = 2; i < args.length; i++) books.add(args[i]);
                System.out.println(br.likeHistoryMN(books));
                break;

            case "user_cf":
                if (args.length < 3) { System.out.println("NONE"); return; }
                System.out.println(br.userCF(args[2]));
                break;

            case "shortest_path":
                if (args.length < 4) { System.out.println("NONE"); return; }
                System.out.println(br.shortestPath(args[2], args[3]));
                break;

            default:
                System.out.println("Unknown command: " + command);
        }
    }
}