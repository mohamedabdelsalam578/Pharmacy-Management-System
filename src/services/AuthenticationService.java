package services;

import models.Admin;
import models.Doctor;
import models.Patient;
import models.Pharmacist;
import models.User;
import utils.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 🔐 AuthenticationService - Security for user authentication 🔐
 *
 * Implements security with hashing, brute force protection, and input validation.
 *
 * 🔑 OOP Concepts:
 * - Encapsulation: Private security methods
 * - Polymorphism: Unified auth for all users
 * - Single Responsibility: Auth-focused
 *
 * 📚 Responsibilities:
 * - Authenticate users, prevent attacks
 * - Hash/verify passwords, validate inputs
 * - Auto-upgrade legacy passwords
 *
 * 🌐 System Role:
 * Security gateway for EL-TA3BAN Pharmacy System
 */
public class AuthenticationService {
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    
    // Maps to track login attempts and lockouts
    private Map<String, Integer> loginAttempts = new HashMap<>();
    private Map<String, Long> lockoutTimes = new HashMap<>();
    
    private List<Admin> admins;
    private List<Patient> patients;
    private List<Doctor> doctors;
    private List<Pharmacist> pharmacists;
    
    /**
     * Constructor for AuthenticationService
     * 
     * @param admins Admins list
     * @param patients Patients list
     * @param doctors Doctors list
     * @param pharmacists Pharmacists list
     */
    public AuthenticationService(List<Admin> admins, List<Patient> patients, 
                               List<Doctor> doctors, List<Pharmacist> pharmacists) {
        this.admins = admins;
        this.patients = patients;
        this.doctors = doctors;
        this.pharmacists = pharmacists;
    }
    
    /**
     * Generic authentication method
     * 
     * @param <T> User type
     * @param username Username
     * @param password Password
     * @param userFinder Function to find user
     * @return User if authenticated, null otherwise
     */
    private <T extends User> T authenticate(String username, String password, Function<String, T> userFinder) {
        if (!validateLoginInput(username, password)) {
            return null;
        }
        
        if (isAccountLocked(username)) {
            System.out.println("Account is temporarily locked due to multiple failed login attempts.");
            System.out.println("Please try again after " + LOCKOUT_DURATION_MINUTES + " minutes.");
            return null;
        }
        
        T user = userFinder.apply(username);
        
        if (user == null) {
            recordFailedLoginAttempt(username);
            return null;
        }
        
        boolean authenticated = false;
        
        // Check if the password is already hashed
        if (user.getPassword().startsWith("$SHA$")) {
            // Verify hashed password
            authenticated = verifyPassword(password, user.getPassword());
        } else {
            // Legacy authentication (plaintext password)
            authenticated = user.getPassword().equals(password);
            
            // Upgrade to hashed password if authentication successful
            if (authenticated) {
                user.setPassword(hashPassword(password));
            }
        }
        
        if (authenticated) {
            resetLoginAttempts(username);
            return user;
        } else {
            recordFailedLoginAttempt(username);
            return null;
        }
    }
    
    /**
     * Authenticate admin
     * 
     * @param username Admin username
     * @param password Admin password
     * @return Admin if authenticated, null otherwise
     */
    public Admin authenticateAdmin(String username, String password) {
        return authenticate(username, password, 
            un -> admins.stream()
                .filter(a -> a.getUsername().equals(un))
                .findFirst()
                .orElse(null));
    }
    
    /**
     * Authenticate patient
     * 
     * @param username Patient username
     * @param password Patient password
     * @return Patient if authenticated, null otherwise
     */
    public Patient authenticatePatient(String username, String password) {
        return authenticate(username, password, 
            un -> patients.stream()
                .filter(p -> p.getUsername().equals(un))
                .findFirst()
                .orElse(null));
    }
    
    /**
     * Authenticate doctor
     * 
     * @param username Doctor username
     * @param password Doctor password
     * @return Doctor if authenticated, null otherwise
     */
    public Doctor authenticateDoctor(String username, String password) {
        return authenticate(username, password, 
            un -> doctors.stream()
                .filter(d -> d.getUsername().equals(un))
                .findFirst()
                .orElse(null));
    }
    
    /**
     * Authenticate pharmacist
     * 
     * @param username Pharmacist username
     * @param password Pharmacist password
     * @return Pharmacist if authenticated, null otherwise
     */
    public Pharmacist authenticatePharmacist(String username, String password) {
        return authenticate(username, password, 
            un -> pharmacists.stream()
                .filter(p -> p.getUsername().equals(un))
                .findFirst()
                .orElse(null));
    }
    
