import java.util.*;

/**
 * Abstract base class for a student.
 * Contains common attributes like name, major, and GPA.
 */
public abstract class Student {
    protected String name;
    protected int age;
    protected String gender;
    protected int year;
    protected String major;
    protected double gpa;
    protected List<String> roommatePreferences;
    protected List<String> previousInternships;

    /**
     * Calculates a connection strength score between this student and another.
     *
     * @param other The other student to compare against.
     * @return An integer score (higher is stronger).
     */
    public abstract int calculateConnectionStrength(Student other);
}
