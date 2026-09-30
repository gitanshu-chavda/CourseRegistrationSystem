package model.course;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A complaint submitted by a student.
 */
public class Complaint {

    public enum Status { PENDING, RESOLVED }

    private static int counter = 1000;

    private final String id;
    private final String studentId;
    private final String studentName;
    private final String description;
    private final LocalDate submittedDate;
    private Status status;
    private String resolutionDetails;

    public Complaint(String studentId, String studentName, String description) {
        this.id               = "CMP-" + (++counter);
        this.studentId        = studentId;
        this.studentName      = studentName;
        this.description      = description;
        this.submittedDate    = LocalDate.now();
        this.status           = Status.PENDING;
        this.resolutionDetails = "";
    }

    public void resolve(String details) {
        this.status = Status.RESOLVED;
        this.resolutionDetails = details;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String    getId()               { return id; }
    public String    getStudentId()        { return studentId; }
    public String    getStudentName()      { return studentName; }
    public String    getDescription()      { return description; }
    public LocalDate getSubmittedDate()    { return submittedDate; }
    public Status    getStatus()           { return status; }
    public String    getResolutionDetails(){ return resolutionDetails; }

    public void print() {
        System.out.printf("  [%s] %s — Status: %s  Date: %s%n",
                id, studentName, status,
                submittedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        System.out.printf("       Description: %s%n", description);
        if (status == Status.RESOLVED)
            System.out.printf("       Resolution:  %s%n", resolutionDetails);
    }
}
