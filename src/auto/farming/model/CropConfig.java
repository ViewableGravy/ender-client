package auto.farming.model;

/**
 * Configuration for a specific crop type including growth properties,
 * quality thresholds, and farming requirements.
 * 
 * <p>Crop configurations define how the bot should handle specific crop types,
 * including quality acceptance ranges and special requirements like watering.</p>
 */
public class CropConfig {
    private final String resourceName;
    private final String displayName;
    private final int growthStages;
    private final boolean wateringRequired;
    private final int minQualityThreshold;
    private final int maxQualityThreshold;
    private final int harvestYield;
    
    /**
     * Private constructor - use Builder instead.
     */
    private CropConfig(Builder builder) {
        this.resourceName = builder.resourceName;
        this.displayName = builder.displayName;
        this.growthStages = builder.growthStages;
        this.wateringRequired = builder.wateringRequired;
        this.minQualityThreshold = builder.minQualityThreshold;
        this.maxQualityThreshold = builder.maxQualityThreshold;
        this.harvestYield = builder.harvestYield;
        
        validate();
    }
    
    /**
     * Validates crop configuration parameters.
     * 
     * @throws IllegalStateException if configuration is invalid
     */
    private void validate() {
        if (resourceName == null || resourceName.trim().isEmpty()) {
            throw new IllegalStateException("Resource name is required");
        }
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalStateException("Display name is required");
        }
        if (growthStages < 1) {
            throw new IllegalStateException("Growth stages must be at least 1");
        }
        if (minQualityThreshold > maxQualityThreshold) {
            throw new IllegalStateException("Min quality threshold cannot exceed max");
        }
        if (minQualityThreshold < -4 || minQualityThreshold > 5) {
            throw new IllegalStateException("Min quality threshold must be between -4 and 5");
        }
        if (maxQualityThreshold < -4 || maxQualityThreshold > 5) {
            throw new IllegalStateException("Max quality threshold must be between -4 and 5");
        }
    }
    
    /**
     * Creates a new builder for CropConfig.
     * 
     * @return New builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * @return Crop resource name (e.g., "gfx/terobjs/plants/wheat")
     */
    public String getResourceName() {
        return resourceName;
    }
    
    /**
     * @return Human-readable crop name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * @return Number of growth stages before harvest
     */
    public int getGrowthStages() {
        return growthStages;
    }
    
    /**
     * @return true if crop requires watering, false otherwise
     */
    public boolean isWateringRequired() {
        return wateringRequired;
    }
    
    /**
     * @return Minimum acceptable quality threshold (-4 to +5)
     */
    public int getMinQualityThreshold() {
        return minQualityThreshold;
    }
    
    /**
     * @return Maximum quality threshold (-4 to +5)
     */
    public int getMaxQualityThreshold() {
        return maxQualityThreshold;
    }
    
    /**
     * @return Estimated harvest yield per crop
     */
    public int getHarvestYield() {
        return harvestYield;
    }
    
    /**
     * Checks if a specific quality value is acceptable for this crop.
     * 
     * @param quality Quality value to check
     * @return true if quality is within acceptable range
     */
    public boolean isQualityAcceptable(int quality) {
        return quality >= minQualityThreshold && quality <= maxQualityThreshold;
    }
    
    @Override
    public String toString() {
        return String.format("CropConfig[%s, stages=%d, quality=%d to %d, watering=%s]",
            displayName, growthStages, minQualityThreshold, maxQualityThreshold, wateringRequired);
    }
    
    /**
     * Builder for CropConfig with fluent API.
     */
    public static class Builder {
        private String resourceName;
        private String displayName;
        private int growthStages = 5; // Default
        private boolean wateringRequired = false; // Default
        private int minQualityThreshold = -4; // Default for quantity
        private int maxQualityThreshold = 5; // Default
        private int harvestYield = 1; // Default
        
        /**
         * Sets the crop resource name.
         * 
         * @param resourceName Resource path (e.g., "gfx/terobjs/plants/wheat")
         * @return This builder
         */
        public Builder resourceName(String resourceName) {
            this.resourceName = resourceName;
            return this;
        }
        
        /**
         * Sets the display name.
         * 
         * @param displayName Human-readable name
         * @return This builder
         */
        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }
        
        /**
         * Sets the number of growth stages.
         * 
         * @param growthStages Number of stages (1+)
         * @return This builder
         */
        public Builder growthStages(int growthStages) {
            this.growthStages = growthStages;
            return this;
        }
        
        /**
         * Sets whether watering is required.
         * 
         * @param wateringRequired true if watering needed
         * @return This builder
         */
        public Builder wateringRequired(boolean wateringRequired) {
            this.wateringRequired = wateringRequired;
            return this;
        }
        
        /**
         * Sets the minimum acceptable quality threshold.
         * 
         * @param minQualityThreshold Min quality (-4 to +5)
         * @return This builder
         */
        public Builder minQualityThreshold(int minQualityThreshold) {
            this.minQualityThreshold = minQualityThreshold;
            return this;
        }
        
        /**
         * Sets the maximum quality threshold.
         * 
         * @param maxQualityThreshold Max quality (-4 to +5)
         * @return This builder
         */
        public Builder maxQualityThreshold(int maxQualityThreshold) {
            this.maxQualityThreshold = maxQualityThreshold;
            return this;
        }
        
        /**
         * Sets the harvest yield estimate.
         * 
         * @param harvestYield Estimated yield per crop
         * @return This builder
         */
        public Builder harvestYield(int harvestYield) {
            this.harvestYield = harvestYield;
            return this;
        }
        
        /**
         * Configures for quality breeding (0 to +5 quality range).
         * 
         * @return This builder
         */
        public Builder forQualityBreeding() {
            this.minQualityThreshold = 0;
            this.maxQualityThreshold = 5;
            return this;
        }
        
        /**
         * Configures for quantity production (-4 to +5 quality range).
         * 
         * @return This builder
         */
        public Builder forQuantityProduction() {
            this.minQualityThreshold = -4;
            this.maxQualityThreshold = 5;
            return this;
        }
        
        /**
         * Builds the CropConfig instance.
         * 
         * @return New CropConfig
         * @throws IllegalStateException if configuration is invalid
         */
        public CropConfig build() {
            return new CropConfig(this);
        }
    }
}
