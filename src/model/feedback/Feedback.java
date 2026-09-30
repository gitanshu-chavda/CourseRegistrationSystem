package model.feedback;

import java.time.LocalDate;

/**
 * Generic feedback class — T can be Integer (rating) or String (comment).
 *
 * OOP Concepts: Generic Programming (Assignment 2 requirement)
 *
 * Usage:
 *   Feedback<Integer> rating  = new Feedback<>(studentId, courseCode, 5);
 *   Feedback<String>  comment = new Feedback<>(studentId, courseCode, "Great course!");
 */
public class Feedback<T> {

    private final String studentId;
    private final String courseCode;
    private final T value;
    private final LocalDate date;
    private final String type;   // "RATING" or "COMMENT"

    public Feedback(String studentId, String courseCode, T value) {
        this.studentId  = studentId;
        this.courseCode = courseCode;
        this.value      = value;
        this.date       = LocalDate.now();

        // Determine type at runtime from T's actual class
        if (value instanceof Integer) {
            this.type = "RATING";
            int rating = (Integer) value;
            if (rating < 1 || rating > 5)
                throw new IllegalArgumentException("Rating must be between 1 and 5.");
        } else if (value instanceof String) {
            this.type = "COMMENT";
        } else {
            this.type = "OTHER";
        }
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String    getStudentId()  { return studentId; }
    public String    getCourseCode() { return courseCode; }
    public T         getValue()      { return value; }
    public LocalDate getDate()       { return date; }
    public String    getType()       { return type; }

    @Override
    public String toString() {
        if (type.equals("RATING")) {
            int stars = (Integer) value;
            return String.format("  [%s] Rating: %s (%d/5)  — %s",
                    studentId, "★".repeat(stars) + "☆".repeat(5 - stars), stars, date);
        }
        return String.format("  [%s] Comment: \"%s\"  — %s", studentId, value, date);
    }
}
