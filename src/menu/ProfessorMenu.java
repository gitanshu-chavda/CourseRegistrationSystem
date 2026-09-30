package menu;

import model.course.Course;
import model.course.Enrollment;
import model.user.Professor;
import model.user.Student;
import repository.CourseRepository;
import repository.UserRepository;
import service.FeedbackService;
import service.GradeService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Professor-facing menu.
 * Design Pattern: Template Method (extends BaseMenu)
 */
public class ProfessorMenu extends BaseMenu {

    private final Professor professor;
    private final GradeService gradeService;
    private final FeedbackService feedbackService;
    private final CourseRepository courseRepo = CourseRepository.getInstance();
    private final UserRepository userRepo = UserRepository.getInstance();

    public ProfessorMenu(Professor professor, GradeService gs,
                         FeedbackService fs, Scanner scanner) {
        super(scanner);
        this.professor = professor;
        this.gradeService = gs;
        this.feedbackService = fs;
    }

    @Override
    protected void printOptions() {
        professor.displayDashboard();
        System.out.println("  ── Professor Menu ──");
        System.out.println("  1. View my courses");
        System.out.println("  2. Update course details");
        System.out.println("  3. View enrolled students");
        System.out.println("  4. Assign grades");
        System.out.println("  5. View course feedback");
        System.out.println("  0. Logout");
    }

    @Override
    protected void handleChoice(int choice) {
        switch (choice) {
            case 1 -> viewMyCourses();
            case 2 -> updateCourseDetails();
            case 3 -> viewEnrolledStudents();
            case 4 -> assignGrades();
            case 5 -> viewFeedback();
            default -> System.out.println("  Invalid option.");
        }
    }

    // ── 1. View my courses ────────────────────────────────────────────────────

    private void viewMyCourses() {
        System.out.println("\n── Your Courses ──");
        List<Course> courses = courseRepo.getByProfessor(professor.getId());
        if (courses.isEmpty()) {
            System.out.println("  No courses assigned yet.");
            pause();
            return;
        }
        printSeparator();
        for (Course c : courses) {
            c.printDetails(professor.getName());
            System.out.println("    Syllabus: " + c.getSyllabus());
            printSeparator();
        }
        pause();
    }

    // ── 2. Update course details ──────────────────────────────────────────────

    private void updateCourseDetails() {
        List<Course> courses = courseRepo.getByProfessor(professor.getId());
        if (courses.isEmpty()) {
            System.out.println("  No courses assigned to you.");
            pause();
            return;
        }

        System.out.println("\n  Your courses:");
        courses.forEach(c -> System.out.printf("    %-8s %s%n", c.getCode(), c.getTitle()));
        String code = readLine("Enter course code to update");
        Optional<Course> opt = courseRepo.findByCode(code.toUpperCase());

        if (opt.isEmpty() || !professor.getId().equals(opt.get().getProfessorId())) {
            System.out.println("  Course not found or not assigned to you.");
            pause();
            return;
        }

        Course course = opt.get();
        System.out.println("\n  What would you like to update?");
        System.out.println("  1. Syllabus");
        System.out.println("  2. Schedule / timings");
        System.out.println("  3. Location");
        System.out.println("  4. Credits (2 or 4)");
        System.out.println("  5. Enrollment limit");
        System.out.println("  6. Office hours");
        System.out.print("  Choice: ");
        int choice = readInt();

        switch (choice) {
            case 1 -> {
                String syl = readLine("Enter new syllabus");
                course.setSyllabus(syl);
                System.out.println("  Syllabus updated.");
            }
            case 2 -> {
                String sched = readLine("Enter new schedule (e.g. Mon/Wed 10:00-11:30)");
                course.setSchedule(sched);
                System.out.println("  Schedule updated.");
            }
            case 3 -> {
                String loc = readLine("Enter new location");
                course.setLocation(loc);
                System.out.println("  Location updated.");
            }
            case 4 -> {
                int credits = readIntInRange("Enter credits", 2, 4);
                if (credits == 2 || credits == 4) {
                    course.setCredits(credits);
                    System.out.println("  Credits updated.");
                } else {
                    System.out.println("  Credits must be 2 or 4.");
                }
            }
            case 5 -> {
                int limit = readIntInRange("Enter new enrollment limit", 1, 200);
                course.setEnrollmentLimit(limit);
                System.out.println("  Enrollment limit updated.");
            }
            case 6 -> {
                String oh = readLine("Enter office hours (e.g. Tue 14:00-16:00)");
                professor.setOfficeHours(oh);
                System.out.println("  Office hours updated.");
            }
            default -> System.out.println("  Invalid option.");
        }
        pause();
    }

