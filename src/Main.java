import menu.LoginMenu;
import observer.ConsoleEnrollmentObserver;
import service.*;
import util.DataSeeder;

import java.util.Scanner;

/**
 * Application entry point.
 *
 * Wires together all services, attaches observers,
 * seeds demo data, and launches the top-level menu.
 */
public class Main {

    public static void main(String[] args) {

        // ── Bootstrap services ────────────────────────────────────────────────
        
        EnrollmentService enrollmentService = new EnrollmentService();
        GradeService      gradeService      = new GradeService();
        ComplaintService  complaintService  = new ComplaintService();
        FeedbackService   feedbackService   = new FeedbackService();
        AuthService       authService       = new AuthService();

        // ── Attach Observer ───────────────────────────────────────────────────
        enrollmentService.addObserver(new ConsoleEnrollmentObserver());

        // ── Seed demo data ────────────────────────────────────────────────────
        DataSeeder.seed();

        // ── Launch application ────────────────────────────────────────────────
        Scanner scanner = new Scanner(System.in);

        LoginMenu loginMenu = new LoginMenu(
                authService, enrollmentService, gradeService,
                complaintService, feedbackService, scanner);

        loginMenu.run();

        System.out.println("\n  Thank you for using the University Course Registration System. Goodbye!");
        scanner.close();
    }
}
