-- MediCare Database Schema
-- Create database and tables for healthcare management system

-- Create database
CREATE DATABASE IF NOT EXISTS medicare_db;
USE medicare_db;

-- Users table (for patients, doctors, and admins)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('patient', 'doctor', 'admin') NOT NULL,
    age INT,
    gender ENUM('male', 'female', 'other'),
    mobile VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Doctors table (extended information for doctors)
CREATE TABLE IF NOT EXISTS doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    specialization VARCHAR(255) NOT NULL,
    experience VARCHAR(100) NOT NULL,
    qualifications TEXT,
    hospital VARCHAR(255),
    consultation_fee DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    patient_name VARCHAR(255) NOT NULL,
    doctor_name VARCHAR(255) NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason TEXT NOT NULL,
    status ENUM('pending', 'approved', 'cancelled', 'completed') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Medical records table (for future use)
CREATE TABLE IF NOT EXISTS medical_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_id INT,
    diagnosis TEXT,
    prescription TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL
);

-- Insert default admin user
INSERT INTO users (name, email, password, role) VALUES 
('Administrator', 'admin@medicare.com', 'admin123', 'admin');

-- Insert sample doctors
INSERT INTO users (name, email, password, role, mobile) VALUES 
('Dr. Om Jain', 'om@medicare.com', 'doctor123', 'doctor', '+91 9876543211'),
('Dr. Michael Chen', 'michael@medicare.com', 'doctor123', 'doctor', '+91 9876543212'),
('Dr. Priya Sharma', 'priya@medicare.com', 'doctor123', 'doctor', '+91 9876543213'),
('Dr. James Wilson', 'james@medicare.com', 'doctor123', 'doctor', '+91 9876543214');

-- Insert doctor details
INSERT INTO doctors (user_id, specialization, experience, qualifications, hospital, consultation_fee) VALUES 
(2, 'Cardiologist', '10 years', 'MBBS, MD Cardiology', 'MediCare Heart Center', 800.00),
(3, 'Neurologist', '8 years', 'MBBS, MD Neurology', 'MediCare Neuro Center', 1000.00),
(4, 'Dermatologist', '12 years', 'MBBS, MD Dermatology', 'MediCare Skin Clinic', 600.00),
(5, 'Orthopedic', '15 years', 'MBBS, MS Orthopedics', 'MediCare Bone Center', 900.00);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_doctors_specialization ON doctors(specialization);
CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointments_date ON appointments(appointment_date);
CREATE INDEX idx_appointments_status ON appointments(status);

-- Create views for common queries
CREATE VIEW doctor_list AS
SELECT 
    u.id,
    u.name,
    u.email,
    u.mobile,
    d.specialization,
    d.experience,
    d.qualifications,
    d.hospital,
    d.consultation_fee
FROM users u
JOIN doctors d ON u.id = d.user_id
WHERE u.role = 'doctor';

CREATE VIEW appointment_details AS
SELECT 
    a.id,
    a.patient_id,
    a.doctor_id,
    a.patient_name,
    a.doctor_name,
    a.appointment_date,
    a.appointment_time,
    a.reason,
    a.status,
    a.created_at,
    d.specialization
FROM appointments a
JOIN doctors d ON a.doctor_id = d.user_id;
