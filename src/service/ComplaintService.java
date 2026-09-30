package service;

import model.course.Complaint;
import model.course.Complaint.Status;
import model.user.Student;
import repository.ComplaintRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Handles complaint submission and management.
 */
public class ComplaintService {

    private final ComplaintRepository repo = ComplaintRepository.getInstance();

    public Complaint submit(Student student, String description) {
        Complaint c = new Complaint(student.getId(), student.getName(), description);
        repo.add(c);
        student.addComplaintId(c.getId());
        System.out.println("  Complaint submitted. ID: " + c.getId());
        return c;
    }

    public void resolve(String complaintId, String resolutionDetails) {
        Optional<Complaint> opt = repo.findById(complaintId);
        if (opt.isEmpty())
            throw new IllegalArgumentException("Complaint not found: " + complaintId);
        opt.get().resolve(resolutionDetails);
        System.out.println("  Complaint " + complaintId + " marked as RESOLVED.");
    }

    public List<Complaint> getAll()                           { return repo.getAll(); }
    public List<Complaint> getByStudent(String studentId)     { return repo.getByStudent(studentId); }
    public List<Complaint> filterByStatus(Status status)      { return repo.filterByStatus(status); }
    public List<Complaint> filterByDate(LocalDate from, LocalDate to) {
        return repo.filterByDate(from, to);
    }
}
