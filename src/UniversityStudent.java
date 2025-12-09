import java.util.*;

/**
 * Represents a university student, extending the base Student class.
 * Holds detailed profile information and must handle concurrent state access.
 */
public class UniversityStudent extends Student {

    private UniversityStudent roommate;
    private List<String> chatHistory;
    private List<String> friendRequestHistory;

    /**
     * Constructs a new UniversityStudent with the given profile information.
     *
     * @param name                 Student's full name.
     * @param age                  Student's age in years.
     * @param gender               Student's reported gender.
     * @param year                 Academic year (e.g., 1..4).
     * @param major                Student's major.
     * @param gpa                  Student's GPA.
     * @param roommatePreferences  Ordered list of preferred roommates by name.
     * @param previousInternships  List of previous internship employers.
     */
    public UniversityStudent(String name, int age, String gender, int year, String major, double gpa, List<String> roommatePreferences, List<String> previousInternships) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.year = year;
        this.major = major;
        this.gpa = gpa;
        this.roommatePreferences = new ArrayList<>(roommatePreferences);
        this.previousInternships = new ArrayList<>(previousInternships);
        // Initialize history lists
        this.chatHistory = new ArrayList<>();
        this.friendRequestHistory = new ArrayList<>();
    }

    /**
     * Sets the roommate for this student.
     *
     * @param roommate The assigned roommate (may be null to clear assignment).
     */
    public void setRoommate(UniversityStudent roommate) {
        this.roommate = roommate;
    }

    /**
     * Returns the assigned roommate for this student.
     *
     * @return The roommate UniversityStudent instance, or null if none assigned.
     */
    public UniversityStudent getRoommate() {
        return this.roommate;
    }

    /**
     * Thread-safe method to add a chat message to history.
     * @param message The message log to add.
     */
    public synchronized void addChat(String message) {
        this.chatHistory.add(message);
    }

    /**
     * Thread-safe retrieval of chat history.
     * @return A copy of the chat history.
     */
    public synchronized List<String> getChatHistory() {
        return new ArrayList<>(this.chatHistory);
    }

    /**
     * Thread-safe method to add a friend request log to history.
     * @param request The request log to add.
     */
    public synchronized void addFriendRequest(String request) {
        this.friendRequestHistory.add(request);
    }

    /**
     * Thread-safe retrieval of friend request history.
     * @return A copy of the friend request history.
     */
    public synchronized List<String> getFriendRequestHistory() {
        return new ArrayList<>(this.friendRequestHistory);
    }

    /**
     * Clears the chat and friend request history. 
     * Useful for resetting state between test runs.
     */
    public synchronized void clearHistory() {
        this.chatHistory.clear();
        this.friendRequestHistory.clear();
    }

    /**
     * Calculates a connection strength score between this student and another student.
     * Scoring rules:
     * - +4 if they are roommates
     * - +3 for each shared internship
     * - +2 if they share the same major
     * - +1 if they are the same age
     *
     * @param other The other student to compare against.
     * @return An integer score (higher is stronger).
     */
    @Override
    public int calculateConnectionStrength(Student other) {
        int strength = 0;
        if (!(other instanceof UniversityStudent)) {
            return strength;
        }
        UniversityStudent otherStudent = (UniversityStudent) other;
        if(this.roommate != null && this.roommate.equals(otherStudent)) {
            strength += 4;
        }
        for(String i : this.previousInternships) {
            if(otherStudent.previousInternships.contains(i)) {
                strength += 3;
            }
        }
        if(this.major.equals(otherStudent.major)) {
            strength += 2;
        }
        if(this.age == otherStudent.age) {
            strength += 1;
        }
        return strength;
    }

    /**
     * Returns the student's name.
     *
     * @return The student's name.
     */
    public String getName() {
        return this.name;
    }
}