package tools.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 📁 FileUtil - Generic file operations for data persistence 📁
 * 
 * This utility class provides standard methods for file operations
 * throughout the pharmacy system. It simplifies common tasks like
 * checking if files exist, creating files/directories, reading lines,
 * and writing data, with proper exception handling.
 * 
 * 🔑 OOP Concepts Demonstrated:
 * - Utility Class Pattern: Stateless class with static methods
 * - Abstraction: Hides implementation details of file operations
 * - Reusability: Common file operations shared across the application
 * 
 * 📚 Class Responsibilities:
 * - Create files and directories if they don't exist
 * - Read data from files with proper resource management
 * - Write data to files with proper resource management
 * - Check file existence and accessibility
 * - Provide standardized exception handling for I/O operations
 * 
 * 🌐 Role in System:
 * This utility provides the foundation for the data persistence layer
 * of the EL-TA3BAN Pharmacy System. It ensures consistent and reliable
 * file operations that maximize data integrity and minimize code duplication.
 */
public class FileUtil {
    
    /**
     * Check if a file exists
     * 
     * @param filePath Path to the file
     * @return True if the file exists, false otherwise
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }
    
    /**
     * Create a directory if it doesn't exist
     * 
     * @param dirPath Path to the directory
     * @return True if directory exists or was created successfully, false otherwise
     */
    public static boolean createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            return dir.isDirectory();
        } else {
            return dir.mkdirs();
        }
    }
    
    /**
     * Create a file if it doesn't exist
     * 
     * @param filePath Path to the file
     * @return True if file exists or was created successfully, false otherwise
     */
    public static boolean createFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.isFile();
        } else {
            try {
                // Ensure parent directory exists
                file.getParentFile().mkdirs();
                return file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file: " + filePath);
                e.printStackTrace();
                return false;
            }
        }
    }
    
    /**
     * Read all lines from a file
     * 
     * @param filePath Path to the file
     * @return List of lines from the file, or empty list if file doesn't exist or error occurs
     */
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        
        if (!fileExists(filePath)) {
            return lines;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            e.printStackTrace();
        }
        
        return lines;
    }
    
    /**
     * Write lines to a file (overwrite existing content)
     * 
     * @param filePath Path to the file
     * @param lines List of lines to write
     * @return True if writing was successful, false otherwise
     */
    public static boolean writeLines(String filePath, List<String> lines) {
        if (!createFile(filePath)) {
            return false;
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filePath);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Append a line to a file
     * 
     * @param filePath Path to the file
     * @param line Line to append
     * @return True if appending was successful, false otherwise
     */
    public static boolean appendLine(String filePath, String line) {
        if (!createFile(filePath)) {
            return false;
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(line);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Error appending to file: " + filePath);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a file
     * 
     * @param filePath Path to the file
     * @return True if file was deleted or doesn't exist, false if deletion failed
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return true;
        }
        
        return file.delete();
    }
    
    /**
     * Rename a file
     * 
     * @param oldPath Old file path
     * @param newPath New file path
     * @return True if file was renamed successfully, false otherwise
     */
    public static boolean renameFile(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);
        
        if (!oldFile.exists()) {
            return false;
        }
        
        return oldFile.renameTo(newFile);
    }
    
    /**
     * Create a backup of a file with timestamp
     * 
     * @param filePath Path to the file
     * @return Path to the backup file if successful, null otherwise
     */
    public static String createBackup(String filePath) {
        if (!fileExists(filePath)) {
            return null;
        }
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        String backupPath = filePath + "." + timestamp + ".bak";
        
        List<String> lines = readLines(filePath);
        if (writeLines(backupPath, lines)) {
            return backupPath;
        } else {
            return null;
        }
    }
}