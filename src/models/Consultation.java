package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Consultation class representing a doctor-patient consultation
 */
public class Consultation {
    private int id;
    private int doctorId;
    private int patientId;
    private LocalDateTime dateTime;
    private String notes;
    private List<Message> messages;

    /**
     * Constructor for Consultation class
     * 
     * @param id Consultation ID
     * @param doctorId ID of the doctor
     * @param patientId ID of the patient
     * @param dateTime Date and time of the consultation
     * @param notes Initial notes about the consultation
     */
    public Consultation(int id, int doctorId, int patientId, LocalDateTime dateTime, String notes) {
        this.id = id;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.dateTime = dateTime;
        this.notes = notes;
        this.messages = new ArrayList<>();
    }
    
    /**
     * Constructor with current date and time
     */
    public Consultation(int id, int doctorId, int patientId, String notes) {
        this(id, doctorId, patientId, LocalDateTime.now(), notes);
    }

    // Getters
    public int getId() { return id; }
    public int getDoctorId() { return doctorId; }
    public int getPatientId() { return patientId; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getNotes() { return notes; }
    public List<Message> getMessages() { return messages; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setMessages(List<Message> messages) { this.messages = messages; }

    /**
     * Add a message to the consultation
     * 
     * @param message The message to add
     */
    public void addMessage(Message message) {
        if (message != null) {
            messages.add(message);
        }
    }

    /**
     * Generate a prescription from this consultation
     * 
     * @param prescriptionId ID for the new prescription
     * @param instructions Instructions for the prescription
     * @return The generated prescription
     */
    public Prescription generatePrescription(int prescriptionId, String instructions) {
        Prescription prescription = new Prescription(
            prescriptionId,
            patientId,
            doctorId,
            java.time.LocalDate.now(),
            java.time.LocalDate.now().plusMonths(1),
            "Created",
            instructions
        );
        
        return prescription;
    }

    /**
     * Display consultation information
     */
    public void displayInfo() {
        System.out.println("\n===== CONSULTATION =====");
        System.out.println("ID: " + id);
        System.out.println("Doctor ID: " + doctorId);
        System.out.println("Patient ID: " + patientId);
        System.out.println("Date/Time: " + dateTime);
        System.out.println("Notes: " + notes);
        System.out.println("Messages: " + messages.size());
    }
}