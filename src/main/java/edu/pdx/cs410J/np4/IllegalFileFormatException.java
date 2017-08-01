package edu.pdx.cs410J.np4;

/**
 * An unchecked exception used to throw an exception when the format of the file does not meet the requirements.
 *
 * @author Nirdeshika Polisetti
 * @version 1.0
 */
public class IllegalFileFormatException extends RuntimeException {
    /**
     * Creates an IllegalFileFormatException object with the input message.
     *
     * @param message The message to be displayed when the exception occurs.
     */
    public IllegalFileFormatException(String message) {
        super(message);
    }
}
