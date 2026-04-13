import csv
import math
import heapq
import sys


class Node:
    def __init__(self, id, name, lat, lon):
        self.id = id
        self.name = name
        self.lat = lat
        self.lon = lon
        self.d = float('inf')   
        self.f = float('inf')   
        self.predecessor = None

class PathFinder:
    def __init__(self, nodes_file, edges_file):
        self.nodes = {}        
        self.graph = {}   
        self.load_nodes(nodes_file)
        self.load_edges(edges_file)

    def load_nodes(self, filename):
        with open(filename, newline='') as f:
            reader = csv.reader(f)
            next(reader)  # skip header
            for row in reader:
                stop_id = int(row[0].strip())
                self.nodes[stop_id] = Node(
                    stop_id,
                    row[1].strip(),
                    float(row[2].strip()),
                    float(row[3].strip())
                )

    def load_edges(self, filename):
        with open(filename, newline='') as f:
            reader = csv.reader(f)
            next(reader)  # skip header
            for row in reader:
                src = int(row[0].strip())
                tgt = int(row[1].strip())
                dist = self.haversine(self.nodes[src], self.nodes[tgt])

                
                if src not in self.graph:
                    self.graph[src] = []
                self.graph[src].append((tgt, dist))

                if tgt not in self.graph:
                    self.graph[tgt] = []
                self.graph[tgt].append((src, dist))

    def haversine(self, a, b):
        lat1 = math.radians(a.lat)
        lat2 = math.radians(b.lat)
        d_lat = math.radians(b.lat - a.lat)
        d_lon = math.radians(b.lon - a.lon)

        h = math.sin(d_lat / 2) ** 2 + math.cos(lat1) * math.cos(lat2) * math.sin(d_lon / 2) ** 2
        return 6371 * 2 * math.asin(math.sqrt(h))

    def init_algo(self, start_id, end_id):
        for v in self.nodes.values():
            v.d = float('inf')
            v.f = float('inf')
            v.predecessor = None
        s = self.nodes[start_id]
        s.d = 0
        s.f = self.haversine(s, self.nodes[end_id])

    def relax_algo(self, u, v, weight, end_id, heap, counter):
        if v.d > u.d + weight:
            v.d = u.d + weight
            v.f = v.d + self.haversine(v, self.nodes[end_id])
            v.predecessor = u.id
            
            heapq.heappush(heap, (v.f, str(v.id), next(counter), v))

    def algo(self, start_id, end_id):
        self.init_algo(start_id, end_id)

        counter = iter(range(10**9))  # unique tiebreaker to avoid comparing Nodes

        # Build initial heap from all nodes
        heap = []
        for node in self.nodes.values():
            heapq.heappush(heap, (node.f, str(node.id), next(counter), node))

        visited = set()  # HashSet<Integer>

        while heap:
            f, _, __, u = heapq.heappop(heap)

            if u.id in visited:
                continue 

            if u.id == end_id:
                return self.reconstruct_path(end_id)

            visited.add(u.id)

            for (tgt_id, weight) in self.graph.get(u.id, []):
                v = self.nodes[tgt_id]
                if v.id in visited:
                    continue
                self.relax_algo(u, v, weight, end_id, heap, counter)

        return None  # no path found

    def reconstruct_path(self, end_id):
        path = []
        current = end_id
        while current is not None:
            path.append(current)
            current = self.nodes[current].predecessor
        path.reverse()
        return path

if __name__ == "__main__":
    if len(sys.argv) < 6:
        print("Not enough arguments")
        sys.exit(1)

    nodes_file = sys.argv[1]
    edges_file = sys.argv[2]
    
    source_id = int(sys.argv[4])
    target_id = int(sys.argv[5])

    pf = PathFinder(nodes_file, edges_file)
    path = pf.algo(source_id, target_id)

    if path is None:
        print("NONE")
    else:
        
        print("->".join(str(n) for n in path))

        total = sum(
            pf.haversine(pf.nodes[path[i]], pf.nodes[path[i+1]])
            for i in range(len(path) - 1)
        )
        print(f"{total:.2f}")