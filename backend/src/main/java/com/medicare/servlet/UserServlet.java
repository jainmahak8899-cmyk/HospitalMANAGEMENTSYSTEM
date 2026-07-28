package com.medicare.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.medicare.dao.UserDAO;
import com.medicare.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * UserServlet handles user registration, login, and profile operations
 * Supports CORS for frontend integration
 */
@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        super.init();
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
                // Register new user
                registerUser(request, response, out);
            } else if (pathInfo.equals("/login")) {
                // User login
                loginUser(request, response, out);
            } else if (pathInfo.equals("/logout")) {
                // User logout
                logoutUser(request, response, out);
            } else {
                sendErrorResponse(out, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error in UserServlet POST: " + e.getMessage());
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
                // Get all users (admin only)
                getAllUsers(request, response, out);
            } else if (pathInfo.startsWith("/profile/")) {
                // Get user profile
                String userId = pathInfo.substring("/profile/".length());
                getUserProfile(userId, request, response, out);
            } else if (pathInfo.equals("/current")) {
                // Get current user from session
                getCurrentUser(request, response, out);
            } else {
                sendErrorResponse(out, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error in UserServlet GET: " + e.getMessage());
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
                // Update user profile
                String userId = pathInfo.substring("/profile/".length());
                updateUserProfile(userId, request, response, out);
            } else {
                sendErrorResponse(out, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error in UserServlet PUT: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(out, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void registerUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);
        if (jsonObject == null) {
            sendErrorResponse(out, "Invalid or empty JSON payload", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String name = getString(jsonObject, "name");
        String email = getString(jsonObject, "email");
        String password = getString(jsonObject, "password");
        String role = getString(jsonObject, "role");
        
        // Validate required fields
        if (name == null || name.trim().isEmpty() || 
            email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty() || 
            role == null || role.trim().isEmpty()) {
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
        user.setRole(role);
        
        // Set optional fields if provided
        if (jsonObject.has("age") && !jsonObject.get("age").isJsonNull()) {
            try {
                user.setAge(jsonObject.get("age").getAsInt());
            } catch (Exception ignored) {}
        }
        if (jsonObject.has("gender") && !jsonObject.get("gender").isJsonNull()) {
            user.setGender(getString(jsonObject, "gender"));
        }
        if (jsonObject.has("mobile") && !jsonObject.get("mobile").isJsonNull()) {
            user.setMobile(getString(jsonObject, "mobile"));
        }
        
        // Create user in database
        int userId = userDAO.createUser(user);
        
        if (userId > 0) {
            user.setId(userId);
            // Remove password from response
            user.setPassword(null);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "User registered successfully");
            responseData.put("user", user);
            
            out.print(gson.toJson(responseData));
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            sendErrorResponse(out, "Failed to register user", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void loginUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);
        if (jsonObject == null) {
            sendErrorResponse(out, "Invalid or empty JSON payload", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String email = getString(jsonObject, "email");
        String password = getString(jsonObject, "password");
        String role = getString(jsonObject, "role");
        
        // Validate required fields
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty() || 
            role == null || role.trim().isEmpty()) {
            sendErrorResponse(out, "Email, password, and role are required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        // Authenticate user
        User user = userDAO.authenticateUser(email, password, role);
        
        if (user != null) {
            // Create session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());
            
            // Remove password from response
            user.setPassword(null);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Login successful");
            responseData.put("user", user);
            
            out.print(gson.toJson(responseData));
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            sendErrorResponse(out, "Invalid credentials", HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    
    private void logoutUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            session.invalidate();
        }
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("message", "Logout successful");
        
        out.print(gson.toJson(responseData));
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private void getAllUsers(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        // Check if user is admin
        if (!isAdmin(request)) {
            sendErrorResponse(out, "Access denied", HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String role = request.getParameter("role");
        java.util.List<User> users;
        
        if (role != null && !role.trim().isEmpty()) {
            users = userDAO.getUsersByRole(role);
        } else {
            users = userDAO.getAllUsers();
        }
        
        // Remove passwords from response
        for (User user : users) {
            user.setPassword(null);
        }
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("users", users);
        
        out.print(gson.toJson(responseData));
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private void getUserProfile(String userId, HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        try {
            int id = Integer.parseInt(userId);
            User user = userDAO.getUserById(id);
            
            if (user != null) {
                // Check if user can access this profile
                if (!canAccessProfile(request, user)) {
                    sendErrorResponse(out, "Access denied", HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                
                // Remove password from response
                user.setPassword(null);
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("user", user);
                
                out.print(gson.toJson(responseData));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                sendErrorResponse(out, "User not found", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(out, "Invalid user ID", HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private void getCurrentUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                // Remove password from response
                user.setPassword(null);
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("user", user);
                
                out.print(gson.toJson(responseData));
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
        }
        
        sendErrorResponse(out, "No active session", HttpServletResponse.SC_UNAUTHORIZED);
    }
    
    private void updateUserProfile(String userId, HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        try {
            int id = Integer.parseInt(userId);
            User existingUser = userDAO.getUserById(id);
            
            if (existingUser == null) {
                sendErrorResponse(out, "User not found", HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // Check if user can update this profile
            if (!canAccessProfile(request, existingUser)) {
                sendErrorResponse(out, "Access denied", HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);
            
            // Update user fields
            if (jsonObject.has("name") && !jsonObject.get("name").isJsonNull()) {
                existingUser.setName(jsonObject.get("name").getAsString());
            }
            if (jsonObject.has("age") && !jsonObject.get("age").isJsonNull()) {
                existingUser.setAge(jsonObject.get("age").getAsInt());
            }
            if (jsonObject.has("gender") && !jsonObject.get("gender").isJsonNull()) {
                existingUser.setGender(jsonObject.get("gender").getAsString());
            }
            if (jsonObject.has("mobile") && !jsonObject.get("mobile").isJsonNull()) {
                existingUser.setMobile(jsonObject.get("mobile").getAsString());
            }
            
            boolean updated = userDAO.updateUser(existingUser);
            
            if (updated) {
                // Update session if it's the current user
                HttpSession session = request.getSession(false);
                if (session != null) {
                    User sessionUser = (User) session.getAttribute("user");
                    if (sessionUser != null && sessionUser.getId() == id) {
                        session.setAttribute("user", existingUser);
                    }
                }
                
                // Remove password from response
                existingUser.setPassword(null);
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Profile updated successfully");
                responseData.put("user", existingUser);
                
                out.print(gson.toJson(responseData));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                sendErrorResponse(out, "Failed to update profile", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(out, "Invalid user ID", HttpServletResponse.SC_BAD_REQUEST);
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
    
    private boolean canAccessProfile(HttpServletRequest request, User targetUser) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User currentUser = (User) session.getAttribute("user");
            String userRole = (String) session.getAttribute("userRole");
            
            // Admin can access any profile
            if ("admin".equals(userRole)) {
                return true;
            }
            
            // Users can access their own profile
            if (currentUser != null && currentUser.getId() == targetUser.getId()) {
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
