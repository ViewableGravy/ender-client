package auto.farming.config;

/**
 * Configuration version for managing schema migrations.
 */
public class ConfigVersion {
    /**
     * Current configuration schema version.
     * Increment this when making breaking changes to the JSON structure.
     */
    public static final int CURRENT_VERSION = 1;
    
    /**
     * Minimum supported version for backward compatibility.
     * Configurations older than this will need manual migration.
     */
    public static final int MIN_SUPPORTED_VERSION = 1;
    
    /**
     * Checks if a configuration version is supported.
     * @param version Version to check
     * @return true if version is supported
     */
    public static boolean isSupported(int version) {
        return version >= MIN_SUPPORTED_VERSION && version <= CURRENT_VERSION;
    }
}
