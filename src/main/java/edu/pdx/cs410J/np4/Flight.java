package edu.pdx.cs410J.np4;

import edu.pdx.cs410J.AbstractFlight;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class represents a concrete flight class. It has a unique identifying number, a source and a destination airport identified by a three-letter code,
 * a departure date and time, and an arrival date and time.
 *
 * @author Nirdeshika Polisetti
 * @version 1.0
 */
public class Flight extends AbstractFlight implements Comparable<Flight> {

    /**
     * Unique identifying number
     */
    private int number = 0;
    /**
     * Source airport's code
     */
    private String source = null;
    /**
     * Destination airport's code
     */
    private String destination = null;
    /**
     * Departure Date time AM/PM
     */
    private Date departTime = null;
    /**
     * Arrival Date Time AM/PM
     */
    private Date arrivalTime = null;

    /**
     * Creates a flight object with the variables set to their corresponding values
     *
     * @param number      Unique identifying number
     * @param source      Source airport's three letter code
     * @param destination Destination airport's three letter code
     * @param departTime  Departure time
     * @param arrivalTime Arrival time
     */
    public Flight(int number, String source, String destination, Date departTime, Date arrivalTime) {
        this.number = number;
        this.source = source;
        this.destination = destination;
        this.departTime = departTime;
        this.arrivalTime = arrivalTime;
    }

    /**
     * Returns the flight's unique identifying number
     *
     * @return Unique identifying number
     */
    @Override
    public int getNumber() {
        return number;
    }

    /**
     * Returns the source airport's code
     *
     * @return A three lettered airport code for the source.
     */
    @Override
    public String getSource() {
        return source;
    }

    /**
     * Returns depart date time am/pm as a String in the format of {@link DateFormat#SHORT}
     *
     * @return Returns depart date time am/pm as a String
     */
    @Override
    public String getDepartureString() {
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
        dateTimeInstance.setLenient(false);
        return dateTimeInstance.format(departTime);
    }

    /**
     * Returns the destination airport's code
     *
     * @return A three lettered airport code for the destination.
     */
    @Override
    public String getDestination() {
        return destination;
    }

    /**
     * Returns arrival date time am/pm as a String in the format of {@link DateFormat#SHORT}
     *
     * @return Returns arrival date time am/pm as a String
     */
    @Override
    public String getArrivalString() {
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
        dateTimeInstance.setLenient(false);
        return dateTimeInstance.format(arrivalTime);
    }

    /**
     * Returns this flight's departure time as a <code>Date</code>.
     */
    @Override
    public Date getDeparture() {
        return departTime;
    }

    /**
     * Returns this flight's arrival time as a <code>Date</code>.
     */
    @Override
    public Date getArrival() {
        return arrivalTime;
    }

    /**
     * Define our own way of comparing two flight objects. Two flights are said to be equal if they have same source and departure time.
     *
     * @param o The object that is to be compared.
     * @return Returns 0 when both are equal. A negative integer when this object is less than o. A positive integer when this object is greater than o.
     */
    @Override
    public int compareTo(Flight o) {
        if (this.getSource().compareToIgnoreCase(o.getSource()) > 0) {
            return 1;
        }

        if (this.getSource().compareToIgnoreCase(o.getSource()) < 0) {
            return -1;
        }

        if (this.getSource().compareToIgnoreCase(o.getSource()) == 0) {
            if (this.getDeparture().compareTo(o.getDeparture()) < 0) {
                return -1;
            }
            if (this.getDeparture().compareTo(o.getDeparture()) > 0) {
                return 1;
            }
        }

        return 0;
    }
}
