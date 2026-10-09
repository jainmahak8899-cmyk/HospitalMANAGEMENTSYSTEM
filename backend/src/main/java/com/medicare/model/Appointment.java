package com.medicare.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Appointment model class representing appointments in the MediCare system
 * Links patients with doctors for scheduled consultations
 */
public class Appointment {
    private int id;
    private int patientId;
    private int doctorId;
    private String patientName;
    private String doctorName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String reason;
    private String status; // pending, approved, cancelled, completed
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for convenience
    private String specialization;
    private String patientEmail;
    private String doctorEmail;
    
    // Default constructor
    public Appointment() {}
    
    // Constructor with basic fields
    public Appointment(int patientId, int doctorId, String patientName, String doctorName,
                       LocalDate appointmentDate, LocalTime appointmentTime, String reason) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.status = "pending";
    }
    
    // Full constructor
    public Appointment(int id, int patientId, int doctorId, String patientName, String doctorName,
                       LocalDate appointmentDate, LocalTime appointmentTime, String reason, String status,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPatientId() {
        return patientId;
    }
    
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
    
    public int getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }
    
    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    
    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }
    
    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Additional getters and setters
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public String getPatientEmail() {
        return patientEmail;
    }
    
    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }
    
    public String getDoctorEmail() {
        return doctorEmail;
    }
    
    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
    }
    
    // Utility methods
    public boolean isPending() {
        return "pending".equals(status);
    }
    
    public boolean isApproved() {
        return "approved".equals(status);
    }
    
    public boolean isCancelled() {
        return "cancelled".equals(status);
    }
    
    public boolean isCompleted() {
        return "completed".equals(status);
    }
    
    public String getFormattedDateTime() {
        return appointmentDate + " at " + appointmentTime;
    }
    
    public String getStatusDisplay() {
        if (status == null) return "Unknown";
        return status.substring(0, 1).toUpperCase() + status.substring(1);
    }
    
    public boolean canBeCancelled() {
        return isPending() || isApproved();
    }
    
    public boolean canBeApproved() {
        return isPending();
    }
    
    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", patientName='" + patientName + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", appointmentDate=" + appointmentDate +
                ", appointmentTime=" + appointmentTime +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", specialization='" + specialization + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Appointment that = (Appointment) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
