package model.user;

import model.course.Course;
import model.course.Enrollment;
import model.feedback.Feedback;

import java.util.*;

/**
 * Student user.
 * OOP Concepts: Inheritance (extends User), Encapsulation
 */
public class Student extends User {

    private int currentSemester;
    private boolean semesterComplete;

    // Enrolled courses this semester
    private final List<Enrollment> currentEnrollments = new ArrayList<>();

    // Completed semesters: semesterNumber -> list of (courseCode, grade)
    private final Map<Integer, List<Enrollment>> completedSemesters = new LinkedHashMap<>();

    // Feedback given: courseCode -> list of feedback items
    private final Map<String, List<Feedback<?>>> feedbackGiven = new HashMap<>();

    // Submitted complaint IDs
    private final List<String> complaintIds = new ArrayList<>();

    public Student(String id, String name, String email, String password) {
        super(id, name, email, password);
        this.currentSemester = 1;
        this.semesterComplete = false;
    }

    @Override
    public String getRole() { return "Student"; }

    @Override
    public void displayDashboard() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.printf( "║  Welcome, %-22s ║%n", getName());
        System.out.println("║  Role: Student                   ║");
        System.out.printf( "║  Semester: %-21d ║%n", currentSemester);
        System.out.printf( "║  Enrolled in: %-18d ║%n", currentEnrollments.size());
        System.out.println("╚══════════════════════════════════╝");
    }

    // ── Enrollment ──────────────────────────────────────────────────────────

    public void enroll(Enrollment e) {
        currentEnrollments.add(e);
    }

    public boolean isEnrolled(String courseCode) {
        return currentEnrollments.stream()
                .anyMatch(e -> e.getCourseCode().equals(courseCode));
    }

    public void dropCourse(String courseCode) {
        currentEnrollments.removeIf(e -> e.getCourseCode().equals(courseCode));
    }

    public int currentCreditLoad() {
        return currentEnrollments.stream().mapToInt(Enrollment::getCredits).sum();
    }

    public List<Enrollment> getCurrentEnrollments() {
        return Collections.unmodifiableList(currentEnrollments);
    }

    // ── Semester completion ──────────────────────────────────────────────────

    /**
     * Called by Admin when all grades for current semester are assigned.
     * Moves current enrollments to completed history and advances semester.
     */
    public void completeSemester() {
        completedSemesters.put(currentSemester, new ArrayList<>(currentEnrollments));
        currentEnrollments.clear();
        currentSemester++;
        semesterComplete = false;
    }

    public boolean allGradesAssigned() {
        return !currentEnrollments.isEmpty() &&
               currentEnrollments.stream().allMatch(e -> e.getGrade() != null);
    }

    public Map<Integer, List<Enrollment>> getCompletedSemesters() {
        return Collections.unmodifiableMap(completedSemesters);
    }

    // ── Prerequisites check ──────────────────────────────────────────────────

    /**
     * Returns the set of course codes completed in previous semesters.
     */
    public Set<String> getCompletedCourseCodes() {
        Set<String> done = new HashSet<>();
        completedSemesters.values().forEach(list ->
            list.forEach(e -> done.add(e.getCourseCode())));
        return done;
    }

    // ── GPA calculation ──────────────────────────────────────────────────────

    public double calculateCGPA() {
        double totalPoints = 0;
        int totalCredits = 0;
        for (List<Enrollment> sem : completedSemesters.values()) {
            for (Enrollment e : sem) {
                if (e.getGrade() != null) {
                    totalPoints += gradeToPoints(e.getGrade()) * e.getCredits();
                    totalCredits += e.getCredits();
                }
            }
        }
        return totalCredits == 0 ? 0.0 : Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }

    public double calculateSGPA(int semester) {
        List<Enrollment> sem = completedSemesters.get(semester);
        if (sem == null) return 0.0;
        double totalPoints = 0;
        int totalCredits = 0;
        for (Enrollment e : sem) {
            if (e.getGrade() != null) {
                totalPoints += gradeToPoints(e.getGrade()) * e.getCredits();
                totalCredits += e.getCredits();
            }
        }
        return totalCredits == 0 ? 0.0 : Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }

    private double gradeToPoints(String grade) {
        return switch (grade.toUpperCase()) {
            case "A+", "A"  -> 10.0;
            case "A-"       -> 9.0;
            case "B+"       -> 8.0;
            case "B"        -> 7.0;
            case "B-"       -> 6.0;
            case "C+"       -> 5.0;
            case "C"        -> 4.0;
            case "D"        -> 3.0;
            case "F"        -> 0.0;
            default         -> 0.0;
        };
    }

    // ── Feedback ─────────────────────────────────────────────────────────────

    public void addFeedback(String courseCode, Feedback<?> fb) {
        feedbackGiven.computeIfAbsent(courseCode, k -> new ArrayList<>()).add(fb);
    }

    public List<Feedback<?>> getFeedbackForCourse(String courseCode) {
        return feedbackGiven.getOrDefault(courseCode, Collections.emptyList());
    }

    // ── Complaints ───────────────────────────────────────────────────────────

    public void addComplaintId(String id) { complaintIds.add(id); }
    public List<String> getComplaintIds()  { return Collections.unmodifiableList(complaintIds); }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int getCurrentSemester()      { return currentSemester; }
    public boolean isSemesterComplete()  { return semesterComplete; }
    public void setSemesterComplete(boolean b) { this.semesterComplete = b; }

    public String getContactInfo() {
        return String.format("Name: %s | Email: %s | Semester: %d | CGPA: %.2f",
                getName(), getEmail(), currentSemester, calculateCGPA());
    }
}
