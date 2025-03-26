package tools.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 📊 DataMapper - Object-to-file mapping utility 📊
 * 
 * This utility class provides generic methods for mapping between
 * Java objects and text file representations. It implements a simplified
 * object-relational mapping (ORM) pattern for file-based persistence.
 * 
 * 🔑 OOP Concepts Demonstrated:
 * - Generic Programming: Type-safe operations on different data types
 * - Functional Programming: Lambda expressions for flexible mapping
 * - Adapter Pattern: Adapts objects to file storage format and back
 * 
 * 📚 Class Responsibilities:
 * - Convert objects to file-compatible string representations
 * - Parse string data from files back into objects
 * - Provide standardized delimiter handling
 * - Encode/decode special characters to prevent data corruption
 * 
 * 🌐 Role in System:
 * This utility simplifies the data persistence layer by providing
 * a standardized way to store and retrieve objects from text files.
 */
public class DataMapper {
    // Standard field delimiter for file storage
    public static final String FIELD_DELIMITER = "|";
    
    // Secondary delimiter for nested structures
    public static final String SECONDARY_DELIMITER = ";";
    
    // Tertiary delimiter for complex nested structures
    public static final String TERTIARY_DELIMITER = ":";
    
    /**
     * Serialize an object to a string representation
     * 
     * @param <T> Type of object
     * @param obj Object to serialize
     * @param mapper Function to map the object to a string array
     * @return String representation using standard delimiter
     */
    public static <T> String objectToString(T obj, Function<T, String[]> mapper) {
        if (obj == null) {
            return "";
        }
        
        String[] fields = mapper.apply(obj);
        if (fields == null || fields.length == 0) {
            return "";
        }
        
        // Join fields with delimiter, handling null values
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(FIELD_DELIMITER);
            }
            
