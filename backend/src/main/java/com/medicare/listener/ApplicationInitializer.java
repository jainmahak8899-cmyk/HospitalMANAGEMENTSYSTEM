package com.medicare.listener;

import com.medicare.util.DatabaseConnection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;

/**
 * Application initializer to set up database connection and perform startup tasks
 */
@WebListener
public class ApplicationInitializer implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("MediCare Backend Application Starting...");
        
        try {
            // Test database connection
            if (DatabaseConnection.testConnection()) {
                System.out.println("Database connection established successfully.");
                
                // Initialize database schema if needed
                initializeDatabase();
                
            } else {
                System.err.println("Failed to establish database connection.");
            }
            
        } catch (Exception e) {
            System.err.println("Error during application initialization: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("MediCare Backend Application Started Successfully!");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("MediCare Backend Application Shutting Down...");
        
        try {
            // Close database connection
            DatabaseConnection.closeConnection();
            System.out.println("Database connection closed.");
            
        } catch (Exception e) {
            System.err.println("Error during application shutdown: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("MediCare Backend Application Shutdown Complete.");
    }
    
    private void initializeDatabase() {
        try {
            // Check if tables exist, create if not
            Connection conn = DatabaseConnection.getConnection();
            
            // You can add database initialization logic here
            // For example, creating tables if they don't exist
            
            System.out.println("Database initialization completed.");
            
        } catch (Exception e) {
            System.err.println("Error during database initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
