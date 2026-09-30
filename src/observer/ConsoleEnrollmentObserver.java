package observer;

/**
 * Concrete observer — prints enrollment events to the console.
 * Design Pattern: Observer (ConcreteObserver)
 *
 * In a production system this could send emails or push notifications.
 */
public class ConsoleEnrollmentObserver implements EnrollmentObserver {

    @Override
    public void onCourseEnrolled(String studentName, String courseCode, int seatsRemaining) {
        System.out.printf("  [NOTIFICATION] %s successfully enrolled in %s. Seats remaining: %d%n",
                studentName, courseCode, seatsRemaining);
    }

    @Override
    public void onCourseDropped(String studentName, String courseCode) {
        System.out.printf("  [NOTIFICATION] %s dropped course %s.%n", studentName, courseCode);
    }

    @Override
    public void onCourseFull(String courseCode) {
        System.out.printf("  [NOTIFICATION] Course %s is now FULL. No more registrations allowed.%n", courseCode);
    }
}
