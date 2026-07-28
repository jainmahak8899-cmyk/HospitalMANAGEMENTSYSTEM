<<<<<<< HEAD
# Hospital-Management-System
=======
# Hospital Management System (MediCare Plus)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Security: OWASP Compliant](https://img.shields.io/badge/Security-OWASP--Compliant-blueviolet.svg)]()

A comprehensive, full-stack Hospital Management & Online Appointment System built with a **Java Servlets & JDBC backend** and a **Vite + React + TypeScript frontend**.

---

## 🚀 Key Features

- **Multi-Role Portals**:
  - **Patients**: Search doctors by specialization/hospital, book interactive time slots (`09:00 AM`, `10:30 AM`, etc.), view appointment statuses, manage medical history (Blood Group, Allergies, Emergency Contacts), and download digital Rx prescription receipts (PDF/Print).
  - **Doctors**: Manage patient requests, approve/reject appointments, diagnose conditions, and record digital prescriptions.
  - **Administrators**: Manage specialist doctor directory (add/delete doctors) and view system-wide appointment logs.
- **Enterprise Security**:
  - SHA-256 salted password hashing via `PasswordUtil.java`.
  - Strict CORS dynamic origin policy (no wildcard/credentials conflict).
  - Parameterized SQL PreparedStatements preventing SQL injection.
  - Thread-isolated JDBC connection management.
- **Resilient UI**:
  - Dual-mode operation: Seamless API connectivity with automated LocalStorage fallback for offline testing.
  - Interactive Toast notification container for real-time feedback.

---

## 🛠️ Technology Stack

### Backend
- **Language**: Java 11
- **Build System**: Maven 3.8+
- **Framework**: Servlets 4.0 & JDBC
- **Database**: MySQL 8.0
- **JSON Processing**: Gson 2.10

### Frontend
- **Framework**: React 18 & TypeScript 5.5
- **Build Tool**: Vite 5.4
- **Styling**: Tailwind CSS 3.4
- **Icons**: Lucide React

---

## 📁 Repository Structure

```
Hospital-Management-System/
├── backend/                              # Java Servlet & JDBC Backend
│   ├── src/main/java/com/medicare/
│   │   ├── dao/                          # UserDAO, DoctorDAO, AppointmentDAO
│   │   ├── filter/                       # CorsFilter (Dynamic Origin)
│   │   ├── model/                        # User, Doctor, Appointment
│   │   ├── servlet/                      # UserServlet, DoctorServlet, AppointmentServlet
│   │   └── util/                         # PasswordUtil, DatabaseConnection
│   ├── src/main/resources/               # Database Schema SQL
│   └── pom.xml                           # Maven Build Configuration
├── project/                              # React + TypeScript Frontend
│   ├── src/
│   │   ├── components/
│   │   │   ├── common/                   # Button, Input, Card, Toast
│   │   │   ├── dashboards/               # Patient, Doctor, Admin Dashboards
│   │   │   └── pages/                    # Landing, Login, Registration, Doctor Signup
│   │   ├── types.ts                      # Centralized TypeScript Interfaces
│   │   └── App.tsx                       # Root Router & Toast Manager
│   └── package.json
├── .gitignore
├── LICENSE
├── SECURITY.md
├── CONTRIBUTING.md
└── README.md
```

---

## 🚦 Quick Start Instructions

### 1. Backend Setup (Java / Maven)
```bash
cd backend
# Compile with Maven
mvn clean compile

# Run embedded Tomcat server
mvn tomcat7:run
```
*API Available at*: `http://localhost:8080/medicare-api/api`

### 2. Frontend Setup (React / Vite)
```bash
cd project
# Install dependencies
npm install

# Start development server
npm run dev
```
*Application Live at*: `http://localhost:5173/`

---

## 📄 License

This project is open-source and licensed under the [MIT License](LICENSE).
>>>>>>> 00f5116 (feat: initial production-ready project setup)
