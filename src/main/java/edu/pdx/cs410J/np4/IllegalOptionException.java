package edu.pdx.cs410J.np4;

/**
 * An unchecked exception used to throw an exception when an invalid option is used.
 *
 * @author Nirdeshika Polisetti
 * @version 1.0
 */
public class IllegalOptionException extends RuntimeException {
    /**
     * Creates an IllegalOptionException object with the input message.
     *
     * @param message The message to be displayed when the exception occurs.
     */
    public IllegalOptionException(String message) {
        super(message);
    }
}
