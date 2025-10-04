package auto.farming.config;

import auto.farming.model.*;
import java.util.*;

/**
 * Root configuration class containing all farming data.
 * Represents the top-level structure of farming.json5.
 */
public class FarmingConfig {
    private int version;
    private List<FarmField> fields;
    private Map<UUID, StorageConfig> storage; // Keyed by field ID
    private List<CropConfig> crops;
    
    /**
     * Creates a new empty farming configuration with current version.
     */
    public FarmingConfig() {
        this.version = ConfigVersion.CURRENT_VERSION;
        this.fields = new ArrayList<>();
        this.storage = new HashMap<>();
        this.crops = new ArrayList<>();
    }
    
    /**
     * Gets the configuration version.
     * @return Version number
     */
    public int getVersion() {
        return version;
    }
    
    /**
     * Sets the configuration version.
     * @param version Version number
     */
    public void setVersion(int version) {
        this.version = version;
    }
    
    /**
     * Gets all farm fields.
     * @return List of fields
     */
    public List<FarmField> getFields() {
        return fields;
    }
    
    /**
     * Sets all farm fields.
     * @param fields List of fields
     */
    public void setFields(List<FarmField> fields) {
        this.fields = fields != null ? fields : new ArrayList<>();
    }
    
    /**
     * Gets storage configurations mapped by field ID.
     * @return Map of field ID to storage config
     */
    public Map<UUID, StorageConfig> getStorage() {
        return storage;
    }
    
    /**
     * Sets storage configurations.
     * @param storage Map of field ID to storage config
     */
    public void setStorage(Map<UUID, StorageConfig> storage) {
        this.storage = storage != null ? storage : new HashMap<>();
    }
    
    /**
     * Gets all crop configurations.
     * @return List of crop configs
     */
    public List<CropConfig> getCrops() {
        return crops;
    }
    
    /**
     * Sets crop configurations.
     * @param crops List of crop configs
     */
    public void setCrops(List<CropConfig> crops) {
        this.crops = crops != null ? crops : new ArrayList<>();
    }
    
    /**
     * Adds a farm field to the configuration.
     * @param field Field to add
     */
    public void addField(FarmField field) {
        if (field != null) {
            fields.add(field);
        }
    }
    
    /**
     * Adds a storage configuration for a field.
     * @param fieldId Field ID
     * @param storageConfig Storage configuration
     */
    public void addStorage(UUID fieldId, StorageConfig storageConfig) {
        if (fieldId != null && storageConfig != null) {
            storage.put(fieldId, storageConfig);
        }
    }
    
    /**
     * Adds a crop configuration.
     * @param cropConfig Crop configuration to add
     */
    public void addCrop(CropConfig cropConfig) {
        if (cropConfig != null) {
            crops.add(cropConfig);
        }
    }
    
    /**
     * Validates this configuration.
     * @return List of validation error messages (empty if valid)
     */
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        
        // Check version
        if (!ConfigVersion.isSupported(version)) {
            errors.add("Unsupported configuration version: " + version);
        }
        
        // Validate fields
        Set<UUID> fieldIds = new HashSet<>();
        for (FarmField field : fields) {
            if (fieldIds.contains(field.getId())) {
                errors.add("Duplicate field ID: " + field.getId());
            }
            fieldIds.add(field.getId());
            
            try {
                field.validate();
            } catch (IllegalStateException e) {
                errors.add("Invalid field '" + field.getName() + "': " + e.getMessage());
            }
        }
        
        // Validate storage references
        for (UUID fieldId : storage.keySet()) {
            if (!fieldIds.contains(fieldId)) {
                errors.add("Storage config references unknown field ID: " + fieldId);
            }
        }
        
        return errors;
    }
    
    /**
     * Checks if this configuration is valid.
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return validate().isEmpty();
    }
    
    /**
     * Creates a default empty configuration.
     * @return New empty configuration
     */
    public static FarmingConfig createDefault() {
        return new FarmingConfig();
    }
}
