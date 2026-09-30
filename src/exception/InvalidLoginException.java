package exception;

/**
 * Thrown when a user provides incorrect login credentials.
 * Assignment 2 — Exception Handling requirement
 */
public class InvalidLoginException extends Exception {
    public InvalidLoginException(String role) {
        super(String.format("Invalid credentials for role '%s'. Please check email/password.", role));
    }
}
