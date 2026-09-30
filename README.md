# University Course Registration System
**Assignment 1 + Assignment 2 | Java | Terminal-based | OOP + Design Patterns**

---

## Table of Contents
1. [How to Run](#how-to-run)
2. [Demo Credentials](#demo-credentials)
3. [Project Structure](#project-structure)
4. [OOP Concepts Applied](#oop-concepts-applied)
5. [Design Patterns](#design-patterns)
6. [Assignment 2 Enhancements](#assignment-2-enhancements)
7. [Feature Walkthrough](#feature-walkthrough)
8. [Assumptions](#assumptions)

---

## How to Run

### Prerequisites
- Java 17 or higher (uses switch expressions and pattern matching)
- Any OS (Windows / macOS / Linux)

### Compile
```bash
# From the project root directory
mkdir out
find src -name "*.java" > sources.txt
javac -d out -sourcepath src @sources.txt
```

### Run
```bash
java -cp out Main
```

### Quick one-liner (Linux / macOS)
```bash
mkdir -p out && find src -name "*.java" | xargs javac -d out -sourcepath src && java -cp out Main
```

---

## Demo Credentials

The system seeds 3 students, 2 professors, 1 admin, and 1 TA automatically on startup.

| Role      | Email / Login             | Password  |
|-----------|--------------------------|-----------|
| Admin     | (no email — choose Admin) | `admin123`|
| Professor | `rohit@univ.edu`          | `prof123` |
| Professor | `sachin@univ.edu`            | `prof123` |
| Professor | `virat@univ.edu`            | `prof123` |
| Student   | `vaibhav@student.edu`        | `pass123` |
| Student   | `lucky@student.edu`       | `pass123` |
| Student   | `dhruv@student.edu`       | `pass123` |
| TA        | `karan@student.edu`       | `pass123` |

### Pre-seeded courses

| Code  | Title                        |             Credits  | Semester | Prerequisites  |
|-------|-------------------------------------------|---------|----------|----------------|
| CS101 | Computer Networks  | 4                    | 1       | None     |                |
| MA101 | Calculus I                                | 4       | 1        | None           |
| CS102 | Data Structures                           | 4       | 1        | None           |
| CS201 | Object-Oriented Programming               | 4       | 2        | CS101, CS102   |
| EC101 | Digital Electronics and Logic Design      | 2       | 2        | MA101          |

---

## Project Structure

```
university/
├── src/
│   ├── Main.java                          ← Entry point
│   ├── model/
│   │   ├── user/
│   │   │   ├── User.java                 ← Abstract base class
│   │   │   ├── Student.java              ← Extends User
│   │   │   ├── Professor.java            ← Extends User
│   │   │   ├── Admin.java                ← Extends User
│   │   │   └── TeachingAssistant.java    ← Extends Student (Assignment 2)
│   │   ├── course/
│   │   │   ├── Course.java
│   │   │   ├── Enrollment.java
│   │   │   └── Complaint.java
│   │   └── feedback/
│   │       └── Feedback.java             ← Generic class Feedback<T> (Assignment 2)
│   ├── exception/
│   │   ├── CourseFullException.java      ← Assignment 2
│   │   ├── InvalidLoginException.java    ← Assignment 2
│   │   ├── DropDeadlinePassedException.java ← Assignment 2
│   │   └── PrerequisiteNotMetException.java
│   ├── factory/
│   │   └── UserFactory.java              ← Factory Pattern
│   ├── repository/
│   │   ├── CourseRepository.java         ← Singleton Pattern
│   │   ├── UserRepository.java           ← Singleton Pattern
│   │   └── ComplaintRepository.java      ← Singleton Pattern
│   ├── observer/
│   │   ├── EnrollmentObserver.java       ← Observer interface
│   │   └── ConsoleEnrollmentObserver.java← Concrete observer
│   ├── strategy/
│   │   ├── GpaCalculationStrategy.java   ← Strategy interface
│   │   ├── TenPointGpaStrategy.java      ← 10-point GPA scale
│   │   └── FourPointGpaStrategy.java     ← 4-point GPA scale
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── EnrollmentService.java
│   │   ├── GradeService.java
│   │   ├── ComplaintService.java
│   │   └── FeedbackService.java          ← Assignment 2
│   ├── menu/
│   │   ├── BaseMenu.java                 ← Template Method (abstract)
│   │   ├── LoginMenu.java
│   │   ├── StudentMenu.java
│   │   ├── ProfessorMenu.java
│   │   └── AdminMenu.java
│   └── util/
│       └── DataSeeder.java
├── UML_Diagram.html                       ← Open in any browser
└── README.md
```

---

## OOP Concepts Applied

### 1. Abstraction
- `User` is an **abstract class** with two abstract methods: `getRole()` and `displayDashboard()`. No one can instantiate a raw `User` — they must use `Student`, `Professor`, `Admin`, or `TeachingAssistant`.
- `BaseMenu` is an **abstract class** with abstract methods `printOptions()` and `handleChoice()`. The `run()` method is `final` so the loop structure cannot be overridden — only the content inside it.
- `EnrollmentObserver` and `GpaCalculationStrategy` are **interfaces** defining contracts without implementation.

### 2. Encapsulation
- All model fields (`id`, `name`, `password`, `credits`, etc.) are `private`.
- Access is strictly through public getters and setters.
- `User.password` has no getter — only a `checkPassword(input)` method, so the raw password is never exposed.
- Repository lists are returned as `Collections.unmodifiableList()` to prevent external mutation.

### 3. Inheritance
- `Student`, `Professor`, `Admin` all extend `User`.
- `TeachingAssistant` extends `Student` — inherits all student capabilities and adds grading-assistance methods.
- `StudentMenu`, `ProfessorMenu`, `AdminMenu`, `LoginMenu` all extend `BaseMenu`.
- `TenPointGpaStrategy` and `FourPointGpaStrategy` both implement `GpaCalculationStrategy`.
- `ConsoleEnrollmentObserver` implements `EnrollmentObserver`.

### 4. Polymorphism
- `UserFactory` returns a `User` reference, but the actual runtime type is `Student`, `Professor`, `Admin`, or `TeachingAssistant`. The caller uses polymorphism to invoke `getRole()` and `displayDashboard()`.
- `EnrollmentService` holds a `List<EnrollmentObserver>` — it calls `onCourseEnrolled()` without knowing which concrete observer handles it.
- `GradeService` holds a `GpaCalculationStrategy` reference — the actual algorithm (10-point or 4-point) is resolved at runtime.
- `LoginMenu` creates any of `StudentMenu`, `ProfessorMenu`, or `AdminMenu` and calls `run()` polymorphically.

### 5. Interface
- `EnrollmentObserver` — defines the observer contract: `onCourseEnrolled`, `onCourseDropped`, `onCourseFull`.
- `GpaCalculationStrategy` — defines `calculate(enrollments)` and `getName()`. Any new GPA scale (e.g., 7-point) can be added without changing `GradeService`.

---

## Design Patterns

### Singleton — `CourseRepository`, `UserRepository`, `ComplaintRepository`
All three repositories use lazy-initialized private static instances. The constructor is private so only `getInstance()` can create the object. This ensures the entire application shares one data store without passing objects around or using global variables.

```java
// Usage anywhere in the codebase:
CourseRepository.getInstance().findByCode("CS101");
```

### Factory — `UserFactory`
Centralizes the creation logic for all user types. The calling code (e.g., `AuthService`) asks the factory for a `Student` or `Professor` without knowing the construction details. The factory also handles ID generation and registration in `UserRepository`.

```java
// Caller doesn't know construction details:
Student s = userFactory.createStudent(name, email, password);
```

### Observer — `EnrollmentService` + `EnrollmentObserver`
`EnrollmentService` maintains a `List<EnrollmentObserver>`. When a student enrolls, drops, or a course fills up, all registered observers are notified. The `ConsoleEnrollmentObserver` prints to the terminal. New observers (e.g., email notifier) can be added without changing `EnrollmentService`.

```java
enrollmentService.addObserver(new ConsoleEnrollmentObserver());
// When enroll() is called → all observers are automatically notified
```

### Strategy — `GpaCalculationStrategy`
`GradeService` holds a reference to `GpaCalculationStrategy`. The student can choose between a 10-point scale (Indian universities) or a 4-point scale (US-style) at runtime. Adding a new scale requires zero changes to `GradeService`.

```java
gradeService.setGpaStrategy(new FourPointGpaStrategy());  // swap at runtime
```

### Template Method — `BaseMenu`
`BaseMenu.run()` is declared `final` — it defines the menu loop skeleton (print → read → handle → repeat). Subclasses implement only `printOptions()` and `handleChoice()`. This prevents code duplication and ensures the loop structure is consistent across all three role menus.

```java
// Skeleton (in BaseMenu — cannot be overridden):
public final void run() {
    do { printOptions(); choice = readInt(); handleChoice(choice); } while (choice != 0);
}

// Subclass fills only what's different:
protected void printOptions() { /* StudentMenu-specific options */ }
protected void handleChoice(int c) { /* StudentMenu-specific logic */ }
```

---

## Assignment 2 Enhancements

### 1. Generic Feedback System — `Feedback<T>`
Located in `model/feedback/Feedback.java` and `service/FeedbackService.java`.

The generic class `Feedback<T>` can hold either an `Integer` (numeric rating 1–5) or a `String` (text comment). The type is determined at construction time by inspecting the actual runtime class of `T`. Students can submit ratings, comments, or both. Professors view a summary with average rating and all comments.

```java
Feedback<Integer> rating  = new Feedback<>(studentId, courseCode, 5);
Feedback<String>  comment = new Feedback<>(studentId, courseCode, "Great course!");
```

**How to demo:**
1. Login as a student → complete a semester (as Admin) → login again → option 8 (Give feedback).

### 2. Teaching Assistant Role — `TeachingAssistant`
Located in `model/user/TeachingAssistant.java`.

`TeachingAssistant extends Student`. TAs inherit all student capabilities (enroll, view schedule, submit complaints, give feedback). They additionally can:
- `viewStudentGradesForCourse()` — see all grades for their assigned course.
- `suggestGrade()` — suggest a grade pending professor approval.
- TAs **cannot** update course details (that requires `ProfessorMenu`).

**How to demo:** Login as `karan@student.edu` / `pass123` → option 9 (TA: View student grades).

### 3. Exception Handling

| Exception | Where thrown | Caught in |
|-----------|-------------|-----------|
| `CourseFullException` | `EnrollmentService.enroll()` when `course.isFull()` | `StudentMenu.registerForCourse()` |
| `InvalidLoginException` | `AuthService.loginStudent/Professor/Admin()` on bad credentials | `LoginMenu.studentFlow/professorFlow/adminFlow()` |
| `DropDeadlinePassedException` | `EnrollmentService.drop()` when `dropDeadlinePassed == true` | `StudentMenu.dropCourse()` |
| `PrerequisiteNotMetException` | `EnrollmentService.enroll()` when prereqs not completed | `StudentMenu.registerForCourse()` |

All exceptions have descriptive messages and are handled with `try-catch` in the menu layer. The service layer throws; the UI layer catches and prints user-friendly messages.

**How to demo `CourseFullException`:** The enrollment limit for demo courses is set to 5. Enroll 5 students in CS101, then try a 6th → `CourseFullException` is thrown and caught.

**How to demo `DropDeadlinePassedException`:** In `EnrollmentService`, call `setDropDeadlinePassed(true)` (the Admin can toggle this), then attempt to drop a course.

---

## Feature Walkthrough

### Student flow
1. Sign up / Login → choose Student.
2. **View courses** — see all courses for your current semester with professor, credits, schedule, location.
3. **Register** — select a course; system checks semester, prerequisites, credit limit (max 20), and seat availability.
4. **View schedule** — weekly timetable with timings, location, professor.
5. **Academic progress** — current enrollments + completed semesters with SGPA/CGPA. Choose 10-point or 4-point scale.
6. **Drop a course** — any enrolled course in the current semester (within deadline).
7. **Submit complaint** — describe an issue; system assigns a complaint ID.
8. **Give feedback** — rate (1–5) and/or comment on completed courses.

### Professor flow
1. Login → choose Professor (or sign up first).
2. **View my courses** — see assigned courses with full details and syllabus.
3. **Update course details** — modify syllabus, schedule, location, credits, enrollment limit, office hours.
4. **View enrolled students** — see all students in a course with semester and email.
5. **Assign grades** — set grades (A+/A/A-/B+/B/B-/C+/C/D/F) per student per course.
6. **View feedback** — see average rating and all comments from students.

### Admin flow
1. Login → choose Administrator → password: `admin123`.
2. **Manage catalog** — view all courses, add new courses (with code, title, credits, semester, limit, schedule, location, prerequisites), delete courses.
3. **Manage student records** — view all students, update name or email.
4. **Assign professor** — link a professor ID to a course code.
5. **Handle complaints** — view all / filter by pending / filter by resolved / resolve with details.
6. **Assign grades** — directly assign grades to any student.
7. **Complete semester** — once all grades are assigned, advance a student to the next semester.

---

## Assumptions

1. **Credit limit**: Fixed at 20 credits per semester. Courses are either 2 or 4 credits.
2. **Semester progression**: A student always starts at Semester 1. They cannot advance until the Admin marks their semester complete (after all grades are assigned).
3. **Prerequisites**: Must be completed in a previous semester (not the current one). They are validated by checking `getCompletedCourseCodes()`.
4. **Drop deadline**: Controlled by a boolean flag `dropDeadlinePassed` in `EnrollmentService`. In a real system this would be a date comparison. For demo purposes it can be toggled.
5. **Admin account**: A single admin (`ADM001`) is pre-seeded. Password is `admin123` as per the assignment spec.
6. **TA**: A TA is both a student (can register for courses) and an assistant (can view grades for one assigned course). They cannot update course details.
7. **GPA scale**: Default is 10-point (Indian standard). Students can switch to 4-point at the progress screen.
8. **Feedback**: Only allowed for courses the student has completed (moved to past semesters). Multiple feedback items per course are allowed.
9. **Data persistence**: All data is in-memory (collections). Data resets when the application exits. No file or database I/O.
10. **No electives/mandatory split**: All courses in a semester are treated equally as per the assignment note.
