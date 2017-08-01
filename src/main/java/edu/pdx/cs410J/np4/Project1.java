package edu.pdx.cs410J.np4;

import edu.pdx.cs410J.AirportNames;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The helper class for the CS410J airline Project.
 *
 * @author Nirdeshika Polisetti
 */
public class Project1 {
    private String nameOfTheAirLine;
    private int flightNumber = -1;
    private String source = null;
    private Date departTime = null;
    private String destination = null;
    private Date arrivalTime = null;

    private ArrayList<String> options = null;
    String nameOfTheFileFromCommandLine;
    String getNameOfTheFileFromCommandLineForPrettyPrint;
    Airline airline;
    private Flight flight;
    String host;
    private String portAsString;
    int port;
    int numberOfOptions;

    /**
     * <p>
     * This method parses the command line arguments and checks for the correct type and format. If any of the
     * arguments do not match the requirements, the program exits gracefully with appropriate error message. If everything passes, then it sets
     * the appropriate variables with the given values.
     * </p>
     *
     * @param args The commandline arguments
     */
    void processCommandLineArguments(String[] args) {
        if (Arrays.asList(args).contains("-README")) {
            System.out.println(getReadMe());
            System.exit(0);
        }

        int countOfArgs = args.length;
        try {
            options = getOptionalArguments(args, countOfArgs);
        } catch (IllegalOptionException ioe) {
            System.out.println(ioe.getMessage());
            System.exit(9);
        }
        numberOfOptions = options.size();

        if (numberOfOptions != 0) {
            try {
                checkValidityOfOptions(options);
            } catch (IllegalOptionException ioe) {
                System.out.println(ioe.getMessage());
                System.exit(9);
            }
        }

        try {
            checkNumberOfArguments(countOfArgs, numberOfOptions);
        } catch (ErroneousNumberOfArgumentsException mae) {
            System.out.println(mae.getMessage());
            System.exit(1);
        }

        nameOfTheAirLine = args[0 + numberOfOptions];
        if (!options.contains("-search")) {
            try {
                flightNumber = getFlightNumber(args[1 + numberOfOptions]);
            } catch (IllegalArgumentException iae) {
                System.out.println(iae.getMessage());
                System.exit(2);
            }

            try {
                checkAirportCodeFormat(args[2 + numberOfOptions]);
                source = args[2 + numberOfOptions];
            } catch (IllegalAirportCodeException iace) {
                System.out.println("Invalid source. " + iace.getMessage());
                System.exit(3);
            }

            try {
                String departDateTimeAMPM = args[3 + numberOfOptions] + " " + args[4 + numberOfOptions] + " " + args[5 + numberOfOptions];
                checkDateTimeFormat(departDateTimeAMPM);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                simpleDateFormat.setLenient(false);
                departTime = simpleDateFormat.parse(departDateTimeAMPM);
            } catch (ErroneousDateTimeFormatException edte) {
                System.out.println("Error in depart time. " + edte.getMessage());
                System.exit(4);
            } catch (ParseException e) {
                System.out.println("Error in depart time. " + e.getMessage());
                System.exit(4);
            }

            try {
                checkAirportCodeFormat(args[6 + numberOfOptions]);
                destination = args[6 + numberOfOptions];
            } catch (IllegalAirportCodeException iace) {
                System.out.println("Invalid destination. " + iace.getMessage());
                System.exit(5);
            }

            try {
                String arrivalDateTimeAMPM = args[7 + numberOfOptions] + " " + args[8 + numberOfOptions] + " " + args[9 + numberOfOptions];
                checkDateTimeFormat(arrivalDateTimeAMPM);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                simpleDateFormat.setLenient(false);
                arrivalTime = simpleDateFormat.parse(arrivalDateTimeAMPM);
            } catch (ErroneousDateTimeFormatException edte) {
                System.out.println("Error in arrival time. " + edte.getMessage());
                System.exit(6);
            } catch (ParseException e) {
                System.out.println("Error in arrival time. " + e.getMessage());
                System.exit(6);
            }
            airline = new Airline(nameOfTheAirLine);
            flight = new Flight(flightNumber, source, destination, departTime, arrivalTime);
            airline.addFlight(flight);

            if (options.contains("-print")) {
                System.out.println(flight);
            }
        } else {
            try {
                checkAirportCodeFormat(args[1 + numberOfOptions]);
                source = args[1 + numberOfOptions];
            } catch (IllegalAirportCodeException iace) {
                System.out.println("Invalid source. " + iace.getMessage());
                System.exit(3);
            }

            try {
                checkAirportCodeFormat(args[2 + numberOfOptions]);
                destination = args[2 + numberOfOptions];
            } catch (IllegalAirportCodeException iace) {
                System.out.println("Invalid destination. " + iace.getMessage());
                System.exit(5);
            }
        }

        if (options.contains("-host") && options.contains("-port")) {
            AirlineRestClient client = new AirlineRestClient(host, port);
            if (options.contains("-search")) {
                try {
                    client.searchFLight(args, numberOfOptions);
                } catch (IOException e) {
                    System.out.println("Error occurred while searching for the flight. " + e.getMessage());
                }
            } else {
                try {
                    client.postAFlight(args, numberOfOptions);
                } catch (IOException e) {
                    System.out.println("Error occurred while adding a flight. " + e.getMessage());
                }
            }
        }


    }

