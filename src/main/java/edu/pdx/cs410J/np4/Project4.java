package edu.pdx.cs410J.np4;

/**
 * The main class that parses the command line and communicates with the
 * Airline server using REST.
 */
public class Project4 {

    /**
     * The entry point for the Project4 via command line. This method process the command line arguments and
     * communicate with the Airline server via AirlineRestClient using REST and AirlineServlet.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        Project1 project1 = new Project1();
        project1.processCommandLineArguments(args);
    }
}