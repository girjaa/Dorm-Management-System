package com.dormmanager.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages database connections to MySQL using properties configuration.
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private final String url;
    private final String user;
    private final String password;

    private DatabaseManager() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.err.println("Could not load db.properties: " + e.getMessage());
        }

        this.url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/dorm_db");
        this.user = props.getProperty("db.user", "root");
        this.password = props.getProperty("db.password", "jogelapese15");

        try {
            Class.forName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found: " + e.getMessage());
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Gets a new connection to the MySQL database.
     *
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Tests if the database connection is currently alive.
     *
     * @return true if connection succeeds, false otherwise
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}
