package strategy;

import model.course.Enrollment;
import java.util.List;

/**
 * Strategy interface for GPA calculation.
 * Design Pattern: Strategy — allows swapping GPA calculation algorithms.
 * OOP Concepts: Interface, Polymorphism
 */
public interface GpaCalculationStrategy {
    double calculate(List<Enrollment> enrollments);
    String getName();
}