    /**
     * Returns the flight number after validating it. If it is not a positive integer, it throws an error.
     *
     * @param flightNumberAsString The command line argument corresponding to flight number.
     * @return The flight number
     * @throws IllegalArgumentException if the number is a negative integer or not a number at all.
     */
    static int getFlightNumber(String flightNumberAsString) {
        int flightNumber = 0;
        try {
            flightNumber = Integer.parseInt(flightNumberAsString);
            if (flightNumber < 0) {
                throw new IllegalArgumentException("Invalid flight number. Flight number should be a positive number.");
            }
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid number: " + flightNumberAsString + ". Flight number should be a positive integer.");
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException(iae.getMessage());
        }
        return flightNumber;
    }

    /**
     * Returns a list of Strings containing the options passed to main method via command line.
     *
     * @param args        The command line arguments passed to main method
     * @param countOfArgs Count of number of command line arguments.
     * @return A list of Strings containing the options.
     * @throws IllegalOptionException When -textFile option is followed by an option instead of a file name.
     * @see edu.pdx.cs410J.np4.Project1#processCommandLineArguments(String[])
     */
    private ArrayList<String> getOptionalArguments(String[] args, int countOfArgs) {
        ArrayList<String> options = new ArrayList<>();

        if (countOfArgs > 0 && args[0].charAt(0) == '-') {
            options.add(args[0]);
        }

        if (countOfArgs > 1) {
            if ((args[0].equals("-host") || args[0].equals("-port")) && args[1].charAt(0) == '-') {
                throw new IllegalOptionException("-host/-port option shouldn't be followed by another option.");
            } else if (args[1].charAt(0) == '-') {
                options.add(args[1]);
            } else if (args[0].equals("-host")) {
                options.add(args[1]);
                host = args[1];
            } else if (args[0].equals("-port")) {
                options.add(args[1]);
                portAsString = args[1];
            }
        }

        if (countOfArgs > 2) {
            if ((args[1].equals("-host") || args[1].equals("-port")) && args[2].charAt(0) == '-') {
                throw new IllegalOptionException("-host/-port option should be followed by a fileName.");
            } else if (args[2].charAt(0) == '-') {
                options.add(args[2]);
            } else if (args[1].equals("-host")) {
                options.add(args[2]);
                host = args[2];
            } else if (args[1].equals("-port")) {
                options.add(args[2]);
                portAsString = args[2];
            }
        }

        if (countOfArgs > 3) {
            if ((args[2].equals("-host") || args[2].equals("-port")) && args[3].charAt(0) == '-') {
                throw new IllegalOptionException("-host/-port option should be followed by a fileName.");
            } else if (args[3].charAt(0) == '-') {
                options.add(args[3]);
            } else if (args[2].equals("-host")) {
                options.add(args[3]);
                host = args[3];
            } else if (args[2].equals("-port")) {
                options.add(args[3]);
                portAsString = args[3];
            }
        }

        if (countOfArgs > 4) {
            if ((args[3].equals("-host") || args[3].equals("-port")) && args[4].charAt(0) == '-') {
                throw new IllegalOptionException("-host/-port option should be followed by a fileName.");
            } else if (args[4].charAt(0) == '-') {
                options.add(args[4]);
            } else if (args[3].equals("-host")) {
                options.add(args[4]);
                host = args[4];
            } else if (args[3].equals("-port")) {
                options.add(args[4]);
                portAsString = args[4];
            }
        }

        if (countOfArgs > 5) {
            if ((args[4].equals("-host") || args[4].equals("-port")) && args[5].charAt(0) == '-') {
                throw new IllegalOptionException("-host/-port option should be followed by a fileName.");
            } else if (args[5].charAt(0) == '-') {
                options.add(args[5]);
            } else if (args[4].equals("-host")) {
                options.add(args[5]);
                host = args[5];
            } else if (args[4].equals("-port")) {
                options.add(args[5]);
                portAsString = args[5];
            }
        }


        if (countOfArgs > 6) {
            if ((args[5].equals("-host") || args[5].equals("-port")) && args[6].charAt(0) == '-') {
                throw new IllegalOptionException("-host/-port option should be followed by a fileName.");
            } else if (args[6].charAt(0) == '-') {
                options.add(args[6]);
            } else if (args[5].equals("-host")) {
                options.add(args[6]);
                host = args[6];
            } else if (args[5].equals("-port")) {
                options.add(args[6]);
                portAsString = args[6];
            }
        }
        return options;
    }

