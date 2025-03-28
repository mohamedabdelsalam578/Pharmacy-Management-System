package models;

import java.io.Serializable;

/**
 * Base class for all users in the system
 * Provides common properties and methods for all user types
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String username;
    private String password;
    private String role;
    private boolean active;
    private int loginAttempts;
    private boolean locked;
    
    /**
     * Constructor for creating a new user
     * 
     * @param id The unique identifier for this user
     * @param username The username for this user
     * @param password The password for this user
     * @param role The role of this user (ADMIN, PATIENT, DOCTOR, PHARMACIST)
     */
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.active = true;
        this.loginAttempts = 0;
        this.locked = false;
    }
    
    /**
     * Get the ID of this user
     * 
     * @return The ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Set the ID of this user
     * 
     * @param id The ID
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Get the username of this user
     * 
     * @return The username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Set the username of this user
     * 
     * @param username The username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Get the password of this user
     * 
     * @return The password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Set the password of this user
     * 
     * @param password The password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Get the role of this user
     * 
     * @return The role
     */
    public String getRole() {
        return role;
    }
    
    /**
     * Set the role of this user
     * 
     * @param role The role
     */
    public void setRole(String role) {
        this.role = role;
    }
    
    /**
     * Check if this user is active
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Set the active state of this user
     * 
     * @param active The active state
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Get the number of login attempts for this user
     * 
     * @return The number of login attempts
     */
    public int getLoginAttempts() {
        return loginAttempts;
    }
    
    /**
     * Set the number of login attempts for this user
     * 
     * @param loginAttempts The number of login attempts
     */
    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }
    
    /**
     * Increment the number of login attempts for this user
     */
    public void incrementLoginAttempts() {
        this.loginAttempts++;
    }
    
    /**
     * Reset the number of login attempts for this user
     */
    public void resetLoginAttempts() {
        this.loginAttempts = 0;
    }
    
    /**
     * Check if this user is locked
     * 
     * @return true if locked, false otherwise
     */
    public boolean isLocked() {
        return locked;
    }
    
    /**
     * Set the locked state of this user
     * 
     * @param locked The locked state
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    /**
     * Get a formatted string representation of this user
     * 
     * @return The formatted string
     */
    @Override
    public String toString() {
        return String.format("User [ID: %d, Username: %s, Role: %s, Active: %s, Locked: %s]", 
                id, username, role, active, locked);
    }
}