package edu.pdx.cs410J.np4;

import edu.pdx.cs410J.AirlineDumper;
import edu.pdx.cs410J.AirportNames;

import static edu.pdx.cs410J.np4.AirlineDumperHelper.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A class that implements AirlineDumper with methods to write the details of the Airline (name + its flights) to a text file in a pretty manner.
 *
 * @author Nirdeshika Polisetti
 * @version 1.0
 */
public class PrettyPrinter implements AirlineDumper<Airline> {

    /**
     * The file into which the details of the Airline are to be written.
     */
    private File file;
    /**
     * true, if the details should be pretty printed to standard output, the console. false, otherwise.
     */
    private boolean printToStandardOutput;


    /**
     * Creates a PrettyPrinter object. It writes the details of the airline to the file given as the input.
     *
     * @param fileName Name of the file to which the content are written to. It sets {@link PrettyPrinter#printToStandardOutput} to true if fileName is "-".
     */
    public PrettyPrinter(String fileName) {
        if (!fileName.equals("-"))
            this.file = new File(fileName);
        else
            printToStandardOutput = true;
    }

    /**
     * It checks if the file exists; if it doesn't, then it creates one and writes to it.
     * If it does, it checks if it is a directory. If it is, then it exits gracefully with an error message.
     * Else, it writes to it. If any of the subdirectories do not exists, then the program exits with an error message.
     *
     * @param airline The airline whose details are to be written in the text file.
     */
    @Override
    public void dump(Airline airline) {
        if (printToStandardOutput) {
            System.out.println(getPrettyPrintContent(airline));
            return;
        }
        BufferedWriter bufferedWriter;

        checkIfFileExistsElseCreateIt(file);
        checkIfItIsADirectory(file);

        bufferedWriter = getBufferedWriter(file, false);

        if (bufferedWriter != null) {
            try {
                bufferedWriter.write(getPrettyPrintContent(airline));
                bufferedWriter.close();
            } catch (IOException e) {
                System.out.println("Cannot pretty print on to the given file. " + e.getMessage());
                System.exit(15);
            }
        }
    }

    /**
     * Creates the contents for the file. It sorts and prints only unique flight objects of the airline.
     *
     * @param airline The airline object whose details are to be printed.
     * @return Contents to be printed.
     */
    String getPrettyPrintContent(Airline airline) {
        String prettyPrintContent = "";
        if (airline != null) {
            SortedSet<Flight> flights = new TreeSet<>(airline.getFlights());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd''yy 'at' hh:mm aa");
            simpleDateFormat.setLenient(false);

            prettyPrintContent += "Name of the airline: " + airline.getName() + "\n";
            for (Flight flight : flights) {
                prettyPrintContent += "Flight number " + flight.getNumber() + " starting from " + flight.getSource().toUpperCase() + "(" + AirportNames.getName(flight.getSource().toUpperCase()) + ")" +
                        " on " + simpleDateFormat.format(flight.getDeparture()) + " reaches " + flight.getDestination().toUpperCase() + "(" + AirportNames.getName(flight.getDestination().toUpperCase()) + ")" +
                        " on " + simpleDateFormat.format(flight.getArrival()) + ".";
                prettyPrintContent += "The duration of this flight is " + calculateDuration(flight.getDeparture(), flight.getArrival()) + " minutes.\n";
            }
        }
        return prettyPrintContent;
    }

    /**
     * Calculates the duration between two dates in minutes.
     *
     * @param startDate The start date
     * @param endDate   The end date
     * @return Duration between startDate and endDate in minutes.
     */
    public long calculateDuration(Date startDate, Date endDate) {
        long timeDifferenceInMilliSeconds = endDate.getTime() - startDate.getTime();
        long noOfMinutes = timeDifferenceInMilliSeconds / (1000 * 60);
        return noOfMinutes;
    }


}
