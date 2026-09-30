package model.user;

/**
 * Abstract base class for all users.
 * OOP Concepts: Abstraction, Encapsulation
 */
public abstract class User {
    private final String id;
    private String name;
    private String email;
    private String password;

    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Abstract methods — subclasses define role-specific behavior
    public abstract String getRole();
    public abstract void displayDashboard();

    public boolean checkPassword(String input) {
        return this.password.equals(input);
    }

    // Getters and setters (Encapsulation)
    public String getId()               { return id; }
    public String getName()             { return name; }
    public String getEmail()            { return email; }
    public void setName(String name)    { this.name = name; }
    public void setEmail(String email)  { this.email = email; }
    public void setPassword(String pw)  { this.password = pw; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) — %s", getRole(), name, id, email);
    }
}