    /**
     * To check if the options are valid. If there are any invalid options, it throws an exception.
     *
     * @param options The options passed via command line
     * @throws IllegalOptionException if it contains any option not listed.
     */
    private void checkValidityOfOptions(ArrayList<String> options) {
        int indexOfHostOption = options.indexOf("-host");
        int indexOfPortOption = options.indexOf("-port");
        String host = null;
        String port = null;

        if (indexOfHostOption != -1 && indexOfPortOption == -1) {
            throw new IllegalOptionException("If host option is given, port should also be given.");
        }
        if (indexOfHostOption == -1 && indexOfPortOption != -1) {
            throw new IllegalOptionException("If port option is given, host should also be given.");
        }

        if (options.contains("-search") && options.contains("-print")) {
            throw new IllegalOptionException("Both -search and -print options should not be mentioned together.");
        }

        if (indexOfHostOption != -1 && (indexOfHostOption + 1) < options.size()) {
            host = options.remove(indexOfHostOption + 1);
            if (indexOfPortOption < options.size())
                port = options.remove(indexOfPortOption);
        }

        if (!Arrays.asList("-README", "-print", "-host", "-port", "-search").containsAll(options)) {
            throw new IllegalOptionException("Please check there is an invalid option. The valid options are -README, -host, -port, -search and -print." +
                    "Note that these are case sensitive.");
        }
        if (indexOfHostOption != -1)
            options.add(indexOfHostOption + 1, host);
        if (indexOfPortOption != -1) {
            options.add(indexOfPortOption + 1, port);

            try {
                this.port = Integer.parseInt(this.portAsString);
            } catch (NumberFormatException nfe) {
                throw new IllegalOptionException("Port should be an integer");
            }
        }

        if (options.contains("-search")) {
            if (!options.contains("-host") || !options.contains("-port")) {
                throw new IllegalOptionException("Please provide a host and port. Unable to connect to server.");
            }
        }
    }

    /**
     * Returns the README of the project, a brief description of what the project does.
     *
     * @return String containing the hard coded README.
     */
    private static String getReadMe() {
        String readMe = "Name: Nirdeshika Polisetti\n";
        readMe += "Name of the assignment: Project 4\n";
        readMe += "Purpose of the project: This project creates a REST-ful web service that supports adding a new flight to the airline," +
                " searching for a flight between the given source and the destination, and pretty printing all the flights of the given airline. " +
                "This projects supports both web browser and command line as clients." +
                "This project also takes five options: -README which prints a brief description of what the project does.\n" +
                "\t\t\t\t\t-print: Prints the details of the flight.\n" +
                "\t\t\t\t\t-host hostName: Host computer on which the server runs.\n" +
                "\t\t\t\t\t-port port: Port on which the server is listening.\n" +
                "\t\t\t\t\t-search: Searches for the flights between the given source and the destination.\n\n";
        readMe += "Command Line Usage: java edu.pdx.cs410J.np4.Project1 [options] <args>\n" +
                "args are (in order)\n" +
                "name : Name of the flight : String\n" +
                "flightNumber: The flight number: positive int\n" +
                "source: A three letter code of departure airport: String containing only characters\n" +
                "departDate: Date on which the flight departs: String of the format mm/dd/yyyy (Month or day can one or two digits but year must be 4 digits.\n" +
                "departTime: Time at which the flight departs: String of HH:mm (Hours can be 1 or digits but minutes should be two digits.\n" +
                "departAM/PM: am/pm Case-sensitive.\n" +
                "destination: A three letter code of arrival airport: String containing only characters\n" +
                "arrivalDate: Date on which the flight arrives: String of the format mm/dd/yyyy (Month or day can one or two digits but year must be 4 digits.\n" +
                "arrivalTime: Time at which the flight arrives: String of HH:mm (Hours can be 1 or digits but minutes should be two digits.\n" +
                "arrivalAM/PM: am/pm Case-sensitive.\n\n";
        readMe += "Note: \nIf the String contains a space, it should be enclosed in double quotes.\nDate Strings should also be enclosed in double quotes.\n" +
                "Options should precede args.\nIf the options contains -README, the program prints a brief description of the project and exits. " +
                "It will not do anything else. Even error checking.\nFor this project, we can add only one flight to the airline.\n" +
                "-print and -search options cannot be given together.\n" +
                "It is an error to specify a host without a port and vice versa.\n" +
                "-port should be a positive integer.\n" +
                "-search prints only unique flights.\n\n";

        return readMe;
    }

