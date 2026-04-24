package com.mitwpu.lca.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class for managing MySQL database connections using JDBC.
 * Reads database configuration from database.properties file.
 */
public class DBConnection {

    private static final String DB_DRIVER;
    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASSWORD;

    static {
        Properties props = new Properties();
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/internship_exam_system";
        String user = "root";
        String password = "root";

        try (InputStream input = DBConnection.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                driver = props.getProperty("db.driver", driver);
                url = props.getProperty("db.url", url);
                user = props.getProperty("db.username", user);
                password = props.getProperty("db.password", password);
            }
        } catch (Exception e) {
            System.err.println("Could not load database.properties. Using default values: " + e.getMessage());
        }

        DB_DRIVER = driver;
        DB_URL = url;
        DB_USER = user;
        DB_PASSWORD = password;

        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Ensure mysql-connector-java is in classpath.");
            e.printStackTrace();
        }
    }

    /**
     * Gets a new database connection.
     * Note: For production, consider using HikariCP or other connection pooling libraries.
     *
     * @return Connection object to the database
     * @throws SQLException if connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(true); // Default to auto-commit; can be overridden for transactions
            return conn;
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Gets a connection with custom auto-commit setting.
     * Useful for transaction management.
     *
     * @param autoCommit whether to enable auto-commit
     * @return Connection object to the database
     * @throws SQLException if connection cannot be established
     */
    public static Connection getConnection(boolean autoCommit) throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(autoCommit);
        return conn;
    }

    /**
     * Closes a database connection.
     * Safely handles null connections and SQLExceptions.
     *
     * @param conn the connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Tests the database connection.
     * Useful for debugging connection issues.
     *
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
