package service;

import model.course.Enrollment;
import model.user.Student;
import repository.CourseRepository;
import repository.UserRepository;
import strategy.GpaCalculationStrategy;
import strategy.TenPointGpaStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles grade assignment and GPA calculation.
 * Design Pattern: Strategy — GPA calculation algorithm is swappable.
 */
public class GradeService {

    private GpaCalculationStrategy gpaStrategy = new TenPointGpaStrategy();

    private final UserRepository userRepo   = UserRepository.getInstance();
    private final CourseRepository courseRepo = CourseRepository.getInstance();

    private static final List<String> VALID_GRADES =
            List.of("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F");

    // ── Strategy swap ─────────────────────────────────────────────────────────

    public void setGpaStrategy(GpaCalculationStrategy strategy) {
        this.gpaStrategy = strategy;
        System.out.println("  GPA strategy changed to: " + strategy.getName());
    }

    public String getCurrentStrategyName() { return gpaStrategy.getName(); }

    // ── Grade assignment ──────────────────────────────────────────────────────

    /**
     * Assigns a grade to a student for a specific course.
     * Only works for courses in the student's current enrollments.
     */
    public void assignGrade(String studentId, String courseCode, String grade) {
        if (!VALID_GRADES.contains(grade.toUpperCase()))
            throw new IllegalArgumentException("Invalid grade: " + grade
                    + ". Valid: " + String.join(", ", VALID_GRADES));

        Optional<Student> opt = userRepo.findStudentById(studentId);
        if (opt.isEmpty())
            throw new IllegalArgumentException("Student not found: " + studentId);

        Student student = opt.get();
        boolean found = false;
        for (Enrollment e : student.getCurrentEnrollments()) {
            if (e.getCourseCode().equalsIgnoreCase(courseCode)) {
                e.setGrade(grade.toUpperCase());
                found = true;
                break;
            }
        }
        if (!found)
            throw new IllegalArgumentException(
                    studentId + " is not enrolled in " + courseCode);

        System.out.printf("  Grade '%s' assigned to %s for course %s.%n",
                grade.toUpperCase(), student.getName(), courseCode);

        // Auto-complete semester if all grades are now assigned
        if (student.allGradesAssigned()) {
            System.out.println("  All grades assigned. Semester can now be completed.");
        }
    }

    /**
     * Completes the current semester for a student (Admin action).
     * Moves enrollments to history and advances the semester counter.
     */
    public void completeSemester(String studentId) {
        Optional<Student> opt = userRepo.findStudentById(studentId);
        if (opt.isEmpty())
            throw new IllegalArgumentException("Student not found: " + studentId);

        Student student = opt.get();
        if (!student.allGradesAssigned())
            throw new IllegalStateException(
                    "Cannot complete semester: not all grades have been assigned yet.");

        student.completeSemester();
        System.out.printf("  Semester completed for %s. Now in Semester %d.%n",
                student.getName(), student.getCurrentSemester());
    }

    // ── GPA reporting ─────────────────────────────────────────────────────────

    public double calculateSGPA(Student student, int semester) {
        List<Enrollment> sem = new ArrayList<>(
                student.getCompletedSemesters().getOrDefault(semester, List.of()));
        return gpaStrategy.calculate(sem);
    }

    public double calculateCGPA(Student student) {
        List<Enrollment> all = new ArrayList<>();
        student.getCompletedSemesters().values().forEach(all::addAll);
        return gpaStrategy.calculate(all);
    }

    // ── Enrollment lookup (used by TA) ────────────────────────────────────────

    /**
     * Returns all enrollments across all students for a given course.
     */
    public List<Enrollment> getEnrollmentsForCourse(String courseCode) {
        List<Enrollment> result = new ArrayList<>();
        for (Student s : userRepo.getAllStudents()) {
            s.getCurrentEnrollments().stream()
                    .filter(e -> e.getCourseCode().equalsIgnoreCase(courseCode))
                    .forEach(result::add);
        }
        return result;
    }

    public List<String> getValidGrades() { return VALID_GRADES; }
}
