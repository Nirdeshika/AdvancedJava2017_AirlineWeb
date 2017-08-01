package edu.pdx.cs410J.np4;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This servlet provides a REST API for working with an <code>Airline</code>.
 */
public class AirlineServlet extends HttpServlet {
    private final Map<String, Airline> airlineMap = new HashMap<>();

    /**
     * Handles an HTTP GET request from a client by writing the details of the flights in the given airline to the HTTP Response.
     * If source and destination are also mentioned, then it writes only those flights between the source and destination to the HTTP response.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (response.getStatus() != 200) {
            response.sendError(response.getStatus(), "Cannot connect. " + response.getStatus());
            return;
        }

        response.setContentType("text/plain");
        PrettyPrinter prettyPrinter = new PrettyPrinter("-");
        PrintWriter printWriter = response.getWriter();

        List<String> parameterNames = Collections.list(request.getParameterNames());

        String nameOfTheAirline = request.getParameter("name");
        if (nameOfTheAirline == null || nameOfTheAirline.equals("")) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Name of the airline cannot be empty. Please enter a valid name.");
            return;
        }
        Airline airline = airlineMap.get(nameOfTheAirline);
        if (airline == null) {
            printWriter.println("Information regarding airline " + nameOfTheAirline + " does not exist.");
            printWriter.flush();
            return;
        }

        if (!parameterNames.containsAll(Arrays.asList("src", "dest"))) {
            printWriter.println(prettyPrinter.getPrettyPrintContent(airline));
            printWriter.flush();
        } else {
            String source = request.getParameter("src");
            String destination = request.getParameter("dest");

            if (source == null || destination == null || source.equals("") || destination.equals("")) {
                response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Source/Destination cannot be null. Please enter a valid value.");
                return;
            } else {
                try {
                    Project1.checkAirportCodeFormat(source);
                } catch (IllegalAirportCodeException iace) {
                    response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Invalid source. " + iace.getMessage());
                    return;
                }
                try {
                    Project1.checkAirportCodeFormat(destination);
                } catch (IllegalAirportCodeException iace) {
                    response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Invalid destination. " + iace.getMessage());
                    return;
                }
                Collection<Flight> flights = airline.getFlights();
                SortedSet<Flight> searchedFlights = new TreeSet<>();
                int count = 0;
                for (Flight flight : flights) {
                    if (flight.getSource().equalsIgnoreCase(source) && flight.getDestination().equalsIgnoreCase(destination)) {
                        searchedFlights.add(flight);
                        count++;
                    }
                }
                if (count == 0) {
                    printWriter.println("No flights between " + source + " and " + destination + " are found.");
                    printWriter.flush();
                } else {
                    for (Flight flight : searchedFlights) {
                        printWriter.println(flight);
                        printWriter.flush();
                    }

                }
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }


    /**
     * Handles an HTTP POST request by creating a new flight object and adding it to the airline.
     * If the airline does not exists, it creates a new airline and then adds the flight to it. It writes a message with the
     * details of the flight created.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (response.getStatus() != 200) {
            response.sendError(response.getStatus(), "Cannot connect. " + response.getStatus());
            return;
        }
        response.setContentType("text/plain");
        PrintWriter printWriter = response.getWriter();

        String nameOfTheAirline = request.getParameter("name");
        String flightNumberAsString = request.getParameter("flightNumber");
        String source = request.getParameter("src");
        String departTimeAsString = request.getParameter("departTime");
        String destination = request.getParameter("dest");
        String arriveTimeAsString = request.getParameter("arriveTime");

        int flightNumber = -1;
        Date departTime = new Date();
        Date arriveTime = new Date();

        if (nameOfTheAirline == null || nameOfTheAirline.equals("")) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Name of the airline cannot be empty. Please enter a valid value.");
            return;
        }

        if (flightNumberAsString == null || flightNumberAsString.equals("")) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Flight number cannot be empty. Please enter a valid value.");
            return;
        }

        if (source == null || source.equals("")) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Source cannot be empty. Please enter a valid value.");
            return;
        }

        if (departTime.equals("")) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Depart Date and Time cannot be empty. Please enter a valid value.");
            return;
        }

        if (destination == null || destination.equals("")) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Destination cannot be empty. Please enter a valid value.");
            return;
        }

        if (arriveTime.equals("")) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Arrive Date and Time cannot be empty. Please enter a valid value.");
            return;
        }

        Airline airline;
        if (!airlineMap.containsKey(nameOfTheAirline)) {
            airline = new Airline(nameOfTheAirline);
            airlineMap.put(nameOfTheAirline, airline);
        } else
            airline = airlineMap.get(nameOfTheAirline);

        try {
            flightNumber = Project1.getFlightNumber(flightNumberAsString);
        } catch (IllegalArgumentException iae) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, iae.getMessage());
            return;
        }
        try {
            Project1.checkDateTimeFormat(departTimeAsString);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
            simpleDateFormat.setLenient(false);
            departTime = simpleDateFormat.parse(departTimeAsString);
        } catch (ErroneousDateTimeFormatException edte) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, edte.getMessage());
            return;
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, e.getMessage());
            return;
        }
        try {
            Project1.checkDateTimeFormat(arriveTimeAsString);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
            simpleDateFormat.setLenient(false);
            arriveTime = simpleDateFormat.parse(arriveTimeAsString);
        } catch (ErroneousDateTimeFormatException edte) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, edte.getMessage());
            return;
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, e.getMessage());
            return;
        }
        try {
            Project1.checkAirportCodeFormat(source);
        } catch (IllegalAirportCodeException iace) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, iace.getMessage());
            return;
        }
        try {
            Project1.checkAirportCodeFormat(destination);
        } catch (IllegalAirportCodeException iace) {
            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, iace.getMessage());
            return;

        }

        Flight flight = new Flight(flightNumber, source, destination, departTime, arriveTime);
        airline.addFlight(flight);

        printWriter.println(flight + " is created.");
        printWriter.flush();
        response.setStatus(HttpServletResponse.SC_OK);
    }

//    /**
//     * Handles an HTTP DELETE request by removing all key/value pairs.  This
//     * behavior is exposed for testing purposes only.  It's probably not
//     * something that you'd want a real application to expose.
//     */
//    @Override
//    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        response.setContentType("text/plain");
//
//        PrintWriter pw = response.getWriter();
//        pw.println(Messages.allMappingsDeleted());
//        pw.flush();
//
//        response.setStatus(HttpServletResponse.SC_OK);
//
//    }
//
//    /**
//     * Writes an error message about a missing parameter to the HTTP response.
//     * <p>
//     * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
//     */
//    private void missingRequiredParameter(HttpServletResponse response, String parameterName)
//            throws IOException {
//        String message = Messages.missingRequiredParameter(parameterName);
//        response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, message);
//    }
//
//    /**
//     * Writes the value of the given key to the HTTP response.
//     * <p>
//     * The text of the message is formatted with
//     * {@link Messages#formatKeyValuePair(String, String)}
//     */
//    private void writeValue(String key, HttpServletResponse response) throws IOException {
//        //   String value = this.data.get(key);
//
//        PrintWriter pw = response.getWriter();
//        //     pw.println(Messages.formatKeyValuePair(key, value));
//
//        pw.flush();
//
//        response.setStatus(HttpServletResponse.SC_OK);
//    }
//
//    /**
//     * Writes all of the key/value pairs to the HTTP response.
//     * <p>
//     * The text of the message is formatted with
//     * {@link Messages#formatKeyValuePair(String, String)}
//     */
//    private void writeAllMappings(HttpServletResponse response) throws IOException {
//        PrintWriter pw = response.getWriter();
//        //  Messages.formatKeyValueMap(pw, data);
//
//        pw.flush();
//
//        response.setStatus(HttpServletResponse.SC_OK);
//    }
//
//    /**
//     * Returns the value of the HTTP request parameter with the given name.
//     *
//     * @return <code>null</code> if the value of the parameter is
//     * <code>null</code> or is the empty string
//     */
//    private String getParameter(String name, HttpServletRequest request) {
//        String value = request.getParameter(name);
//        if (value == null || "".equals(value)) {
//            return null;
//
//        } else {
//            return value;
//        }
//    }
//
//    @VisibleForTesting
//    void setValueForKey(String key, String value) {
//        //this.data.put(key, value);
//    }
//
//    @VisibleForTesting
//    String getValueForKey(String key) {
//        return "";
//        //return this.data.get(key);
//    }
}
