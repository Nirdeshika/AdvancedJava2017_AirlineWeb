package edu.pdx.cs410J.np4;

/**
 * An unchecked exception used to throw an exception when the format of the airport does not meet the requirements.
 *
 * @author Nirdeshika Polisetti
 * @version 1.0
 */
public class IllegalAirportCodeException extends RuntimeException {
    /**
     * Creates an IllegalAirportCodeException object with the input message.
     *
     * @param message The message to be displayed when the exception occurs.
     */
    public IllegalAirportCodeException(String message) {
        super(message);
    }
}
