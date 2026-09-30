package strategy;

import model.course.Enrollment;
import java.util.List;

/**
 * 10-point GPA scale (standard Indian university scale).
 * Design Pattern: Strategy (ConcreteStrategy)
 */
public class TenPointGpaStrategy implements GpaCalculationStrategy {

    @Override
    public double calculate(List<Enrollment> enrollments) {
        double totalPoints = 0;
        int totalCredits = 0;
        for (Enrollment e : enrollments) {
            if (e.getGrade() != null) {
                totalPoints += toPoints(e.getGrade()) * e.getCredits();
                totalCredits += e.getCredits();
            }
        }
        if (totalCredits == 0) return 0.0;
        return Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }

    private double toPoints(String grade) {
        return switch (grade.toUpperCase()) {
            case "A+", "A"  -> 10.0;
            case "A-"       -> 9.0;
            case "B+"       -> 8.0;
            case "B"        -> 7.0;
            case "B-"       -> 6.0;
            case "C+"       -> 5.0;
            case "C"        -> 4.0;
            case "D"        -> 3.0;
            default         -> 0.0;   // F or unrecognised
        };
    }

    @Override
    public String getName() { return "10-Point Scale"; }
}
