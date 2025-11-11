package lecbilling.mokopanemakhetha.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database configuration for PostgreSQL (Supabase).
 * Simple connection management without HikariCP to avoid compatibility issues.
 */
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static String jdbcUrl;
    private static String username;
    private static String password;

    static {
        try {
            initializeConfiguration();
        } catch (Exception e) {
            logger.error("Failed to initialize database configuration", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private static void initializeConfiguration() {
        Properties props = loadDatabaseProperties();

        // Check for DATABASE_URL environment variable (Supabase, Railway, etc.)
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            // Cloud deployment - parse DATABASE_URL
            logger.info("Using DATABASE_URL from environment");
            parseConnectionUrl(databaseUrl);
        } else {
            // Local development - use properties file
            logger.info("Using local database configuration");
            jdbcUrl = props.getProperty("db.url", "jdbc:postgresql://localhost:5432/lec_billing_db");
            username = props.getProperty("db.username", "postgres");
            password = props.getProperty("db.password", "postgres");
        }

        logger.info("Database configuration initialized: {}", jdbcUrl);
    }

    /**
     * Parse DATABASE_URL format: postgresql://user:password@host:port/database
     * Convert to JDBC format: jdbc:postgresql://host:port/database
     */
    private static void parseConnectionUrl(String databaseUrl) {
        try {
            // Remove postgresql:// prefix
            String withoutProtocol = databaseUrl.substring("postgresql://".length());

            // Split user:password@host:port/database
            String[] parts = withoutProtocol.split("@");
            String[] credentials = parts[0].split(":");
            username = credentials[0];
            password = credentials.length > 1 ? credentials[1] : "";

            // Build JDBC URL
            jdbcUrl = "jdbc:postgresql://" + parts[1];

            logger.debug("Parsed connection URL successfully");
        } catch (Exception e) {
            logger.error("Error parsing DATABASE_URL, using as-is", e);
            jdbcUrl = databaseUrl.startsWith("jdbc:") ? databaseUrl : "jdbc:" + databaseUrl;
        }
    }

    private static Properties loadDatabaseProperties() {
        Properties props = new Properties();
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                logger.debug("Database properties loaded from file");
            } else {
                logger.debug("database.properties not found, using defaults");
            }
        } catch (IOException e) {
            logger.warn("Error loading database.properties, using defaults", e);
        }
        return props;
    }

    /**
     * Get a database connection
     */
    public static Connection getConnection() throws SQLException {
        if (jdbcUrl == null) {
            throw new SQLException("Database is not configured");
        }

        try {
            // Load PostgreSQL driver
            Class.forName("org.postgresql.Driver");

            // Create connection properties
            Properties connectionProps = new Properties();
            connectionProps.setProperty("user", username);
            connectionProps.setProperty("password", password);

            // Disable prepared statements for Supabase transaction mode pooler (port 6543)
            // Transaction mode doesn't support prepared statements
            if (jdbcUrl.contains(":6543/")) {
                connectionProps.setProperty("prepareThreshold", "0");
                logger.debug("Disabled prepared statements for transaction mode pooler");
            }

            // Create connection with properties
            Connection conn = DriverManager.getConnection(jdbcUrl, connectionProps);
            logger.debug("Database connection established");
            return conn;
        } catch (ClassNotFoundException e) {
            logger.error("PostgreSQL driver not found", e);
            throw new SQLException("PostgreSQL driver not found", e);
        } catch (SQLException e) {
            logger.error("Failed to connect to database: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Close a connection (no-op for simple connections, kept for compatibility)
     */
    public static void closePool() {
        logger.info("Connection pool not used (simple connection mode)");
    }

    /**
     * Get connection statistics (simplified version)
     */
    public static String getPoolStats() {
        return String.format("Simple connection mode - URL: %s, User: %s", jdbcUrl, username);
    }

    /**
     * Private constructor to prevent instantiation
     */
    private DatabaseConfig() {
        throw new UnsupportedOperationException("Utility class");
    }
}

