package services;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * BaseService - Abstract base class for service implementations
 * 
 * @param <T> The entity type this service operates on
 * @param <ID> The type of the entity's identifier
 */
public abstract class BaseService<T, ID> {

    /**
     * Get all entities managed by this service
     * 
     * @return List of all entities
     */
    protected abstract List<T> getEntities();

    /**
     * Extract the ID from an entity
     * 
     * @param entity The entity to extract ID from
     * @return The ID of the entity
     */
    protected abstract ID getEntityId(T entity);

    /**
     * Find an entity by its ID
     * 
     * @param id The ID to search for
     * @return Optional containing the entity if found, empty otherwise
     */
    public Optional<T> findById(ID id) {
        return getEntities().stream()
                .filter(entity -> getEntityId(entity).equals(id))
                .findFirst();
    }

    /**
     * Find entities matching a predicate
     * 
     * @param predicate The predicate to filter entities
     * @return List of entities matching the predicate
     */
    public List<T> findByPredicate(Predicate<T> predicate) {
        return getEntities().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Check if an entity with the given ID exists
     * 
     * @param id The ID to check
     * @return true if the entity exists, false otherwise
     */
    public boolean exists(ID id) {
        return findById(id).isPresent();
    }

    /**
     * Add a new entity to the collection
     * 
     * @param entity The entity to add
     * @return true if added successfully, false if entity with same ID already exists
     */
    public boolean add(T entity) {
        ID id = getEntityId(entity);
        if (exists(id)) {
            return false;
        }
        getEntities().add(entity);
        return true;
    }

    /**
     * Remove an entity by its ID
     * 
     * @param id The ID of the entity to remove
     * @return true if removed successfully, false if not found
     */
    public boolean removeById(ID id) {
        Optional<T> entity = findById(id);
        if (entity.isPresent()) {
            getEntities().remove(entity.get());
            return true;
        }
        return false;
    }

    /**
     * Count the total number of entities
     * 
     * @return The count of entities
     */
    public int count() {
        return getEntities().size();
    }
}