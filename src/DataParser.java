import java.io.*;
import java.nio.Buffer;
import java.util.*;

/**
 * A utility class to parse student data from a file.
 */
public class DataParser {

    /**
     * Reads a file and converts its contents into a list of UniversityStudent objects.
     *
     * @param filename The path to the data file.
     * @return A list of UniversityStudent objects.
     * @throws IOException If the file cannot be read.
     */
    public static List<UniversityStudent> parseStudents(String filename) throws IOException {
        List<UniversityStudent> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            Map<String, String> fields = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    if (fields != null) {
                        students.add(buildStudentFromFields(fields));
                        fields = null;
                    }
                    continue;
                }

                if (line.equals("Student:")) {
                    if (fields != null) {
                        students.add(buildStudentFromFields(fields));
                    }
                    fields = new HashMap<>();
                    continue;
                }

                if (!line.contains(":")) {
                    throw new IOException("Parsing error: Incorrect format in line: '" + line + "'. Expected format 'Name: <value>'.");
                }
                String[] kv = line.split(":", 2);
                String key = kv[0].trim();
                String value = kv.length > 1 ? kv[1].trim() : "";
                if (fields == null) {
                    fields = new HashMap<>();
                }
                fields.put(key, value);
            }

            if (fields != null && !fields.isEmpty()) {
                students.add(buildStudentFromFields(fields));
            }
        }

        return students;
    }

    /**
     * Builds a UniversityStudent instance from a map of parsed field keys to values.
     * Validates required fields and converts numeric types, throwing an IOException
     * when required data is missing or malformed.
     *
     * @param fields Map of field names (e.g., "Name", "Age") to their string values.
     * @return A constructed UniversityStudent object.
     * @throws IOException If required fields are missing or numeric parsing fails.
     */
    private static UniversityStudent buildStudentFromFields(Map<String, String> fields) throws IOException {
        String name = fields.get("Name");
        if (name == null || name.isEmpty()) {
            throw new IOException("Parsing error: Missing required field 'Name' in student entry.");
        }

        String ageStr = fields.get("Age");
        String gender = fields.get("Gender");
        String yearStr = fields.get("Year");
        String major = fields.get("Major");
        String gpaStr = fields.get("GPA");
        String roommatePrefStr = fields.get("RoommatePreferences");
        String prevInternStr = fields.get("PreviousInternships");

        if (roommatePrefStr == null) {
            throw new IOException("Parsing error: Missing required field 'RoommatePreferences' in student entry for " + name + ".");
        }

        if (prevInternStr == null) {
            prevInternStr = "";
        }

        int age;
        try {
            age = Integer.parseInt(ageStr != null ? ageStr.trim() : "");
        } catch (NumberFormatException e) {
            throw new IOException("Number format error: Invalid number format for age: '" + ageStr + "' in student entry for " + name + ".");
        }

        int year = 0;
        try {
            year = Integer.parseInt(yearStr != null ? yearStr.trim() : "0");
        } catch (NumberFormatException e) {
            year = 0;
        }

        double gpa;
        try {
            gpa = Double.parseDouble(gpaStr != null ? gpaStr.trim() : "0");
        } catch (NumberFormatException e) {
            throw new IOException("Number format error: Invalid number format for GPA: '" + gpaStr + "' in student entry for " + name + ".");
        }

        List<String> roommatePreferences = parseListField(roommatePrefStr);
        List<String> previousInternships = parseListField(prevInternStr);

        return new UniversityStudent(name, age, gender != null ? gender : "", year, major != null ? major : "", gpa, roommatePreferences, previousInternships);
    }

    private static List<String> parseListField(String raw) {
        /**
         * Parses a comma-separated list field from the input file.
         *
         * @param raw The raw string containing comma-separated values (or "None").
         * @return A list of trimmed string values; returns an empty list for "None" or empty input.
         */
        if (raw == null) return new ArrayList<>();
        if (raw.trim().equalsIgnoreCase("None") || raw.trim().isEmpty()) return new ArrayList<>();
        String[] parts = raw.split(",");
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            String v = p.trim();
            if (!v.isEmpty()) list.add(v);
        }
        return list;
    }
}
