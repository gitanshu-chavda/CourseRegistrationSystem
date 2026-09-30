package menu;

import exception.CourseFullException;
import exception.DropDeadlinePassedException;
import exception.PrerequisiteNotMetException;
import model.course.Complaint;
import model.course.Course;
import model.course.Enrollment;
import model.user.Student;
import model.user.TeachingAssistant;
import repository.CourseRepository;
import repository.UserRepository;
import service.*;
import strategy.FourPointGpaStrategy;
import strategy.TenPointGpaStrategy;

import java.util.List;
import java.util.Scanner;

/**
 * Student-facing menu.
 * Design Pattern: Template Method (extends BaseMenu)
 */
public class StudentMenu extends BaseMenu {

    private final Student student;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final ComplaintService complaintService;
    private final FeedbackService feedbackService;
    private final CourseRepository courseRepo = CourseRepository.getInstance();

    public StudentMenu(Student student, EnrollmentService es, GradeService gs,
                       ComplaintService cs, FeedbackService fs, Scanner scanner) {
        super(scanner);
        this.student = student;
        this.enrollmentService = es;
        this.gradeService = gs;
        this.complaintService = cs;
        this.feedbackService = fs;
    }

    @Override
    protected void printOptions() {
        student.displayDashboard();
        System.out.println("  ── Student Menu ──");
        System.out.println("  1. View available courses");
        System.out.println("  2. Register for a course");
        System.out.println("  3. View my schedule");
        System.out.println("  4. View academic progress (grades + GPA)");
        System.out.println("  5. Drop a course");
        System.out.println("  6. Submit a complaint");
        System.out.println("  7. View my complaints");
        System.out.println("  8. Give course feedback");
        if (student instanceof TeachingAssistant ta) {
            System.out.println("  9. [TA] View student grades for assigned course");
        }
        System.out.println("  0. Logout");
    }

    @Override
    protected void handleChoice(int choice) {
        switch (choice) {
            case 1 -> viewAvailableCourses();
            case 2 -> registerForCourse();
            case 3 -> viewSchedule();
            case 4 -> viewAcademicProgress();
            case 5 -> dropCourse();
            case 6 -> submitComplaint();
            case 7 -> viewComplaints();
            case 8 -> giveFeedback();
            case 9 -> {
                if (student instanceof TeachingAssistant ta)
                    ta.viewStudentGradesForCourse(gradeService);
                else
                    System.out.println("  Invalid option.");
            }
            default -> System.out.println("  Invalid option. Please try again.");
        }
    }

    // ── 1. View available courses ─────────────────────────────────────────────

    private void viewAvailableCourses() {
        System.out.println("\n── Courses available for Semester " + student.getCurrentSemester() + " ──");
        List<Course> courses = courseRepo.getBySemester(student.getCurrentSemester());
        if (courses.isEmpty()) {
            System.out.println("  No courses available for this semester yet.");
            return;
        }
        printSeparator();
        for (Course c : courses) {
            String profName = UserRepository.getInstance()
                    .findProfessorById(c.getProfessorId() == null ? "" : c.getProfessorId())
                    .map(p -> p.getName()).orElse("TBA");
            c.printDetails(profName);
            printSeparator();
        }
        pause();
    }

    // ── 2. Register for a course ──────────────────────────────────────────────

