package model.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Professor user.
 * OOP Concepts: Inheritance (extends User), Encapsulation
 */
public class Professor extends User {

    private String department;
    private String officeHours;
    private final List<String> assignedCourseCodes = new ArrayList<>();

    public Professor(String id, String name, String email, String password, String department) {
        super(id, name, email, password);
        this.department = department;
        this.officeHours = "Not set";
    }

    @Override
    public String getRole() { return "Professor"; }

    @Override
    public void displayDashboard() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.printf( "║  Welcome, Prof. %-16s ║%n", getName());
        System.out.println("║  Role: Professor                 ║");
        System.out.printf( "║  Dept: %-25s ║%n", department);
        System.out.printf( "║  Courses assigned: %-13d ║%n", assignedCourseCodes.size());
        System.out.println("╚══════════════════════════════════╝");
    }

    public void assignCourse(String courseCode) {
        if (!assignedCourseCodes.contains(courseCode))
            assignedCourseCodes.add(courseCode);
    }

    public void unassignCourse(String courseCode) {
        assignedCourseCodes.remove(courseCode);
    }

    public List<String> getAssignedCourseCodes() {
        return Collections.unmodifiableList(assignedCourseCodes);
    }

    // Getters / setters
    public String getDepartment()              { return department; }
    public void   setDepartment(String d)      { this.department = d; }
    public String getOfficeHours()             { return officeHours; }
    public void   setOfficeHours(String oh)    { this.officeHours = oh; }
}
