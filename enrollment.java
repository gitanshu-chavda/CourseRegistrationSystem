public class enrollment {
    private final String studentId;
    private final String studentName;
    private final String courseCode;
    private final String courseTitle;
    private final int    credits;
    private String grade;          // null until assigned

    public enrollment(String studentId, String studentName,
                      String courseCode, String courseTitle, int credits) {
        this.studentId   = studentId;
        this.studentName = studentName;
        this.courseCode  = courseCode;
        this.courseTitle = courseTitle;
        this.credits     = credits;
        this.grade       = null;
    }

    // ── Getters / setters ─────────────────────────────────────────────────
    public String getStudentId()   { return studentId; }
    public String getStudentName() { return studentName; }
    public String getCourseCode()  { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int    getCredits()     { return credits; }
    public String getGrade()       { return grade; }
    public void   setGrade(String g) { this.grade = g; }

    @Override
    public String toString() {
        return String.format("%-8s %-30s Credits: %d  Grade: %s",
                courseCode, courseTitle, credits,
                grade == null ? "Pending" : grade);
    }
}
