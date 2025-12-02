import java.util.*;

/**
 * Implements the Gale-Shapley stable matching algorithm to assign roommates.
 */
public class GaleShapley {

    /**
     * Assigns stable roommate pairings for a list of students.
     * <p>
     * This method runs the algorithm and should update each student
     * object with their assigned roommate.
     *
     * @param students The list of all students to be paired.
     */
    public static void assignRoommates(List<UniversityStudent> students) {
        Map<UniversityStudent, UniversityStudent> pairs = new HashMap<>();
        Map<UniversityStudent, Integer> proposals = new HashMap<>();
        Map<String, UniversityStudent> nameToStudent = new HashMap<>();

        for(UniversityStudent s : students) {
            nameToStudent.put(s.getName(), s);
            proposals.put(s, 0);
        }

        Queue<UniversityStudent> freeStudents = new LinkedList<>(students);
        for(UniversityStudent s : students) {
            if(!s.roommatePreferences.isEmpty()) {
                freeStudents.offer(s);
            }
        }

        while(!freeStudents.isEmpty()) {
            UniversityStudent s = freeStudents.poll();
            int index = proposals.get(s);
            if(s.getRoommate() != null || index >= s.roommatePreferences.size()) {
                continue;
            }

            String pref = s.roommatePreferences.get(index);
            proposals.put(s, index + 1);
            UniversityStudent t = nameToStudent.get(pref);
            if(t == null) {
                if(proposals.get(s) < s.roommatePreferences.size()) {
                    freeStudents.offer(s);
                }
                continue;
            }

            if(!t.roommatePreferences.contains(s.getName())) {
                if(proposals.get(s) < s.roommatePreferences.size()) {
                    freeStudents.offer(s);
                }
                continue;
            }

            if(t.getRoommate() == null) {
                pairs.put(s, t);
                pairs.put(t, s);
                s.setRoommate(t);
                t.setRoommate(s);
            } else {
                UniversityStudent current = t.getRoommate();
                int currentIndex = t.roommatePreferences.indexOf(current.getName());
                int newIndex = t.roommatePreferences.indexOf(s.getName());
                if(newIndex < currentIndex) {
                    pairs.put(s, t);
                    pairs.put(t, s);
                    pairs.remove(current);
                    freeStudents.offer(current);
                    current.setRoommate(null);
                    s.setRoommate(t);
                    t.setRoommate(s);
                } else {
                    if(proposals.get(s) < s.roommatePreferences.size()) {
                        freeStudents.offer(s);
                    }
                }
            }
        }
    }
}
