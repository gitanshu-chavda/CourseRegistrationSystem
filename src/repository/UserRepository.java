package repository;

import model.user.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Singleton repository for all users.
 * Design Pattern: Singleton — one shared user store for the entire application.
 */
public class UserRepository {

    private static UserRepository instance;

    private final Map<String, Student>   students   = new LinkedHashMap<>();
    private final Map<String, Professor> professors = new LinkedHashMap<>();
    private final Map<String, Admin>     admins     = new LinkedHashMap<>();
    private final Map<String, TeachingAssistant> tas = new LinkedHashMap<>();

    private UserRepository() {}

    public static UserRepository getInstance() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }

    // ── Students ─────────────────────────────────────────────────────────────

    public void addStudent(Student s)   { students.put(s.getId(), s); }
    public Optional<Student> findStudentById(String id)    { return Optional.ofNullable(students.get(id)); }
    public Optional<Student> findStudentByEmail(String email) {
        return students.values().stream().filter(s -> s.getEmail().equalsIgnoreCase(email)).findFirst();
    }
    public Collection<Student> getAllStudents() { return Collections.unmodifiableCollection(students.values()); }
    public boolean studentEmailExists(String email) {
        return students.values().stream().anyMatch(s -> s.getEmail().equalsIgnoreCase(email));
    }

    // ── Professors ────────────────────────────────────────────────────────────

    public void addProfessor(Professor p) { professors.put(p.getId(), p); }
    public Optional<Professor> findProfessorById(String id) { return Optional.ofNullable(professors.get(id)); }
    public Optional<Professor> findProfessorByEmail(String email) {
        return professors.values().stream().filter(p -> p.getEmail().equalsIgnoreCase(email)).findFirst();
    }
    public Collection<Professor> getAllProfessors() { return Collections.unmodifiableCollection(professors.values()); }
    public boolean professorEmailExists(String email) {
        return professors.values().stream().anyMatch(p -> p.getEmail().equalsIgnoreCase(email));
    }

    // ── Admins ────────────────────────────────────────────────────────────────

    public void addAdmin(Admin a)   { admins.put(a.getId(), a); }
    public Optional<Admin> findAdminById(String id) { return Optional.ofNullable(admins.get(id)); }
    public Collection<Admin> getAllAdmins() { return Collections.unmodifiableCollection(admins.values()); }

    // ── TAs ───────────────────────────────────────────────────────────────────

    public void addTA(TeachingAssistant ta)  { tas.put(ta.getId(), ta); }
    public Optional<TeachingAssistant> findTAByEmail(String email) {
        return tas.values().stream().filter(t -> t.getEmail().equalsIgnoreCase(email)).findFirst();
    }
    public Collection<TeachingAssistant> getAllTAs() { return Collections.unmodifiableCollection(tas.values()); }

    // ── ID generation ─────────────────────────────────────────────────────────

    public String nextStudentId()   { return "STU" + String.format("%03d", students.size() + tas.size() + 1); }
    public String nextProfessorId() { return "PRF" + String.format("%03d", professors.size() + 1); }
    public String nextTAId()        { return "TA"  + String.format("%03d", tas.size() + 1); }
}
