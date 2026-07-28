package com.medicare.dao;

import com.medicare.model.Appointment;
import com.medicare.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Appointment operations
 * Handles all database operations related to appointments
 */
public class AppointmentDAO {
    
    private static final String INSERT_APPOINTMENT = 
        "INSERT INTO appointments (patient_id, doctor_id, patient_name, doctor_name, appointment_date, appointment_time, reason) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_APPOINTMENT_BY_ID = 
        "SELECT a.*, d.specialization FROM appointments a LEFT JOIN doctors d ON a.doctor_id = d.user_id WHERE a.id = ?";
    
    private static final String SELECT_APPOINTMENTS_BY_PATIENT = 
        "SELECT a.*, d.specialization FROM appointments a LEFT JOIN doctors d ON a.doctor_id = d.user_id WHERE a.patient_id = ? ORDER BY a.appointment_date DESC, a.appointment_time DESC";
    
    private static final String SELECT_APPOINTMENTS_BY_DOCTOR = 
        "SELECT a.*, d.specialization FROM appointments a LEFT JOIN doctors d ON a.doctor_id = d.user_id WHERE a.doctor_id = ? ORDER BY a.appointment_date DESC, a.appointment_time DESC";
    
    private static final String SELECT_ALL_APPOINTMENTS = 
        "SELECT a.*, d.specialization FROM appointments a LEFT JOIN doctors d ON a.doctor_id = d.user_id ORDER BY a.appointment_date DESC, a.appointment_time DESC";
    
    private static final String SELECT_APPOINTMENTS_BY_STATUS = 
        "SELECT a.*, d.specialization FROM appointments a LEFT JOIN doctors d ON a.doctor_id = d.user_id WHERE a.status = ? ORDER BY a.appointment_date DESC, a.appointment_time DESC";
    
    private static final String SELECT_APPOINTMENTS_BY_DATE = 
        "SELECT a.*, d.specialization FROM appointments a LEFT JOIN doctors d ON a.doctor_id = d.user_id WHERE a.appointment_date = ? ORDER BY a.appointment_time";
    
    private static final String SELECT_APPOINTMENTS_BY_DATE_AND_DOCTOR = 
        "SELECT a.*, d.specialization FROM appointments a LEFT JOIN doctors d ON a.doctor_id = d.user_id WHERE a.appointment_date = ? AND a.doctor_id = ? ORDER BY a.appointment_time";
    
    private static final String UPDATE_APPOINTMENT_STATUS = 
        "UPDATE appointments SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String UPDATE_APPOINTMENT = 
        "UPDATE appointments SET patient_name = ?, doctor_name = ?, appointment_date = ?, appointment_time = ?, reason = ?, status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String DELETE_APPOINTMENT = 
        "DELETE FROM appointments WHERE id = ?";
    
    private static final String SELECT_APPOINTMENT_DETAILS_VIEW = 
        "SELECT * FROM appointment_details ORDER BY appointment_date DESC, appointment_time DESC";
    
    /**
     * Create a new appointment
     * @param appointment Appointment object to create
     * @return Generated appointment ID, -1 if failed
     */
    public int createAppointment(Appointment appointment) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_APPOINTMENT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setString(3, appointment.getPatientName());
            stmt.setString(4, appointment.getDoctorName());
            stmt.setDate(5, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setTime(6, Time.valueOf(appointment.getAppointmentTime()));
            stmt.setString(7, appointment.getReason());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Get appointment by ID
     * @param id Appointment ID
     * @return Appointment object, null if not found
     */
    public Appointment getAppointmentById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_APPOINTMENT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAppointment(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting appointment by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get appointments by patient ID
     * @param patientId Patient ID
     * @return List of appointments for the patient
     */
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_APPOINTMENTS_BY_PATIENT)) {
            
            stmt.setInt(1, patientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting appointments by patient: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * Get appointments by doctor ID
     * @param doctorId Doctor ID
     * @return List of appointments for the doctor
     */
    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_APPOINTMENTS_BY_DOCTOR)) {
            
            stmt.setInt(1, doctorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting appointments by doctor: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * Get all appointments
     * @return List of all appointments
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_APPOINTMENTS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all appointments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * Get appointments by status
     * @param status Appointment status
     * @return List of appointments with specified status
     */
    public List<Appointment> getAppointmentsByStatus(String status) {
        List<Appointment> appointments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_APPOINTMENTS_BY_STATUS)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting appointments by status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * Get appointments by date
     * @param date Appointment date
     * @return List of appointments on specified date
     */
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_APPOINTMENTS_BY_DATE)) {
            
            stmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting appointments by date: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * Get appointments by date and doctor
     * @param date Appointment date
     * @param doctorId Doctor ID
     * @return List of appointments for doctor on specified date
     */
    public List<Appointment> getAppointmentsByDateAndDoctor(LocalDate date, int doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_APPOINTMENTS_BY_DATE_AND_DOCTOR)) {
            
            stmt.setDate(1, Date.valueOf(date));
            stmt.setInt(2, doctorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting appointments by date and doctor: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * Update appointment status
     * @param appointmentId Appointment ID
     * @param status New status
     * @return true if successful, false otherwise
     */
    public boolean updateAppointmentStatus(int appointmentId, String status) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_APPOINTMENT_STATUS)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating appointment status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update appointment
     * @param appointment Appointment object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateAppointment(Appointment appointment) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_APPOINTMENT)) {
            
            stmt.setString(1, appointment.getPatientName());
            stmt.setString(2, appointment.getDoctorName());
            stmt.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setTime(4, Time.valueOf(appointment.getAppointmentTime()));
            stmt.setString(5, appointment.getReason());
            stmt.setString(6, appointment.getStatus());
            stmt.setInt(7, appointment.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete appointment
     * @param id Appointment ID
     * @return true if successful, false otherwise
     */
    public boolean deleteAppointment(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_APPOINTMENT)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get appointment details from view
     * @return List of appointment details
     */
    public List<Appointment> getAppointmentDetails() {
        List<Appointment> appointments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_APPOINTMENT_DETAILS_VIEW);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting appointment details: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * Check if appointment time slot is available
     * @param doctorId Doctor ID
     * @param date Appointment date
     * @param time Appointment time
     * @return true if available, false otherwise
     */
    public boolean isTimeSlotAvailable(int doctorId, LocalDate date, LocalTime time) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? AND status != 'cancelled'")) {
            
            stmt.setInt(1, doctorId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(time));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking time slot availability: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Map ResultSet to Appointment object
     * @param rs ResultSet
     * @return Appointment object
     * @throws SQLException if mapping fails
     */
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setId(rs.getInt("id"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDoctorId(rs.getInt("doctor_id"));
        appointment.setPatientName(rs.getString("patient_name"));
        appointment.setDoctorName(rs.getString("doctor_name"));
        appointment.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        appointment.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
        appointment.setReason(rs.getString("reason"));
        appointment.setStatus(rs.getString("status"));
        appointment.setSpecialization(rs.getString("specialization"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            appointment.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            appointment.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return appointment;
    }
}