    private void registerForCourse() {
        viewAvailableCourses();
        String code = readLine("Enter course code to register");
        if (code.isBlank()) return;

        try {
            enrollmentService.enroll(student, code.toUpperCase());
            System.out.println("  Successfully registered for " + code.toUpperCase());
        } catch (CourseFullException e) {
            System.out.println("  ERROR: " + e.getMessage());
        } catch (PrerequisiteNotMetException e) {
            System.out.println("  ERROR: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        pause();
    }

    // ── 3. View schedule ──────────────────────────────────────────────────────

    private void viewSchedule() {
        System.out.println("\n── Your Weekly Schedule (Semester " + student.getCurrentSemester() + ") ──");
        List<Enrollment> enrollments = student.getCurrentEnrollments();
        if (enrollments.isEmpty()) {
            System.out.println("  You are not enrolled in any courses.");
            pause();
            return;
        }
        printSeparator();
        System.out.printf("  %-8s %-28s %-22s %-20s%n",
                "Code", "Course", "Schedule", "Location");
        printSeparator();
        for (Enrollment e : enrollments) {
            courseRepo.findByCode(e.getCourseCode()).ifPresent(c -> {
                String profName = UserRepository.getInstance()
                        .findProfessorById(c.getProfessorId() == null ? "" : c.getProfessorId())
                        .map(p -> p.getName()).orElse("TBA");
                System.out.printf("  %-8s %-28s %-22s %-20s  Prof: %s%n",
                        c.getCode(), c.getTitle(), c.getSchedule(), c.getLocation(), profName);
            });
        }
        pause();
    }

    // ── 4. Academic progress ──────────────────────────────────────────────────

    private void viewAcademicProgress() {
        System.out.println("\n── Academic Progress ──");

        // Choose GPA scale
        System.out.println("  Select GPA scale:");
        System.out.println("  1. 10-point scale");
        System.out.println("  2. 4-point scale");
        System.out.print("  Choice: ");
        int scaleChoice = readInt();
        if (scaleChoice == 2)
            gradeService.setGpaStrategy(new FourPointGpaStrategy());
        else
            gradeService.setGpaStrategy(new TenPointGpaStrategy());

        // Current semester enrollments
        System.out.println("\n  Current semester (" + student.getCurrentSemester() + ") — enrolled courses:");
        if (student.getCurrentEnrollments().isEmpty()) {
            System.out.println("  None.");
        } else {
            student.getCurrentEnrollments().forEach(e ->
                    System.out.println("    " + e));
        }

        // Completed semesters
        if (student.getCompletedSemesters().isEmpty()) {
            System.out.println("\n  No completed semesters yet.");
        } else {
            student.getCompletedSemesters().forEach((sem, enrollments) -> {
                System.out.println("\n  Semester " + sem + ":");
                enrollments.forEach(e -> System.out.println("    " + e));
                double sgpa = gradeService.calculateSGPA(student, sem);
                System.out.printf("  SGPA (Semester %d): %.2f%n", sem, sgpa);
            });
            double cgpa = gradeService.calculateCGPA(student);
            System.out.printf("\n  ╔══════════════════════╗%n");
            System.out.printf("  ║  CGPA: %-14.2f║%n", cgpa);
            System.out.printf("  ╚══════════════════════╝%n");
        }
        pause();
    }

    // ── 5. Drop course ────────────────────────────────────────────────────────

    private void dropCourse() {
        List<Enrollment> enrollments = student.getCurrentEnrollments();
        if (enrollments.isEmpty()) {
            System.out.println("  You have no courses to drop.");
            pause();
            return;
        }
        System.out.println("\n── Currently enrolled courses ──");
        enrollments.forEach(e -> System.out.println("  " + e.getCourseCode() + " — " + e.getCourseTitle()));

        String code = readLine("Enter course code to drop (or leave blank to cancel)");
        if (code.isBlank()) return;

        try {
            enrollmentService.drop(student, code.toUpperCase());
            System.out.println("  Course " + code.toUpperCase() + " dropped successfully.");
        } catch (DropDeadlinePassedException e) {
            System.out.println("  ERROR: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        pause();
    }

    // ── 6. Submit complaint ───────────────────────────────────────────────────

    private void submitComplaint() {
        System.out.println("\n── Submit a Complaint ──");
        String desc = readLine("Describe your issue");
        if (desc.isBlank()) {
            System.out.println("  Complaint cannot be empty.");
            return;
        }
        complaintService.submit(student, desc);
        pause();
    }

    // ── 7. View complaints ────────────────────────────────────────────────────

    private void viewComplaints() {
        System.out.println("\n── Your Complaints ──");
        List<Complaint> complaints = complaintService.getByStudent(student.getId());
        if (complaints.isEmpty()) {
            System.out.println("  No complaints submitted.");
        } else {
            complaints.forEach(Complaint::print);
        }
        pause();
    }

    // ── 8. Give feedback ──────────────────────────────────────────────────────

    private void giveFeedback() {
        System.out.println("\n── Course Feedback ──");

        // Show completed courses
        if (student.getCompletedSemesters().isEmpty()) {
            System.out.println("  You have no completed courses to give feedback on.");
            pause();
            return;
        }

        System.out.println("  Completed courses:");
        student.getCompletedSemesters().values().stream()
                .flatMap(List::stream)
                .forEach(e -> System.out.println("    " + e.getCourseCode() + " — " + e.getCourseTitle()));

        String code = readLine("Enter course code");
        if (code.isBlank()) return;

        System.out.println("  What would you like to submit?");
        System.out.println("  1. Numeric rating (1–5)");
        System.out.println("  2. Text comment");
        System.out.println("  3. Both");
        System.out.print("  Choice: ");
        int choice = readInt();

        try {
            if (choice == 1 || choice == 3) {
                int rating = readIntInRange("Enter rating", 1, 5);
                feedbackService.submitRating(student, code.toUpperCase(), rating);
            }
            if (choice == 2 || choice == 3) {
                String comment = readLine("Enter your comment");
                feedbackService.submitComment(student, code.toUpperCase(), comment);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        pause();
    }
}
