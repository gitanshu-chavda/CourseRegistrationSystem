package model.user;

/**
 * Administrator user. Password is fixed at construction time.
 * OOP Concepts: Inheritance (extends User)
 */
public class Admin extends User {

    private static final String FIXED_PASSWORD = "admin123";

    public Admin(String id, String name) {
        super(id, name, "admin@university.edu", FIXED_PASSWORD);
    }

    @Override
    public String getRole() { return "Admin"; }

    @Override
    public void displayDashboard() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.printf( "║  Welcome, %-22s ║%n", getName());
        System.out.println("║  Role: Administrator             ║");
        System.out.println("║  Full system access              ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    public static String getFixedPassword() { return FIXED_PASSWORD; }
}
