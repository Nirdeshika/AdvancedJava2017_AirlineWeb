package edu.pdx.cs410J.np4;

import edu.pdx.cs410J.AirlineParser;
import edu.pdx.cs410J.ParserException;

import java.io.*;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * A class that implements AirlineParser with methods to read the file and parse it.
 */
public class TextParser implements AirlineParser<Airline> {
    /**
     * The file that is to be read and parsed.
     */
    private File file;
    /**
     * The Airline object whose details the text file contains.
     */
    private Airline airline = null;
    /**
     * The flight number. Should be a positive integer.
     */
    private int flightNumber;
    /**
     * The airport code of source. It should be a three-lettered string.
     */
    private String source;
    /**
     * The airport code of destination. It should be a three-lettered string.
     */
    private String destination;

    /**
     * The departure Date time am/pm. Date is of the format mm/dd/yyyy. Time is of the format HH:mm
     * Month, Date and Hours can be 1 or 2 digits. Year should be 4 digits and
     */
    private Date departTime;
    /**
     * The arrival Date time am/pm. Date is of the format mm/dd/yyyy. Time is of the format HH:mm
     * Month, Date and Hours can be 1 or 2 digits. Year should be 4 digits and
     */
    private Date arrivalTime;
    /**
     * Departure date time am/pm as a String in the format of {@link DateFormat#SHORT}.
     */
    private String departureString;
    /**
     * Arrival date time am/pm as a String in the format of {@link DateFormat#SHORT}
     */
    private String arrivalString;

    /**
     * StringTokenizer object used to parse the file.
     */
    StringTokenizer stringTokenizer;
    /**
     * BufferedReader object to read the file.
     */
    BufferedReader bufferedReader;

    /**
     * Creates a TextParser obejct. It takes the file as an argument whose contents are to be read and parsed.
     *
     * @param fileName The name of the file that is to be read and parsed.
     */
    public TextParser(String fileName) {
        this.file = new File(fileName);
    }

    /**
     * Reads the contents of the file and parses it to create an airline object and flight objects. It also adds flight objects to the airline.
     * It also checks the format of the file and the format and type of the individual members of the flight object.
     *
     * @return An airline object whose name and flights are read and parsed from the given file.
     * @throws ParserException
     */
    @Override
    public Airline parse() throws ParserException {
        checkIfFileExists();
        checkIfItIsAFile();

        bufferedReader = getBufferedReader();
        if (bufferedReader != null) {
            try {
                airline = new Airline(getAirlineName());

                String flightObjectDetails;
                while ((flightObjectDetails = bufferedReader.readLine()) != null) {
                    stringTokenizer = getStringTokenizer(flightObjectDetails);
                    checkIfItIsInTheCorrectFormat(stringTokenizer, false);

                    if (stringTokenizer.hasMoreTokens()) {
                        try {
                            flightNumber = Project1.getFlightNumber(stringTokenizer.nextToken());
                        } catch (IllegalArgumentException iae) {
                            throw new IllegalFileFormatException("Invalid flight value in the file. " + iae.getMessage());
                        }
                    } else {
                        throw new IllegalFileFormatException("Format of the file is invalid. Not enough details about flight.");
                    }

                    if (stringTokenizer.hasMoreTokens()) {
                        source = stringTokenizer.nextToken();
                        try {
                            Project1.checkAirportCodeFormat(source);
                        } catch (IllegalAirportCodeException iae) {
                            throw new IllegalFileFormatException("Invalid value for the airport code of the source in the file. " + iae.getMessage());
                        }

                    } else {
                        throw new IllegalFileFormatException("Format of the file is invalid. Not enough details about flight.");
                    }

                    if (stringTokenizer.hasMoreTokens()) {
                        departureString = stringTokenizer.nextToken();
                        try {
                            setDateAndTime(departureString, true);
                        } catch (ErroneousDateTimeFormatException iae) {
                            throw new IllegalFileFormatException("Invalid departure DateTime value in the file. " + iae.getMessage());
                        }

                    } else {
                        throw new IllegalFileFormatException("Format of the file is invalid. Not enough details about flight.");
                    }

                    if (stringTokenizer.hasMoreTokens()) {
                        destination = stringTokenizer.nextToken();
                        try {
                            Project1.checkAirportCodeFormat(destination);
                        } catch (IllegalAirportCodeException iae) {
                            throw new IllegalFileFormatException("Invalid value for the airport code of the destination in the file. " + iae.getMessage());
                        }
                    } else {
                        throw new IllegalFileFormatException("Format of the file is invalid. Not enough details about flight.");
                    }

                    if (stringTokenizer.hasMoreTokens()) {
                        arrivalString = stringTokenizer.nextToken();
                        try {
                            setDateAndTime(arrivalString, false);
                        } catch (ErroneousDateTimeFormatException iae) {
                            throw new IllegalFileFormatException("Invalid arrival time/date value in the file. " + iae.getMessage());
                        }
                    } else {
                        throw new IllegalFileFormatException("Format of the file is invalid. Not enough details about flight.");
                    }

                    Flight flight = new Flight(flightNumber, source, destination, departTime, arrivalTime);
                    airline.addFlight(flight);
                }
            } catch (IOException e) {
                System.out.println("Cannot read from this file. " + e.getMessage());
                System.exit(6);
            } catch (IllegalFileFormatException iffe) {
                System.out.println(iffe.getMessage());
                System.exit(7);
            }
        }

        return airline;
    }

