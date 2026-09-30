package model.user;

import model.course.Enrollment;
import service.GradeService;

import java.util.List;

/**
 * Teaching Assistant — inherits all Student capabilities,
 * adds grade-viewing and grade-assistance for a specific course.
 *
 * OOP Concepts: Inheritance (extends Student), Polymorphism
 * Assignment 2: Object class / TA role requirement
 */
public class TeachingAssistant extends Student {

    private String assistingCourseCode;   // the course this TA is helping with

    public TeachingAssistant(String id, String name, String email,
                             String password, String assistingCourseCode) {
        super(id, name, email, password);
        this.assistingCourseCode = assistingCourseCode;
    }

    @Override
    public String getRole() { return "TA"; }

    @Override
    public void displayDashboard() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.printf( "║  Welcome, TA %-24s ║%n", getName());
        System.out.println("║  Role: Teaching Assistant            ║");
        System.out.printf( "║  Assisting course: %-17s ║%n", assistingCourseCode);
        System.out.println("╚══════════════════════════════════════╝");
    }

    // ── TA-only methods ──────────────────────────────────────────────────────

    /**
     * View all student grades for the assisted course.
     * TAs can view but NOT update course details (no professor privileges).
     */
    public void viewStudentGradesForCourse(GradeService gradeService) {
        System.out.println("\n── Grades for course: " + assistingCourseCode + " ──");
        List<Enrollment> enrollments = gradeService.getEnrollmentsForCourse(assistingCourseCode);
        if (enrollments.isEmpty()) {
            System.out.println("  No students enrolled.");
            return;
        }
        System.out.printf("  %-12s %-20s %-8s%n", "Student ID", "Name", "Grade");
        System.out.println("  " + "─".repeat(42));
        for (Enrollment e : enrollments) {
            System.out.printf("  %-12s %-20s %-8s%n",
                    e.getStudentId(),
                    e.getStudentName(),
                    e.getGrade() == null ? "Pending" : e.getGrade());
        }
    }

    /**
     * Suggest a grade (marks it as "suggested by TA" — Admin/Prof must confirm).
     */
    public void suggestGrade(String studentId, String grade, GradeService gradeService) {
        System.out.printf("  [TA Suggestion] Grade %s for student %s in %s — pending professor approval.%n",
                grade, studentId, assistingCourseCode);
        // In a real system this would go to a pending queue; here we log it.
    }

    // Getters / setters
    public String getAssistingCourseCode()             { return assistingCourseCode; }
    public void   setAssistingCourseCode(String code)  { this.assistingCourseCode = code; }
}
