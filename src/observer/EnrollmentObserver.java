package observer;

/**
 * Observer interface for the enrollment notification system.
 * Design Pattern: Observer
 *
 * Observers are notified when course enrollment events occur
 * (e.g., a course fills up, a student is dropped).
 */
public interface EnrollmentObserver {
    void onCourseEnrolled(String studentName, String courseCode, int seatsRemaining);
    void onCourseDropped(String studentName, String courseCode);
    void onCourseFull(String courseCode);
}
