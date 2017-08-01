package edu.pdx.cs410J.np4;

import edu.pdx.cs410J.AbstractAirline;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents concrete Airline class. It consists of a collection of flights and the name of the airline.
 *
 * @author Nirdeshika Polisetti
 * @version 1.0
 */
public class Airline extends AbstractAirline<Flight> {
    /**
     * Collection of flight objects that the airline contains.
     */
    private Collection<Flight> flights = new ArrayList<>();
    /**
     * Name of the airline
     */
    private String name = null;

    /**
     * Creates an Airline object with the name as the input argument.
     *
     * @param name The name of the airline
     */
    public Airline(String name) {
        this.name = name;
    }

    /**
     * A getter method to retrieve the name of the airline.
     *
     * @return The name of the airline.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Adds a flight object to the collection of flights.
     *
     * @param flight The flight object that is to be added to the airline.
     */
    @Override
    public void addFlight(Flight flight) {
        this.flights.add(flight);
    }

    /**
     * A getter method to retrieve collection of flights.
     *
     * @return The collection of flight objects that were added to the airline.
     */
    @Override
    public Collection<Flight> getFlights() {
        return this.flights;
    }
}
