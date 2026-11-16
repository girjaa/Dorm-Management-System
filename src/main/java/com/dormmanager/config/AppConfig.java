package com.dormmanager.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Application configuration loaded from properties files and/or command-line arguments.
 * Supports: --config=path/to/config.properties and --mode=student|admin
 * Requirement: "Configuration files and/or program arguments start the program in different ways."
 */
public class AppConfig {

    private static AppConfig instance;

    private String appName;
    private String appMode; // "student" or "admin"
    private String dataDirectory;
    private String dataFormat;

    private AppConfig() {
        // defaults
        this.appName = "Dorm Manager";
        this.appMode = "student";
        this.dataDirectory = "data";
        this.dataFormat = "csv";
    }

    /**
     * Returns the singleton instance.
     */
    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    /**
     * Loads configuration from the default classpath resource, then overrides
     * with any command-line arguments.
     *
     * @param args the command-line arguments (e.g. --config=path --mode=admin)
     */
    public void loadFromArgs(String[] args) {
        String configPath = null;

        // Parse CLI args first to find a custom config file
        for (String arg : args) {
            if (arg.startsWith("--config=")) {
                configPath = arg.substring("--config=".length());
            }
        }

        // Load properties (from file or classpath)
        Properties props = new Properties();
        if (configPath != null) {
            try (InputStream in = new FileInputStream(configPath)) {
                props.load(in);
                System.out.println("Loaded config from: " + configPath);
            } catch (FileNotFoundException e) {
                System.err.println("Config file not found: " + configPath + ". Using defaults.");
            } catch (IOException e) {
                System.err.println("Error reading config: " + e.getMessage() + ". Using defaults.");
            }
        } else {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                if (in != null) {
                    props.load(in);
                    System.out.println("Loaded default config from classpath.");
                }
            } catch (IOException e) {
                System.err.println("Error loading default config: " + e.getMessage());
            }
        }

        // Apply properties
        this.appName = props.getProperty("app.name", this.appName);
        this.appMode = props.getProperty("app.mode", this.appMode);
        this.dataDirectory = props.getProperty("data.directory", this.dataDirectory);
        this.dataFormat = props.getProperty("data.format", this.dataFormat);

        // CLI overrides (higher priority than file)
        for (String arg : args) {
            if (arg.startsWith("--mode=")) {
                this.appMode = arg.substring("--mode=".length());
            }
        }

        System.out.println("Running as: " + appName + " [mode=" + appMode + "]");
    }

    public boolean isAdminMode() {
        return "admin".equalsIgnoreCase(appMode);
    }

    // --- Getters ---

    public String getAppName() { return appName; }
    public String getAppMode() { return appMode; }
    public String getDataDirectory() { return dataDirectory; }
    public String getDataFormat() { return dataFormat; }
}