    /**
     * hashPassword - Creates secure password hash
     * 
     * Uses salt and SHA-256 with Base64 encoding.
     * 
     * @param plainPassword Plain password
     * @return Formatted hash with salt
     */
    public String hashPassword(String plainPassword) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Create SHA-256 hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            
            // Convert to Base64 for storage
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);
            
            // Format: $SHA$salt$hash
            return "$SHA$" + saltBase64 + "$" + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            // Fallback to plaintext if hashing fails
            System.err.println("Warning: Password hashing failed. Using plaintext password.");
            return plainPassword;
        }
    }
    
    /**
     * verifyPassword - Validates password
     * 
     * Extracts salt and uses secure comparison.
     * 
     * @param plainPassword Password to check
     * @param storedHash Stored hash
     * @return True if matching, false otherwise
     */
    private boolean verifyPassword(String plainPassword, String storedHash) {
        try {
            if (!storedHash.startsWith("$SHA$")) {
                // Not a hashed password, can't verify
                return false;
            }
            
            // Split the hash into components
            String[] parts = storedHash.split("\\$");
            if (parts.length != 4) {
                return false;
            }
            
            // Extract salt and hash
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] storedHashBytes = Base64.getDecoder().decode(parts[3]);
            
            // Hash the plaintext password with the same salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] computedHash = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            
            // Compare hashes
            return MessageDigest.isEqual(storedHashBytes, computedHash);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * validateLoginInput - Prevents attacks
     * 
     * Checks for empty inputs and validates format.
     * 
     * @param username Username to validate
     * @param password Password to validate
     * @return True if valid, false otherwise
     */
    private boolean validateLoginInput(String username, String password) {
        // Use StringUtils to check for empty strings
        if (StringUtils.isEmpty(username)) {
            System.out.println("Username cannot be empty.");
            return false;
        }
        
        if (StringUtils.isEmpty(password)) {
            System.out.println("Password cannot be empty.");
            return false;
        }
        
        if (username.length() < 3) {
            System.out.println("Username must be at least 3 characters long.");
            return false;
        }
        
        // Check for potentially dangerous characters
        if (username.contains(";") || username.contains("'") || username.contains("\"") || 
            username.contains("`") || username.contains("--")) {
            System.out.println("Username contains invalid characters.");
            return false;
        }
        
        return true;
    }
    
    /**
     * recordFailedLoginAttempt - Protects against brute force attacks
     * 
     * Tracks failed attempts and implements lockouts.
     * Provides user feedback on remaining attempts.
     * 
     * @param username Username that failed login
     */
    private void recordFailedLoginAttempt(String username) {
        int attempts = loginAttempts.getOrDefault(username, 0) + 1;
        loginAttempts.put(username, attempts);
        
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            // Lock the account
            lockoutTimes.put(username, System.currentTimeMillis());
            System.out.println("Too many failed login attempts. Account locked for " + 
                             LOCKOUT_DURATION_MINUTES + " minutes.");
        } else {
            int remainingAttempts = MAX_LOGIN_ATTEMPTS - attempts;
            System.out.println("Login failed. " + remainingAttempts + 
                             " attempt" + (remainingAttempts > 1 ? "s" : "") + " remaining.");
        }
    }
    
    /**
     * resetLoginAttempts - Clears security constraints
     * 
     * Resets lockout tracking after successful login.
     * 
     * @param username Username to reset attempts for
     */
    private void resetLoginAttempts(String username) {
        loginAttempts.remove(username);
        lockoutTimes.remove(username);
    }
    
    /**
     * isAccountLocked - Enforces account lockout policy
     * 
     * Implements temporary, time-based lockouts with auto-expiry.
     * 
     * @param username Username to check
     * @return True if locked, false if accessible
     */
    private boolean isAccountLocked(String username) {
        Long lockTime = lockoutTimes.get(username);
        if (lockTime == null) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        long lockoutDurationMillis = LOCKOUT_DURATION_MINUTES * 60 * 1000;
        
        if (currentTime - lockTime > lockoutDurationMillis) {
            // Lockout period has passed
            lockoutTimes.remove(username);
            loginAttempts.remove(username);
            return false;
        }
        
        return true;
    }
    
    /**
     * setHashedPassword - Secures user credentials
     * 
     * Securely stores passwords for new or updated accounts.
     * 
     * @param user User to secure
     * @param plainPassword Plain password to hash
     */
    public void setHashedPassword(User user, String plainPassword) {
        // We still use the local hashPassword method since it's a public interface
        // that might be used elsewhere in the codebase
        String hashedPassword = hashPassword(plainPassword);
        user.setPassword(hashedPassword);
    }
}