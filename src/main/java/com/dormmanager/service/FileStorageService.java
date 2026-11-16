package com.dormmanager.service;

import com.dormmanager.exception.DataLoadException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic file storage service for reading and writing data files.
 * Handles IOException, FileNotFoundException, and SecurityException.
 * Requirement: "The application stores its data in files and reads it back."
 */
public class FileStorageService {

    private final String dataDirectory;

    public FileStorageService(String dataDirectory) {
        this.dataDirectory = dataDirectory;
        // Create data directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(dataDirectory));
        } catch (IOException e) {
            System.err.println("Warning: Could not create data directory: " + e.getMessage());
        }
    }

    /**
     * Saves a list of lines to a file, overwriting any existing content.
     *
     * @param filename the file name (relative to data directory)
     * @param lines    the lines to write
     * @throws DataLoadException if writing fails
     */
    public void saveToFile(String filename, List<String> lines) {
        Path path = Paths.get(dataDirectory, filename);
        try {
            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (SecurityException e) {
            throw new DataLoadException("Permission denied writing to: " + path, e);
        } catch (IOException e) {
            throw new DataLoadException("Failed to write file: " + path, e);
        }
    }

    /**
     * Loads all lines from a file.
     *
     * @param filename the file name (relative to data directory)
     * @return the list of lines, or an empty list if file doesn't exist
     */
    public List<String> loadFromFile(String filename) {
        Path path = Paths.get(dataDirectory, filename);
        try {
            if (!Files.exists(path)) {
                return new ArrayList<>();
            }
            return Files.readAllLines(path);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + path + ". Starting fresh.");
            return new ArrayList<>();
        } catch (SecurityException e) {
            throw new DataLoadException("Permission denied reading: " + path, e);
        } catch (IOException e) {
            throw new DataLoadException("Failed to read file: " + path, e);
        }
    }

    /**
     * Appends a single line to a file.
     *
     * @param filename the file name
     * @param line     the line to append
     */
    public void appendToFile(String filename, String line) {
        Path path = Paths.get(dataDirectory, filename);
        try {
            Files.writeString(path, line + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new DataLoadException("Failed to append to file: " + path, e);
        }
    }

    /**
     * Checks if a data file exists.
     */
    public boolean fileExists(String filename) {
        return Files.exists(Paths.get(dataDirectory, filename));
    }
}
