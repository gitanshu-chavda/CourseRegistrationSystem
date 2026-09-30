package menu;

import java.util.Scanner;

/**
 * Abstract base menu defining the Template Method pattern.
 * Design Pattern: Template Method — defines the menu loop skeleton;
 *                 subclasses fill in printOptions() and handleChoice().
 * OOP Concepts: Abstraction, Inheritance
 */
public abstract class BaseMenu {

    protected final Scanner scanner;

    public BaseMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Template method — final so subclasses cannot change the loop structure.
     */
    public final void run() {
        int choice;
        do {
            System.out.println();
            printOptions();
            System.out.print("  Enter choice: ");
            choice = readInt();
            if (choice != 0) handleChoice(choice);
        } while (choice != 0);
    }

    /** Subclasses print their own numbered menu options. */
    protected abstract void printOptions();

    /** Subclasses handle their own choices. */
    protected abstract void handleChoice(int choice);

    // ── Input helpers ─────────────────────────────────────────────────────────

    protected int readInt() {
        try {
            String line = scanner.nextLine().trim();
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println("  Invalid input — please enter a number.");
            return -1;
        }
    }

    protected String readLine(String prompt) {
        System.out.print("  " + prompt + ": ");
        return scanner.nextLine().trim();
    }

    protected int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.printf("  %s (%d–%d): ", prompt, min, max);
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                if (val >= min && val <= max) return val;
                System.out.printf("  Please enter a value between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input.");
            }
        }
    }

    protected void pause() {
        System.out.print("\n  Press Enter to continue...");
        scanner.nextLine();
    }

    protected void printSeparator() {
        System.out.println("  " + "─".repeat(50));
    }
}
