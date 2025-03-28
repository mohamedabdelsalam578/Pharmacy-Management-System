package utils;

import models.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * FileHandler class to handle all file operations for data persistence
 * Simplified implementation with better error handling and more generic methods
 */
public class FileHandler {
    // File paths for storing data
    private static final String DATA_DIR = "data";
    private static final String[] ALL_FILES = {
        "admins.txt", "patients.txt", "clients.txt", "doctors.txt", 
        "pharmacists.txt", "pharmacies.txt", "medicines.txt", 
        "orders.txt", "prescriptions.txt"
    };
    
    // File path constants for easier access
    public static final String ADMINS_FILE = DATA_DIR + "/admins.txt";
    public static final String PATIENTS_FILE = DATA_DIR + "/patients.txt";
    public static final String LEGACY_PATIENTS_FILE = DATA_DIR + "/clients.txt";
    public static final String DOCTORS_FILE = DATA_DIR + "/doctors.txt";
    public static final String PHARMACISTS_FILE = DATA_DIR + "/pharmacists.txt";
    public static final String PHARMACIES_FILE = DATA_DIR + "/pharmacies.txt";
    public static final String MEDICINES_FILE = DATA_DIR + "/medicines.txt";
    public static final String ORDERS_FILE = DATA_DIR + "/orders.txt";
    public static final String PRESCRIPTIONS_FILE = DATA_DIR + "/prescriptions.txt";
    
    // Standard date formatters
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Initialize all data files
     */
    public static void initializeFiles() {
        // Create data directory if it doesn't exist
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
        
        // Create all files in a loop
        Arrays.stream(ALL_FILES)
              .map(file -> DATA_DIR + "/" + file)
              .forEach(FileHandler::createFile);
    }
    
