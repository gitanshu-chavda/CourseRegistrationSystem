package strategy;

import model.course.Enrollment;
import java.util.List;

/**
 * 4-point GPA scale (US-style).
 * Design Pattern: Strategy (ConcreteStrategy)
 */
public class FourPointGpaStrategy implements GpaCalculationStrategy {

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
            case "A+", "A"  -> 4.0;
            case "A-"       -> 3.7;
            case "B+"       -> 3.3;
            case "B"        -> 3.0;
            case "B-"       -> 2.7;
            case "C+"       -> 2.3;
            case "C"        -> 2.0;
            case "D"        -> 1.0;
            default         -> 0.0;
        };
    }

    @Override
    public String getName() { return "4-Point Scale"; }
}
