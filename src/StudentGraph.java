
import java.util.*;

/**
 * Represents a social network graph of UniversityStudent objects.
 */
public class StudentGraph {
    
    public static class Edge {
        public UniversityStudent neighbor;
        public int weight;

        /**
         * Represents an adjacency edge to a neighbor student with a weight.
         *
         * @param neighbor The neighboring student node.
         * @param weight   The connection strength weight.
         */
        public Edge(UniversityStudent neighbor, int weight) {
            this.neighbor = neighbor;
            this.weight = weight;
        }
    }

    private Map<UniversityStudent, List<Edge>> adjacencyList;

    /**
     * Builds a StudentGraph from a list of students by computing pairwise
     * connection strengths and adding edges for positive-strength pairs.
     *
     * @param students The list of students to include in the graph.
     */
    public StudentGraph(List<UniversityStudent> students) {
        adjacencyList = new HashMap<>();
        for (UniversityStudent s : students) {
            adjacencyList.put(s, new ArrayList<>());
        }

        for(int i = 0; i < students.size(); i++) {
            UniversityStudent s1 = students.get(i);
            for(int j = i + 1; j < students.size(); j++) {
                UniversityStudent s2 = students.get(j);
                int strength = s1.calculateConnectionStrength(s2);
                if (strength > 0) {
                    addEdge(s1, s2, strength);
                }
            }
        }
    }

    /**
     * Adds an undirected weighted edge between two students in the graph.
     *
     * @param s1     First student.
     * @param s2     Second student.
     * @param weight Edge weight representing connection strength.
     */
    public void addEdge(UniversityStudent s1, UniversityStudent s2, int weight) {
        adjacencyList.get(s1).add(new Edge(s2, weight));
        adjacencyList.get(s2).add(new Edge(s1, weight));
    }

    /**
     * Returns the list of edges (neighbors) for a given student.
     *
     * @param student The student node whose neighbors are requested.
     * @return A list of Edge objects, or null if the student is not in the graph.
     */
    public List<Edge> getNeighbors(UniversityStudent student) {
        return adjacencyList.get(student);
    }

    /**
     * Returns all student nodes contained in the graph.
     *
     * @return A set of UniversityStudent nodes.
     */
    public Set<UniversityStudent> getAllNodes() {
        return adjacencyList.keySet();
    }

    /**
     * Prints a simple textual representation of the graph to stdout (for debugging).
     */
    public void displayGraph() {
        System.out.println("\nStudent Graph:");
        for (UniversityStudent student : adjacencyList.keySet()) {
            System.out.println(student.getName() + " -> " + adjacencyList.get(student));
        }
    }
}
