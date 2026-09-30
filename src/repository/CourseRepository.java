package repository;

import model.course.Course;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Singleton repository for all courses.
 * Design Pattern: Singleton — one shared catalog for the entire application.
 */
public class CourseRepository {

    private static CourseRepository instance;
    private final List<Course> courses = new ArrayList<>();

    private CourseRepository() {}

    /** Returns the single shared instance (lazy initialization). */
    public static CourseRepository getInstance() {
        if (instance == null) instance = new CourseRepository();
        return instance;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public void add(Course course) {
        courses.add(course);
    }

    public boolean remove(String courseCode) {
        return courses.removeIf(c -> c.getCode().equalsIgnoreCase(courseCode));
    }

    public Optional<Course> findByCode(String code) {
        return courses.stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code))
                .findFirst();
    }

    public List<Course> getAll() {
        return Collections.unmodifiableList(courses);
    }

    public List<Course> getBySemester(int semester) {
        return courses.stream()
                .filter(c -> c.getSemester() == semester)
                .collect(Collectors.toList());
    }

    public List<Course> getByProfessor(String professorId) {
        return courses.stream()
                .filter(c -> professorId.equals(c.getProfessorId()))
                .collect(Collectors.toList());
    }

    public boolean exists(String code) {
        return courses.stream().anyMatch(c -> c.getCode().equalsIgnoreCase(code));
    }
}
