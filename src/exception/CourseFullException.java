package exception;

/**
 * Thrown when a student tries to enroll in a course that has no seats left.
 * Assignment 2 — Exception Handling requirement
 */
public class CourseFullException extends RuntimeException {
    private final String courseCode;

    public CourseFullException(String courseCode) {
        super(String.format("Course '%s' is full. No seats available.", courseCode));
        this.courseCode = courseCode;
    }

    public String getCourseCode() { return courseCode; }
}
