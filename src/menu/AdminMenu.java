package menu;

import model.course.Complaint;
import model.course.Complaint.Status;
import model.course.Course;
import model.user.Admin;
import model.user.Professor;
import model.user.Student;
import repository.CourseRepository;
import repository.UserRepository;
import service.ComplaintService;
import service.GradeService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Admin-facing menu.
 * Design Pattern: Template Method (extends BaseMenu)
 */
public class AdminMenu extends BaseMenu {

    private final Admin admin;
    private final GradeService gradeService;
    private final ComplaintService complaintService;
    private final CourseRepository courseRepo = CourseRepository.getInstance();
    private final UserRepository userRepo = UserRepository.getInstance();

    public AdminMenu(Admin admin, GradeService gs, ComplaintService cs, Scanner scanner) {
        super(scanner);
        this.admin = admin;
        this.gradeService = gs;
        this.complaintService = cs;
    }

    @Override
    protected void printOptions() {
        admin.displayDashboard();
        System.out.println("  ── Admin Menu ──");
        System.out.println("  1. Manage course catalog");
        System.out.println("  2. Manage student records");
        System.out.println("  3. Assign professor to course");
        System.out.println("  4. Handle complaints");
        System.out.println("  5. Assign grades to student");
        System.out.println("  6. Complete student semester");
        System.out.println("  0. Logout");
    }

    @Override
    protected void handleChoice(int choice) {
        switch (choice) {
            case 1 -> manageCatalog();
            case 2 -> manageStudents();
            case 3 -> assignProfessor();
            case 4 -> handleComplaints();
            case 5 -> assignGrade();
            case 6 -> completeSemester();
            default -> System.out.println("  Invalid option.");
        }
    }

    // ── 1. Manage catalog ─────────────────────────────────────────────────────

    private void manageCatalog() {
        System.out.println("\n── Course Catalog Management ──");
        System.out.println("  1. View all courses");
        System.out.println("  2. Add a new course");
        System.out.println("  3. Delete a course");
        System.out.print("  Choice: ");
        int choice = readInt();

        switch (choice) {
            case 1 -> {
                System.out.println("\n── All Courses ──");
                List<Course> all = courseRepo.getAll();
                if (all.isEmpty()) { System.out.println("  No courses in catalog."); }
                else all.forEach(c -> c.printDetails(
                        userRepo.findProfessorById(c.getProfessorId() == null ? "" : c.getProfessorId())
                                .map(p -> p.getName()).orElse("TBA")));
            }
            case 2 -> addCourse();
            case 3 -> {
                String code = readLine("Enter course code to delete");
                if (courseRepo.remove(code.toUpperCase())) {
                    System.out.println("  Course " + code.toUpperCase() + " deleted.");
                } else {
                    System.out.println("  Course not found.");
                }
            }
            default -> System.out.println("  Invalid option.");
        }
        pause();
    }

    private void addCourse() {
        System.out.println("\n  ── Add New Course ──");
        String code    = readLine("Course code (e.g. CS101)").toUpperCase();
        if (courseRepo.exists(code)) {
            System.out.println("  Course code already exists.");
            return;
        }
        String title   = readLine("Course title");
        int credits    = readIntInRange("Credits (2 or 4)", 2, 4);
        if (credits != 2 && credits != 4) { System.out.println("  Credits must be 2 or 4."); return; }
        int semester   = readIntInRange("Semester number", 1, 8);
        int limit      = readIntInRange("Enrollment limit", 1, 200);
        String sched   = readLine("Schedule (e.g. Mon/Wed 09:00-10:30)");
        String loc     = readLine("Location (e.g. Room 101)");
        String prereqInput = readLine("Prerequisites (comma-separated codes, or leave blank)");

        List<String> prereqs = prereqInput.isBlank()
                ? List.of()
                : Arrays.stream(prereqInput.split(","))
                        .map(String::trim).map(String::toUpperCase).toList();

        Course c = new Course(code, title, credits, semester, limit, sched, loc, prereqs);
        courseRepo.add(c);
        System.out.println("  Course " + code + " added successfully.");
    }

    // ── 2. Manage student records ─────────────────────────────────────────────

