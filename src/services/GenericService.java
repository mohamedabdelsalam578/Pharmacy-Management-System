package services;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * GenericService - Generic interface for service operations
 * 
 * @param <T> The entity type this service operates on
 * @param <ID> The type of the entity's identifier
 */
public interface GenericService<T, ID> {
    
    /**
     * Get all entities
     * 
     * @return List of all entities
     */
    List<T> getAll();
    
    /**
     * Find an entity by its ID
     * 
     * @param id The ID to search for
     * @return Optional containing the entity if found, empty otherwise
     */
    Optional<T> findById(ID id);
    
    /**
     * Find entities matching a predicate
     * 
     * @param predicate The predicate to filter entities
     * @return List of entities matching the predicate
     */
    List<T> findByPredicate(Predicate<T> predicate);
    
    /**
     * Save an entity (create or update)
     * 
     * @param entity The entity to save
     * @return The saved entity
     */
    T save(T entity);
    
    /**
     * Delete an entity by its ID
     * 
     * @param id The ID of the entity to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean deleteById(ID id);
    
    /**
     * Check if an entity with the given ID exists
     * 
     * @param id The ID to check
     * @return true if the entity exists, false otherwise
     */
    boolean exists(ID id);
    
    /**
     * Count the total number of entities
     * 
     * @return The count of entities
     */
    int count();
    
    /**
     * Persist changes to permanent storage
     */
    void saveChanges();
}