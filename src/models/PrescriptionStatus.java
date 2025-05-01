package models;

public enum PrescriptionStatus {
    PENDING("Pending"),
    VALIDATED("Validated"),
    REJECTED("Rejected"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    SENT_TO_PHARMACY("Sent to Pharmacy"),
    FILLED("Filled");
    
    private final String displayName;
    
    PrescriptionStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String toLowerCase() {
        return displayName.toLowerCase();
    }
    
    public static PrescriptionStatus fromString(String status) {
        try {
            return valueOf(status.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            // Default to PENDING if the status string is invalid
            return PENDING;
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
} 