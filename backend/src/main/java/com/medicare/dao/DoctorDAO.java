package com.medicare.dao;

import com.medicare.model.Doctor;
import com.medicare.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Doctor operations
 * Handles all database operations related to doctors
 */
public class DoctorDAO {
    
    private static final String INSERT_DOCTOR = 
        "INSERT INTO doctors (user_id, specialization, experience, qualifications, hospital, consultation_fee) VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_DOCTOR_BY_ID = 
        "SELECT d.*, u.name, u.email, u.mobile FROM doctors d JOIN users u ON d.user_id = u.id WHERE d.id = ?";
    
    private static final String SELECT_DOCTOR_BY_USER_ID = 
        "SELECT d.*, u.name, u.email, u.mobile FROM doctors d JOIN users u ON d.user_id = u.id WHERE d.user_id = ?";
    
    private static final String SELECT_ALL_DOCTORS = 
        "SELECT d.*, u.name, u.email, u.mobile FROM doctors d JOIN users u ON d.user_id = u.id ORDER BY d.created_at DESC";
    
    private static final String SELECT_DOCTORS_BY_SPECIALIZATION = 
        "SELECT d.*, u.name, u.email, u.mobile FROM doctors d JOIN users u ON d.user_id = u.id WHERE d.specialization = ? ORDER BY d.created_at DESC";
    
    private static final String SELECT_DOCTOR_LIST_VIEW = 
        "SELECT * FROM doctor_list ORDER BY name";
    
    private static final String UPDATE_DOCTOR = 
        "UPDATE doctors SET specialization = ?, experience = ?, qualifications = ?, hospital = ?, consultation_fee = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String DELETE_DOCTOR = 
        "DELETE FROM doctors WHERE id = ?";
    
    private static final String DELETE_DOCTOR_BY_USER_ID = 
        "DELETE FROM doctors WHERE user_id = ?";
    
    /**
     * Create a new doctor
     * @param doctor Doctor object to create
     * @return Generated doctor ID, -1 if failed
     */
    public int createDoctor(Doctor doctor) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_DOCTOR, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, doctor.getUserId());
            stmt.setString(2, doctor.getSpecialization());
            stmt.setString(3, doctor.getExperience());
            stmt.setString(4, doctor.getQualifications());
            stmt.setString(5, doctor.getHospital());
            stmt.setBigDecimal(6, doctor.getConsultationFee());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating doctor: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Get doctor by ID
     * @param id Doctor ID
     * @return Doctor object, null if not found
     */
    public Doctor getDoctorById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_DOCTOR_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDoctor(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting doctor by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get doctor by user ID
     * @param userId User ID
     * @return Doctor object, null if not found
     */
    public Doctor getDoctorByUserId(int userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_DOCTOR_BY_USER_ID)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDoctor(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting doctor by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all doctors
     * @return List of all doctors
     */
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_DOCTORS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all doctors: " + e.getMessage());
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    /**
     * Get doctors by specialization
     * @param specialization Doctor specialization
     * @return List of doctors with specified specialization
     */
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        List<Doctor> doctors = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_DOCTORS_BY_SPECIALIZATION)) {
            
            stmt.setString(1, specialization);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(mapResultSetToDoctor(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting doctors by specialization: " + e.getMessage());
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    /**
     * Get doctor list from view (simplified doctor information)
     * @return List of doctors with basic information
     */
    public List<Doctor> getDoctorList() {
        List<Doctor> doctors = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_DOCTOR_LIST_VIEW);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setId(rs.getInt("id"));
                doctor.setName(rs.getString("name"));
                doctor.setEmail(rs.getString("email"));
                doctor.setMobile(rs.getString("mobile"));
                doctor.setSpecialization(rs.getString("specialization"));
                doctor.setExperience(rs.getString("experience"));
                doctor.setQualifications(rs.getString("qualifications"));
                doctor.setHospital(rs.getString("hospital"));
                doctor.setConsultationFee(rs.getBigDecimal("consultation_fee"));
                doctors.add(doctor);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting doctor list: " + e.getMessage());
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    /**
     * Update doctor information
     * @param doctor Doctor object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateDoctor(Doctor doctor) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_DOCTOR)) {
            
            stmt.setString(1, doctor.getSpecialization());
            stmt.setString(2, doctor.getExperience());
            stmt.setString(3, doctor.getQualifications());
            stmt.setString(4, doctor.getHospital());
            stmt.setBigDecimal(5, doctor.getConsultationFee());
            stmt.setInt(6, doctor.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating doctor: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete doctor
     * @param id Doctor ID
     * @return true if successful, false otherwise
     */
    public boolean deleteDoctor(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_DOCTOR)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting doctor: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete doctor by user ID
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean deleteDoctorByUserId(int userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_DOCTOR_BY_USER_ID)) {
            
            stmt.setInt(1, userId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting doctor by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get available specializations
     * @return List of unique specializations
     */
    public List<String> getSpecializations() {
        List<String> specializations = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT specialization FROM doctors ORDER BY specialization");
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                specializations.add(rs.getString("specialization"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting specializations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return specializations;
    }
    
    /**
     * Map ResultSet to Doctor object
     * @param rs ResultSet
     * @return Doctor object
     * @throws SQLException if mapping fails
     */
    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setId(rs.getInt("id"));
        doctor.setUserId(rs.getInt("user_id"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setExperience(rs.getString("experience"));
        doctor.setQualifications(rs.getString("qualifications"));
        doctor.setHospital(rs.getString("hospital"));
        doctor.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        
        // Additional fields from users table
        doctor.setName(rs.getString("name"));
        doctor.setEmail(rs.getString("email"));
        doctor.setMobile(rs.getString("mobile"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            doctor.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            doctor.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return doctor;
    }
}
