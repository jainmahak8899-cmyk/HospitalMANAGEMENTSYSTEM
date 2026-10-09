package com.medicare.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Doctor model class representing doctors in the MediCare system
 * Contains extended information specific to doctors
 */
public class Doctor {
    private int id;
    private int userId;
    private String specialization;
    private String experience;
    private String qualifications;
    private String hospital;
    private BigDecimal consultationFee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields from User table (for convenience)
    private String name;
    private String email;
    private String mobile;
    
    // Default constructor
    public Doctor() {}
    
    // Constructor with basic fields
    public Doctor(int userId, String specialization, String experience, 
                  String qualifications, String hospital, BigDecimal consultationFee) {
        this.userId = userId;
        this.specialization = specialization;
        this.experience = experience;
        this.qualifications = qualifications;
        this.hospital = hospital;
        this.consultationFee = consultationFee;
    }
    
    // Full constructor
    public Doctor(int id, int userId, String specialization, String experience, 
                  String qualifications, String hospital, BigDecimal consultationFee,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.specialization = specialization;
        this.experience = experience;
        this.qualifications = qualifications;
        this.hospital = hospital;
        this.consultationFee = consultationFee;
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
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public String getExperience() {
        return experience;
    }
    
    public void setExperience(String experience) {
        this.experience = experience;
    }
    
    public String getQualifications() {
        return qualifications;
    }
    
    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }
    
    public String getHospital() {
        return hospital;
    }
    
    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
    
    public BigDecimal getConsultationFee() {
        return consultationFee;
    }
    
    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
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
    
    // Additional getters and setters for User fields
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    // Utility methods
    public String getDisplayName() {
        return name != null ? name : "Dr. " + specialization;
    }
    
    public String getFormattedFee() {
        return consultationFee != null ? "₹" + consultationFee : "N/A";
    }
    
    public boolean hasValidCredentials() {
        return userId > 0 && specialization != null && !specialization.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", userId=" + userId +
                ", specialization='" + specialization + '\'' +
                ", experience='" + experience + '\'' +
                ", qualifications='" + qualifications + '\'' +
                ", hospital='" + hospital + '\'' +
                ", consultationFee=" + consultationFee +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Doctor doctor = (Doctor) obj;
        return id == doctor.id && userId == doctor.userId;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, userId);
    }
}
