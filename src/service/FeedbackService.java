package service;

import model.feedback.Feedback;
import model.user.Student;
import repository.CourseRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages course feedback from students.
 * Assignment 2: Generic Programming — stores Feedback<Integer> and Feedback<String>.
 */
public class FeedbackService {

    // courseCode -> list of all feedback (ratings + comments mixed)
    private final Map<String, List<Feedback<?>>> feedbackMap = new HashMap<>();

    private final CourseRepository courseRepo = CourseRepository.getInstance();

    /**
     * Submit a numeric rating (1–5) for a completed course.
     */
    public void submitRating(Student student, String courseCode, int rating) {
        validateCompletedCourse(student, courseCode);
        Feedback<Integer> fb = new Feedback<>(student.getId(), courseCode, rating);
        store(courseCode, fb);
        student.addFeedback(courseCode, fb);
        System.out.println("  Rating submitted. Thank you!");
    }

    /**
     * Submit a text comment for a completed course.
     */
    public void submitComment(Student student, String courseCode, String comment) {
        validateCompletedCourse(student, courseCode);
        Feedback<String> fb = new Feedback<>(student.getId(), courseCode, comment);
        store(courseCode, fb);
        student.addFeedback(courseCode, fb);
        System.out.println("  Comment submitted. Thank you!");
    }

    /**
     * Returns all feedback for a course (professor-facing view).
     */
    public List<Feedback<?>> getFeedbackForCourse(String courseCode) {
        return feedbackMap.getOrDefault(courseCode, List.of());
    }

    /**
     * Prints a summary: average rating + all comments.
     */
    public void printFeedbackSummary(String courseCode) {
        List<Feedback<?>> list = feedbackMap.getOrDefault(courseCode, List.of());
        if (list.isEmpty()) {
            System.out.println("  No feedback yet for " + courseCode);
            return;
        }
        System.out.println("\n── Feedback summary for " + courseCode + " ──");

        List<Integer> ratings = new ArrayList<>();
        List<String>  comments = new ArrayList<>();

        for (Feedback<?> fb : list) {
            if ("RATING".equals(fb.getType()))  ratings.add((Integer) fb.getValue());
            if ("COMMENT".equals(fb.getType())) comments.add((String)  fb.getValue());
        }

        if (!ratings.isEmpty()) {
            double avg = ratings.stream().mapToInt(i -> i).average().orElse(0);
            System.out.printf("  Average rating: %.1f / 5.0  (%d ratings)%n", avg, ratings.size());
        }

        if (!comments.isEmpty()) {
            System.out.println("  Comments:");
            list.stream()
                .filter(fb -> "COMMENT".equals(fb.getType()))
                .forEach(fb -> System.out.println("    " + fb));
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void store(String courseCode, Feedback<?> fb) {
        feedbackMap.computeIfAbsent(courseCode, k -> new ArrayList<>()).add(fb);
    }

    private void validateCompletedCourse(Student student, String courseCode) {
        boolean completed = student.getCompletedSemesters().values().stream()
                .flatMap(List::stream)
                .anyMatch(e -> e.getCourseCode().equalsIgnoreCase(courseCode));
        if (!completed)
            throw new IllegalArgumentException(
                    "You can only give feedback for courses you have completed. "
                    + "Course '" + courseCode + "' is not in your completed history.");
    }
}
