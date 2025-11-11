package lecbilling.mokopanemakhetha.service;

import lecbilling.mokopanemakhetha.config.DatabaseConfig;
import lecbilling.mokopanemakhetha.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Service for handling user authentication and authorization
 */
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private static AuthenticationService instance;

    private AuthenticationService() {
    }

    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    /**
     * Authenticate a user with username and password
     */
    public User authenticateUser(String username, String password) {
        logger.info("Authentication attempt for user: {}", username);
        
        String query = "SELECT id, username, password, role, full_name, email, is_active " +
                      "FROM users WHERE username = ? AND is_active = TRUE";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    
                    // TODO: In production, use BCrypt or similar for password hashing
                    if (password.equals(storedPassword)) {
                        User user = new User(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                        );
                        
                        // Update last login time
                        updateLastLogin(rs.getInt("id"));
                        
                        logger.info("Authentication successful for user: {}", username);
                        return user;
                    } else {
                        logger.warn("Authentication failed for user: {} - Invalid password", username);
                    }
                } else {
                    logger.warn("Authentication failed for user: {} - User not found", username);
                }
            }
        } catch (SQLException e) {
            logger.error("Database error during authentication for user: {}", username, e);
        }
        
        return null;
    }

    /**
     * Update the last login timestamp for a user
     */
    private void updateLastLogin(int userId) {
        String query = "UPDATE users SET last_login = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            
            logger.debug("Updated last login time for user ID: {}", userId);
        } catch (SQLException e) {
            logger.error("Error updating last login time for user ID: {}", userId, e);
        }
    }

    /**
     * Check if a user has a specific role
     */
    public boolean hasRole(String username, String role) {
        String query = "SELECT role FROM users WHERE username = ? AND is_active = TRUE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return role.equalsIgnoreCase(rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking role for user: {}", username, e);
        }
        
        return false;
    }

    /**
     * Get user details by username
     */
    public User getUserByUsername(String username) {
        String query = "SELECT username, password, role, full_name FROM users " +
                      "WHERE username = ? AND is_active = TRUE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving user: {}", username, e);
        }
        
        return null;
    }
}