    // ── 3. View enrolled students ─────────────────────────────────────────────

    private void viewEnrolledStudents() {
        System.out.println("\n  Your courses:");
        List<Course> courses = courseRepo.getByProfessor(professor.getId());
        if (courses.isEmpty()) {
            System.out.println("  No courses assigned.");
            pause();
            return;
        }
        courses.forEach(c -> System.out.printf("    %-8s %s%n", c.getCode(), c.getTitle()));
        String code = readLine("Enter course code");

        List<Enrollment> enrollments = gradeService.getEnrollmentsForCourse(code.toUpperCase());
        System.out.println("\n── Enrolled Students in " + code.toUpperCase() + " ──");
        if (enrollments.isEmpty()) {
            System.out.println("  No students enrolled.");
            pause();
            return;
        }
        printSeparator();
        System.out.printf("  %-10s %-22s %-8s %-30s%n", "ID", "Name", "Sem", "Contact");
        printSeparator();
        for (Enrollment e : enrollments) {
            userRepo.findStudentById(e.getStudentId()).ifPresent(s ->
                System.out.printf("  %-10s %-22s %-8d %-30s%n",
                        s.getId(), s.getName(), s.getCurrentSemester(), s.getEmail()));
        }
        pause();
    }

    // ── 4. Assign grades ──────────────────────────────────────────────────────

    private void assignGrades() {
        List<Course> courses = courseRepo.getByProfessor(professor.getId());
        if (courses.isEmpty()) {
            System.out.println("  No courses assigned.");
            pause();
            return;
        }
        courses.forEach(c -> System.out.printf("    %-8s %s%n", c.getCode(), c.getTitle()));
        String code = readLine("Enter course code");

        List<Enrollment> enrollments = gradeService.getEnrollmentsForCourse(code.toUpperCase());
        if (enrollments.isEmpty()) {
            System.out.println("  No students enrolled in " + code.toUpperCase());
            pause();
            return;
        }

        System.out.println("\n  Valid grades: " + String.join(", ", gradeService.getValidGrades()));
        for (Enrollment e : enrollments) {
            System.out.printf("%n  Student: %s (%s) — Current grade: %s%n",
                    e.getStudentName(), e.getStudentId(),
                    e.getGrade() == null ? "Not assigned" : e.getGrade());
            String grade = readLine("Enter grade (or press Enter to skip)");
            if (!grade.isBlank()) {
                try {
                    gradeService.assignGrade(e.getStudentId(), code.toUpperCase(), grade);
                } catch (IllegalArgumentException ex) {
                    System.out.println("  ERROR: " + ex.getMessage());
                }
            }
        }
        pause();
    }

    // ── 5. View feedback ──────────────────────────────────────────────────────

    private void viewFeedback() {
        List<Course> courses = courseRepo.getByProfessor(professor.getId());
        if (courses.isEmpty()) {
            System.out.println("  No courses assigned.");
            pause();
            return;
        }
        courses.forEach(c -> System.out.printf("    %-8s %s%n", c.getCode(), c.getTitle()));
        String code = readLine("Enter course code to view feedback");
        feedbackService.printFeedbackSummary(code.toUpperCase());
        pause();
    }
}
