package com.medicare.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.medicare.dao.AppointmentDAO;
import com.medicare.dao.DoctorDAO;
import com.medicare.dao.UserDAO;
import com.medicare.model.Appointment;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AppointmentServlet handles appointment-related operations
 * Supports CORS for frontend integration
 */
@WebServlet("/api/appointments/*")
public class AppointmentServlet extends HttpServlet {
    
    private AppointmentDAO appointmentDAO;
    private DoctorDAO doctorDAO;
    private UserDAO userDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        super.init();
        appointmentDAO = new AppointmentDAO();
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
                // Create new appointment
                createAppointment(request, response, out);
            } else {
                sendErrorResponse(out, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error in AppointmentServlet POST: " + e.getMessage());
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
                // Get appointments based on user role
                getAppointments(request, response, out);
            } else if (pathInfo.startsWith("/")) {
                // Get appointment by ID
                String appointmentId = pathInfo.substring(1);
                getAppointmentById(appointmentId, request, response, out);
            } else {
                sendErrorResponse(out, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error in AppointmentServlet GET: " + e.getMessage());
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
            if (pathInfo != null && pathInfo.startsWith("/")) {
                // Update appointment
                String appointmentId = pathInfo.substring(1);
                updateAppointment(appointmentId, request, response, out);
            } else {
                sendErrorResponse(out, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error in AppointmentServlet PUT: " + e.getMessage());
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
                // Delete appointment
                String appointmentId = pathInfo.substring(1);
                deleteAppointment(appointmentId, request, response, out);
            } else {
                sendErrorResponse(out, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error in AppointmentServlet DELETE: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(out, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void createAppointment(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        // Check if user is logged in
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            sendErrorResponse(out, "Authentication required", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);
        if (jsonObject == null) {
            sendErrorResponse(out, "Invalid or empty JSON payload", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        int doctorId = 0;
        if (jsonObject.has("doctorId") && !jsonObject.get("doctorId").isJsonNull()) {
            try {
                doctorId = jsonObject.get("doctorId").getAsInt();
            } catch (Exception ignored) {}
        }
        String appointmentDateStr = getString(jsonObject, "appointmentDate");
        String appointmentTimeStr = getString(jsonObject, "appointmentTime");
        String reason = getString(jsonObject, "reason");
        
        // Validate required fields
        if (doctorId <= 0 ||
            appointmentDateStr == null || appointmentDateStr.trim().isEmpty() || 
            appointmentTimeStr == null || appointmentTimeStr.trim().isEmpty() || 
            reason == null || reason.trim().isEmpty()) {
            sendErrorResponse(out, "All fields are required and doctor ID must be valid", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        try {
            // Parse date and time
            LocalDate appointmentDate = LocalDate.parse(appointmentDateStr);
            LocalTime appointmentTime = LocalTime.parse(appointmentTimeStr);
            
            // Get doctor information
            Doctor doctor = doctorDAO.getDoctorById(doctorId);
            if (doctor == null) {
                sendErrorResponse(out, "Doctor not found", HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // Check if time slot is available
            if (!appointmentDAO.isTimeSlotAvailable(doctorId, appointmentDate, appointmentTime)) {
                sendErrorResponse(out, "Time slot is not available", HttpServletResponse.SC_CONFLICT);
                return;
            }
            
            // Create appointment object
            Appointment appointment = new Appointment();
            appointment.setPatientId(currentUser.getId());
            appointment.setDoctorId(doctorId);
            appointment.setPatientName(currentUser.getName());
            appointment.setDoctorName(doctor.getName());
            appointment.setAppointmentDate(appointmentDate);
            appointment.setAppointmentTime(appointmentTime);
            appointment.setReason(reason);
            appointment.setStatus("pending");
            
            // Create appointment in database
            int appointmentId = appointmentDAO.createAppointment(appointment);
            
            if (appointmentId > 0) {
                // Get the complete appointment information
                Appointment createdAppointment = appointmentDAO.getAppointmentById(appointmentId);
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Appointment created successfully");
                responseData.put("appointment", createdAppointment);
                
                out.print(gson.toJson(responseData));
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                sendErrorResponse(out, "Failed to create appointment", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            System.err.println("Error parsing date/time: " + e.getMessage());
            sendErrorResponse(out, "Invalid date or time format", HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private void getAppointments(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            sendErrorResponse(out, "Authentication required", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        String status = request.getParameter("status");
        String date = request.getParameter("date");
        
        List<Appointment> appointments;
        
        if ("admin".equals(currentUser.getRole())) {
            // Admin can see all appointments
            if (status != null && !status.trim().isEmpty()) {
                appointments = appointmentDAO.getAppointmentsByStatus(status);
            } else {
                appointments = appointmentDAO.getAllAppointments();
            }
        } else if ("doctor".equals(currentUser.getRole())) {
            // Doctor can see their appointments
            if (date != null && !date.trim().isEmpty()) {
                LocalDate appointmentDate = LocalDate.parse(date);
                appointments = appointmentDAO.getAppointmentsByDateAndDoctor(appointmentDate, currentUser.getId());
            } else {
                appointments = appointmentDAO.getAppointmentsByDoctor(currentUser.getId());
            }
        } else {
            // Patient can see their appointments
            appointments = appointmentDAO.getAppointmentsByPatient(currentUser.getId());
        }
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("appointments", appointments);
        
        out.print(gson.toJson(responseData));
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private void getAppointmentById(String appointmentId, HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            sendErrorResponse(out, "Authentication required", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        try {
            int id = Integer.parseInt(appointmentId);
            Appointment appointment = appointmentDAO.getAppointmentById(id);
            
            if (appointment == null) {
                sendErrorResponse(out, "Appointment not found", HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // Check if user can access this appointment
            if (!canAccessAppointment(currentUser, appointment)) {
                sendErrorResponse(out, "Access denied", HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("appointment", appointment);
            
            out.print(gson.toJson(responseData));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (NumberFormatException e) {
            sendErrorResponse(out, "Invalid appointment ID", HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private void updateAppointment(String appointmentId, HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            sendErrorResponse(out, "Authentication required", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        try {
            int id = Integer.parseInt(appointmentId);
            Appointment existingAppointment = appointmentDAO.getAppointmentById(id);
            
            if (existingAppointment == null) {
                sendErrorResponse(out, "Appointment not found", HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // Check if user can update this appointment
            if (!canAccessAppointment(currentUser, existingAppointment)) {
                sendErrorResponse(out, "Access denied", HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);
            
            // Update appointment status (for doctors and admins)
            if (jsonObject.has("status") && !jsonObject.get("status").isJsonNull()) {
                String newStatus = jsonObject.get("status").getAsString();
                
                // Validate status transition
                if (isValidStatusTransition(existingAppointment.getStatus(), newStatus, currentUser.getRole())) {
                    boolean updated = appointmentDAO.updateAppointmentStatus(id, newStatus);
                    
                    if (updated) {
                        Appointment updatedAppointment = appointmentDAO.getAppointmentById(id);
                        
                        Map<String, Object> responseData = new HashMap<>();
                        responseData.put("success", true);
                        responseData.put("message", "Appointment status updated successfully");
                        responseData.put("appointment", updatedAppointment);
                        
                        out.print(gson.toJson(responseData));
                        response.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        sendErrorResponse(out, "Failed to update appointment", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                } else {
                    sendErrorResponse(out, "Invalid status transition", HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                sendErrorResponse(out, "Status is required for update", HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(out, "Invalid appointment ID", HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private void deleteAppointment(String appointmentId, HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            sendErrorResponse(out, "Authentication required", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        try {
            int id = Integer.parseInt(appointmentId);
            Appointment appointment = appointmentDAO.getAppointmentById(id);
            
            if (appointment == null) {
                sendErrorResponse(out, "Appointment not found", HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // Check if user can delete this appointment
            if (!canAccessAppointment(currentUser, appointment)) {
                sendErrorResponse(out, "Access denied", HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            boolean deleted = appointmentDAO.deleteAppointment(id);
            
            if (deleted) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Appointment deleted successfully");
                
                out.print(gson.toJson(responseData));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                sendErrorResponse(out, "Failed to delete appointment", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(out, "Invalid appointment ID", HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }
    
    private boolean canAccessAppointment(User currentUser, Appointment appointment) {
        // Admin can access any appointment
        if ("admin".equals(currentUser.getRole())) {
            return true;
        }
        
        // Doctor can access their appointments
        if ("doctor".equals(currentUser.getRole()) && currentUser.getId() == appointment.getDoctorId()) {
            return true;
        }
        
        // Patient can access their appointments
        if ("patient".equals(currentUser.getRole()) && currentUser.getId() == appointment.getPatientId()) {
            return true;
        }
        
        return false;
    }
    
    private boolean isValidStatusTransition(String currentStatus, String newStatus, String userRole) {
        // Admin can change any status
        if ("admin".equals(userRole)) {
            return true;
        }
        
        // Doctor can approve or reject pending appointments
        if ("doctor".equals(userRole)) {
            return "pending".equals(currentStatus) && 
                   ("approved".equals(newStatus) || "cancelled".equals(newStatus));
        }
        
        // Patient can cancel their own appointments
        if ("patient".equals(userRole)) {
            return ("pending".equals(currentStatus) || "approved".equals(currentStatus)) && 
                   "cancelled".equals(newStatus);
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
