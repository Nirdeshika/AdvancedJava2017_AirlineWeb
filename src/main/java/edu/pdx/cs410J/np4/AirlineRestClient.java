package edu.pdx.cs410J.np4;

import edu.pdx.cs410J.web.HttpRequestHelper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A helper class for accessing the rest client.  This class searches for a flight from the given source and destination via GET and
 * adds a new flight to the airline via POST.
 */
public class AirlineRestClient extends HttpRequestHelper {
    private static final String WEB_APP = "airline";
    private static final String SERVLET = "flights";

    private final String url;


    /**
     * Creates a client to the airline REST service running on the given host and port
     *
     * @param hostName The name of the host
     * @param port     The port
     */
    public AirlineRestClient(String hostName, int port) {
        this.url = String.format("http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET);
    }

    /**
     * Creates a Http POST request to create a new flight and add it to the airline.
     *
     * @param args            The command line arguments from which it extracts the request parameters.
     * @param numberOfOptions The number of options given in the command line.
     * @throws IOException
     */
    public void postAFlight(String[] args, int numberOfOptions) throws IOException {
        Response response = post(url, "name", args[0 + numberOfOptions], "flightNumber", args[1 + numberOfOptions],
                "src", args[2 + numberOfOptions], "departTime", args[3 + numberOfOptions] + " " + args[4 + numberOfOptions] + " " + args[5 + numberOfOptions],
                "dest", args[6 + numberOfOptions], "arriveTime", args[7 + numberOfOptions] + " " + args[8 + numberOfOptions] + " " + args[9 + numberOfOptions]);
        if (response.getCode() != HttpServletResponse.SC_OK) {
            System.out.println("Error occurred while adding a new flight. " + response.getCode());
        }
    }

    /**
     * Creates a Http GET request to search for the flight between the given source and destination.
     *
     * @param args            The command line arguments from which it extracts the request parameters.
     * @param numberOfOptions The number of options given in the command line.
     * @throws IOException
     */
    public void searchFLight(String[] args, int numberOfOptions) throws IOException {

        Response response = get(url, "name", args[0 + numberOfOptions], "src", args[1 + numberOfOptions], "dest", args[2 + numberOfOptions]);
        if (response.getCode() != HttpServletResponse.SC_OK) {
            System.out.println("Error occurred while searching for a flight. " + response.getCode());
        }

    }
}

