package dataAccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String databaseName;
    private static final String user;
    private static final String password;
    private static final String connectionUrl;

    /*a
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to laod db.properties");
                Properties props = new Properties();
                props.load(propStream);
                databaseName = props.getProperty("db.name");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }
    public static void clearDatabase() throws DataAccessException {
        try (Connection conn = DriverManager.getConnection(connectionUrl, user, password)) {
            conn.setCatalog(databaseName);

            // List all tables that you want to clear. Be careful with the order if there are foreign key constraints.
            String[] tablesToClear = {"auth_tokens", "games", "users"};

            // Disable foreign key checks to prevent constraint violations during deletion.
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS=0;");
            }

            // Clear each table.
            for (String table : tablesToClear) {
                try (Statement stmt = conn.createStatement()) {
                    String sql = "TRUNCATE TABLE " + table + ";";
                    stmt.execute(sql);
                }
            }

            // Re-enable foreign key checks.
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS=1;");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear database: " + e.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    public static void initializeDatabase() {
        // First, try to create the database if it doesn't exist
        try (Connection conn = DriverManager.getConnection(connectionUrl, user, password)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS " + databaseName);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create the database: " + e.getMessage());
        }

        // Now, connect to the newly created or existing database
        try (Connection conn = getConnection()) {
            // Ensure the 'users' table exists
            try (Statement stmt = conn.createStatement()) {
                String sqlCreateUsers = "CREATE TABLE IF NOT EXISTS users (" +
                        "username VARCHAR(255) PRIMARY KEY, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "email VARCHAR(255) NOT NULL UNIQUE);";
                stmt.execute(sqlCreateUsers);
            }

            // Ensure the 'games' table exists
            try (Statement stmt = conn.createStatement()) {
                String sqlCreateGames = "CREATE TABLE IF NOT EXISTS games (" +
                        "gameID INT AUTO_INCREMENT PRIMARY KEY, " +
                        "gameName VARCHAR(255) NOT NULL, " +
                        "whiteUsername VARCHAR(255), " +
                        "blackUsername VARCHAR(255), " +
                        "gameState TEXT, " +
                        "FOREIGN KEY (whiteUsername) REFERENCES users(username) ON DELETE SET NULL, " +
                        "FOREIGN KEY (blackUsername) REFERENCES users(username) ON DELETE SET NULL);";
                stmt.execute(sqlCreateGames);
            }

            // Ensure the 'auth_tokens' table exists
            try (Statement stmt = conn.createStatement()) {
                String sqlCreateAuthTokens = "CREATE TABLE IF NOT EXISTS auth_tokens (" +
                        "authToken VARCHAR(255) PRIMARY KEY, " +
                        "username VARCHAR(255) NOT NULL, " +
                        "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE);";
                stmt.execute(sqlCreateAuthTokens);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to initialize tables: " + e.getMessage());
        }
    }
}
