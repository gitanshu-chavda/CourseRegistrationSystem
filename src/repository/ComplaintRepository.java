package repository;

import model.course.Complaint;
import model.course.Complaint.Status;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Singleton repository for all complaints.
 * Design Pattern: Singleton
 */
public class ComplaintRepository {

    private static ComplaintRepository instance;
    private final List<Complaint> complaints = new ArrayList<>();

    private ComplaintRepository() {}

    public static ComplaintRepository getInstance() {
        if (instance == null) instance = new ComplaintRepository();
        return instance;
    }

    public void add(Complaint c)     { complaints.add(c); }

    public Optional<Complaint> findById(String id) {
        return complaints.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public List<Complaint> getAll()  { return Collections.unmodifiableList(complaints); }

    public List<Complaint> getByStudent(String studentId) {
        return complaints.stream()
                .filter(c -> c.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public List<Complaint> filterByStatus(Status status) {
        return complaints.stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Complaint> filterByDate(LocalDate from, LocalDate to) {
        return complaints.stream()
                .filter(c -> !c.getSubmittedDate().isBefore(from) && !c.getSubmittedDate().isAfter(to))
                .collect(Collectors.toList());
    }
}