            String field = fields[i];
            sb.append(field != null ? encodeField(field) : "");
        }
        
        return sb.toString();
    }
    
    /**
     * Deserialize a string into an object
     * 
     * @param <T> Type of object
     * @param str String representation
     * @param mapper Function to map a string array to an object
     * @return Deserialized object
     */
    public static <T> T stringToObject(String str, Function<String[], T> mapper) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        
        // Split by delimiter and decode each field
        String[] fields = str.split("\\" + FIELD_DELIMITER);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = decodeField(fields[i]);
        }
        
        return mapper.apply(fields);
    }
    
    /**
     * Serialize a list of objects to a list of strings
     * 
     * @param <T> Type of objects
     * @param objects List of objects
     * @param mapper Function to map each object to a string array
     * @return List of string representations
     */
    public static <T> List<String> objectsToStrings(List<T> objects, Function<T, String[]> mapper) {
        List<String> strings = new ArrayList<>();
        
        if (objects != null) {
            for (T obj : objects) {
                strings.add(objectToString(obj, mapper));
            }
        }
        
        return strings;
    }
    
    /**
     * Deserialize a list of strings into a list of objects
     * 
     * @param <T> Type of objects
     * @param strings List of string representations
     * @param mapper Function to map string arrays to objects
     * @return List of deserialized objects
     */
    public static <T> List<T> stringsToObjects(List<String> strings, Function<String[], T> mapper) {
        List<T> objects = new ArrayList<>();
        
        if (strings != null) {
            for (String str : strings) {
                if (str != null && !str.trim().isEmpty()) {
                    T obj = stringToObject(str, mapper);
                    if (obj != null) {
                        objects.add(obj);
                    }
                }
            }
        }
        
        return objects;
    }
    
    /**
     * Encode a field value to escape delimiters
     * 
     * @param field Field value
     * @return Encoded field value
     */
    public static String encodeField(String field) {
        if (field == null) {
            return "";
        }
        
        // Replace delimiter characters with escape sequences
        return field.replace(FIELD_DELIMITER, "\\|")
                   .replace(SECONDARY_DELIMITER, "\\;")
                   .replace(TERTIARY_DELIMITER, "\\:")
                   .replace("\n", "\\n");
    }
    
    /**
     * Decode a field value to restore escaped delimiters
     * 
     * @param field Encoded field value
     * @return Decoded field value
     */
    public static String decodeField(String field) {
        if (field == null) {
            return "";
        }
        
        // Restore escaped characters
        return field.replace("\\|", FIELD_DELIMITER)
                   .replace("\\;", SECONDARY_DELIMITER)
                   .replace("\\:", TERTIARY_DELIMITER)
                   .replace("\\n", "\n");
    }
    
    /**
     * Encode nested data structures (lists, maps, etc.)
     * 
     * @param items Array of items to encode
     * @param mapper Function to map each item to a string
     * @return Encoded string using secondary delimiter
     */
    public static <T> String encodeNestedStructure(T[] items, Function<T, String> mapper) {
        if (items == null || items.length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            if (i > 0) {
                sb.append(SECONDARY_DELIMITER);
            }
            
            T item = items[i];
            String itemStr = item != null ? mapper.apply(item) : "";
            sb.append(encodeField(itemStr));
        }
        
        return sb.toString();
    }
    
    /**
     * Encode a list as a nested structure
     * 
     * @param <T> Type of list items
     * @param items List of items
     * @param mapper Function to map each item to a string
     * @return Encoded string using secondary delimiter
     */
    public static <T> String encodeList(List<T> items, Function<T, String> mapper) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(SECONDARY_DELIMITER);
            }
            
            T item = items.get(i);
            String itemStr = item != null ? mapper.apply(item) : "";
            sb.append(encodeField(itemStr));
        }
        
        return sb.toString();
    }
    
    /**
     * Decode a nested structure into a list of objects
     * 
     * @param <T> Type of list items
     * @param str Encoded string
     * @param mapper Function to map each string to an object
     * @return List of decoded objects
     */
    public static <T> List<T> decodeList(String str, Function<String, T> mapper) {
        List<T> items = new ArrayList<>();
        
        if (str == null || str.trim().isEmpty()) {
            return items;
        }
        
        String[] parts = str.split("\\" + SECONDARY_DELIMITER);
        for (String part : parts) {
            if (part != null && !part.trim().isEmpty()) {
                T item = mapper.apply(decodeField(part));
                if (item != null) {
                    items.add(item);
                }
            }
        }
        
        return items;
    }
    
    /**
     * Example of using the DataMapper to serialize/deserialize an object
     */
    public static void exampleUsage() {
        // Example object mapper (Person -> String[])
        Function<Person, String[]> personToStringArray = (person) -> new String[] {
            String.valueOf(person.id),
            person.name,
            String.valueOf(person.age),
            person.email
        };
        
        // Example string array mapper (String[] -> Person)
        Function<String[], Person> stringArrayToPerson = (fields) -> {
            if (fields.length < 4) return null;
            
            try {
                int id = Integer.parseInt(fields[0]);
                String name = fields[1];
                int age = Integer.parseInt(fields[2]);
                String email = fields[3];
                
                return new Person(id, name, age, email);
            } catch (NumberFormatException e) {
                return null;
            }
        };
        
        // Create a sample person
        Person person = new Person(1, "Ahmed Mohamed", 28, "ahmed@example.com");
        
        // Serialize to string
        String serialized = DataMapper.objectToString(person, personToStringArray);
        System.out.println("Serialized: " + serialized);
        
        // Deserialize back to object
        Person deserialized = DataMapper.stringToObject(serialized, stringArrayToPerson);
        System.out.println("Deserialized: " + deserialized);
    }
    
    /**
     * Example Person class for demonstration
     */
    private static class Person {
        private int id;
        private String name;
        private int age;
        private String email;
        
        public Person(int id, String name, int age, String email) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.email = email;
        }
        
        @Override
        public String toString() {
            return "Person{id=" + id + ", name='" + name + "', age=" + age + ", email='" + email + "'}";
        }
    }
}