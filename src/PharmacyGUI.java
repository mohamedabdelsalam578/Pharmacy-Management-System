import gui.MainFrame;
import services.PharmacyService;
import models.Doctor;

import javax.swing.*;
import java.awt.*;

/**
 * Main class for the Pharmacy GUI application
 */
public class PharmacyGUI {
    
    /**
     * Main method to start the application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Starting PharmacyGUI application...");
        
        try {
            // Set Look and Feel
            System.out.println("Setting Look and Feel...");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("Look and Feel set successfully.");
            
            // Initialize files
            System.out.println("Initializing files...");
            utils.FileHandler.initializeFiles();
            System.out.println("Files initialized successfully.");
            
            // Create PharmacyService
            System.out.println("Creating PharmacyService...");
            final PharmacyService service = PharmacyService.getInstance();
            
            // CRITICAL: Ensure sample data is loaded
            if ((service.getPatients() == null || service.getPatients().isEmpty()) ||
                (service.getDoctors() == null || service.getDoctors().isEmpty()) ||
                (service.getAdmins() == null || service.getAdmins().isEmpty())) {
                
                System.out.println("Initializing PharmacyService with sample data...");
                service.initialize();
                service.saveDataToFiles();
                
                // Verify data was loaded
                System.out.println("Patients: " + 
                    (service.getPatients() != null ? service.getPatients().size() : "NULL"));
                System.out.println("Doctors: " + 
                    (service.getDoctors() != null ? service.getDoctors().size() : "NULL"));
                System.out.println("Admins: " + 
                    (service.getAdmins() != null ? service.getAdmins().size() : "NULL"));
            }
            
            System.out.println("PharmacyService created successfully.");
            
            // Create and show MainFrame
            System.out.println("Creating and showing MainFrame...");
            SwingUtilities.invokeLater(() -> {
                System.out.println("In SwingUtilities.invokeLater...");
                MainFrame mainFrame = new MainFrame();
                
                // Set our initialized service via proper method
                try {
                    // Get the current service from MainFrame to check if it's null
                    PharmacyService currentService = mainFrame.getPharmacyService();
                    
                    // Use reflection as a last resort if getPharmacyService doesn't work properly
                    if (currentService == null) {
                        java.lang.reflect.Field serviceField = MainFrame.class.getDeclaredField("service");
                        serviceField.setAccessible(true);
                        serviceField.set(mainFrame, service);
                        System.out.println("Set service using reflection.");
                    } else {
                        // This approach would work if MainFrame had a setService method
                        System.out.println("MainFrame already has a service initialized. Data may be inconsistent.");
                    }
                } catch (Exception e) {
                    System.err.println("Could not set service: " + e.getMessage());
                    e.printStackTrace();
                }
                
                mainFrame.setVisible(true);
                System.out.println("MainFrame set visible.");
            });
            
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 