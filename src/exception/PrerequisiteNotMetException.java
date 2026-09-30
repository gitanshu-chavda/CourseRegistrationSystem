package exception;

import java.util.List;

/**
 * Thrown when a student has not completed the prerequisites for a course.
 */
public class PrerequisiteNotMetException extends Exception {
    public PrerequisiteNotMetException(String courseCode, List<String> missing) {
        super(String.format("Cannot register for '%s'. Missing prerequisites: %s",
                courseCode, String.join(", ", missing)));
    }
}
