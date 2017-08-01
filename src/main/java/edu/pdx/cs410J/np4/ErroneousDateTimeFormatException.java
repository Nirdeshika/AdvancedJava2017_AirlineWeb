package edu.pdx.cs410J.np4;

/**
 * An unchecked exception used to throw an exception when the format of the date/time is not as required. It is also thrown when it is
 * not a valid date/time.
 *
 * @author Nirdeshika Polisetti
 * @version 1.0
 */
public class ErroneousDateTimeFormatException extends RuntimeException {
    /**
     * Creates an ErroneousDateTimeFormatException object with the input message.
     *
     * @param message The message to be displayed when the exception occurs.
     */
    public ErroneousDateTimeFormatException(String message) {
        super(message);
    }
}