    private void manageStudents() {
        System.out.println("\n── Student Records ──");
        System.out.println("  1. View all students");
        System.out.println("  2. View / update specific student");
        System.out.print("  Choice: ");
        int choice = readInt();

        if (choice == 1) {
            userRepo.getAllStudents().forEach(s -> System.out.println("  " + s));
        } else if (choice == 2) {
            String id = readLine("Enter student ID");
            Optional<Student> opt = userRepo.findStudentById(id);
            if (opt.isEmpty()) { System.out.println("  Student not found."); pause(); return; }
            Student s = opt.get();
            System.out.println("\n  " + s.getContactInfo());
            System.out.println("  1. Update name");
            System.out.println("  2. Update email");
            System.out.print("  Choice (0 to cancel): ");
            int sub = readInt();
            if (sub == 1) {
                s.setName(readLine("New name"));
                System.out.println("  Name updated.");
            } else if (sub == 2) {
                s.setEmail(readLine("New email"));
                System.out.println("  Email updated.");
            }
        }
        pause();
    }

    // ── 3. Assign professor ───────────────────────────────────────────────────

    private void assignProfessor() {
        System.out.println("\n── Assign Professor to Course ──");

        System.out.println("\n  Available professors:");
        userRepo.getAllProfessors().forEach(p ->
                System.out.printf("  %-8s %-22s %s%n", p.getId(), p.getName(), p.getDepartment()));

        System.out.println("\n  Courses without professor:");
        courseRepo.getAll().stream()
                .filter(c -> c.getProfessorId() == null || c.getProfessorId().isBlank())
                .forEach(c -> System.out.printf("  %-8s %s%n", c.getCode(), c.getTitle()));

        String profId  = readLine("Enter professor ID");
        String code    = readLine("Enter course code").toUpperCase();

        Optional<Professor> profOpt   = userRepo.findProfessorById(profId);
        Optional<Course>    courseOpt = courseRepo.findByCode(code);

        if (profOpt.isEmpty())   { System.out.println("  Professor not found."); pause(); return; }
        if (courseOpt.isEmpty()) { System.out.println("  Course not found.");    pause(); return; }

        Professor prof = profOpt.get();
        Course course  = courseOpt.get();

        // Unassign from old professor if any
        if (course.getProfessorId() != null) {
            userRepo.findProfessorById(course.getProfessorId())
                    .ifPresent(old -> old.unassignCourse(code));
        }

        course.setProfessorId(prof.getId());
        prof.assignCourse(code);
        System.out.printf("  Prof. %s assigned to course %s.%n", prof.getName(), code);
        pause();
    }

    // ── 4. Handle complaints ──────────────────────────────────────────────────

    private void handleComplaints() {
        System.out.println("\n── Complaints Management ──");
        System.out.println("  1. View all complaints");
        System.out.println("  2. View pending complaints");
        System.out.println("  3. View resolved complaints");
        System.out.println("  4. Resolve a complaint");
        System.out.print("  Choice: ");
        int choice = readInt();

        List<Complaint> list = switch (choice) {
            case 1 -> complaintService.getAll();
            case 2 -> complaintService.filterByStatus(Status.PENDING);
            case 3 -> complaintService.filterByStatus(Status.RESOLVED);
            default -> List.of();
        };

        if (choice >= 1 && choice <= 3) {
            if (list.isEmpty()) System.out.println("  No complaints found.");
            else list.forEach(Complaint::print);
        }

        if (choice == 4) {
            String id = readLine("Enter complaint ID");
            String resolution = readLine("Enter resolution details");
            try {
                complaintService.resolve(id, resolution);
            } catch (IllegalArgumentException e) {
                System.out.println("  ERROR: " + e.getMessage());
            }
        }
        pause();
    }

    // ── 5. Assign grade ───────────────────────────────────────────────────────

    private void assignGrade() {
        String studentId = readLine("Student ID");
        String courseCode = readLine("Course code").toUpperCase();
        String grade = readLine("Grade (" + String.join(", ", gradeService.getValidGrades()) + ")");
        try {
            gradeService.assignGrade(studentId, courseCode, grade);
        } catch (IllegalArgumentException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        pause();
    }

    // ── 6. Complete semester ──────────────────────────────────────────────────

    private void completeSemester() {
        System.out.println("\n── Complete Student Semester ──");
        System.out.println("  (All grades must be assigned before completing a semester.)");
        String studentId = readLine("Enter student ID");
        try {
            gradeService.completeSemester(studentId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        pause();
    }
}
