package auto.farming.model;

import java.util.Objects;

/**
 * Represents a quality range rule for filtering crops.
 * Used to determine which output chest should receive crops based on their quality.
 */
public class QualityRule {
    private final int minQuality;
    private final int maxQuality;
    
    /**
     * Creates a quality rule with the specified range.
     * @param minQuality Minimum quality (inclusive), typically -4 to +5
     * @param maxQuality Maximum quality (inclusive), typically -4 to +5
     * @throws IllegalArgumentException if min > max
     */
    public QualityRule(int minQuality, int maxQuality) {
        if (minQuality > maxQuality) {
            throw new IllegalArgumentException(
                "Minimum quality (" + minQuality + ") cannot be greater than maximum quality (" + maxQuality + ")"
            );
        }
        this.minQuality = minQuality;
        this.maxQuality = maxQuality;
    }
    
    /**
     * Gets the minimum quality value for this rule.
     * @return Minimum quality (inclusive)
     */
    public int getMinQuality() {
        return minQuality;
    }
    
    /**
     * Gets the maximum quality value for this rule.
     * @return Maximum quality (inclusive)
     */
    public int getMaxQuality() {
        return maxQuality;
    }
    
    /**
     * Checks if a quality value matches this rule.
     * @param quality Quality value to check
     * @return true if quality is within range [min, max]
     */
    public boolean matches(int quality) {
        return quality >= minQuality && quality <= maxQuality;
    }
    
    /**
     * Creates a rule for high quality crops (breeding quality).
     * Range: 0 to +5
     * @return Quality rule for breeding quality crops
     */
    public static QualityRule highQuality() {
        return new QualityRule(0, 5);
    }
    
    /**
     * Creates a rule for premium quality crops.
     * Range: +3 to +5
     * @return Quality rule for premium quality crops
     */
    public static QualityRule premiumQuality() {
        return new QualityRule(3, 5);
    }
    
    /**
     * Creates a rule for medium quality crops.
     * Range: 0 to +2
     * @return Quality rule for medium quality crops
     */
    public static QualityRule mediumQuality() {
        return new QualityRule(0, 2);
    }
    
    /**
     * Creates a rule for low quality crops (quantity production).
     * Range: -4 to -1
     * @return Quality rule for low quality crops
     */
    public static QualityRule lowQuality() {
        return new QualityRule(-4, -1);
    }
    
    /**
     * Creates a rule that accepts any quality.
     * Range: -4 to +5
     * @return Quality rule that accepts all quality values
     */
    public static QualityRule anyQuality() {
        return new QualityRule(-4, 5);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QualityRule that = (QualityRule) o;
        return minQuality == that.minQuality && maxQuality == that.maxQuality;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(minQuality, maxQuality);
    }
    
    @Override
    public String toString() {
        return "QualityRule{" + minQuality + " to " + maxQuality + '}';
    }
}
