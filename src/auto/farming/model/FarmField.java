package auto.farming.model;

import auto.farming.model.subdivision.SubdivisionConfig;
import haven.Coord2d;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a single farm field with all necessary properties for field management,
 * tracking, and persistence.
 * 
 * <p>A FarmField defines a bounded area on the game map where automated farming
 * operations can be performed. Each field has a unique ID, type (quality/quantity),
 * shape (rectangle/circle), and associated crop type.</p>
 * 
 * <p>Fields are the fundamental unit of organization for the farming bot system.</p>
 */
public class FarmField {
    private final UUID id;
    private String name;
    private FieldType type;
    private FieldShape shape;
    private FieldGrid grid;
    private SubdivisionConfig subdivisionConfig;
    private CropConfig cropConfig;
    private FieldTileMap tileMap;
    private StorageConfig storageConfig;
    private Coord2d gridOrigin;
    private String cropType; // Resource name e.g., "gfx/terobjs/plants/wheat"
    private boolean enabled;
    private long createdAt;
    
    /**
     * Constructs a new FarmField with the specified properties.
     * 
     * <p>The field is created with a unique UUID, enabled by default, and
     * timestamped with the current system time.</p>
     * 
     * @param name User-defined name for the field (must not be null or empty)
     * @param type Field type (QUALITY or QUANTITY)
     * @param shape Field shape (RECTANGLE or CIRCLE)
     * @throws IllegalArgumentException if name is null or empty
     * @throws NullPointerException if type or shape is null
     */
    public FarmField(String name, FieldType type, FieldShape shape) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or empty");
        }
        if (type == null) {
            throw new NullPointerException("Field type cannot be null");
        }
        if (shape == null) {
            throw new NullPointerException("Field shape cannot be null");
        }
        
        this.id = UUID.randomUUID();
        this.name = name;
        this.type = type;
        this.shape = shape;
        this.enabled = true;
        this.createdAt = System.currentTimeMillis();
    }
    
    /**
     * Validates that all required field properties are set.
     * 
     * @throws IllegalStateException if any required field is missing or invalid
     */
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("Field name required");
        }
        if (type == null || shape == null) {
            throw new IllegalStateException("Field type and shape required");
        }
        if (cropType == null || cropType.trim().isEmpty()) {
            throw new IllegalStateException("Crop type required");
        }
    }
    
    // Getters
    
    /**
     * @return Unique identifier for this field
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * @return User-defined field name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return Field type (QUALITY or QUANTITY)
     */
    public FieldType getType() {
        return type;
    }
    
    /**
     * @return Field shape (RECTANGLE or CIRCLE)
     */
    public FieldShape getShape() {
        return shape;
    }
    
    /**
     * @return Field grid boundaries (can be null if not yet defined)
     */
    public FieldGrid getGrid() {
        return grid;
    }
    
    /**
     * @return Subdivision configuration (can be null if no subdivision)
     */
    public SubdivisionConfig getSubdivisionConfig() {
        return subdivisionConfig;
    }
    
    /**
     * @return Crop configuration (can be null if not yet configured)
     */
    public CropConfig getCropConfig() {
        return cropConfig;
    }
    
    /**
     * @return Tile state map for tracking individual tile states (can be null if not initialized)
     */
    public FieldTileMap getTileMap() {
        return tileMap;
    }
    
    /**
     * @return Storage configuration for input/output chests (can be null if not configured)
     */
    public StorageConfig getStorageConfig() {
        return storageConfig;
    }
    
    /**
     * @return Grid origin coordinates (can be null if not yet positioned)
     */
    public Coord2d getGridOrigin() {
        return gridOrigin;
    }
    
    /**
     * @return Resource name of the crop type (e.g., "gfx/terobjs/plants/wheat")
     */
    public String getCropType() {
        return cropType;
    }
    
    /**
     * @return true if field is enabled for bot operations, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * @return Timestamp when this field was created (milliseconds since epoch)
     */
    public long getCreatedAt() {
        return createdAt;
    }
    
    // Setters
    
    /**
     * Sets the field name.
     * 
     * @param name New field name (must not be null or empty)
     * @throws IllegalArgumentException if name is null or empty
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or empty");
        }
        this.name = name;
    }
    
    /**
     * Sets the field type.
     * 
     * @param type New field type (must not be null)
     * @throws NullPointerException if type is null
     */
    public void setType(FieldType type) {
        if (type == null) {
            throw new NullPointerException("Field type cannot be null");
        }
        this.type = type;
    }
    
    /**
     * Sets the field shape.
     * 
     * @param shape New field shape (must not be null)
     * @throws NullPointerException if shape is null
     */
    public void setShape(FieldShape shape) {
        if (shape == null) {
            throw new NullPointerException("Field shape cannot be null");
        }
        this.shape = shape;
    }
    
    /**
     * Sets the field grid boundaries.
     * 
     * @param grid New field grid (can be null)
     */
    public void setGrid(FieldGrid grid) {
        this.grid = grid;
        // Reinitialize tile map if grid changes
        if (grid != null) {
            this.tileMap = new FieldTileMap(this);
        }
    }
    
    /**
     * Sets the subdivision configuration.
     * 
     * @param subdivisionConfig New subdivision configuration (can be null)
     */
    public void setSubdivisionConfig(SubdivisionConfig subdivisionConfig) {
        this.subdivisionConfig = subdivisionConfig;
    }
    
    /**
     * Sets the crop configuration.
     * 
     * @param cropConfig New crop configuration (can be null)
     */
    public void setCropConfig(CropConfig cropConfig) {
        this.cropConfig = cropConfig;
    }
    
    /**
     * Sets the storage configuration.
     * 
     * @param storageConfig New storage configuration (can be null)
     */
    public void setStorageConfig(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }
    
    /**
     * Sets the grid origin coordinates.
     * 
     * @param gridOrigin Grid coordinates for field origin (can be null)
     */
    public void setGridOrigin(Coord2d gridOrigin) {
        this.gridOrigin = gridOrigin;
    }
    
    /**
     * Sets the crop type resource name.
     * 
     * @param cropType Resource name (e.g., "gfx/terobjs/plants/wheat")
     */
    public void setCropType(String cropType) {
        this.cropType = cropType;
    }
    
    /**
     * Sets whether this field is enabled for bot operations.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Sets the creation timestamp.
     * 
     * @param createdAt Timestamp in milliseconds since epoch
     */
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Compares this field to another object for equality.
     * 
     * <p>Two fields are considered equal if they have the same UUID.</p>
     * 
     * @param o Object to compare
     * @return true if objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FarmField)) return false;
        FarmField that = (FarmField) o;
        return id.equals(that.id);
    }
    
    /**
     * Returns the hash code for this field.
     * 
     * <p>Hash code is based solely on the field's UUID.</p>
     * 
     * @return Hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    /**
     * Returns a string representation of this field.
     * 
     * @return String containing field ID, name, type, and crop type
     */
    @Override
    public String toString() {
        return String.format("FarmField[id=%s, name=%s, type=%s, crop=%s]", 
            id, name, type, cropType);
    }
}
