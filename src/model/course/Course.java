package model.course;

import java.util.*;

/**
 * Represents a course in the catalog.
 * OOP Concepts: Encapsulation
 */
public class Course {

    private final String code;
    private String title;
    private String professorId;     // assigned professor's ID
    private int credits;            // 2 or 4
    private int semester;           // which semester this course belongs to
    private int enrollmentLimit;
    private int enrolled;           // current enrollment count
    private List<String> prerequisites;  // list of course codes
    private String schedule;        // e.g. "Mon/Wed 10:00–11:30"
    private String location;        // e.g. "Room 204, Block B"
    private String syllabus;

    public Course(String code, String title, int credits, int semester,
                  int enrollmentLimit, String schedule, String location,
                  List<String> prerequisites) {
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.semester = semester;
        this.enrollmentLimit = enrollmentLimit;
        this.schedule = schedule;
        this.location = location;
        this.prerequisites = prerequisites == null ? new ArrayList<>() : new ArrayList<>(prerequisites);
        this.syllabus = "Syllabus not uploaded yet.";
        this.enrolled = 0;
    }

    public boolean isFull()  { return enrolled >= enrollmentLimit; }
    public boolean hasSeats(){ return enrolled < enrollmentLimit;  }

    public void incrementEnrollment() { enrolled++; }
    public void decrementEnrollment() { if (enrolled > 0) enrolled--; }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String       getCode()            { return code; }
    public String       getTitle()           { return title; }
    public String       getProfessorId()     { return professorId; }
    public int          getCredits()         { return credits; }
    public int          getSemester()        { return semester; }
    public int          getEnrollmentLimit() { return enrollmentLimit; }
    public int          getEnrolled()        { return enrolled; }
    public List<String> getPrerequisites()   { return Collections.unmodifiableList(prerequisites); }
    public String       getSchedule()        { return schedule; }
    public String       getLocation()        { return location; }
    public String       getSyllabus()        { return syllabus; }

    // ── Setters (Professor-accessible fields) ─────────────────────────────
    public void setTitle(String t)            { this.title = t; }
    public void setProfessorId(String pid)    { this.professorId = pid; }
    public void setCredits(int c)             { this.credits = c; }
    public void setEnrollmentLimit(int lim)   { this.enrollmentLimit = lim; }
    public void setPrerequisites(List<String> p) { this.prerequisites = new ArrayList<>(p); }
    public void setSchedule(String s)         { this.schedule = s; }
    public void setLocation(String l)         { this.location = l; }
    public void setSyllabus(String s)         { this.syllabus = s; }
    public void setSemester(int sem)          { this.semester = sem; }

    public void printDetails(String professorName) {
        System.out.printf("  %-8s %-30s Prof: %-20s Credits: %d  Sem: %d  Seats: %d/%d%n",
                code, title, professorName == null ? "TBA" : professorName,
                credits, semester, enrolled, enrollmentLimit);
        System.out.printf("         Schedule: %-25s Location: %s%n", schedule, location);
        if (!prerequisites.isEmpty())
            System.out.printf("         Prerequisites: %s%n", String.join(", ", prerequisites));
    }
}
