package auto.farming;

import auto.farming.config.ConfigLoader;
import auto.farming.config.FarmingConfig;
import auto.farming.model.FarmField;
import auto.farming.model.StorageConfig;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Singleton manager for all farming operations and field data.
 * Provides centralized access to fields, handles persistence, and notifies listeners of changes.
 * 
 * <p>Thread-safe for concurrent access from UI and bot threads.</p>
 */
public class FarmingManager {
    private static final FarmingManager INSTANCE = new FarmingManager();
    
    private final Map<UUID, FarmField> fields;
    private final Map<UUID, StorageConfig> storageConfigs;
    private final List<FieldChangeListener> listeners;
    private boolean initialized;
    
    /**
     * Private constructor for singleton pattern.
     */
    private FarmingManager() {
        this.fields = new ConcurrentHashMap<>();
        this.storageConfigs = new ConcurrentHashMap<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.initialized = false;
    }
    
    /**
     * Gets the singleton instance of the farming manager.
     * @return The singleton instance
     */
    public static FarmingManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Initializes the farming manager by loading configuration from disk.
     * Should be called once during client startup.
     */
    public synchronized void initialize() {
        if (initialized) {
            System.err.println("FarmingManager already initialized");
            return;
        }
        
        try {
            FarmingConfig config = ConfigLoader.load();
            
            // Load fields
            for (FarmField field : config.getFields()) {
                fields.put(field.getId(), field);
            }
            
            // Load storage configs
            storageConfigs.putAll(config.getStorage());
            
            System.out.println("FarmingManager initialized with " + fields.size() + " fields");
            initialized = true;
        } catch (IOException e) {
            System.err.println("Error loading farming config, starting with empty state: " + e.getMessage());
            initialized = true;
        }
    }
    
    /**
     * Shuts down the farming manager and saves configuration to disk.
     * Should be called during client shutdown.
     */
    public synchronized void shutdown() {
        if (!initialized) {
            return;
        }
        
        try {
            saveConfig();
            System.out.println("FarmingManager shutdown, config saved");
        } catch (IOException e) {
            System.err.println("Error saving farming config on shutdown: " + e.getMessage());
        }
        
        fields.clear();
        storageConfigs.clear();
        listeners.clear();
        initialized = false;
    }
    
    /**
     * Adds a new field to the manager and persists the change.
     * @param field Field to add
     * @throws IllegalArgumentException if field with same ID already exists
     */
    public void addField(FarmField field) {
        Objects.requireNonNull(field, "Field cannot be null");
        
        if (fields.containsKey(field.getId())) {
            throw new IllegalArgumentException("Field with ID " + field.getId() + " already exists");
        }
        
        fields.put(field.getId(), field);
        saveConfigQuietly();
        notifyFieldAdded(field);
    }
    
    /**
     * Updates an existing field and persists the change.
     * @param field Field to update (identified by ID)
     * @throws IllegalArgumentException if field doesn't exist
     */
    public void updateField(FarmField field) {
        Objects.requireNonNull(field, "Field cannot be null");
        
        if (!fields.containsKey(field.getId())) {
            throw new IllegalArgumentException("Field with ID " + field.getId() + " does not exist");
        }
        
        fields.put(field.getId(), field);
        saveConfigQuietly();
        notifyFieldUpdated(field);
    }
    
    /**
     * Removes a field from the manager and persists the change.
     * @param id ID of the field to remove
     * @return The removed field, or null if not found
     */
    public FarmField removeField(UUID id) {
        Objects.requireNonNull(id, "Field ID cannot be null");
        
        FarmField removed = fields.remove(id);
        if (removed != null) {
            // Also remove associated storage config
            storageConfigs.remove(id);
            saveConfigQuietly();
            notifyFieldRemoved(removed);
        }
        
        return removed;
    }
    
