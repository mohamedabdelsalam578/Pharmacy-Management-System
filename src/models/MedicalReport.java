package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a medical report in the pharmacy system
 */
public class MedicalReport {
    private int id;
    private int patientId;
    private int doctorId;
    private LocalDate date;
    private String type;
    private String summary;
    private String content;
    private List<String> symptoms;
    private List<String> testResults;
    private List<Prescription> relatedPrescriptions;
    private byte[] attachments;
    private String notes;

    /**
     * Constructor for MedicalReport class
     * 
     * @param id Report ID
     * @param patientId ID of the patient this report is for
     * @param doctorId ID of the doctor who created this report
     * @param date Date the report was created
     * @param type Type of the report
     * @param summary Summary of the report
     * @param content Content of the report
     */
    public MedicalReport(int id, int patientId, int doctorId, LocalDate date, String type, String summary, String content) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.type = type;
        this.summary = summary;
        this.content = content;
        this.symptoms = new ArrayList<>();
        this.testResults = new ArrayList<>();
        this.relatedPrescriptions = new ArrayList<>();
        this.attachments = new byte[0];
        this.notes = "";
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getPatientId() {
        return patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getSummary() {
        return summary;
    }

    public String getContent() {
        return content;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public List<String> getTestResults() {
        return testResults;
    }

    public List<Prescription> getRelatedPrescriptions() {
        return relatedPrescriptions;
    }

    public byte[] getAttachments() {
        return attachments;
    }

    public String getNotes() {
        return notes;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public void setTestResults(List<String> testResults) {
        this.testResults = testResults;
    }

    public void setRelatedPrescriptions(List<Prescription> relatedPrescriptions) {
        this.relatedPrescriptions = relatedPrescriptions;
    }

    public void setAttachments(byte[] attachments) {
        this.attachments = attachments;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Add a symptom to the report
     * 
     * @param symptom The symptom to add
     */
    public void addSymptom(String symptom) {
        if (symptom != null && !symptom.isEmpty()) {
            symptoms.add(symptom);
        }
    }

    /**
     * Add a test result to the report
     * 
     * @param testResult The test result to add
     */
    public void addTestResult(String testResult) {
        if (testResult != null && !testResult.isEmpty()) {
            testResults.add(testResult);
        }
    }

    /**
     * Link a prescription to this report
     * 
     * @param prescription The prescription to link
     */
    public void linkPrescription(Prescription prescription) {
        if (prescription != null && !relatedPrescriptions.contains(prescription)) {
            relatedPrescriptions.add(prescription);
        }
    }

    /**
     * Display detailed information about this medical report
     */
    public void displayInfo() {
        System.out.println("\n===== MEDICAL REPORT =====");
        System.out.println("ID: " + id);
        System.out.println("Patient ID: " + patientId);
        System.out.println("Doctor ID: " + doctorId);
        System.out.println("Date: " + date);
        System.out.println("Type: " + type);
        System.out.println("Summary: " + summary);
        
        System.out.println("\nSymptoms:");
        if (symptoms.isEmpty()) {
            System.out.println("  None recorded");
        } else {
            for (String symptom : symptoms) {
                System.out.println("  - " + symptom);
            }
        }
        
        System.out.println("\nTest Results:");
        if (testResults.isEmpty()) {
            System.out.println("  None recorded");
        } else {
            for (String result : testResults) {
                System.out.println("  - " + result);
            }
        }
        
        System.out.println("\nRelated Prescriptions: " + relatedPrescriptions.size());
        System.out.println("Notes: " + notes);
    }

    @Override
    public String toString() {
        return "MedicalReport{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }
}