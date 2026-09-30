package factory;

import model.user.*;
import repository.UserRepository;

/**
 * Factory for creating User objects.
 * Design Pattern: Factory — centralizes object creation logic.
 * OOP Concepts: Polymorphism (returns User supertype)
 */
public class UserFactory {

    public enum Role { STUDENT, PROFESSOR, ADMIN, TA }

    private final UserRepository userRepo = UserRepository.getInstance();

    /**
     * Creates and registers a new Student.
     */
    public Student createStudent(String name, String email, String password) {
        String id = userRepo.nextStudentId();
        Student s = new Student(id, name, email, password);
        userRepo.addStudent(s);
        return s;
    }

    /**
     * Creates and registers a new Professor.
     */
    public Professor createProfessor(String name, String email, String password, String department) {
        String id = userRepo.nextProfessorId();
        Professor p = new Professor(id, name, email, password, department);
        userRepo.addProfessor(p);
        return p;
    }

    /**
     * Creates and registers a new Admin.
     */
    public Admin createAdmin(String name) {
        Admin a = new Admin("ADM001", name);
        userRepo.addAdmin(a);
        return a;
    }

    /**
     * Creates and registers a new Teaching Assistant.
     * TA is also stored in the student map (inherits Student).
     */
    public TeachingAssistant createTA(String name, String email,
                                      String password, String assistingCourseCode) {
        String id = userRepo.nextTAId();
        TeachingAssistant ta = new TeachingAssistant(id, name, email, password, assistingCourseCode);
        userRepo.addTA(ta);
        return ta;
    }
}
