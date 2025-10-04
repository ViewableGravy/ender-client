package auto.farming.model.subdivision;

import auto.farming.model.FieldGrid;
import java.util.List;

/**
 * Interface for field subdivision strategies.
 * 
 * <p>Subdivisions allow splitting a field into smaller segments for
 * managing different crops or quality zones within a single field boundary.</p>
 */
public interface FieldSubdivision {
    
    /**
     * Gets the total number of subdivisions.
     * 
     * @return Number of segments this subdivision creates
     */
    int getCount();
    
    /**
     * Gets a specific subdivision segment.
     * 
     * @param index Segment index (0-based)
     * @return FieldGrid representing the specified segment
     * @throws IndexOutOfBoundsException if index is invalid
     */
    FieldGrid getSubdivision(int index);
    
    /**
     * Gets all subdivision segments.
     * 
     * @return List of FieldGrids representing all segments
     */
    List<FieldGrid> getAllSubdivisions();
    
    /**
     * Checks if a point belongs to a specific subdivision.
     * 
     * @param x Grid X coordinate
     * @param y Grid Y coordinate
     * @param segmentIndex Segment index to check
     * @return true if point is in the specified segment
     */
    boolean isInSubdivision(double x, double y, int segmentIndex);
}
