package edu.pdx.cs410J.np4;

/**
 * An unchecked exception used to throw an exception when there are incorrect number of arguments i.e. less or greater than required number
 * of arguments.
 *
 * @author Nirdeshika Polisetti
 * @version 1.0
 */
public class ErroneousNumberOfArgumentsException extends RuntimeException {
    /**
     * Creates an ErroneousNumberOfArgumentsException object with the input message.
     *
     * @param message The message to be displayed when the exception occurs.
     */
    public ErroneousNumberOfArgumentsException(String message) {
        super(message);
    }
}
