package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * FileUtils - Utility methods for file operations
 */
public class FileUtils {
    
    /**
     * Interface for creating entities from string data
     * 
     * @param <T> Type of entity to create
     */
    @FunctionalInterface
    public interface EntityCreator<T> {
        T create(String line);
    }
    
    /**
     * Interface for converting entities to string format
     * 
     * @param <T> Type of entity to format
     */
    @FunctionalInterface
    public interface EntityFormatter<T> {
        String format(T entity);
    }
    
    /**
     * Read data from a file and convert to entity objects
     * 
     * @param <T> Type of entity to create
     * @param filePath Path to the file
     * @param creator Function to convert each line to an entity
     * @return List of entities
     */
    public static <T> List<T> readFromFile(String filePath, EntityCreator<T> creator) {
        List<T> result = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                T entity = creator.create(line);
                if (entity != null) {
                    result.add(entity);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from file " + filePath + ": " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Write entities to a file
     * 
     * @param <T> Type of entity to write
     * @param filePath Path to the file
     * @param entities List of entities to write
     * @param formatter Function to convert each entity to a string
     */
    public static <T> void writeToFile(String filePath, List<T> entities, EntityFormatter<T> formatter) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (T entity : entities) {
                writer.write(formatter.format(entity));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
    
    /**
     * Create a directory if it doesn't exist
     * 
     * @param dirPath Path of the directory to create
     * @return true if directory exists or was created successfully, false otherwise
     */
    public static boolean ensureDirectoryExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return dir.isDirectory();
    }
    
    /**
     * Create a file if it doesn't exist
     * 
     * @param filePath Path of the file to create
     * @return true if file exists or was created successfully, false otherwise
     */
    public static boolean ensureFileExists(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file " + filePath + ": " + e.getMessage());
                return false;
            }
        }
        return file.isFile();
    }
    
    /**
     * Check if a file exists
     * 
     * @param filePath Path of the file to check
     * @return true if file exists, false otherwise
     */
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }
    
    /**
     * Get the last modification timestamp of a file
     * 
     * @param filePath Path of the file
     * @return The last modification timestamp, or 0 if file doesn't exist
     */
    public static long getLastModified(String filePath) {
        File file = new File(filePath);
        return file.exists() ? file.lastModified() : 0;
    }
    
    /**
     * Delete a file
     * 
     * @param filePath Path of the file to delete
     * @return true if file was deleted successfully, false otherwise
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }
    
    /**
     * Append data to a file
     * 
     * @param filePath Path of the file
     * @param data Data to append
     * @return true if data was appended successfully, false otherwise
     */
    public static boolean appendToFile(String filePath, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(data);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Error appending to file " + filePath + ": " + e.getMessage());
            return false;
        }
    }
}