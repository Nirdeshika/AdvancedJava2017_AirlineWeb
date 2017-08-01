package edu.pdx.cs410J.np4;

import java.io.*;

/**
 * The class that is used to do some preliminary checks before AirlineDumper objects dumps the contents.
 *
 * @author Nirdeshika Polisetti
 * @version 1.0
 */
public class AirlineDumperHelper {

    /**
     * Gives the BufferedWriter object which aids in writing to the text file.
     *
     * @param file   The file object on to which the contents are to be written to.
     * @param append true, if you want to append the new content. false, if you want to overwrite.
     * @return BufferedWriter object wrapped around FileWriter.
     */
    static BufferedWriter getBufferedWriter(File file, boolean append) {
        FileWriter fileWriter;
        BufferedWriter bufferedWriter = null;

        try {
            fileWriter = new FileWriter(file, append);
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (FileNotFoundException e) {
            checkIfFileExistsElseCreateIt(file);
        } catch (IOException e) {
            System.out.println("Cannot write to this file. " + e.getMessage());
            System.exit(3);
        }
        return bufferedWriter;
    }

    /**
     * Checks if the file exists. If it does not exists, it creates a new file.
     *
     * @param file File object that is to be checked.
     */
    static void checkIfFileExistsElseCreateIt(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("File does not exist and cannot be created. " + file + " : " + e.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * Checks if it is a directory.
     *
     * @param file The file object that is to be checked.
     */
    static void checkIfItIsADirectory(File file) {
        if (file.isDirectory()) {
            try {
                throw new FileNotFoundException("A file is expected, but a directory is provided.");
            } catch (FileNotFoundException fnfe) {
                System.out.println(fnfe.getMessage());
                System.exit(2);
            }

        }
    }
}
