package util;

import factory.UserFactory;
import model.course.Course;
import model.user.*;
import repository.CourseRepository;
import repository.UserRepository;

import java.util.List;

/**
 * Seeds the in-memory stores with demo data required by the assignment:
 *   - 3 students
 *   - 2 professors
 *   - 1 admin
 *   - 5 courses
 *   - 1 Teaching Assistant
 */
public class DataSeeder {

    public static void seed() {
        UserFactory factory     = new UserFactory();
        CourseRepository courses = CourseRepository.getInstance();
        UserRepository users    = UserRepository.getInstance();

        // ── Admin ────────────────────────────────────────────────────────────
        factory.createAdmin("Dr. System Admin");

        // ── Professors ───────────────────────────────────────────────────────
        Professor profA = factory.createProfessor(
                "Dr. Rohit Sharma", "rohit@univ.edu", "prof123", "Computer Networks");
        Professor profB = factory.createProfessor(
                "Dr. Sachin Tendulkar",   "sachin@univ.edu",   "prof123", "Mathematics");
        Professor profC = factory.createProfessor(
                "Dr. Virat Kohli",   "virat@univ.edu",   "prof123", "Electronics");        

        // ── Students ─────────────────────────────────────────────────────────
        factory.createStudent("Vaibhav Parmar",   "vaibhav@student.edu",  "pass123");
        factory.createStudent("Lucky Makwana",  "lucky@student.edu", "pass123");
        factory.createStudent("Dhruv Solanki", "dhruv@student.edu", "pass123");

        // ── Teaching Assistant ────────────────────────────────────────────────
        factory.createTA("Karan Hirpara", "karan@student.edu", "pass123", "CS101");

        // ── Courses ──────────────────────────────────────────────────────────
        // Semester 1 (no prerequisites)
        Course cs101 = new Course("CS101", "Computer Networks",
                4, 1, 5, "Mon/Wed 09:00-11:00", "Room 101, Block A", List.of());
        Course ma101 = new Course("MA101", "Linear Algebra and Statistics",
                4, 1, 5, "Tue/Thu 09:00-11:00", "Room 201, Block B", List.of());
        Course cs102 = new Course("CS102", "Data Structures",
                4, 1, 5, "Mon/Wed 11:00-13:00", "Room 102, Block A", List.of());

        // Semester 2 (requires semester 1 courses)
        Course cs201 = new Course("CS201", "Object-Oriented Programming",
                4, 2, 5, "Mon/Wed 14:00-16:00", "Room 103, Block A",
                List.of("CS101", "CS102"));
        Course ec101 = new Course("EC101", "Digital Electronics and Logic Design",
                2, 2, 5, "Tue/Thu 11:00-12:00", "Room 202, Block B",
                List.of("EC101"));

        courses.add(cs101);
        courses.add(ma101);
        courses.add(cs102);
        courses.add(cs201);
        courses.add(ec101);

        // Assign professors
        cs101.setProfessorId(profA.getId());  profA.assignCourse("CS101");
        cs102.setProfessorId(profA.getId());  profA.assignCourse("CS102");
        cs201.setProfessorId(profA.getId());  profA.assignCourse("CS201");
        ma101.setProfessorId(profB.getId());  profB.assignCourse("MA101");
        ec101.setProfessorId(profC.getId());  profC.assignCourse("EC101");

        System.out.println("  [System] Demo data loaded.");
        System.out.println("  [System] Admin password:      admin123");
        System.out.println("  [System] Professor passwords: prof123");
        System.out.println("  [System] Student passwords:   pass123");
        System.out.println("  [System] Student emails:      vaibhav@student.edu, lucky@student.edu, dhruv@student.edu");
        System.out.println("  [System] TA email:            karan@student.edu (assisting CS101)");
        System.out.println("  [System] Professor emails:    rohit@univ.edu, virat@univ.edu");
    }
}