    /**
     * Create a file if it doesn't exist
     * 
     * @param filePath Path of the file to create
     */
    private static void createFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logError("Error creating file: " + filePath, e);
            }
        }
    }
    
    /**
     * Generic method to load entities from a file
     * 
     * @param <T> Type of entity to load
     * @param filePath Path to the file
     * @param lineParser Function to convert a line into an entity
     * @return List of loaded entities
     */
    private static <T> List<T> loadEntities(String filePath, Function<String, Optional<T>> lineParser) {
        List<T> entities = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineParser.apply(line).ifPresent(entities::add);
            }
        } catch (IOException e) {
            logError("Error loading from file " + filePath, e);
        }
        return entities;
    }
    
    /**
     * Generic method to save entities to a file
     * 
     * @param <T> Type of entity to save
     * @param entities List of entities to save
     * @param filePath File path to save to
     * @param formatter Function to format an entity to a string
     */
    private static <T> void saveEntities(List<T> entities, String filePath, Function<T, String> formatter) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (T entity : entities) {
                writer.write(formatter.apply(entity));
                writer.newLine();
            }
        } catch (IOException e) {
            logError("Error saving to file " + filePath, e);
        }
    }
    
    /**
     * Log an error message with exception details
     * 
     * @param message Error message
     * @param e Exception
     */
    private static void logError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
    }
    
    // ================ Admin Methods ================
    
    /**
     * Load admins from file
     * 
     * @return List of Admin objects
     */
    public static List<Admin> loadAdmins() {
        return loadEntities(ADMINS_FILE, line -> {
            String[] parts = line.split("\\|");
            if (parts.length < 8) return Optional.empty();
            
            try {
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String username = parts[2].trim();
                String password = parts[3].trim();
                String email = parts[4].trim();
                String phone = parts[5].trim();
                String position = parts[6].trim();
                String department = parts[7].trim();
                
                return Optional.of(new Admin(id, name, username, password, email, phone, position, department));
            } catch (Exception e) {
                logError("Error parsing admin", e);
                return Optional.empty();
            }
        });
    }
    
    /**
     * Save admins to file
     * 
     * @param admins List of Admin objects to save
     */
    public static void saveAdmins(List<Admin> admins) {
        saveEntities(admins, ADMINS_FILE, admin -> 
            String.format("%d|%s|%s|%s|%s|%s|%s|%s", 
                admin.getId(), 
                admin.getName(),
                admin.getUsername(),
                admin.getPassword(),
                admin.getEmail(),
                admin.getPhoneNumber(),
                admin.getRole(),
                admin.getDepartment())
        );
    }
    
    // ================ Patient Methods ================
    
    /**
     * Load patients from file
     * 
     * @return List of Patient objects
     */
    public static List<Patient> loadPatients() {
        List<Patient> patients = loadEntities(PATIENTS_FILE, line -> {
            String[] parts = line.split("\\|");
            if (parts.length < 7) return Optional.empty();
            
            try {
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String username = parts[2].trim();
                String password = parts[3].trim();
                String email = parts[4].trim();
                String phone = parts[5].trim();
                String address = parts[6].trim();
                
                Patient patient = new Patient(id, name, username, password, email, phone, address);
                
                // Load wallet balance if available
                if (parts.length >= 8) {
                    double balance = Double.parseDouble(parts[7].trim());
                    if (balance > 0) {
                        patient.getWallet().deposit(balance, "Balance loaded from file");
                    }
                }
                
                // Load transactions if available
                if (parts.length >= 9) {
                    loadWalletTransactions(patient, parts[8].trim());
                }
                
                return Optional.of(patient);
            } catch (Exception e) {
                logError("Error parsing patient", e);
                return Optional.empty();
            }
        });
        
        // Try legacy file if no patients found
        if (patients.isEmpty()) {
            patients = loadEntities(LEGACY_PATIENTS_FILE, line -> {
                String[] parts = line.split("\\|");
                if (parts.length < 7) return Optional.empty();
                
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String username = parts[2].trim();
                    String password = parts[3].trim();
                    String email = parts[4].trim();
                    String phone = parts[5].trim();
                    String address = parts[6].trim();
                    
                    return Optional.of(new Patient(id, name, username, password, email, phone, address));
                } catch (Exception e) {
                    logError("Error parsing legacy patient", e);
                    return Optional.empty();
                }
            });
        }
        
        return patients;
    }
    
    /**
     * Helper method to load wallet transactions
     * 
     * @param patient Patient to load transactions for
     * @param transactionsData Transaction data string
     */
    private static void loadWalletTransactions(Patient patient, String transactionsData) {
        // Create a new wallet to clear any existing transactions
        patient.setWallet(new Wallet(patient));
        
        String[] transactions = transactionsData.split(";");
        for (String txnStr : transactions) {
            String[] txnParts = txnStr.split(":");
            if (txnParts.length < 5) continue;
            
            try {
                String txnId = txnParts[0].trim();
                double amount = Double.parseDouble(txnParts[1].trim());
                String type = txnParts[2].trim();
                String description = txnParts[3].trim().replace("-", ":").replace(",", ";");
                
                // Parse datetime
                LocalDateTime dateTime;
                try {
                    dateTime = LocalDateTime.parse(txnParts[4].trim());
                } catch (Exception e) {
                    dateTime = LocalDateTime.now();
                }
                
                // Process transaction based on type
                switch (type) {
                    case "DEPOSIT":
                        patient.getWallet().deposit(amount, description);
                        break;
                    case "WITHDRAWAL":
                        // Ensure enough balance
                        if (patient.getWallet().getBalance() < amount) {
                            patient.getWallet().deposit(amount, "Balance adjustment for withdrawal");
                        }
                        patient.getWallet().withdraw(amount, description);
                        break;
                    case "PAYMENT":
                        // Ensure enough balance
                        if (patient.getWallet().getBalance() < amount) {
                            patient.getWallet().deposit(amount, "Balance adjustment for payment");
                        }
                        // Extract order ID if possible
                        int orderId = 0;
                        if (description.contains("#")) {
                            try {
                                String orderIdStr = description.substring(description.indexOf("#") + 1);
                                orderId = Integer.parseInt(orderIdStr.split(" ")[0]);
                            } catch (Exception ignored) {}
                        }
                        patient.getWallet().makePayment(amount, orderId, description);
                        break;
                }
            } catch (Exception e) {
                logError("Error parsing transaction", e);
            }
        }
    }
    
    /**
     * Save patients to file
     * 
     * @param patients List of Patient objects to save
     */
    public static void savePatients(List<Patient> patients) {
        // Save to main patients file with wallet data
        saveEntities(patients, PATIENTS_FILE, patient -> {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%d|%s|%s|%s|%s|%s|%s|%.2f", 
                patient.getId(), 
                patient.getName(),
                patient.getUsername(),
                patient.getPassword(),
                patient.getEmail(),
                patient.getPhoneNumber(),
                patient.getAddress(),
                patient.getWallet().getBalance()));
            
            // Add transactions if any exist
            List<Wallet.Transaction> transactions = patient.getWallet().getTransactions();
            if (!transactions.isEmpty()) {
                sb.append("|");
                sb.append(transactions.stream()
                    .map(txn -> {
                        String formattedDateTime = ISO_DATE_FORMAT.format(txn.getDateTime());
                        return String.format("%s:%.2f:%s:%s:%s", 
                            txn.getId(),
                            txn.getAmount(), 
                            txn.getType().name(),
                            txn.getDescription().replace(":", "-").replace(";", ","),
                            formattedDateTime);
                    })
                    .collect(Collectors.joining(";")));
            }
            
            return sb.toString();
        });
        
        // Also save to legacy file for backward compatibility
        saveEntities(patients, LEGACY_PATIENTS_FILE, patient -> 
            String.format("%d|%s|%s|%s|%s|%s|%s", 
                patient.getId(), 
                patient.getName(),
                patient.getUsername(),
                patient.getPassword(),
                patient.getEmail(),
                patient.getPhoneNumber(),
                patient.getAddress())
        );
    }
    
    // ================ Doctor Methods ================
    
    /**
     * Load doctors from file
     * 
     * @return List of Doctor objects
     */
    public static List<Doctor> loadDoctors() {
        return loadEntities(DOCTORS_FILE, line -> {
            String[] parts = line.split("\\|");
            if (parts.length < 8) return Optional.empty();
            
            try {
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String username = parts[2].trim();
                String password = parts[3].trim();
                String email = parts[4].trim();
                String phone = parts[5].trim();
                String specialty = parts[6].trim();
                String licenseNumber = parts[7].trim();
                
                return Optional.of(new Doctor(id, name, username, password, email, phone, specialty, licenseNumber));
            } catch (Exception e) {
                logError("Error parsing doctor", e);
                return Optional.empty();
            }
        });
    }
    
    /**
     * Save doctors to file
     * 
     * @param doctors List of Doctor objects to save
     */
    public static void saveDoctors(List<Doctor> doctors) {
        saveEntities(doctors, DOCTORS_FILE, doctor -> 
            String.format("%d|%s|%s|%s|%s|%s|%s|%s", 
                doctor.getId(), 
                doctor.getName(),
                doctor.getUsername(),
                doctor.getPassword(),
                doctor.getEmail(),
                doctor.getPhoneNumber(),
                doctor.getSpecialization(),
                doctor.getLicenseNumber())
        );
    }
    
    // ================ Medicine Methods ================
    
    /**
     * Load medicines from file
     * 
     * @return List of Medicine objects
     */
    public static List<Medicine> loadMedicines() {
        return loadEntities(MEDICINES_FILE, line -> {
            String[] parts = line.split("\\|");
            if (parts.length < 8) return Optional.empty();
            
            try {
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                int stock = Integer.parseInt(parts[3].trim());
                String description = parts[4].trim();
                String dosage = parts[5].trim();
                String category = parts[6].trim();
                boolean requiresPrescription = Boolean.parseBoolean(parts[7].trim());
                
                return Optional.of(new Medicine(id, name, description, "Egyptian Pharma", price, stock, category, requiresPrescription));
            } catch (Exception e) {
                logError("Error parsing medicine", e);
                return Optional.empty();
            }
        });
    }
    
    /**
     * Save medicines to file
     * 
     * @param medicines List of Medicine objects to save
     */
    public static void saveMedicines(List<Medicine> medicines) {
        saveEntities(medicines, MEDICINES_FILE, medicine -> 
            String.format("%d|%s|%.2f|%d|%s|%s|%s|%b", 
                medicine.getId(), 
                medicine.getName(),
                medicine.getPrice(),
                medicine.getStock(),
                medicine.getDescription(),
                medicine.getDosage(),
                medicine.getCategory(),
                medicine.isRequiresPrescription())
        );
    }
    
    // ================ Order Methods ================
    
    /**
     * Load orders from file
     * 
     * @param allMedicines List of all medicines for reference when loading order items
     * @return List of Order objects
     */
    public static List<Order> loadOrders(List<Medicine> allMedicines) {
        return loadEntities(ORDERS_FILE, line -> {
            String[] parts = line.split("\\|");
            if (parts.length < 6) return Optional.empty();
            
            try {
                int id = Integer.parseInt(parts[0].trim());
                int patientId = Integer.parseInt(parts[1].trim());
                String orderDateStr = parts[2].trim();
                double totalAmount = Double.parseDouble(parts[3].trim());
                
                // Create order
                Order order = new Order(id, patientId, orderDateStr);
                order.setTotalAmount(totalAmount);
                
                // Set status
                try {
                    order.setStatus(Order.Status.valueOf(parts[4].trim()));
                } catch (IllegalArgumentException e) {
                    order.setStatus(Order.Status.PENDING);
                }
                
                // Set payment method
                try {
                    order.setPaymentMethod(Order.PaymentMethod.valueOf(parts[5].trim()));
                } catch (IllegalArgumentException e) {
                    order.setPaymentMethod(Order.PaymentMethod.NOT_PAID);
                }
                
                // Set paid status
                if (parts.length >= 7) {
                    order.setPaid(Boolean.parseBoolean(parts[6].trim()));
                }
                
                // Process order items
                if (parts.length >= 8 && !parts[7].trim().isEmpty()) {
                    String[] itemsArray = parts[7].trim().split(";");
                    for (String itemStr : itemsArray) {
                        String[] itemParts = itemStr.split(":");
                        if (itemParts.length >= 2) {
                            int medicineId = Integer.parseInt(itemParts[0].trim());
                            int quantity = Integer.parseInt(itemParts[1].trim());
                            
                            // Find and add medicine
                            allMedicines.stream()
                                .filter(med -> med.getId() == medicineId)
                                .findFirst()
                                .ifPresent(med -> order.addMedicine(med, quantity));
                        }
                    }
                }
                
                // Set additional fields if available
                if (parts.length >= 9 && !parts[8].trim().isEmpty()) {
                    order.setPatientName(parts[8].trim());
                }
                
                if (parts.length >= 10 && !parts[9].trim().isEmpty()) {
                    order.setPatientPhone(parts[9].trim());
                }
                
                if (parts.length >= 11 && !parts[10].trim().isEmpty()) {
                    order.setPatientAddress(parts[10].trim());
                }
                
                if (parts.length >= 12 && !parts[11].trim().isEmpty()) {
                    try {
                        order.setDeliveryMethod(Order.DeliveryMethod.valueOf(parts[11].trim()));
                    } catch (IllegalArgumentException e) {
                        order.setDeliveryMethod(Order.DeliveryMethod.PICKUP);
                    }
                }
                
                return Optional.of(order);
            } catch (Exception e) {
                logError("Error parsing order", e);
                return Optional.empty();
            }
        });
    }
    
    /**
     * Save orders to file
     * 
     * @param orders List of Order objects to save
     */
    public static void saveOrders(List<Order> orders) {
        saveEntities(orders, ORDERS_FILE, order -> {
            String orderDateStr = DATE_FORMAT.format(order.getOrderDate());
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%d|%d|%s|%.2f|%s|%s|%b",
                order.getId(),
                order.getPatientId(),
                orderDateStr,
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getPaymentMethod().name(),
                order.isPaid()));
            
            // Add order items
            sb.append("|");
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                String itemsStr = order.getItems().stream()
                    .map(item -> item.getMedicineId() + ":" + item.getQuantity())
                    .collect(Collectors.joining(";"));
                sb.append(itemsStr);
            }
            
            // Add optional fields
            sb.append("|").append(order.getPatientName() != null ? order.getPatientName() : "");
            sb.append("|").append(order.getPatientPhone() != null ? order.getPatientPhone() : "");
            sb.append("|").append(order.getPatientAddress() != null ? order.getPatientAddress() : "");
            sb.append("|").append(order.getDeliveryMethod() != null ? order.getDeliveryMethod().name() : "");
            
            return sb.toString();
        });
    }
    
    // ================ Customer Methods (Legacy) ================
    
    /**
     * Load customers (legacy method)
     * 
     * @return List of Patient objects
     */
    public static List<Patient> loadCustomers() {
        return loadPatients();  // Delegate to the newer method
    }
    
    /**
     * Save customers (legacy method)
     * 
     * @param patients List of Patient objects to save as customers
     */
    public static void saveCustomers(List<Patient> patients) {
        savePatients(patients);  // Delegate to the newer method
    }
    
    // ================ Pharmacist Methods ================
    
    /**
     * Load pharmacists from file
     * 
     * @return List of Pharmacist objects
     */
    public static List<Pharmacist> loadPharmacists() {
        return loadEntities(PHARMACISTS_FILE, line -> {
            String[] parts = line.split("\\|");
            if (parts.length < 8) return Optional.empty();
            
            try {
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String username = parts[2].trim();
                String password = parts[3].trim();
                String email = parts[4].trim();
                String phone = parts[5].trim();
                String licenseNumber = parts[6].trim();
                int pharmacyId = Integer.parseInt(parts[7].trim());
                
                Pharmacist pharmacist = new Pharmacist(id, name, username, password, email, phone, licenseNumber, "Qualified Pharmacist");
                pharmacist.setPharmacyId(pharmacyId);
                
                return Optional.of(pharmacist);
            } catch (Exception e) {
                logError("Error parsing pharmacist", e);
                return Optional.empty();
            }
        });
    }
    
    /**
     * Save pharmacists to file
     * 
     * @param pharmacists List of Pharmacist objects to save
     */
    public static void savePharmacists(List<Pharmacist> pharmacists) {
        saveEntities(pharmacists, PHARMACISTS_FILE, pharmacist -> 
            String.format("%d|%s|%s|%s|%s|%s|%s|%d", 
                pharmacist.getId(), 
                pharmacist.getName(),
                pharmacist.getUsername(),
                pharmacist.getPassword(),
                pharmacist.getEmail(),
                pharmacist.getPhoneNumber(),
                pharmacist.getLicenseNumber(),
                pharmacist.getPharmacyId())
        );
    }
    
    // ================ Pharmacy Methods ================
    
    /**
     * Load pharmacies from file
     * 
     * @return List of Pharmacy objects
     */
    public static List<Pharmacy> loadPharmacies() {
        return loadEntities(PHARMACIES_FILE, line -> {
            String[] parts = line.split("\\|");
            if (parts.length < 7) return Optional.empty();
            
            try {
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String address = parts[2].trim();
                String phone = parts[3].trim();
                String email = parts[4].trim();
                String manager = parts[5].trim();
                String openingHours = parts[6].trim();
                
                return Optional.of(new Pharmacy(id, name, address, phone, email));
            } catch (Exception e) {
                logError("Error parsing pharmacy", e);
                return Optional.empty();
            }
        });
    }
    
    /**
     * Save pharmacies to file
     * 
     * @param pharmacies List of Pharmacy objects to save
     */
    public static void savePharmacies(List<Pharmacy> pharmacies) {
        saveEntities(pharmacies, PHARMACIES_FILE, pharmacy -> 
            String.format("%d|%s|%s|%s|%s|%s|%s", 
                pharmacy.getId(), 
                pharmacy.getName(),
                pharmacy.getAddress(),
                pharmacy.getPhoneNumber(),
                pharmacy.getEmail(),
                "Manager", // Default manager name since getManager() doesn't exist
                "9:00 AM - 9:00 PM" // Default opening hours since getOpeningHours() doesn't exist
            )
        );
    }
    
    // ================ Prescription Methods ================
    
    /**
     * Load prescriptions from file
     * 
     * @param allMedicines List of all medicines for reference when loading prescription items
     * @return List of Prescription objects
     */
    public static List<Prescription> loadPrescriptions(List<Medicine> allMedicines) {
        return loadEntities(PRESCRIPTIONS_FILE, line -> {
            String[] parts = line.split("\\|");
            if (parts.length < 5) return Optional.empty();
            
            try {
                int id = Integer.parseInt(parts[0].trim());
                int patientId = Integer.parseInt(parts[1].trim());
                int doctorId = Integer.parseInt(parts[2].trim());
                
                // Parse date to LocalDate
                LocalDate issueDate;
                try {
                    // Convert Date to LocalDate
                    Date date = DATE_ONLY_FORMAT.parse(parts[3].trim());
                    issueDate = new java.sql.Date(date.getTime()).toLocalDate();
                } catch (Exception e) {
                    issueDate = LocalDate.now();  // Use current date if parsing fails
                }
                
                // Create expiry date (30 days after issue date)
                LocalDate expiryDate = issueDate.plusDays(30);
                
                // Create prescription with appropriate constructor
                Prescription prescription = new Prescription(id, patientId, doctorId, issueDate, expiryDate, "Pending", "Take as directed");
                
                // Add medicines
                if (!parts[4].trim().isEmpty()) {
                    String[] itemsArray = parts[4].trim().split(";");
                    for (String itemStr : itemsArray) {
                        String[] itemParts = itemStr.split(":");
                        if (itemParts.length >= 2) {
                            int medicineId = Integer.parseInt(itemParts[0].trim());
                            int quantity = Integer.parseInt(itemParts[1].trim());
                            
                            // Find medicine and add to prescription
                            allMedicines.stream()
                                .filter(med -> med.getId() == medicineId)
                                .findFirst()
                                .ifPresent(med -> prescription.addMedicine(med, quantity));
                        }
                    }
                }
                
                return Optional.of(prescription);
            } catch (Exception e) {
                logError("Error parsing prescription", e);
                return Optional.empty();
            }
        });
    }
    
    /**
     * Save prescriptions to file
     * 
     * @param prescriptions List of Prescription objects to save
     */
    public static void savePrescriptions(List<Prescription> prescriptions) {
        saveEntities(prescriptions, PRESCRIPTIONS_FILE, prescription -> {
            // Convert LocalDate to Date for formatting
            Date issueDate = java.sql.Date.valueOf(prescription.getIssueDate());
            String dateStr = DATE_ONLY_FORMAT.format(issueDate);
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%d|%d|%d|%s|",
                prescription.getId(),
                prescription.getPatientId(),
                prescription.getDoctorId(),
                dateStr));
            
            // Add medicines without using stream since we can't use stream on Map directly
            if (!prescription.getMedicines().isEmpty()) {
                StringBuilder itemsStr = new StringBuilder();
                boolean first = true;
                
                for (Map.Entry<Medicine, Integer> entry : prescription.getMedicines().entrySet()) {
                    if (!first) {
                        itemsStr.append(";");
                    }
                    itemsStr.append(String.format("%d:%d", 
                        entry.getKey().getId(), 
                        entry.getValue()));
                    first = false;
                }
                
                sb.append(itemsStr.toString());
            }
            
            // Prescription doesn't have diagnosis and notes methods
            sb.append("||"); // Add empty placeholders for diagnosis and notes
            
            return sb.toString();
        });
    }
}