    /**
     * Gets the name of the airline from the text file.
     *
     * @return Name of the airline.
     * @throws IllegalFileFormatException If the format of the file does not match with the requirements.
     */
    String getAirlineName() {
        String airlineName = null;
        try {
            airlineName = bufferedReader.readLine();
            stringTokenizer = getStringTokenizer(airlineName);
            checkIfItIsInTheCorrectFormat(stringTokenizer, true);
            airlineName = stringTokenizer.nextToken();
        } catch (IOException e) {
            System.out.println("Cannot read from this file. " + e.getMessage());
            System.exit(6);
        } catch (IllegalFileFormatException iffe) {
            System.out.println(iffe.getMessage());
            System.exit(7);
        }
        return airlineName;

    }

    /**
     * Sets the date and time from the string.
     *
     * @param string      The departure/arrival string
     * @param isDeparture true: if it is departure string, false otherwise.
     */
    private void setDateAndTime(String string, boolean isDeparture) {
        DateFormat dateFormat = getDateFormatInstance();
        Date date;
        try {
            date = dateFormat.parse(string);
        } catch (ParseException e) {
            throw new ErroneousDateTimeFormatException("Invalid DateTime format: " + string + ". Format should be MM/dd/yyyy HH:mm am/pm");
        }
        SimpleDateFormat simpleDateFormatForDate = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat simpleDateFormatForTime = new SimpleDateFormat("hh:mm aa");
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        dateFormatSymbols.setAmPmStrings(new String[]{"am", "pm"});
        simpleDateFormatForTime.setDateFormatSymbols(dateFormatSymbols);

        Project1.checkDateFormat(simpleDateFormatForDate.format(date));
        Project1.checkTimeFormat(simpleDateFormatForTime.format(date));

        if (isDeparture) {
            try {
                departTime = dateFormat.parse(string);
            } catch (ParseException e) {
                throw new ErroneousDateTimeFormatException("Invalid DateTime format: " + string + ". Format should be MM/dd/yyyy HH:mm am/pm");
            }
        } else {
            try {
                arrivalTime = dateFormat.parse(string);
            } catch (ParseException e) {
                throw new ErroneousDateTimeFormatException("Invalid DateTime format: " + e + ". Format should be MM/dd/yyyy HH:mm am/pm");
            }
        }

    }

    /**
     * Gets the DateFormat instance with setLenient false.
     *
     * @return SimpleDateFormat object
     */
    private DateFormat getDateFormatInstance() {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
        dateFormat.setLenient(false);
        return dateFormat;
    }

    /**
     * Checks if the file to be parsed exists.
     */
    private void checkIfFileExists() {
        if (!file.exists())
            try {
                throw new FileNotFoundException("Cannot find file: " + file);
            } catch (FileNotFoundException fnfe) {
                System.out.println(fnfe.getMessage());
                System.exit(4);
            }
    }

    /**
     * Checks if the file is a directory.
     */
    private void checkIfItIsAFile() {
        if (file.isDirectory()) {
            try {
                throw new FileNotFoundException("A file is expected, but a directory is provided.");
            } catch (FileNotFoundException fnfe) {
                System.out.println(fnfe.getMessage());
                System.exit(5);
            }

        }
    }

    /**
     * Gives the BufferedReader object which aids in reading to the text file.
     *
     * @return BufferedWriter object wrapped around FileReader.
     */
    private BufferedReader getBufferedReader() {
        FileReader fileReader;
        BufferedReader bufferedReader = null;

        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(4);
        }
        return bufferedReader;
    }

    /**
     * Retuns a StringTokenizer object used to parse the text file.
     *
     * @param string The string that is to be parsed.
     * @return A StringTokenizer object with delimiter |
     */
    private StringTokenizer getStringTokenizer(String string) {
        return new StringTokenizer(string, "|");
    }

    /**
     * Checks if the file is in the required format.
     *
     * @param stringTokenizer StringTokenizer object used to parse the text file/
     * @param isAirlineName   true, if the line corresponds to the Airline name, else false.
     */
    private void checkIfItIsInTheCorrectFormat(StringTokenizer stringTokenizer, boolean isAirlineName) {
        if (isAirlineName && stringTokenizer.countTokens() != 1) {
            throw new IllegalFileFormatException("Error while parsing Airline name. Format of the file: " + file + " is not valid. Please check.");
        }
        if (!isAirlineName && stringTokenizer.countTokens() != 5) {
            throw new IllegalFileFormatException("Error while parsing flight object. Format of the file: " + file + " is not valid. Please check.");
        }
    }
}
