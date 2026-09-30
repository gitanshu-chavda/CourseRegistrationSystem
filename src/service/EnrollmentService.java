package service;

import exception.CourseFullException;
import exception.DropDeadlinePassedException;
import exception.PrerequisiteNotMetException;
import model.course.Course;
import model.course.Enrollment;
import model.user.Student;
import observer.EnrollmentObserver;
import repository.CourseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles all enrollment and drop logic.
 * Design Pattern: Observer — notifies observers on enrollment events.
 * Exception Handling: CourseFullException, DropDeadlinePassedException,
 *                     PrerequisiteNotMetException
 */
public class EnrollmentService {

    private static final int MAX_CREDITS = 20;

    // Drop deadline: within first 6 weeks (day 42).
    // Set to true to simulate deadline passed for testing.
    private boolean dropDeadlinePassed = false;

    private final CourseRepository courseRepo = CourseRepository.getInstance();
    private final List<EnrollmentObserver> observers = new ArrayList<>();

    // ── Observer management ───────────────────────────────────────────────────

    public void addObserver(EnrollmentObserver o)    { observers.add(o); }
    public void removeObserver(EnrollmentObserver o) { observers.remove(o); }

    private void notifyEnrolled(String name, String code, int seats) {
        observers.forEach(o -> o.onCourseEnrolled(name, code, seats));
    }
    private void notifyDropped(String name, String code) {
        observers.forEach(o -> o.onCourseDropped(name, code));
    }
    private void notifyFull(String code) {
        observers.forEach(o -> o.onCourseFull(code));
    }

    // ── Enroll ────────────────────────────────────────────────────────────────

    /**
     * Enrolls a student in a course after checking:
     *  1. Course exists and belongs to the student's current semester
     *  2. Student not already enrolled
     *  3. Credit limit (max 20)
     *  4. Prerequisites met
     *  5. Seats available (throws CourseFullException if full)
     */
    public void enroll(Student student, String courseCode)
            throws CourseFullException, PrerequisiteNotMetException {

        Optional<Course> opt = courseRepo.findByCode(courseCode);
        if (opt.isEmpty())
            throw new IllegalArgumentException("Course not found: " + courseCode);

        Course course = opt.get();

        if (course.getSemester() != student.getCurrentSemester())
            throw new IllegalArgumentException(
                    "Course '" + courseCode + "' is not available in your current semester ("
                    + student.getCurrentSemester() + ").");

        if (student.isEnrolled(courseCode))
            throw new IllegalArgumentException("Already enrolled in " + courseCode);

        if (student.currentCreditLoad() + course.getCredits() > MAX_CREDITS)
            throw new IllegalArgumentException(
                    "Credit limit exceeded. Max " + MAX_CREDITS + " credits allowed. "
                    + "You currently have " + student.currentCreditLoad() + " credits.");

        // Prerequisites check
        List<String> missing = new ArrayList<>();
        for (String prereq : course.getPrerequisites()) {
            if (!student.getCompletedCourseCodes().contains(prereq))
                missing.add(prereq);
        }
        if (!missing.isEmpty())
            throw new PrerequisiteNotMetException(courseCode, missing);

        // Seat check — throw CourseFullException
        if (course.isFull()) {
            notifyFull(courseCode);
            throw new CourseFullException(courseCode);
        }

        // All checks passed — enroll
        Enrollment e = new Enrollment(
                student.getId(), student.getName(),
                courseCode, course.getTitle(), course.getCredits());
        student.enroll(e);
        course.incrementEnrollment();

        int seatsLeft = course.getEnrollmentLimit() - course.getEnrolled();
        notifyEnrolled(student.getName(), courseCode, seatsLeft);

        if (course.isFull()) notifyFull(courseCode);
    }

    // ── Drop ──────────────────────────────────────────────────────────────────

    /**
     * Drops a course for a student.
     * Throws DropDeadlinePassedException if the drop window has closed.
     */
    public void drop(Student student, String courseCode)
            throws DropDeadlinePassedException {

        if (dropDeadlinePassed)
            throw new DropDeadlinePassedException();

        if (!student.isEnrolled(courseCode))
            throw new IllegalArgumentException("Not enrolled in course: " + courseCode);

        student.dropCourse(courseCode);

        courseRepo.findByCode(courseCode).ifPresent(Course::decrementEnrollment);
        notifyDropped(student.getName(), courseCode);
    }

    // ── Deadline control (for testing / admin use) ────────────────────────────

    public void setDropDeadlinePassed(boolean passed) { this.dropDeadlinePassed = passed; }
    public boolean isDropDeadlinePassed()              { return dropDeadlinePassed; }
}
