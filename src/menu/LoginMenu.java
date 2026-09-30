package menu;

import exception.InvalidLoginException;
import model.user.*;
import service.*;

import java.util.Scanner;

/**
 * Top-level login/signup menu shown at application start.
 */
public class LoginMenu extends BaseMenu {

    private final AuthService authService;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final ComplaintService complaintService;
    private final FeedbackService feedbackService;

    public LoginMenu(AuthService auth, EnrollmentService es,
                     GradeService gs, ComplaintService cs,
                     FeedbackService fs, Scanner scanner) {
        super(scanner);
        this.authService = auth;
        this.enrollmentService = es;
        this.gradeService = gs;
        this.complaintService = cs;
        this.feedbackService = fs;
    }

    @Override
    protected void printOptions() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   University Course Registration System  ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  1. Login / Sign up as Student           ║");
        System.out.println("║  2. Login / Sign up as Professor         ║");
        System.out.println("║  3. Login as Administrator               ║");
        System.out.println("║  0. Exit Application                     ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    @Override
    protected void handleChoice(int choice) {
        switch (choice) {
            case 1 -> studentFlow();
            case 2 -> professorFlow();
            case 3 -> adminFlow();
            default -> System.out.println("  Invalid option.");
        }
    }

    // ── Student login / signup ────────────────────────────────────────────────

    private void studentFlow() {
        System.out.println("\n  1. Login");
        System.out.println("  2. Sign up");
        System.out.print("  Choice: ");
        int choice = readInt();

        if (choice == 1) {
            String email = readLine("Email");
            String pass  = readLine("Password");
            try {
                Student student = authService.loginStudent(email, pass);
                System.out.println("  Login successful. Welcome, " + student.getName() + "!");
                new StudentMenu(student, enrollmentService, gradeService,
                        complaintService, feedbackService, scanner).run();
            } catch (InvalidLoginException e) {
                System.out.println("  ERROR: " + e.getMessage());
            }
        } else if (choice == 2) {
            String name  = readLine("Full name");
            String email = readLine("Email");
            String pass  = readLine("Create password");
            try {
                Student s = authService.signupStudent(name, email, pass);
                System.out.println("  Account created! ID: " + s.getId());
                System.out.println("  Please login to continue.");
            } catch (IllegalArgumentException e) {
                System.out.println("  ERROR: " + e.getMessage());
            }
        }
    }

    // ── Professor login / signup ──────────────────────────────────────────────

    private void professorFlow() {
        System.out.println("\n  1. Login");
        System.out.println("  2. Sign up");
        System.out.print("  Choice: ");
        int choice = readInt();

        if (choice == 1) {
            String email = readLine("Email");
            String pass  = readLine("Password");
            try {
                Professor prof = authService.loginProfessor(email, pass);
                System.out.println("  Login successful. Welcome, Prof. " + prof.getName() + "!");
                new ProfessorMenu(prof, gradeService, feedbackService, scanner).run();
            } catch (InvalidLoginException e) {
                System.out.println("  ERROR: " + e.getMessage());
            }
        } else if (choice == 2) {
            String name  = readLine("Full name");
            String email = readLine("Email");
            String pass  = readLine("Create password");
            String dept  = readLine("Department");
            try {
                Professor p = authService.signupProfessor(name, email, pass, dept);
                System.out.println("  Account created! ID: " + p.getId());
            } catch (IllegalArgumentException e) {
                System.out.println("  ERROR: " + e.getMessage());
            }
        }
    }

    // ── Admin login ───────────────────────────────────────────────────────────

    private void adminFlow() {
        String pass = readLine("Admin password");
        try {
            Admin admin = authService.loginAdmin(pass);
            System.out.println("  Login successful. Welcome, " + admin.getName() + "!");
            new AdminMenu(admin, gradeService, complaintService, scanner).run();
        } catch (InvalidLoginException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }
}
