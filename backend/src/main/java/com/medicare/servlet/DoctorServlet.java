package com.medicare.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.medicare.dao.DoctorDAO;
import com.medicare.dao.UserDAO;
import com.medicare.model.Doctor;
import com.medicare.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DoctorServlet handles doctor-related operations
 * Supports CORS for frontend integration
 */
@WebServlet("/api/doctors/*")
public class DoctorServlet extends HttpServlet {
    
    private DoctorDAO doctorDAO;
    private UserDAO userDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        super.init();
        doctorDAO = new DoctorDAO();
        userDAO = new UserDAO();
        gson = new Gson();
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        setCorsHeaders(response);
        response.setContentType("application/json");
        
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Register new doctor
                registerDoctor(request, response, out);
            } else {
                sendErrorResponse(out, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error in DoctorServlet POST: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(out, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        setCorsHeaders(response);
        response.setContentType("application/json");
        
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all doctors
                getAllDoctors(request, response, out);
            } else if (pathInfo.startsWith("/profile/")) {
                // Get doctor profile
                String doctorId = pathInfo.substring("/profile/".length());
                getDoctorProfile(doctorId, request, response, out);
            } else if (pathInfo.equals("/specializations")) {
                // Get all specializations
                getSpecializations(request, response, out);
            } else if (pathInfo.startsWith("/specialization/")) {
                // Get doctors by specialization
                String specialization = pathInfo.substring("/specialization/".length());
                getDoctorsBySpecialization(specialization, request, response, out);
            } else {
                sendErrorResponse(out, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error in DoctorServlet GET: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(out, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        setCorsHeaders(response);
        response.setContentType("application/json");
        
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        
        try {
            if (pathInfo != null && pathInfo.startsWith("/profile/")) {
                // Update doctor profile
                String doctorId = pathInfo.substring("/profile/".length());
                updateDoctorProfile(doctorId, request, response, out);
            } else {
                sendErrorResponse(out, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error in DoctorServlet PUT: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(out, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        setCorsHeaders(response);
        response.setContentType("application/json");
        
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        
        try {
            if (pathInfo != null && pathInfo.startsWith("/")) {
                // Delete doctor
                String doctorId = pathInfo.substring(1);
                deleteDoctor(doctorId, request, response, out);
            } else {
                sendErrorResponse(out, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error in DoctorServlet DELETE: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(out, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void registerDoctor(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);
        if (jsonObject == null) {
            sendErrorResponse(out, "Invalid or empty JSON payload", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        // Extract user information
        String name = getString(jsonObject, "name");
        String email = getString(jsonObject, "email");
        String password = getString(jsonObject, "password");
        String mobile = getString(jsonObject, "mobile");
        
        // Extract doctor-specific information
        String specialization = getString(jsonObject, "specialization");
        String experience = getString(jsonObject, "experience");
        String qualifications = getString(jsonObject, "qualifications");
        String hospital = getString(jsonObject, "hospital");
        BigDecimal consultationFee = BigDecimal.ZERO;
        if (jsonObject.has("consultationFee") && !jsonObject.get("consultationFee").isJsonNull()) {
            try {
                consultationFee = jsonObject.get("consultationFee").getAsBigDecimal();
            } catch (Exception ignored) {}
        }
        
        // Validate required fields
        if (name == null || name.trim().isEmpty() || 
            email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty() || 
            specialization == null || specialization.trim().isEmpty() || 
            experience == null || experience.trim().isEmpty() || 
            qualifications == null || qualifications.trim().isEmpty() || 
            hospital == null || hospital.trim().isEmpty()) {
            sendErrorResponse(out, "All fields are required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        // Check if email already exists
        if (userDAO.emailExists(email)) {
            sendErrorResponse(out, "Email already exists", HttpServletResponse.SC_CONFLICT);
            return;
        }
        
        // Create user object
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("doctor");
        user.setMobile(mobile);
        
        // Create user in database
        int userId = userDAO.createUser(user);
        
        if (userId > 0) {
            // Create doctor object
            Doctor doctor = new Doctor();
            doctor.setUserId(userId);
            doctor.setSpecialization(specialization);
            doctor.setExperience(experience);
            doctor.setQualifications(qualifications);
            doctor.setHospital(hospital);
            doctor.setConsultationFee(consultationFee);
            
            // Create doctor in database
            int doctorId = doctorDAO.createDoctor(doctor);
            
            if (doctorId > 0) {
                // Get the complete doctor information
                Doctor createdDoctor = doctorDAO.getDoctorById(doctorId);
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Doctor registered successfully");
                responseData.put("doctor", createdDoctor);
                
                out.print(gson.toJson(responseData));
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                // Rollback user creation if doctor creation fails
                userDAO.deleteUser(userId);
                sendErrorResponse(out, "Failed to register doctor", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            sendErrorResponse(out, "Failed to register user", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void getAllDoctors(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("doctors", doctors);
        
        out.print(gson.toJson(responseData));
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private void getDoctorProfile(String doctorId, HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        try {
            int id = Integer.parseInt(doctorId);
            Doctor doctor = doctorDAO.getDoctorById(id);
            
            if (doctor != null) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("doctor", doctor);
                
                out.print(gson.toJson(responseData));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                sendErrorResponse(out, "Doctor not found", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(out, "Invalid doctor ID", HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private void getSpecializations(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        List<String> specializations = doctorDAO.getSpecializations();
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("specializations", specializations);
        
        out.print(gson.toJson(responseData));
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private void getDoctorsBySpecialization(String specialization, HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        List<Doctor> doctors = doctorDAO.getDoctorsBySpecialization(specialization);
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("doctors", doctors);
        responseData.put("specialization", specialization);
        
        out.print(gson.toJson(responseData));
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private void updateDoctorProfile(String doctorId, HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        try {
            int id = Integer.parseInt(doctorId);
            Doctor existingDoctor = doctorDAO.getDoctorById(id);
            
            if (existingDoctor == null) {
                sendErrorResponse(out, "Doctor not found", HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // Check if user can update this doctor profile
            if (!canAccessDoctorProfile(request, existingDoctor)) {
                sendErrorResponse(out, "Access denied", HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);
            
            // Update doctor fields
            if (jsonObject.has("specialization") && !jsonObject.get("specialization").isJsonNull()) {
                existingDoctor.setSpecialization(jsonObject.get("specialization").getAsString());
            }
            if (jsonObject.has("experience") && !jsonObject.get("experience").isJsonNull()) {
                existingDoctor.setExperience(jsonObject.get("experience").getAsString());
            }
            if (jsonObject.has("qualifications") && !jsonObject.get("qualifications").isJsonNull()) {
                existingDoctor.setQualifications(jsonObject.get("qualifications").getAsString());
            }
            if (jsonObject.has("hospital") && !jsonObject.get("hospital").isJsonNull()) {
                existingDoctor.setHospital(jsonObject.get("hospital").getAsString());
            }
            if (jsonObject.has("consultationFee") && !jsonObject.get("consultationFee").isJsonNull()) {
                existingDoctor.setConsultationFee(jsonObject.get("consultationFee").getAsBigDecimal());
            }
            
            boolean updated = doctorDAO.updateDoctor(existingDoctor);
            
            if (updated) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Doctor profile updated successfully");
                responseData.put("doctor", existingDoctor);
                
                out.print(gson.toJson(responseData));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                sendErrorResponse(out, "Failed to update doctor profile", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(out, "Invalid doctor ID", HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private void deleteDoctor(String doctorId, HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        // Check if user is admin
        if (!isAdmin(request)) {
            sendErrorResponse(out, "Access denied", HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        try {
            int id = Integer.parseInt(doctorId);
            Doctor doctor = doctorDAO.getDoctorById(id);
            
            if (doctor == null) {
                sendErrorResponse(out, "Doctor not found", HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            boolean deleted = doctorDAO.deleteDoctor(id);
            
            if (deleted) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Doctor deleted successfully");
                
                out.print(gson.toJson(responseData));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                sendErrorResponse(out, "Failed to delete doctor", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(out, "Invalid doctor ID", HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String userRole = (String) session.getAttribute("userRole");
            return "admin".equals(userRole);
        }
        return false;
    }
    
    private boolean canAccessDoctorProfile(HttpServletRequest request, Doctor doctor) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User currentUser = (User) session.getAttribute("user");
            String userRole = (String) session.getAttribute("userRole");
            
            // Admin can access any doctor profile
            if ("admin".equals(userRole)) {
                return true;
            }
            
            // Doctors can access their own profile
            if (currentUser != null && currentUser.getId() == doctor.getUserId()) {
                return true;
            }
        }
        return false;
    }
    
    private void setCorsHeaders(HttpServletResponse response) {
        setCorsHeaders(null, response);
    }

    private void setCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        if (request != null) {
            String origin = request.getHeader("Origin");
            if (origin != null && !origin.isEmpty()) {
                response.setHeader("Access-Control-Allow-Origin", origin);
            } else {
                response.setHeader("Access-Control-Allow-Origin", "*");
            }
        } else {
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept, Origin");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    private String getString(JsonObject obj, String memberName) {
        if (obj != null && obj.has(memberName) && !obj.get(memberName).isJsonNull()) {
            try {
                return obj.get(memberName).getAsString();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
    private void sendErrorResponse(PrintWriter out, String message, int statusCode) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", message);
        
        out.print(gson.toJson(errorResponse));
        out.flush();
    }
}