    /**
     * Checks if there are correct number of arguments required. If not, throws an exception.
     *
     * @param countOfArgs     Number of command line arguments
     * @param numberOfOptions Number of options passed in command line
     * @throws ErroneousNumberOfArgumentsException
     */
    private void checkNumberOfArguments(int countOfArgs, int numberOfOptions) {
        if (options.contains("-search")) {
            if (countOfArgs - numberOfOptions < 3)
                throw new ErroneousNumberOfArgumentsException("Please Check! Some of the arguments are missing.");
            if (countOfArgs - numberOfOptions > 3)
                throw new ErroneousNumberOfArgumentsException("Please Check! There are some extra arguments.");
            return;
        }
        if (countOfArgs - numberOfOptions < 10)
            throw new ErroneousNumberOfArgumentsException("Please Check! Some of the arguments are missing.");
        if (countOfArgs - numberOfOptions > 10)
            throw new ErroneousNumberOfArgumentsException("Please Check! There are some extra arguments.");
    }

    /**
     * Checks if the input is string of three characters. If not, throws an exception.
     *
     * @param airportCode A String of three lettered code for departure or arrival airport.
     * @throws IllegalAirportCodeException
     */
    static void checkAirportCodeFormat(String airportCode) {
        if (!airportCode.matches("[a-zA-z][a-zA-Z][a-zA-Z]"))
            throw new IllegalAirportCodeException("Please check. Not a valid airport code: " + airportCode + ". Airport code should be a three lettered string.");
        checkIfItIsAValidAirportCode(airportCode);

    }

    /**
     * Check if the given airport code corresponds to a known airport.
     *
     * @param airportCode The airport code that is to be checked.
     * @throws IllegalAirportCodeException If airportCode does not correspond to any known airport.
     */
    static void checkIfItIsAValidAirportCode(String airportCode) {
        if (AirportNames.getName(airportCode.toUpperCase()) == null) {
            throw new IllegalAirportCodeException(airportCode + " is not a known airport");
        }
    }

    /**
     * Checks if the input String is of the format mm/dd/yyyy. Month and day can be 1 or digits, but year should be of 4 digits.
     * It also checks if the input is a valid date. If not, throws an exception.
     *
     * @param date Departure date or Arrival date
     * @throws ErroneousDateTimeFormatException
     */
    static void checkDateFormat(String date) {
        if (!date.matches("\\d{1,2}/\\d{1,2}/\\d{4}"))
            throw new ErroneousDateTimeFormatException("Please check. Invalid date format: " + date);

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            throw new ErroneousDateTimeFormatException("Please check. Invalid date : " + date);
        }
    }

    /**
     * Checks if the input string is of the format HH:mm. Hours can be 1 or 2 digits, but minutes should be 2 digits.
     * It also checks if it is a valid time. If not, it throws an exception.
     *
     * @param time Departure time or Arrival time
     * @throws ErroneousDateTimeFormatException
     */
    static void checkTimeFormat(String time) {
        if (!time.matches("\\d{1,2}:\\d{2} (am|pm)"))
            throw new ErroneousDateTimeFormatException("Please check. Invalid time format: " + time);

        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(time);
        } catch (ParseException e) {
            throw new ErroneousDateTimeFormatException("Please check. Invalid time: " + time);
        }
    }

    /**
     * Checks if the given String follows all the requirements of the Date i.e it should be of the format MM/dd/yyyy hh:mm am/pm.
     *
     * @param dateTimeAMPM The string that is to be checked.
     * @throws ErroneousDateTimeFormatException If the string dateTimeAMPM does not match any of the requirements.
     */
    static void checkDateTimeFormat(String dateTimeAMPM) {
        StringTokenizer stringTokenizer = new StringTokenizer(dateTimeAMPM, " ");
        String timeComponent = "";
        if (stringTokenizer.countTokens() != 3) {
            throw new ErroneousDateTimeFormatException("Invalid DateTime format: " + dateTimeAMPM + ". Format should be mm/dd/yyyy HH:mm am/pm");
        } else {
            if (stringTokenizer.hasMoreTokens()) {
                checkDateFormat(stringTokenizer.nextToken());
            }
            if (stringTokenizer.hasMoreTokens()) {
                timeComponent += stringTokenizer.nextToken();
            }
            if (stringTokenizer.hasMoreTokens()) {
                timeComponent += " " + stringTokenizer.nextToken();
            }
            checkTimeFormat(timeComponent);
        }
    }

}