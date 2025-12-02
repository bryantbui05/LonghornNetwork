import java.util.*;

/**
 * Finds the strongest referral path from one student to another in a StudentGraph.
 * Uses Dijkstra's graph search algorithm.
 */
public class ReferralPathFinder {
    private StudentGraph graph;
    /**
     * Creates a new ReferralPathFinder.
     *
     * @param graph The StudentGraph containing all students and connections.
     */
    public ReferralPathFinder(StudentGraph graph) {
        // Constructor
        this.graph = graph;
    }

    /**
     * Finds the strongest-connected path from a start student to any student
     * who has interned at the target company.
     *
     * @param start         The student to start the search from.
     * @param targetCompany The company name to find a referral for.
     * @return A list of students representing the path, or an empty list if none found.
     */
    public List<UniversityStudent> findReferralPath(UniversityStudent start, String targetCompany) {
        // Method signature only
        Map<UniversityStudent, Double> distance = new HashMap<>();
        Map<UniversityStudent, UniversityStudent> previous = new HashMap<>();
        Set<UniversityStudent> visited = new HashSet<>();

        for(UniversityStudent s : graph.getAllNodes()) {
            distance.put(s, Double.MAX_VALUE);
            previous.put(s, null);
        }
        distance.put(start, 0.0);

        PriorityQueue<UniversityStudent> pq = new PriorityQueue<>(Comparator.comparingDouble(distance::get));
        pq.add(start);

        while(!pq.isEmpty()) {
            UniversityStudent current = pq.poll();
            if(visited.contains(current)) {
                continue;
            }
            visited.add(current);

            for(String i : current.previousInternships) {
                if(i.equalsIgnoreCase(targetCompany)) {
                    List<UniversityStudent> path = new ArrayList<>();
                    UniversityStudent step = current;
                    while(step != null) {
                        path.add(step);
                        step = previous.get(step);
                    }
                    Collections.reverse(path);
                    return path;
                }
            }

            for(StudentGraph.Edge edge : graph.getNeighbors(current)) {
                UniversityStudent neighbor = edge.neighbor;
                if(visited.contains(neighbor)) {
                    continue;
                }
                double newDistance = distance.get(current) + (1.0 / edge.weight);
                if(newDistance < distance.get(neighbor)) {
                    distance.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                    pq.add(neighbor);
                }
            }
        }

        return new ArrayList<>();
    }
}
