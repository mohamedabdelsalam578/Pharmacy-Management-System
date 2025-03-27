package utils;

import models.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * FileHandler class to handle all file operations for data persistence
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
    
    // Formatter for date time
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
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
        for (String file : ALL_FILES) {
            createFile(DATA_DIR + "/" + file);
        }
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
                System.err.println("Error creating file: " + filePath);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Load admins from file
     * 
     * @return List of Admin objects
     */
    public static List<Admin> loadAdmins() {
        List<Admin> admins = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMINS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String username = parts[2].trim();
                    String password = parts[3].trim();
                    String email = parts[4].trim();
                    String phone = parts[5].trim();
                    String position = parts[6].trim();
                    String department = parts[7].trim();
                    
                    Admin admin = new Admin(id, name, username, password, email, phone, position, department);
                    admins.add(admin);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading admins: " + e.getMessage());
        }
        
        return admins;
    }
    
    /**
     * Save admins to file
     * 
     * @param admins List of Admin objects to save
     */
    public static void saveAdmins(List<Admin> admins) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMINS_FILE))) {
            for (Admin admin : admins) {
                writer.write(String.format("%d|%s|%s|%s|%s|%s|%s|%s", 
                    admin.getId(), 
                    admin.getName(),
                    admin.getUsername(),
                    admin.getPassword(),
                    admin.getEmail(),
                    admin.getPhoneNumber(),
                    admin.getRole(),
                    admin.getDepartment()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving admins: " + e.getMessage());
        }
    }
    
    /**
     * Load patients from file
     * 
     * @return List of Patient objects
     */
    public static List<Patient> loadPatients() {
        List<Patient> patients = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(PATIENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 7) {
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
                        // Initialize wallet with loaded balance (requires depositing the amount)
                        if (balance > 0) {
                            patient.getWallet().deposit(balance, "Balance loaded from file");
                        }
                    }
                    
                    // Load transactions if available
                    if (parts.length >= 9) {
                        String transactionsData = parts[8].trim();
                        String[] transactions = transactionsData.split(";");
                        
                        // Clear any existing transactions (the deposit from above)
                        patient.setWallet(new Wallet(patient));
                        
                        // Add transactions from file
                        for (String txnStr : transactions) {
                            String[] txnParts = txnStr.split(":");
                            if (txnParts.length >= 5) {
                                int txnId = Integer.parseInt(txnParts[0].trim());
                                double amount = Double.parseDouble(txnParts[1].trim());
                                String type = txnParts[2].trim();
                                String description = txnParts[3].trim();
                                
                                // Parse datetime
                                LocalDateTime dateTime;
                                try {
                                    dateTime = LocalDateTime.parse(txnParts[4].trim());
                                } catch (Exception e) {
                                    // If parsing fails, use current time
                                    dateTime = LocalDateTime.now();
                                }
                                
                                // Add transaction based on type
                                if (type.equals("DEPOSIT")) {
                                    patient.getWallet().deposit(amount, description);
                                } else if (type.equals("WITHDRAWAL")) {
                                    // For withdrawals, first ensure there's enough balance
                                    if (patient.getWallet().getBalance() < amount) {
                                        patient.getWallet().deposit(amount, "Balance adjustment for withdrawal");
                                    }
                                    patient.getWallet().withdraw(amount, description);
                                } else if (type.equals("PAYMENT")) {
                                    // For payments, first ensure there's enough balance
                                    if (patient.getWallet().getBalance() < amount) {
                                        patient.getWallet().deposit(amount, "Balance adjustment for payment");
                                    }
                                    // Extract order ID from description if possible
                                    int orderId = 0;
                                    if (description.contains("#")) {
                                        try {
                                            String orderIdStr = description.substring(description.indexOf("#") + 1);
                                            orderId = Integer.parseInt(orderIdStr.split(" ")[0]);
                                        } catch (Exception e) {
                                            // If parsing fails, use 0
                                        }
                                    }
                                    patient.getWallet().makePayment(amount, orderId);
                                }
                            }
                        }
                    }
                    
                    patients.add(patient);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading patients: " + e.getMessage());
        }
        
        // Also try to load from legacy files for backward compatibility
        if (patients.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(LEGACY_PATIENTS_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 7) {
                        int id = Integer.parseInt(parts[0].trim());
                        String name = parts[1].trim();
                        String username = parts[2].trim();
                        String password = parts[3].trim();
                        String email = parts[4].trim();
                        String phone = parts[5].trim();
                        String address = parts[6].trim();
                        
                        Patient patient = new Patient(id, name, username, password, email, phone, address);
                        patients.add(patient);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading from legacy patients file: " + e.getMessage());
            }
        }
        
        return patients;
    }
    
    /**
     * Save patients to file
     * 
     * @param patients List of Patient objects to save
     */
    public static void savePatients(List<Patient> patients) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATIENTS_FILE))) {
            for (Patient patient : patients) {
                // Include wallet balance in the saved data
                writer.write(String.format("%d|%s|%s|%s|%s|%s|%s|%.2f", 
                    patient.getId(), 
                    patient.getName(),
                    patient.getUsername(),
                    patient.getPassword(),
                    patient.getEmail(),
                    patient.getPhoneNumber(),
                    patient.getAddress(),
                    patient.getWallet().getBalance()));
                
                // If the patient has transactions, save them as well (format: txnId:amount:type:description:dateTime;)
                if (!patient.getWallet().getTransactions().isEmpty()) {
                    writer.write("|");
                    StringBuilder transactionsStr = new StringBuilder();
                    for (Wallet.Transaction txn : patient.getWallet().getTransactions()) {
                        if (transactionsStr.length() > 0) {
                            transactionsStr.append(";");
                        }
                        transactionsStr.append(String.format("%d:%.2f:%s:%s:%s", 
                            txn.getId(), 
                            txn.getAmount(),
                            txn.getType(),
                            txn.getDescription().replace(":", "-").replace(";", ","), // Escape special chars
                            txn.getDateTime().toString()));
                    }
                    writer.write(transactionsStr.toString());
                }
                
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving patients: " + e.getMessage());
        }
        
        // Also save to legacy files for backward compatibility (without wallet data)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LEGACY_PATIENTS_FILE))) {
            for (Patient patient : patients) {
                writer.write(String.format("%d|%s|%s|%s|%s|%s|%s", 
                    patient.getId(), 
                    patient.getName(),
                    patient.getUsername(),
                    patient.getPassword(),
                    patient.getEmail(),
                    patient.getPhoneNumber(),
                    patient.getAddress()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving to legacy patients file: " + e.getMessage());
        }
    }
    
    /**
     * Generic interface for creating objects from file data
     */
    @FunctionalInterface
    private interface EntityCreator<T> {
        T create(String[] parts);
    }
    
    /**
     * Generic method to load data from a file
     * 
     * @param <T> Type of entity to load
     * @param filePath Path to the file
     * @param creator Function to create entity from string parts
     * @param minParts Minimum number of parts required
     * @return List of entities
     */
    private static <T> List<T> loadFromFile(String filePath, EntityCreator<T> creator, int minParts) {
        List<T> entities = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= minParts) {
                    T entity = creator.create(parts);
                    if (entity != null) {
                        entities.add(entity);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading from file " + filePath + ": " + e.getMessage());
        }
        
        return entities;
    }
    
    /**
     * Load doctors from file
     * 
     * @return List of Doctor objects
     */
    public static List<Doctor> loadDoctors() {
        return loadFromFile(DOCTORS_FILE, parts -> {
            try {
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String username = parts[2].trim();
                String password = parts[3].trim();
                String email = parts[4].trim();
                String phone = parts[5].trim();
                String specialization = parts[6].trim();
                String licenseNumber = parts[7].trim();
                
                return new Doctor(id, name, username, password, email, phone, specialization, licenseNumber);
            } catch (Exception e) {
                System.err.println("Error parsing doctor data: " + e.getMessage());
                return null;
            }
        }, 8);
    }
    
    /**
     * Generic interface for converting objects to string representations
     */
    @FunctionalInterface
    private interface EntityFormatter<T> {
        String format(T entity);
    }
    
    /**
     * Generic method to save data to a file
     * 
     * @param <T> Type of entity to save
     * @param filePath Path to the file
     * @param entities List of entities to save
     * @param formatter Function to format entity to string
     */
    private static <T> void saveToFile(String filePath, List<T> entities, EntityFormatter<T> formatter) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (T entity : entities) {
                writer.write(formatter.format(entity));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving to file " + filePath + ": " + e.getMessage());
        }
    }
    
    /**
     * Save doctors to file
     * 
     * @param doctors List of Doctor objects to save
     */
    public static void saveDoctors(List<Doctor> doctors) {
        saveToFile(DOCTORS_FILE, doctors, doctor -> String.format("%d|%s|%s|%s|%s|%s|%s|%s", 
            doctor.getId(), 
            doctor.getName(),
            doctor.getUsername(),
            doctor.getPassword(),
            doctor.getEmail(),
            doctor.getPhoneNumber(),
            doctor.getSpecialization(),
            doctor.getLicenseNumber()));
    }
    
    /**
     * Load medicines from file
     * 
     * @return List of Medicine objects
     */
    public static List<Medicine> loadMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String description = parts[2].trim();
                    String manufacturer = parts[3].trim();
                    double price = Double.parseDouble(parts[4].trim());
                    int quantity = Integer.parseInt(parts[5].trim());
                    String category = parts[6].trim();
                    boolean requiresPrescription = Boolean.parseBoolean(parts[7].trim());
                    
                    Medicine medicine = new Medicine(id, name, description, manufacturer, price, quantity, category, requiresPrescription);
                    medicines.add(medicine);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading medicines: " + e.getMessage());
        }
        
        return medicines;
    }
    
    /**
     * Save medicines to file
     * 
     * @param medicines List of Medicine objects to save
     */
    public static void saveMedicines(List<Medicine> medicines) {
        saveToFile(MEDICINES_FILE, medicines, medicine -> String.format("%d|%s|%s|%s|%.2f|%d|%s|%b", 
            medicine.getId(), 
            medicine.getName(),
            medicine.getDescription(),
            medicine.getManufacturer(),
            medicine.getPrice(),
            medicine.getQuantity(),
            medicine.getCategory(),
            medicine.isRequiresPrescription()));
    }
    
    /**
     * Load orders from file
     * 
     * @param allMedicines List of all medicines (for reference)
     * @return List of Order objects
     */
    public static List<Order> loadOrders(List<Medicine> allMedicines) {
        List<Order> orders = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    int id = Integer.parseInt(parts[0].trim());
                    int patientId = Integer.parseInt(parts[1].trim());
                    String orderDateStr = parts[2].trim();
                    double totalAmount = Double.parseDouble(parts[3].trim());
                    String status = parts[4].trim();
                    
                    // Create order with specific date
                    Order order = new Order(id, patientId, orderDateStr);
                    order.setStatus(status);
                    order.setTotalAmount(totalAmount);
                    
                    // Load payment info if available
                    if (parts.length >= 7) {
                        String paymentMethod = parts[5].trim();
                        String paymentStatus = parts[6].trim();
                        order.setPaymentMethod(paymentMethod);
                        order.setPaymentStatus(paymentStatus);
                    }
                    
                    // Load medicines if the parts array has more elements
                    if (parts.length > 7) {
                        String[] medicineData = parts[7].split(";");
                        for (String medicineEntry : medicineData) {
                            String[] medicineInfo = medicineEntry.split(":");
                            if (medicineInfo.length == 2) {
                                int medicineId = Integer.parseInt(medicineInfo[0].trim());
                                int quantity = Integer.parseInt(medicineInfo[1].trim());
                                
                                // Find medicine by ID
                                Medicine medicine = null;
                                for (Medicine m : allMedicines) {
                                    if (m.getId() == medicineId) {
                                        medicine = m;
                                        break;
                                    }
                                }
                                
                                if (medicine != null) {
                                    order.addMedicine(medicine, quantity);
                                }
                            }
                        }
                    } else if (parts.length > 5 && parts.length < 7) {
                        // Backward compatibility for older file format without payment info
                        String[] medicineData = parts[5].split(";");
                        for (String medicineEntry : medicineData) {
                            String[] medicineInfo = medicineEntry.split(":");
                            if (medicineInfo.length == 2) {
                                int medicineId = Integer.parseInt(medicineInfo[0].trim());
                                int quantity = Integer.parseInt(medicineInfo[1].trim());
                                
                                // Find medicine by ID
                                Medicine medicine = null;
                                for (Medicine m : allMedicines) {
                                    if (m.getId() == medicineId) {
                                        medicine = m;
                                        break;
                                    }
                                }
                                
                                if (medicine != null) {
                                    order.addMedicine(medicine, quantity);
                                }
                            }
                        }
                    }
                    
                    orders.add(order);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading orders: " + e.getMessage());
        }
        
        return orders;
    }
    
    /**
     * Save orders to file
     * 
     * @param orders List of Order objects to save
     */
    public static void saveOrders(List<Order> orders) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDERS_FILE))) {
            for (Order order : orders) {
                // Format: OrderID|PatientID|OrderDate|TotalAmount|Status|PaymentMethod|PaymentStatus|MedicineID1:Quantity1;MedicineID2:Quantity2...
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("%d|%d|%s|%.2f|%s|%s|%s|", 
                    order.getId(), 
                    order.getPatientId(),
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getOrderDate()),
                    order.getTotalAmount(),
                    order.getStatus(),
                    order.getPaymentMethod(),
                    order.getPaymentStatus()));
                
                // Add medicines and quantities
                List<Medicine> medicines = order.getMedicinesList();
                List<Integer> quantities = order.getQuantities();
                
                for (int i = 0; i < medicines.size(); i++) {
                    if (i > 0) {
                        sb.append(";");
                    }
                    sb.append(medicines.get(i).getId()).append(":").append(quantities.get(i));
                }
                
                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving orders: " + e.getMessage());
        }
    }
    
    /**
     * Deprecated: Use loadPatients() instead
     * Legacy method for backward compatibility
     * 
     * @return List of Patient objects
     */
    public static List<Patient> loadCustomers() {
        System.out.println("Warning: loadCustomers() is deprecated. Use loadPatients() instead.");
        return loadPatients();
    }
    
    /**
     * Deprecated: Use savePatients() instead
     * Legacy method for backward compatibility
     * 
     * @param patients List of Patient objects to save
     */
    public static void saveCustomers(List<Patient> patients) {
        System.out.println("Warning: saveCustomers() is deprecated. Use savePatients() instead.");
        savePatients(patients);
    }
    
    /**
     * Load pharmacists from file
     * 
     * @return List of Pharmacist objects
     */
    public static List<Pharmacist> loadPharmacists() {
        List<Pharmacist> pharmacists = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(PHARMACISTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 9) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String username = parts[2].trim();
                    String password = parts[3].trim();
                    String email = parts[4].trim();
                    String phone = parts[5].trim();
                    String licenseNumber = parts[6].trim();
                    String qualification = parts[7].trim();
                    int pharmacyId = Integer.parseInt(parts[8].trim());
                    
                    Pharmacist pharmacist = new Pharmacist(id, name, username, password, email, phone, 
                                                         licenseNumber, qualification, pharmacyId);
                    pharmacists.add(pharmacist);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading pharmacists: " + e.getMessage());
        }
        
        return pharmacists;
    }
    
    /**
     * Save pharmacists to file
     * 
     * @param pharmacists List of Pharmacist objects to save
     */
    public static void savePharmacists(List<Pharmacist> pharmacists) {
        saveToFile(PHARMACISTS_FILE, pharmacists, pharmacist -> String.format("%d|%s|%s|%s|%s|%s|%s|%s|%d", 
            pharmacist.getId(), 
            pharmacist.getName(),
            pharmacist.getUsername(),
            pharmacist.getPassword(),
            pharmacist.getEmail(),
            pharmacist.getPhoneNumber(),
            pharmacist.getLicenseNumber(),
            pharmacist.getQualification(),
            pharmacist.getPharmacyId()));
    }
    
    /**
     * Load pharmacies from file
     * 
     * @return List of Pharmacy objects
     */
    public static List<Pharmacy> loadPharmacies() {
        List<Pharmacy> pharmacies = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(PHARMACIES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String address = parts[2].trim();
                    String phone = parts[3].trim();
                    String email = parts[4].trim();
                    
                    Pharmacy pharmacy = new Pharmacy(id, name, address, phone, email);
                    pharmacies.add(pharmacy);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading pharmacies: " + e.getMessage());
        }
        
        return pharmacies;
    }
    
    /**
     * Save pharmacies to file
     * 
     * @param pharmacies List of Pharmacy objects to save
     */
    public static void savePharmacies(List<Pharmacy> pharmacies) {
        saveToFile(PHARMACIES_FILE, pharmacies, pharmacy -> String.format("%d|%s|%s|%s|%s", 
            pharmacy.getId(), 
            pharmacy.getName(),
            pharmacy.getAddress(),
            pharmacy.getPhoneNumber(),
            pharmacy.getEmail()));
    }
    
    /**
     * Load prescriptions from file
     * 
     * @param allMedicines List of all medicines (for reference)
     * @return List of Prescription objects
     */
    public static List<Prescription> loadPrescriptions(List<Medicine> allMedicines) {
        List<Prescription> prescriptions = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(PRESCRIPTIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 7) {
                    int id = Integer.parseInt(parts[0].trim());
                    int patientId = Integer.parseInt(parts[1].trim());
                    int doctorId = Integer.parseInt(parts[2].trim());
                    String issueDateStr = parts[3].trim();
                    String expiryDateStr = parts[4].trim();
                    String status = parts[5].trim();
                    String instructions = parts[6].trim();
                    
                    // Parse dates
                    LocalDate issueDate = LocalDate.parse(issueDateStr, DATE_ONLY_FORMATTER);
                    LocalDate expiryDate = LocalDate.parse(expiryDateStr, DATE_ONLY_FORMATTER);
                    
                    // Create prescription
                    Prescription prescription = new Prescription(id, patientId, doctorId, issueDate, expiryDate, instructions);
                    prescription.setStatus(status);
                    
                    // Load medicines if the parts array has more elements
                    if (parts.length > 7) {
                        String[] medicineData = parts[7].split(";");
                        for (String medicineEntry : medicineData) {
                            String[] medicineInfo = medicineEntry.split(":");
                            if (medicineInfo.length == 2) {
                                int medicineId = Integer.parseInt(medicineInfo[0].trim());
                                int quantity = Integer.parseInt(medicineInfo[1].trim());
                                
                                // Find medicine by ID
                                Medicine medicine = null;
                                for (Medicine m : allMedicines) {
                                    if (m.getId() == medicineId) {
                                        medicine = m;
                                        break;
                                    }
                                }
                                
                                if (medicine != null) {
                                    prescription.addMedicine(medicine, quantity);
                                }
                            }
                        }
                    }
                    
                    prescriptions.add(prescription);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading prescriptions: " + e.getMessage());
        }
        
        return prescriptions;
    }
    
    /**
     * Save prescriptions to file
     * 
     * @param prescriptions List of Prescription objects to save
     */
    public static void savePrescriptions(List<Prescription> prescriptions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRESCRIPTIONS_FILE))) {
            for (Prescription prescription : prescriptions) {
                // Format: PrescriptionID|PatientID|DoctorID|IssueDate|ExpiryDate|Status|Instructions|MedicineID1:Quantity1;MedicineID2:Quantity2...
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("%d|%d|%d|%s|%s|%s|%s|", 
                    prescription.getId(), 
                    prescription.getPatientId(),
                    prescription.getDoctorId(),
                    prescription.getIssueDate().format(DATE_ONLY_FORMATTER),
                    prescription.getExpiryDate().format(DATE_ONLY_FORMATTER),
                    prescription.getStatus(),
                    prescription.getInstructions()));
                
                // Add medicines and quantities
                boolean firstMedicine = true;
                for (Map.Entry<Medicine, Integer> entry : prescription.getMedicines().entrySet()) {
                    Medicine medicine = entry.getKey();
                    int quantity = entry.getValue();
                    
                    if (!firstMedicine) {
                        sb.append(";");
                    }
                    sb.append(medicine.getId()).append(":").append(quantity);
                    firstMedicine = false;
                }
                
                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving prescriptions: " + e.getMessage());
        }
    }
    
    /**
     * Note: This method is deprecated and may be removed in the future.
     * The system now uses text-based data persistence methods instead of serialization.
     * 
     * @param <T> The type of objects (unused in the deprecated implementation)
     * @param objects The list of objects to save (unused in the deprecated implementation)
     * @param filePath The file path to save to (unused in the deprecated implementation)
     */
    public static <T> void saveToFile(List<T> objects, String filePath) {
        System.err.println("Warning: saveToFile using serialization is deprecated and will be removed.");
        System.err.println("Please use the appropriate text-based save method instead.");
    }
    
    /**
     * Note: This method is deprecated and may be removed in the future.
     * The system now uses text-based data persistence methods instead of serialization.
     * 
     * @param filePath The file path to load from (unused in the deprecated implementation)
     * @return An empty ArrayList since the method is deprecated
     */
    public static Object loadFromFile(String filePath) {
        System.err.println("Warning: loadFromFile using serialization is deprecated and will be removed.");
        System.err.println("Please use the appropriate text-based load method instead.");
        return new ArrayList<>();
    }
}