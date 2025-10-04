package auto.farming.config;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import haven.Config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * Handles loading and saving farming configuration from/to JSON files.
 */
public class ConfigLoader {
    private static final String CONFIG_FILENAME = "farming.json5";
    private static final Gson gson = ConfigSerializer.createGson();
    
    /**
     * Loads farming configuration from the file system.
     * If the file doesn't exist, returns a default empty configuration.
     * @return Loaded or default farming configuration
     * @throws IOException if file exists but cannot be read
     * @throws JsonSyntaxException if file contains invalid JSON
     */
    public static FarmingConfig load() throws IOException {
        File configFile = Config.getFile(CONFIG_FILENAME);
        
        // Create default if file doesn't exist
        if (!configFile.exists()) {
            return FarmingConfig.createDefault();
        }
        
        // Read and parse file
        try (Reader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            FarmingConfig config = gson.fromJson(reader, FarmingConfig.class);
            
            // Validate loaded configuration
            if (config == null) {
                throw new JsonSyntaxException("Configuration file is empty or invalid");
            }
            
            List<String> errors = config.validate();
            if (!errors.isEmpty()) {
                throw new IllegalStateException(
                    "Configuration validation failed:\n- " + String.join("\n- ", errors)
                );
            }
            
            return config;
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException("Failed to parse farming configuration: " + e.getMessage(), e);
        }
    }
    
    /**
     * Loads farming configuration, returning a default on any error.
     * Logs errors but does not throw exceptions.
     * @return Loaded configuration or default if error occurs
     */
    public static FarmingConfig loadOrDefault() {
        try {
            return load();
        } catch (Exception e) {
            System.err.println("Error loading farming configuration: " + e.getMessage());
            e.printStackTrace();
            return FarmingConfig.createDefault();
        }
    }
    
    /**
     * Saves farming configuration to the file system.
     * @param config Configuration to save
     * @throws IOException if file cannot be written
     */
    public static void save(FarmingConfig config) throws IOException {
        if (config == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        
        // Validate before saving
        List<String> errors = config.validate();
        if (!errors.isEmpty()) {
            throw new IllegalStateException(
                "Cannot save invalid configuration:\n- " + String.join("\n- ", errors)
            );
        }
        
        File configFile = Config.getFile(CONFIG_FILENAME);
        
        // Create parent directories if needed
        File parentDir = configFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create config directory: " + parentDir.getAbsolutePath());
            }
        }
        
        // Write to temporary file first, then rename (atomic operation)
        File tempFile = new File(configFile.getParentFile(), CONFIG_FILENAME + ".tmp");
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
            gson.toJson(config, writer);
        }
        
        // Atomic rename
        Files.move(tempFile.toPath(), configFile.toPath(), 
                   java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
    
    /**
     * Saves configuration, logging errors instead of throwing exceptions.
     * @param config Configuration to save
     * @return true if saved successfully, false otherwise
     */
    public static boolean saveQuietly(FarmingConfig config) {
        try {
            save(config);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving farming configuration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets the path to the configuration file.
     * @return Configuration file path
     */
    public static File getConfigFile() {
        return Config.getFile(CONFIG_FILENAME);
    }
    
    /**
     * Checks if a configuration file exists.
     * @return true if config file exists
     */
    public static boolean configExists() {
        return Config.getFile(CONFIG_FILENAME).exists();
    }
    
    /**
     * Deletes the configuration file.
     * @return true if file was deleted, false if it didn't exist or couldn't be deleted
     */
    public static boolean deleteConfig() {
        File configFile = Config.getFile(CONFIG_FILENAME);
        if (configFile.exists()) {
            return configFile.delete();
        }
        return false;
    }
}
