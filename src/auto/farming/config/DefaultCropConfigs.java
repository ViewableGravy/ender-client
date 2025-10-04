package auto.farming.config;

import auto.farming.model.CropConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * Default crop configurations for common Haven & Hearth crops.
 * 
 * <p>Provides pre-configured CropConfig instances for standard crops
 * with typical growth stages and requirements.</p>
 */
public class DefaultCropConfigs {
    
    private static final Map<String, CropConfig> CONFIGS = new HashMap<>();
    
    // Common Field Crops
    public static final CropConfig WHEAT = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/wheat")
        .displayName("Wheat")
        .growthStages(7)
        .wateringRequired(false)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    public static final CropConfig BARLEY = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/barley")
        .displayName("Barley")
        .growthStages(7)
        .wateringRequired(false)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    public static final CropConfig MILLET = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/millet")
        .displayName("Millet")
        .growthStages(7)
        .wateringRequired(false)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    public static final CropConfig FLAX = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/flax")
        .displayName("Flax")
        .growthStages(6)
        .wateringRequired(false)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    public static final CropConfig HEMP = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/hemp")
        .displayName("Hemp")
        .growthStages(6)
        .wateringRequired(false)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    public static final CropConfig PIPEWEED = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/pipeweed")
        .displayName("Pipeweed")
        .growthStages(6)
        .wateringRequired(false)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    // Garden Vegetables
    public static final CropConfig CARROT = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/carrot")
        .displayName("Carrot")
        .growthStages(6)
        .wateringRequired(true)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    public static final CropConfig BEETROOT = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/beet")
        .displayName("Beetroot")
        .growthStages(6)
        .wateringRequired(true)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    public static final CropConfig TURNIP = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/turnip")
        .displayName("Turnip")
        .growthStages(6)
        .wateringRequired(true)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    public static final CropConfig LETTUCE = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/lettuce")
        .displayName("Lettuce")
        .growthStages(5)
        .wateringRequired(true)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    public static final CropConfig CABBAGE = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/cabbage")
        .displayName("Cabbage")
        .growthStages(6)
        .wateringRequired(true)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    public static final CropConfig ONION = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/onion")
        .displayName("Onion")
        .growthStages(6)
        .wateringRequired(true)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    public static final CropConfig LEEK = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/leek")
        .displayName("Leek")
        .growthStages(6)
        .wateringRequired(true)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    public static final CropConfig PEAS = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/pea")
        .displayName("Peas")
        .growthStages(6)
        .wateringRequired(true)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    // Gourds
    public static final CropConfig PUMPKIN = register(CropConfig.builder()
        .resourceName("gfx/terobjs/plants/pumpkin")
        .displayName("Pumpkin")
        .growthStages(7)
        .wateringRequired(true)
        .forQuantityProduction()
        .harvestYield(1)
        .build());
    
    /**
     * Registers a crop config in the lookup map.
     */
    private static CropConfig register(CropConfig config) {
        CONFIGS.put(config.getResourceName(), config);
        return config;
    }
    
    /**
     * Gets a crop configuration by resource name.
     * 
     * @param resourceName Crop resource path
     * @return CropConfig if found, null otherwise
     */
    public static CropConfig get(String resourceName) {
        return CONFIGS.get(resourceName);
    }
    
    /**
     * Gets a crop configuration by resource name, or creates a default one if not found.
     * 
     * @param resourceName Crop resource path
     * @param displayName Display name for unknown crop
     * @return CropConfig instance
     */
    public static CropConfig getOrDefault(String resourceName, String displayName) {
        CropConfig config = CONFIGS.get(resourceName);
        if (config != null) {
            return config;
        }
        
        // Create a generic default configuration
        return CropConfig.builder()
            .resourceName(resourceName)
            .displayName(displayName != null ? displayName : "Unknown Crop")
            .growthStages(6)
            .wateringRequired(false)
            .forQuantityProduction()
            .harvestYield(1)
            .build();
    }
    
    /**
     * Checks if a crop configuration exists.
     * 
     * @param resourceName Crop resource path
     * @return true if configuration exists
     */
    public static boolean has(String resourceName) {
        return CONFIGS.containsKey(resourceName);
    }
    
    /**
     * @return Map of all registered crop configurations
     */
    public static Map<String, CropConfig> getAll() {
        return new HashMap<>(CONFIGS);
    }
}
