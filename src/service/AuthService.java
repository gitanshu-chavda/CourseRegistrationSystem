package service;

import exception.InvalidLoginException;
import model.user.*;
import repository.UserRepository;

import java.util.Optional;

/**
 * Handles login and signup for all user roles.
 * Exception Handling: throws InvalidLoginException on bad credentials.
 */
public class AuthService {

    private final UserRepository userRepo = UserRepository.getInstance();
    private final factory.UserFactory userFactory = new factory.UserFactory();

    // ── Login ─────────────────────────────────────────────────────────────────

    /**
     * Authenticates a student by email + password.
     * @throws InvalidLoginException if credentials are wrong
     */
    public Student loginStudent(String email, String password) throws InvalidLoginException {
        // Check TAs first (they inherit Student)
        Optional<TeachingAssistant> ta = userRepo.findTAByEmail(email);
        if (ta.isPresent()) {
            if (!ta.get().checkPassword(password)) throw new InvalidLoginException("Student/TA");
            return ta.get();
        }
        Optional<Student> student = userRepo.findStudentByEmail(email);
        if (student.isEmpty() || !student.get().checkPassword(password))
            throw new InvalidLoginException("Student");
        return student.get();
    }

    /**
     * Authenticates a professor by email + password.
     * @throws InvalidLoginException if credentials are wrong
     */
    public Professor loginProfessor(String email, String password) throws InvalidLoginException {
        Optional<Professor> prof = userRepo.findProfessorByEmail(email);
        if (prof.isEmpty() || !prof.get().checkPassword(password))
            throw new InvalidLoginException("Professor");
        return prof.get();
    }

    /**
     * Authenticates an admin by fixed password.
     * @throws InvalidLoginException if password is wrong
     */
    public Admin loginAdmin(String password) throws InvalidLoginException {
        Optional<Admin> admin = userRepo.getAllAdmins().stream().findFirst();
        if (admin.isEmpty() || !admin.get().checkPassword(password))
            throw new InvalidLoginException("Admin");
        return admin.get();
    }

    // ── Signup ────────────────────────────────────────────────────────────────

    public Student signupStudent(String name, String email, String password)
            throws IllegalArgumentException {
        if (userRepo.studentEmailExists(email))
            throw new IllegalArgumentException("Email already registered: " + email);
        return userFactory.createStudent(name, email, password);
    }

    public Professor signupProfessor(String name, String email,
                                     String password, String department)
            throws IllegalArgumentException {
        if (userRepo.professorEmailExists(email))
            throw new IllegalArgumentException("Email already registered: " + email);
        return userFactory.createProfessor(name, email, password, department);
    }

    public TeachingAssistant signupTA(String name, String email,
                                      String password, String courseCode)
            throws IllegalArgumentException {
        if (userRepo.studentEmailExists(email))
            throw new IllegalArgumentException("Email already registered: " + email);
        return userFactory.createTA(name, email, password, courseCode);
    }
}