    /**
     * Gets a field by ID.
     * @param id Field ID
     * @return Optional containing the field if found, empty otherwise
     */
    public Optional<FarmField> getField(UUID id) {
        return Optional.ofNullable(fields.get(id));
    }
    
    /**
     * Gets all fields.
     * @return Unmodifiable collection of all fields
     */
    public Collection<FarmField> getAllFields() {
        return Collections.unmodifiableCollection(fields.values());
    }
    
    /**
     * Gets all enabled fields.
     * @return List of enabled fields
     */
    public List<FarmField> getEnabledFields() {
        List<FarmField> enabled = new ArrayList<>();
        for (FarmField field : fields.values()) {
            if (field.isEnabled()) {
                enabled.add(field);
            }
        }
        return enabled;
    }
    
    /**
     * Gets storage configuration for a field.
     * @param fieldId Field ID
     * @return Optional containing storage config if found, empty otherwise
     */
    public Optional<StorageConfig> getStorageConfig(UUID fieldId) {
        return Optional.ofNullable(storageConfigs.get(fieldId));
    }
    
    /**
     * Sets storage configuration for a field.
     * @param fieldId Field ID
     * @param storageConfig Storage configuration
     */
    public void setStorageConfig(UUID fieldId, StorageConfig storageConfig) {
        Objects.requireNonNull(fieldId, "Field ID cannot be null");
        Objects.requireNonNull(storageConfig, "Storage config cannot be null");
        
        if (!fields.containsKey(fieldId)) {
            throw new IllegalArgumentException("Field with ID " + fieldId + " does not exist");
        }
        
        storageConfigs.put(fieldId, storageConfig);
        saveConfigQuietly();
    }
    
    /**
     * Removes storage configuration for a field.
     * @param fieldId Field ID
     * @return The removed storage config, or null if not found
     */
    public StorageConfig removeStorageConfig(UUID fieldId) {
        StorageConfig removed = storageConfigs.remove(fieldId);
        if (removed != null) {
            saveConfigQuietly();
        }
        return removed;
    }
    
    /**
     * Adds a field change listener.
     * @param listener Listener to add
     */
    public void addFieldChangeListener(FieldChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes a field change listener.
     * @param listener Listener to remove
     */
    public void removeFieldChangeListener(FieldChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Saves configuration to disk.
     * @throws IOException if save fails
     */
    public void saveConfig() throws IOException {
        FarmingConfig config = new FarmingConfig();
        config.setFields(new ArrayList<>(fields.values()));
        config.setStorage(new HashMap<>(storageConfigs));
        ConfigLoader.save(config);
    }
    
    /**
     * Saves configuration quietly (logs errors instead of throwing).
     */
    private void saveConfigQuietly() {
        try {
            saveConfig();
        } catch (IOException e) {
            System.err.println("Error saving farming config: " + e.getMessage());
        }
    }
    
    /**
     * Checks if the manager is initialized.
     * @return true if initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Gets the number of fields currently managed.
     * @return Number of fields
     */
    public int getFieldCount() {
        return fields.size();
    }
    
    /**
     * Clears all fields and storage configs.
     * WARNING: This will delete all data!
     */
    public void clearAll() {
        fields.clear();
        storageConfigs.clear();
        saveConfigQuietly();
    }
    
    // Notification methods
    
    private void notifyFieldAdded(FarmField field) {
        for (FieldChangeListener listener : listeners) {
            try {
                listener.onFieldAdded(field);
            } catch (Exception e) {
                System.err.println("Error in field change listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyFieldUpdated(FarmField field) {
        for (FieldChangeListener listener : listeners) {
            try {
                listener.onFieldUpdated(field);
            } catch (Exception e) {
                System.err.println("Error in field change listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyFieldRemoved(FarmField field) {
        for (FieldChangeListener listener : listeners) {
            try {
                listener.onFieldRemoved(field);
            } catch (Exception e) {
                System.err.println("Error in field change listener: " + e.getMessage());
            }
        }
    }
}
