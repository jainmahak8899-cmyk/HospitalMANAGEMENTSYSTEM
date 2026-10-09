package com.medicare.dao;

import com.medicare.model.User;
import com.medicare.util.DatabaseConnection;
import com.medicare.util.PasswordUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations
 * Handles all database operations related to users
 */
public class UserDAO {
    
    private static final String INSERT_USER = 
        "INSERT INTO users (name, email, password, role, age, gender, mobile) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_USER_BY_ID = 
        "SELECT * FROM users WHERE id = ?";
    
    private static final String SELECT_USER_BY_EMAIL = 
        "SELECT * FROM users WHERE email = ?";
    
    private static final String SELECT_USER_BY_EMAIL_AND_ROLE = 
        "SELECT * FROM users WHERE email = ? AND role = ?";
    
    private static final String SELECT_ALL_USERS = 
        "SELECT * FROM users ORDER BY created_at DESC";
    
    private static final String SELECT_USERS_BY_ROLE = 
        "SELECT * FROM users WHERE role = ? ORDER BY created_at DESC";
    
    private static final String UPDATE_USER = 
        "UPDATE users SET name = ?, email = ?, age = ?, gender = ?, mobile = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String UPDATE_PASSWORD = 
        "UPDATE users SET password = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String DELETE_USER = 
        "DELETE FROM users WHERE id = ?";
    
    private static final String CHECK_EMAIL_EXISTS = 
        "SELECT COUNT(*) FROM users WHERE email = ?";
    
    /**
     * Create a new user
     * @param user User object to create
     * @return Generated user ID, -1 if failed
     */
    public int createUser(User user) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, PasswordUtil.createSaltedHash(user.getPassword()));
            stmt.setString(4, user.getRole());
            stmt.setObject(5, user.getAge());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getMobile());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Get user by ID
     * @param id User ID
     * @return User object, null if not found
     */
    public User getUserById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get user by email
     * @param email User email
     * @return User object, null if not found
     */
    public User getUserByEmail(String email) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_EMAIL)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get user by email and role
     * @param email User email
     * @param role User role
     * @return User object, null if not found
     */
    public User getUserByEmailAndRole(String email, String role) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_EMAIL_AND_ROLE)) {
            
            stmt.setString(1, email);
            stmt.setString(2, role);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by email and role: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_USERS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Get users by role
     * @param role User role
     * @return List of users with specified role
     */
    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USERS_BY_ROLE)) {
            
            stmt.setString(1, role);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting users by role: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Update user information
     * @param user User object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateUser(User user) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_USER)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setObject(3, user.getAge());
            stmt.setString(4, user.getGender());
            stmt.setString(5, user.getMobile());
            stmt.setInt(6, user.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update user password
     * @param userId User ID
     * @param newPassword New password
     * @return true if successful, false otherwise
     */
    public boolean updatePassword(int userId, String newPassword) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_PASSWORD)) {
            
            stmt.setString(1, PasswordUtil.createSaltedHash(newPassword));
            stmt.setInt(2, userId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete user
     * @param id User ID
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Check if email exists
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_EMAIL_EXISTS)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Authenticate user login
     * @param email User email
     * @param password User password
     * @param role User role
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String email, String password, String role) {
        User user = getUserByEmailAndRole(email, role);
        if (user != null && PasswordUtil.verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }
    
    /**
     * Map ResultSet to User object
     * @param rs ResultSet
     * @return User object
     * @throws SQLException if mapping fails
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setAge(rs.getObject("age", Integer.class));
        user.setGender(rs.getString("gender"));
        user.setMobile(rs.getString("mobile"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return user;
    }
}
