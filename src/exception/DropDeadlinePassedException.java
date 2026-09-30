package exception;

/**
 * Thrown when a student attempts to drop a course after the allowed deadline.
 * Assignment 2 — Exception Handling requirement
 *
 * Drop deadline is set to Week 6 of the semester (represented as day 42).
 */
public class DropDeadlinePassedException extends Exception {
    public DropDeadlinePassedException() {
        super("Drop deadline has passed. Courses can only be dropped within the first 6 weeks of the semester.");
    }
}
